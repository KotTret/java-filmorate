package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    void add(Film film);

    void update(Film film);

    void delete(Integer id);

    int getCount();

    List<Film> getFilms();

    boolean containsId(Integer id);

    Film get(Integer id);
}
