package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    void add(User user);

    void update(User user);

    void delete(Integer id);

    List<User> findAll();

    User get(Integer id);

    boolean containsId(Integer id);
}
