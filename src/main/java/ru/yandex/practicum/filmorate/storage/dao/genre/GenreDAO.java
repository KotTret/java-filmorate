package ru.yandex.practicum.filmorate.storage.dao.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenreDAO implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate nameJdbcTemplate;

    static Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }

    @Override
    public Genre findById(Integer id) {
        String sqlQuery = "select * from GENRES where genre_id = ?";
        return jdbcTemplate.query(sqlQuery, GenreDAO::mapRowToGenre, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Жанр с id=%d не найден.", id)));
    }
    @Override
    public List<Genre> findAll() {
        String sqlQuery = "select * from genres order by GENRE_ID";
        return jdbcTemplate.query(sqlQuery, GenreDAO::mapRowToGenre);
    }
    @Override
    public void findGenresForFilm(Film film) {
        String sqlQuery = "SELECT * from GENRES where GENRE_ID in (select GENRE_ID from FILM_GENRES where FILM_ID = ?)";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDAO::mapRowToGenre, film.getId());
        film.setGenres(genres);
    }
    @Override
    public void findGenresForFilm(List<Film> films) {
        Map<Integer, Film> resFilms = films.stream().collect(Collectors.toMap(Film::getId, film -> film));
        Set<Integer> idFilms = resFilms.keySet();
        SqlParameterSource parameters = new MapSqlParameterSource("idFilms", idFilms);
        String sql = "SELECT fg.film_id, fg.genre_id, g.genre_name from FILM_GENRES as fg " +
                "LEFT JOIN genres AS g ON  fg.genre_id = g.genre_id where film_id IN (:idFilms)";
        nameJdbcTemplate.query(sql, parameters,
                (rs, rowNum) -> resFilms.get(rs.getInt("film_id")).getGenres().add(mapRowToGenre(rs, rowNum)));

    }
}