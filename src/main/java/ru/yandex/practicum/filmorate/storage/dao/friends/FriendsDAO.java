package ru.yandex.practicum.filmorate.storage.dao.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDbStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendsDAO implements FriendsStorage {

    private final JdbcTemplate jdbcTemplate;

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
        return jdbcTemplate.query(sqlQuery, UserDbStorage::mapRowToUser, id);
    }
    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        String sqlQuery = "SELECT * FROM USERS WHERE USER_ID IN (SELECT f1.FRIEND_ID FROM USER_FRIENDS f1 " +
                "JOIN USER_FRIENDS f2 ON f2.USER_ID = ? AND f2.FRIEND_ID = f1.FRIEND_ID WHERE f1.USER_ID = ?)";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::mapRowToUser, id, otherId);
    }
}
