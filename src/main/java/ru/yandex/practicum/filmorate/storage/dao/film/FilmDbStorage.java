package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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
        setDirectors(film);
    }

    @Override
    public void update(Film film) {
        String sqlQuery = "UPDATE films SET NAME = ?, RELEASE_DATE = ?, DESCRIPTION = ?, DURATION_IN_MINUTES = ?" +
                ", MPA_ID = ?, RATE = ? WHERE FILM_ID = ?";
       if(jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getRate(),
                film.getId()) < 1) {
           throw new FilmNotFoundException("Такого фильма ещё нет, невозможно обновить!");
       }
        setMpaAndGenres(film);
        setDirectors(film);
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
    public List<Film> searchFilmsByTitle(String query) {
        query = query + "%";
        String sqlQuery = "SELECT * FROM FILMS as f join MPA M on f.MPA_ID = M.MPA_ID WHERE f.NAME LIKE ? ";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, query);
    }


    @Override
    public List<Film> searchFilmsByDirector(String query) {
        query = query + "%";
        String sqlQuery = "SELECT * FROM DIRECTORS AS d join FILMS AS f ON d.DIRECTOR_ID = f.DIRECTOR_ID " +
                "JOIN MPA M on f.MPA_ID = M.MPA_ID WHERE d.NAME LIKE ? ";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, query);
    }



    @Override
    public List<Film> findPopular(Integer count) {
        String sqlQuery = "SELECT * FROM FILMS as f join MPA M on f.MPA_ID = M.MPA_ID order by RATE desc limit ?";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, count);
    }


    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        String sqlQuery = "SELECT * " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON m.MPA_ID = f.MPA_ID " +
                "JOIN FILM_LIKES AS l1 ON (l1.film_id = f.film_id AND l1.user_id = ?) " +
                "JOIN FILM_LIKES AS l2 ON (l2.film_id = f.film_id AND l2.user_id = ?) " +
                "JOIN " +
                "(SELECT film_id, COUNT(user_id) AS rate " +
                "FROM FILM_LIKES " +
                "GROUP BY film_id) AS fl ON (fl.film_id = f.film_id) " +
                "ORDER BY fl.rate DESC ";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, userId, friendId);
    }

  
    public static Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
    
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration_in_minutes"))
                .mpa(new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name")))
                .rate(resultSet.getInt("rate"))
                .genres(new ArrayList<>())
                .directors(new ArrayList<>())
                .build();
    }

    private void setMpaAndGenres(Film film) {
        if (film.getGenres() != null) {
            String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery, film.getId());
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

    private void setDirectors(Film film) {
        if (film.getDirectors() != null) {
            String sqlQuery = "DELETE FROM FILM_DIRECTORS WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery, film.getId());
            List<Integer> idDirectors = new ArrayList<>();
            for (Director director : film.getDirectors()) {
                idDirectors.add(director.getId());
            }
            jdbcTemplate.batchUpdate("MERGE INTO FILM_DIRECTORS values (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt( 1, film.getId());
                    ps.setInt( 2, idDirectors.get(i));
                }

                @Override
                public int getBatchSize() {
                    return idDirectors.size();
                }
            });
        }
    }

}
