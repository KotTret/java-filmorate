package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmValidator filmValidator;

    public List<Film> findAll() {
        List<Film> films = filmStorage.getFilms();
        log.info("Текущее количество фильмов: {}",films.size());
        return films;
    }

    public Film create(Film film) {
        filmValidator.validate(film);
        filmStorage.add(film);
        log.info("Добавлен фильм: {}", film.getName());
        return film;
    }

    public Film put(Film film) {
        filmValidator.validate(film);
        if (film.getId() == null) {
            throw new FilmNotFoundException("Идентификатор фильма отсутствует, невозможно обновить фильм. Фильм не найден");
        }
        if (!filmStorage.containsId(film.getId())) {
            throw new FilmNotFoundException("Такого фильма ещё нет, невозможно обновить!");
        }
        filmStorage.update(film);
        log.info("Информация о фильме обнолвена: {}", film.getName());
        return film;
    }

    public Film get(Integer id) {
        checkFilm(id);
        log.info("Запрошена информация о фильме: {}", filmStorage.get(id).getName());
        return filmStorage.get(id);
    }

    public void putLike(Integer id, Integer userId) {
        checkUserAndFilm(userId, id);
        filmStorage.putLike(id, userId);
        log.info("Пользователю: c id:{} понравился фильм: id:{}", userId, id);
    }

    public void deleteLike(Integer id, Integer userId) {
        checkUserAndFilm(userId, id);
        filmStorage.deleteLike(id, userId);
        log.info("Пользователю: c id:{} удалил лайк у  фильмв: id:{}", userId, id);
    }

    public List<Film> findPopular(Integer count) {
     return filmStorage.findPopular(count);
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
}
