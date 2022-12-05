package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    void add(Film film);

    void update(Film film);

    void delete(Integer id);

    List<Film> getFilms();

    void putLike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);

    boolean containsId(Integer id);

    List<Film> findPopular(Integer count);

    Film get(Integer id);
}
