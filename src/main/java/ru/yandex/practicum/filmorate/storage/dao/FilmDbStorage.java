package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;

    @Override
    public void add(Film film) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("films")
                    .usingGeneratedKeyColumns("film_id");
        Integer id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.setId(id);

       film.setMpa(mpaDbStorage.findById(film.getMpa().getId()));

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlQuery = "MERGE INTO film_genres values (?, ?)";
                jdbcTemplate.update(sqlQuery,
                        id,
                        genre.getId());
            }
            film.setGenres(findFilmGenresByFilmId(film.getId()));
        }
    }

    @Override
    public void update(Film film) {
        String sqlQuery = "update films set NAME = ?, RELEASE_DATE = ?, DESCRIPTION = ?, DURATION_IN_MINUTES = ?" +
                ", MPA_ID = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        film.setMpa(mpaDbStorage.findById(film.getMpa().getId()));

        sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                sqlQuery = "MERGE into film_genres values (?, ?)";
                jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            }
            film.setGenres(findFilmGenresByFilmId(film.getId()));
        }
    }

    @Override
    public void delete(Integer id) {
        String sqlQuery = "delete from FILMS where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }


    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public boolean containsId(Integer id) {
        String sqlQuery = "SELECT FILM_ID FROM FILMS where FILM_ID = ?";
        return !jdbcTemplate.queryForList(sqlQuery, Integer.class, id).isEmpty();

    }

    @Override
    public Film get(Integer id) {
        String sqlQuery = "SELECT * FROM films f  WHERE f.film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id=%d не найден.", id)));
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
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
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
                .genres(findFilmGenresByFilmId(resultSet.getInt("film_id")))
                .likes(findUsersIdWhoLikedFilm(resultSet.getInt("film_id")))
                .build();
    }

    private List<Integer> findUsersIdWhoLikedFilm(Integer filmId) {
        String sqlQuery = "SELECT user_id from film_likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);
    }

    private List<Genre> findFilmGenresByFilmId(Integer filmId) {
        String sqlQuery = "SELECT * from GENRES where GENRE_ID in (select GENRE_ID from FILM_GENRES where FILM_ID = ?)";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::mapRowToGenre, filmId);
    }

}
