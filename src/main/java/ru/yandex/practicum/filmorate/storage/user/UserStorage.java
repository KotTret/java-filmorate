package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    void addUser(User user);

    void upDateUser(User user);

    void delete(Integer id);

    List<User> getUsers();

    int getNumberUsers();

    User getUser(Integer id);

    boolean containsId(Integer id);
}
