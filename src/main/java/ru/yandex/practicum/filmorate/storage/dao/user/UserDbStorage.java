package ru.yandex.practicum.filmorate.storage.dao.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Integer id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        user.setId(id);

    }

    @Override
    public void update(User user) {
        String sqlQuery = "update USERS set EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        if (jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()) < 1) {
            throw new UserNotFoundException("Такого пользователя ещё нет, невозможно обновить!");
        }
    }

    @Override
    public void delete(Integer id) {
        String sqlQuery = "delete from USERS where USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT * FROM USERS";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::mapRowToUser);
    }

    @Override
    public User get(Integer id) {
        String sqlQuery = "SELECT * FROM USERS u  WHERE u.user_id = ?";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::mapRowToUser, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id=%d не найден.", id)));
    }

    @Override
    public boolean containsId(Integer id) {
        String sqlQuery = "SELECT USER_ID FROM USERS where USER_ID = ?";
        return !jdbcTemplate.queryForList(sqlQuery, Integer.class, id).isEmpty();
    }

    @Override
    public List<Event> getFeed(Integer id) {
        if(!containsId(id)) {
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден.", id));
        }
        String sqlQuery = "SELECT * FROM  EVENTS AS e WHERE e.user_id = ?";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::mapRowToEvent, id);
    }


    public static User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    private static Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        Event event = Event.builder()
                .eventId(resultSet.getInt("event_id"))
                .userId(resultSet.getInt("user_id"))
                .entityId(resultSet.getInt("entity_id"))
                .build();
        String s = new SimpleDateFormat("MMddyyyyHHmmss").format(resultSet.getTimestamp("timestamp"));
        System.out.println(s);
        event.setTimestamp(Long.parseLong(s));
        event.setEventType((EventType.valueOf(resultSet.getString("event_type"))));
        event.setOperation((Operation.valueOf((resultSet.getString("operation")))));
        return event;
    }

}
