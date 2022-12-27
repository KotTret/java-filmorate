package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    void add(Film film);

    void update(Film film);

    void delete(Integer id);

    List<Film> getFilms();


    boolean containsId(Integer id);

    List<Film> searchByDirectorAndTitle(String query);

    List<Film> getPopular(Integer count, Integer genreId, Integer year);

    Film get(Integer id);
    List<Film> getCommon(Integer userId, Integer friendId);
    List<Film> searchByTitle(String query);
    List<Film> searchByDirector(String query);
}
