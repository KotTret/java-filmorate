package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    void add(User user);

    void update(User user);

    void delete(Integer id);

    List<User> findAll();

    User get(Integer id);

    void addToFriends(Integer id, Integer friendId);

    void deleteFromFriends(Integer id, Integer friendId);

    List<User> getFriends(Integer id);

    List<User> getCommonFriends(Integer id, Integer otherId);

    boolean containsId(Integer id);
}
