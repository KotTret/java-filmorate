package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre findById(Integer id);

    List<Genre> findAll();

    void findGenresForFilm(Film film);

    void findGenresForFilm(List<Film> films);


}
