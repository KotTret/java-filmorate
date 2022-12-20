package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final GenreStorage genreStorage;

    private final MpaStorage mpaStorage;
    private final LikesStorage likesStorage;

    public List<Film> findAll() {
        List<Film> films = filmStorage.getFilms();
        genreStorage.findGenresForFilm(films);
        log.info("Текущее количество фильмов: {}",films.size());
        return films;
    }

    public Film create(Film film) {
        filmStorage.add(film);
        setMpaAndGenres(film);
        log.info("Добавлен фильм: {}", film.getName());
        return film;
    }

    public Film put(Film film) {
        if (film.getId() == null) {
            throw new FilmNotFoundException("Идентификатор фильма отсутствует, невозможно обновить фильм. Фильм не найден");
        }
        filmStorage.update(film);
        setMpaAndGenres(film);
        log.info("Информация о фильме обнолвена: {}", film.getName());
        return film;
    }

    public Film get(Integer id) {
        log.info("Запрошена информация о фильме: {}", filmStorage.get(id).getName());
        Film film = filmStorage.get(id);
        genreStorage.findGenresForFilm(film);
        return film;
    }

    public void putLike(Integer id, Integer userId) {
        checkUserAndFilm(userId, id);
        likesStorage.putLike(id, userId);
        log.info("Пользователю: c id:{} понравился фильм: id:{}", userId, id);
    }

    public void deleteLike(Integer id, Integer userId) {
        checkUserAndFilm(userId, id);
        likesStorage.deleteLike(id, userId);
        log.info("Пользователю: c id:{} удалил лайк у  фильмв: id:{}", userId, id);
    }

    public List<Film> findPopular(Integer count) {
        List<Film> films = filmStorage.findPopular(count);
        genreStorage.findGenresForFilm(films);
        return films;
    }


    private void checkFilm(Integer id) {
        if (!filmStorage.containsId(id)) {
            throw new FilmNotFoundException("Фильм не найден, проверьте верно ли указан Id");
        }
    }

    private void checkUserAndFilm(Integer idUser, Integer idFilm) {
        if (!userStorage.containsId(idUser)) {
            throw new UserNotFoundException("Пользователь не найден, проверьте верно ли указан Id");
        }
        checkFilm(idFilm);
    }

    private void setMpaAndGenres(Film film) {
        film.setMpa(mpaStorage.findById(film.getMpa().getId()));
        genreStorage.findGenresForFilm(film);
    }

    public void delete(Integer id) {
        checkFilm(id);
        filmStorage.delete(id);
    }
}
