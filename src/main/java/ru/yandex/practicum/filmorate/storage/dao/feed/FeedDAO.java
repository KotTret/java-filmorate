package ru.yandex.practicum.filmorate.storage.dao.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedDAO implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public void add(Integer id, Integer userId, EventType type, Operation operation) {
        String sqlQuery = "insert into EVENTS (TIMESTAMP, USER_ID,EVENT_TYPE, OPERATION, ENTITY_ID) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, Timestamp.from(Instant.now()), userId, type.name(), operation.name(), id);
    }

    @Override
    public List<Event> get(Integer id) {
        String sqlQuery = "SELECT * FROM  EVENTS AS e WHERE e.user_id = ?";
        return jdbcTemplate.query(sqlQuery, FeedDAO::mapRowToEvent, id);
    }

    private static Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        String timestamp = new SimpleDateFormat("MMddyyyyHHmmss")
                .format(resultSet.getTimestamp("timestamp"));
        return Event.builder()
                .eventId(resultSet.getInt("event_id"))
                .userId(resultSet.getInt("user_id"))
                .entityId(resultSet.getInt("entity_id"))
                .eventType((EventType.valueOf(resultSet.getString("event_type"))))
                .operation((Operation.valueOf((resultSet.getString("operation")))))
                .timestamp(Long.parseLong(timestamp))
                .build();
    }
}
