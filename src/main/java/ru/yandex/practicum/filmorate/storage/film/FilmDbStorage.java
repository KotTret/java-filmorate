package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;
    @Override
    public void add(Film film) {
/*        String sqlQuery = "insert into films (name, description, release_date, duration_in_minutes, mpa_rating_id) " +
                "values (?, ?, ?, ?, ?)";*/
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("films")
                    .usingGeneratedKeyColumns("film_id");
            return simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
    }

    @Override
    public void update(Film film) {

    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public List<Film> getFilms() {
        return null;
    }

    @Override
    public boolean containsId(Integer id) {
        return false;
    }

    @Override
    public Film get(Integer id) {
        String sqlQuery = "SELECT * FROM films f  WHERE f.film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id=%d не найден.", id)));
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration_in_minutes"))
                .mpa(resultSet.getString("mpa"))
                .numberOfLikes(resultSet.getLong("number_of_likes"))
                .genres(findFilmGenresByFilmId(resultSet.getLong("id")))
                .likes(findUsersIdWhoLikedFilm(resultSet.getLong("id")))
                .build();
    }

    private List<Long> findUsersIdWhoLikedFilm(Integer filmId) {
        String sqlQuery = "SELECT user_id from film_likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, filmId);
    }

    private List<Genre> findFilmGenresByFilmId(Integer filmId) {
        String sqlQuery = "SELECT * from GENRES where ID in (select GENRE_ID from FILM_GENRE where FILM_ID = ?)";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::mapRowToGenre, filmId);
    }

}
