package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {

    void addToFriends(Integer id, Integer friendId);

    void deleteFromFriends(Integer id, Integer friendId);

    List<User> getFriends(Integer id);

    List<User> getCommonFriends(Integer id, Integer otherId);
}
