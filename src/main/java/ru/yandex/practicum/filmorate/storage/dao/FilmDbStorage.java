package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;

    private  final  GenreDbStorage genreDbStorage;

    @Override
    public void add(Film film) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("films")
                    .usingGeneratedKeyColumns("film_id");
        Integer id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.setId(id);
        setMpaAndGenres(film);
    }

    @Override
    public void update(Film film) {
        String sqlQuery = "UPDATE films SET NAME = ?, RELEASE_DATE = ?, DESCRIPTION = ?, DURATION_IN_MINUTES = ?" +
                ", MPA_ID = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        deleteGenres(film.getId());
        setMpaAndGenres(film);
    }

    @Override
    public void delete(Integer id) {
        String sqlQuery = "delete from FILMS where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }


    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM FILMS";
        List<Film> resultFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        genreDbStorage.findGenresForFilm(resultFilms);
        return resultFilms;
    }

    @Override
    public boolean containsId(Integer id) {
        String sqlQuery = "SELECT FILM_ID FROM FILMS where FILM_ID = ?";
        return !jdbcTemplate.queryForList(sqlQuery, Integer.class, id).isEmpty();

    }

    @Override
    public Film get(Integer id) {
        String sqlQuery = "SELECT * FROM films f  WHERE f.film_id = ?";
        Film film = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id=%d не найден.", id)));
        genreDbStorage.findGenresForFilm(film);
        return film;
    }

    @Override
    public void putLike(Integer id, Integer userId) {
        String sqlQuery = "insert into film_likes(FILM_ID, USER_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);

        sqlQuery = "update FILMS set NUMBER_OF_LIKES =  NUMBER_OF_LIKES + 1 " +
                "where film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? and USER_ID = ?";
        if (jdbcTemplate.update(sqlQuery, id, userId) < 1) {
            throw new ObjectNotFoundException(String.format("Пользователь с id=%d не ставил лайк фильму с id=%d.", userId, id));
        }

        sqlQuery = "update FILMS set NUMBER_OF_LIKES =  NUMBER_OF_LIKES - 1 " +
                "where film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Film> findPopular(Integer count) {
        String sqlQuery = "SELECT * FROM FILMS order by NUMBER_OF_LIKES desc limit ?";
        List<Film> resFilms =  jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        genreDbStorage.findGenresForFilm(resFilms);
        return resFilms;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration_in_minutes"))
                .mpa(mpaDbStorage.findById(resultSet.getInt("mpa_id")))
                .numberOfLikes(resultSet.getInt("number_of_likes"))
                .genres(new ArrayList<>())
                .build();
    }

    private void setMpaAndGenres(Film film) {
        film.setMpa(mpaDbStorage.findById(film.getMpa().getId()));
        if (film.getGenres() != null) {
            List<Integer> idGenres = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                idGenres.add(genre.getId());
            }
            jdbcTemplate.batchUpdate("MERGE INTO film_genres values (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt( 1, film.getId());
                    ps.setInt( 2, idGenres.get(i));
                }

                @Override
                public int getBatchSize() {
                    return idGenres.size();
                }
            });
            genreDbStorage.findGenresForFilm(film);
        }
    }

    private void deleteGenres(Integer filmId) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

}
