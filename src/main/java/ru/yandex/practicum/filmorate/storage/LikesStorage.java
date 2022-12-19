package ru.yandex.practicum.filmorate.storage;

public interface LikesStorage {

    void putLike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);



}
