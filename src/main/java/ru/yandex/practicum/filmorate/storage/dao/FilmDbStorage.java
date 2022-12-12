package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

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
       if(jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()) < 1) {
           throw new FilmNotFoundException("Такого фильма ещё нет, невозможно обновить!");
       }
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
        String sqlQuery = "SELECT * FROM FILMS as f join MPA M on f.MPA_ID = M.MPA_ID";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm);
    }

    @Override
    public boolean containsId(Integer id) {
        String sqlQuery = "SELECT FILM_ID FROM FILMS where FILM_ID = ?";
        return !jdbcTemplate.queryForList(sqlQuery, Integer.class, id).isEmpty();

    }

    @Override
    public Film get(Integer id) {
        String sqlQuery = "SELECT * FROM films f join MPA M on f.MPA_ID = M.MPA_ID WHERE f.film_id = ? ";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id=%d не найден.", id)));
    }


    @Override
    public List<Film> findPopular(Integer count) {
        String sqlQuery = "SELECT * FROM FILMS as f join MPA M on f.MPA_ID = M.MPA_ID order by RATE desc limit ?";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, count);
    }

    static Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration_in_minutes"))
                .mpa(new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name")))
                .rate(resultSet.getInt("rate"))
                .genres(new ArrayList<>())
                .build();
    }

    private void setMpaAndGenres(Film film) {
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
        }
    }

    private void deleteGenres(Integer filmId) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

}
