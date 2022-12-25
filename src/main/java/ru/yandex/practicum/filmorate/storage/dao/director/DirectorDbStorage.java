package ru.yandex.practicum.filmorate.storage.dao.director;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate nameJdbcTemplate;

    @Override
    public void add(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        Integer id = simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue();
        director.setId(id);
    }

    @Override
    public void update(Director director) {
        String sqlQuery = "update DIRECTORS set DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?";
        if (jdbcTemplate.update(sqlQuery,
                director.getName(), director.getId()) < 1) {
            throw  new ObjectNotFoundException("Такого режиссёра ещё нет, невозможно обновить!");
        }
    }

    @Override
    public void delete(Integer id) {
        String  sqlQuery = "delete from DIRECTORS where DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Director> findAll() {
        String sqlQuery = "SELECT * FROM DIRECTORS";
        return jdbcTemplate.query(sqlQuery, DirectorDbStorage::mapRowToDirector);
    }

    @Override
    public boolean containsId(Integer id) {
        String sqlQuery = "SELECT DIRECTOR_ID FROM DIRECTORS where DIRECTOR_ID = ?";
        return !jdbcTemplate.queryForList(sqlQuery, Integer.class, id).isEmpty();
    }

    @Override
    public Director get(Integer id) {
        String sqlQuery = "SELECT * FROM DIRECTORS   WHERE DIRECTOR_ID = ?";
        return jdbcTemplate.query(sqlQuery, DirectorDbStorage::mapRowToDirector, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("Режиссёр с id=%d не найден.", id)));
    }

    @Override
    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        if(!containsId(directorId)){
            throw  new ObjectNotFoundException("Идентификатор режиссёра отсутствует, " +
                    "режиссёр не найден");
        }
        if (sortBy.equals("likes")){
            String sqlQuery = "SELECT * FROM FILMS as f join MPA M on f.MPA_ID = M.MPA_ID where FILM_ID in" +
                    " (select FILM_ID from FILM_DIRECTORS where DIRECTOR_ID = ?) order by RATE desc ";
            return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, directorId);
        } else if (sortBy.equals("year")){
            String sqlQuery = "SELECT * FROM FILMS as f join MPA M on f.MPA_ID = M.MPA_ID where FILM_ID in" +
                    " (select FILM_ID from FILM_DIRECTORS where DIRECTOR_ID = ?) order by RELEASE_DATE";
            return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, directorId);
        } else {
            String sqlQuery = "SELECT * FROM FILMS as f join MPA M on f.MPA_ID = M.MPA_ID where FILM_ID in" +
                    " (select FILM_ID from FILM_DIRECTORS where DIRECTOR_ID = ?)";
            return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, directorId);
        }
    }

    @Override
    public void findDirectorsForFilm(Film film) {
        String sqlQuery = "SELECT * from DIRECTORS where DIRECTOR_ID in (select DIRECTOR_ID from FILM_DIRECTORS where FILM_ID = ?)";
        List<Director> directors = jdbcTemplate.query(sqlQuery, DirectorDbStorage::mapRowToDirector, film.getId());
        film.setDirectors(directors);
    }

    @Override
    public void findDirectorsForFilm(List<Film> films) {
        Map<Integer, Film> resFilms = films.stream().collect(Collectors.toMap(Film::getId, film -> film));
        Set<Integer> idFilms = resFilms.keySet();
        SqlParameterSource parameters = new MapSqlParameterSource("idFilms", idFilms);
        String sql = "SELECT fd.film_id, fd.director_id, d.director_name from FILM_DIRECTORS as fd " +
                "LEFT JOIN directors AS d ON  fd.director_id = d.director_id where film_id IN (:idFilms)";
        nameJdbcTemplate.query(sql, parameters,
                (rs, rowNum) -> resFilms.get(rs.getInt("film_id")).getDirectors().add(mapRowToDirector(rs, rowNum)));

    }

    static Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("director_name"))
                .build();
    }

}
