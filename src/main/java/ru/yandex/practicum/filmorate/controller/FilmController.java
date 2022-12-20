package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class FilmController {
    private  final FilmService filmService;

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping(value = "/films")
    public Film put(@Valid @RequestBody Film film) {
        return filmService.put(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void putLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.putLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.deleteLike(id, userId);
    }

    @DeleteMapping(value = "/films/{filmId}")
    public void delete(@PathVariable Integer filmId) {
        filmService.delete(filmId);
    }

    @GetMapping("/films/popular")
    public List<Film> findPopular(@RequestParam(required = false, defaultValue = "10")
                                  @Positive(message = "Передаваемый параметр должен быть больше 0")
                                   Integer count) {
        return filmService.findPopular(count);
    }

    @GetMapping("/films/{filmId}")
    public Film get(@PathVariable Integer filmId) {
        return filmService.get(filmId);
    }
}
