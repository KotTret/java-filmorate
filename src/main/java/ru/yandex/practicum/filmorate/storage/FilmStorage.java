package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    void add(Film film);

    void update(Film film);

    void delete(Integer id);

    List<Film> getFilms();


    boolean containsId(Integer id);

    List<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    Film get(Integer id);
    List<Film> getCommonFilms(Integer userId, Integer friendId);
    List<Film> searchFilmsByTitle(String query);
    List<Film> searchFilmsByDirector(String query);
}
