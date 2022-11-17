package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmValidator filmValidator;

    public List<Film> findAll() {
        log.info("Текущее количество фильмов: {}", filmStorage.getCount());
        return filmStorage.getFilms();
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
            throw new UserNotFoundException("Идентификатор фильма отсутствует, невозможно обновить фильм. Фильм не найден");
        }
        if (!filmStorage.containsId(film.getId())) {
            throw new UserNotFoundException("Такого фильма ещё нет, невозможно обновить!");
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
        filmStorage.get(id).getLikesOfUsers().add(userId);
        userStorage.get(userId).getFavoriteMovies().add(id);
        log.info("Пользователю: {} понравился фильм: {}", userStorage.get(userId).getEmail(),
                filmStorage.get(id).getName());
    }

    public void deleteLike(Integer id, Integer userId) {
        checkUserAndFilm(userId, id);
        filmStorage.get(id).getLikesOfUsers().remove(userId);
        userStorage.get(userId).getFavoriteMovies().remove(id);
        log.info("Пользователю: {} удалил лайк у  фильмв: {}", userStorage.get(userId).getEmail(),
                filmStorage.get(id).getName());
    }

    public List<Film> findPopular(Integer count) {
        List<Film> films = filmStorage.getFilms();
        return films.stream()
                .sorted(Comparator.comparing(o -> -o.getLikesOfUsers().size()))
                .limit(count)
                .collect(Collectors.toList());
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
