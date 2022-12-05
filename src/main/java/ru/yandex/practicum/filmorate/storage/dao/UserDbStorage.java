package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
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
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
    }

    @Override
    public void delete(Integer id) {
        String sqlQuery = "delete from USERS where USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT * FROM USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User get(Integer id) {
        String sqlQuery = "SELECT * FROM USERS u  WHERE u.user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id)
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
    public void addToFriends(Integer id, Integer friendId) {
        String sqlQuery = "insert into USER_FRIENDS(USER_ID, FRIEND_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void deleteFromFriends(Integer id, Integer friendId) {
        String sqlQuery = "delete from USER_FRIENDS where USER_ID = ? and FRIEND_ID = ?";
         jdbcTemplate.update(sqlQuery, id, friendId);

    }

    @Override
    public List<User> getFriends(Integer id) {
        String sqlQuery = "select * from USERS where USER_ID in (select FRIEND_ID from USER_FRIENDS where USER_ID = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        String sqlQuery = "SELECT * FROM USERS WHERE USER_ID IN (SELECT f1.FRIEND_ID FROM USER_FRIENDS f1 " +
                "JOIN USER_FRIENDS f2 ON f2.USER_ID = ? AND f2.FRIEND_ID = f1.FRIEND_ID WHERE f1.USER_ID = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, otherId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(getFriends(resultSet.getInt("user_id")).stream().map(User::getId)
                        .collect(Collectors.toList()))
                .favoriteMovies(findFavoriteMovies(resultSet.getInt("user_id")))
                .build();
    }

    private List<Integer> findFavoriteMovies(Integer userId) {
        String sqlQuery = "SELECT FILM_ID from film_likes WHERE USER_ID = ?";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);
    }
}
