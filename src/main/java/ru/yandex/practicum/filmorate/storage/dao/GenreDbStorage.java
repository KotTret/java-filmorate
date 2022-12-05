package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
}