package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface DirectorStorage {

    void add(Director director);

    void update(Director director);

    void delete(Integer id);

    List<Director> findAll();

    boolean containsId(Integer id);

    Director get(Integer id);

    List<Film> getFilmsByDirector(Integer directorId, String sortBy);

    void findDirectorsForFilm(Film film);
    void findDirectorsForFilm(List<Film> films);
}
