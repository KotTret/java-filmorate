package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    static Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }

    public Genre findById(Integer id) {
        String sqlQuery = "select * from GENRES where genre_id = ?";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::mapRowToGenre, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Жанр с id=%d не найден.", id)));
    }

    public List<Genre> findAll() {
        String sqlQuery = "select * from genres order by GENRE_ID";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::mapRowToGenre);
    }

    public void findGenresForFilm(Film film) {
        String sqlQuery = "SELECT * from GENRES where GENRE_ID in (select GENRE_ID from FILM_GENRES where FILM_ID = ?)";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDbStorage::mapRowToGenre, film.getId());
        film.setGenres(genres);
    }
    public void findGenresForFilm(List<Film> films) {
        Map<Integer, Film> resFilms = films.stream().collect(Collectors.toMap(Film::getId, film -> film));
        String sqlQuery = "SELECT fg.film_id, fg.genre_id, g.genre_name from FILM_GENRES as fg " +
                "LEFT JOIN genres AS g ON  fg.genre_id = g.genre_id";
        jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> (resFilms.get(rs.getInt("film_id")).getGenres().add(mapRowToGenre(rs, rowNum))));
    }
}