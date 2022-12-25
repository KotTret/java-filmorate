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
@RequestMapping("/films")
public class FilmController {
    private  final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        return filmService.put(film);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void putLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.putLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.deleteLike(id, userId);
    }

    @DeleteMapping(value = "/{filmId}")
    public void delete(@PathVariable Integer filmId) {
        filmService.delete(filmId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@Positive @RequestParam(defaultValue = "10") Integer count,
                                      @RequestParam(required = false) Integer genreId,
                                      @RequestParam(required = false) Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/{filmId}")
    public Film get(@PathVariable Integer filmId) {
        return filmService.get(filmId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping(value = "/director/{directorId}")
    public List<Film> getFilmsByDirector(@Valid @PathVariable Integer directorId, @RequestParam String sortBy){
        return filmService.getFilmsByDirector(directorId,sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam("query") String query, @RequestParam("by") String [] searchBy) {
        return filmService.searchFilms(query,searchBy);
    }

}
