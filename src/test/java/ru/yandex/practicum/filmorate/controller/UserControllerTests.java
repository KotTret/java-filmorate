package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDbStorage;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTests {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final FriendsStorage friendsDAO;

    private User getUser() {
        return User.builder()
                .email("kot@mail.ru")
                .login("login")
                .name("name")
                .birthday(Date.valueOf("1955-01-01").toLocalDate())
                .build();
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM USER_FRIENDS");
        jdbcTemplate.update("DELETE FROM USERS");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1");

    }

    @Test
    void testSaveAndGetUserAndDelete() {
        User user = getUser();
        userDbStorage.add(user);
        user.setId(1);
        assertEquals(user, userDbStorage.get(1));
        userDbStorage.delete(1);
        assertThrows(UserNotFoundException.class, () -> userDbStorage.get(1));
    }


    @Test
    void testUpdateUser() {
        User user = getUser();
        userDbStorage.add(user);
        user.setLogin("testUpdateLogin");
        userDbStorage.update(user);
        assertEquals(user, userDbStorage.get(1));
    }

    @Test
    void testContainsIdUser() {
        User user = getUser();
        userDbStorage.add(user);
        assertFalse(userDbStorage.containsId(999));
    }

    @Test
    void testFindAllUsers() {
        User user1 = getUser();
        User user2 = getUser();
        user2.setEmail("tret@jd.ru");
        user2.setLogin("KOT");
        userDbStorage.add(user1);
        userDbStorage.add(user2);
        List<User> users = List.of(user1, user2);
        assertEquals(users, userDbStorage.findAll());
    }

    @Test
    void testSaveAndGetFriendsAndDelete() {
        User user1 = getUser();
        User user2 = getUser();
        user2.setEmail("tret@jd.ru");
        user2.setLogin("KOT");
        userDbStorage.add(user1);
        userDbStorage.add(user2);
        friendsDAO.addToFriends(1, 2);
        assertEquals(List.of(user2), friendsDAO.getFriends(1));
        friendsDAO.deleteFromFriends(1, 2);
        assertEquals(List.of(), friendsDAO.getFriends(1));

    }

    @Test
    void testFindOneCommonFriend() {
        User user1 = getUser();
        User user2 = getUser();
        user2.setEmail("tret@jd.ru");
        user2.setLogin("KOT");
        User user3 = getUser();
        user3.setEmail("tret1@jd.ru");
        user3.setLogin("KOT1");
        userDbStorage.add(user1);
        userDbStorage.add(user2);
        userDbStorage.add(user3);
        friendsDAO.addToFriends(1, 3);
        friendsDAO.addToFriends(2, 3);
        assertEquals(List.of(user3), friendsDAO.getCommonFriends(1, 2));
    }
}
