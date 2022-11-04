package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.ValidatorException;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public List<Film> findAll() {
        log.info("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) throws ValidatorException {
        validate(film);
        film.setId(Film.idCounter++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film.getName());
        return film;
    }

    @PutMapping(value = "/films")
    public Film put(@RequestBody Film film) throws ValidatorException {
        validate(film);
        films.put(film.getId(), film);
        log.info("Информация о фильме обнолвена: {}", film.getName());
        return film;
    }

    public void validate(Film film) throws ValidatorException {
        if (film.getId() != null && !films.containsKey(film.getId())) {
            throw new ValidatorException("Фильм не найден");
        }

        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidatorException("Название не может быть пустым.");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidatorException("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidatorException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            throw new ValidatorException("Продолжительность фильма должна быть положительной");
        }
    }
}
