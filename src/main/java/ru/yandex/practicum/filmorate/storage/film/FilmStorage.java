package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    void addFilm(Film film);

    void upDateFilm(Film film);

    void delete(Integer id);

    int getNumberFilms();

    List<Film> getFilms();

    boolean containsId(Integer id);

    Film getFilm(Integer id);
}
