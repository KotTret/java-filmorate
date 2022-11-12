package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.IdGenerator;


import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final static LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    private final IdGenerator idGenerator;

    @GetMapping("/films")
    public List<Film> findAll() {
        log.info("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(idGenerator.getId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film.getName());
        return film;
    }

    @PutMapping(value = "/films")
    public Film put(@Valid @RequestBody Film film) {
        validate(film);
        films.put(film.getId(), film);
        log.info("Информация о фильме обнолвена: {}", film.getName());
        return film;
    }

    public void validate(Film film) {
        if (film.getId() != null && !films.containsKey(film.getId())) {
            throw new ValidationException("Фильм не найден");
        }

        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            throw new ValidationException("Дата релиза не указана или не может быть раньше 28 декабря 1895 года");
        }

    }
}
