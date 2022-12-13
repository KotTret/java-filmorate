package ru.yandex.practicum.filmorate.storage.dao.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Component
@RequiredArgsConstructor
public class MpaDAO implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public Mpa findById(Integer id) {
        String sqlQuery = "select * from MPA where mpa_id = ?";
        return jdbcTemplate.query(sqlQuery, MpaDAO::mapRowToMpa, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(String.format("MPA с id=%d не найден.", id)));
    }

    @Override
    public List<Mpa> findAll() {
        String sqlQuery = "select * from MPA ORDER BY mpa_id";
        return jdbcTemplate.query(sqlQuery, MpaDAO::mapRowToMpa);
    }

    static Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();
    }

}
