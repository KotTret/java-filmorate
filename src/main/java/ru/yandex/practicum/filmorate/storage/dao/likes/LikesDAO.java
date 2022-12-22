package ru.yandex.practicum.filmorate.storage.dao.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.sql.Timestamp;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class LikesDAO  implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void putLike(Integer id, Integer userId) {
        String sqlQuery = "insert into film_likes(FILM_ID, USER_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
        sqlQuery = "update FILMS f set rate = RATE + 1 where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        sqlQuery = "insert into EVENTS (TIMESTAMP, USER_ID,EVENT_TYPE, OPERATION, ENTITY_ID) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, Timestamp.from(Instant.now()), userId, EventType.LIKE.name(), Operation.ADD.name(), id);
    }
    @Override
    public void deleteLike(Integer id, Integer userId) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? and USER_ID = ?";
        if (jdbcTemplate.update(sqlQuery, id, userId) < 1) {
            throw new ObjectNotFoundException(String.format("Пользователь с id=%d не ставил лайк фильму с id=%d.", userId, id));
        }
        sqlQuery = "update FILMS f set rate = RATE - 1 where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        sqlQuery = "insert into EVENTS (TIMESTAMP, USER_ID,EVENT_TYPE, OPERATION, ENTITY_ID) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, Timestamp.from(Instant.now()), userId, EventType.LIKE.name(), Operation.REMOVE.name(), id);
    }
}
