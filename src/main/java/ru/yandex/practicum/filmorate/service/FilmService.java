package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private  final FilmStorage filmStorage;
    private final FilmValidator filmValidator;

    public List<Film> findAll() {
        log.info("Текущее количество фильмов: {}", filmStorage.getNumberFilms());
        return filmStorage.getFilms();
    }

    public Film create(Film film) {
        filmValidator.validate(film);
        filmStorage.addFilm(film);
        log.info("Добавлен фильм: {}", film.getName());
        return film;
    }

    public Film put(Film film) {
        filmValidator.validate(film);
        filmStorage.upDateFilm(film);
        log.info("Информация о фильме обнолвена: {}", film.getName());
        return film;
    }
}
