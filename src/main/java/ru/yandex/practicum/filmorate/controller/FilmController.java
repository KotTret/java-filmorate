package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Reviews;
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

    @GetMapping("/films/common")
    public List<Film> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping(value = "/films/director/{directorId}")
    public List<Film> getFilmsByDirector(@Valid @PathVariable Integer directorId, @RequestParam String sortBy){
        return filmService.getFilmsByDirector(directorId,sortBy);
    }

    @GetMapping("/reviews")
    public List<Reviews> findAllReviews() {
        return filmService.findAllReviews();
    }

    @GetMapping("/reviews?")
    public List<Reviews> getReviewsById(@RequestParam Integer filmId, @RequestParam(defaultValue = "10") Integer count) {
        return filmService.getReviewByFilmId(filmId, count);
    }

    @GetMapping("/reviews/{reviewId}")
    public Reviews getReviewsByUrlId(@PathVariable Integer reviewId) {
        return filmService.getReviewById(reviewId);
    }

    @PostMapping(value = "/reviews")
    public Reviews addReviews(@Valid @RequestBody Reviews reviews) {
        return filmService.addReviews(reviews);
    }

    @PutMapping(value = "/reviews")
    public Reviews updateReviews(@Valid @RequestBody Reviews reviews) {
        return filmService.updateReviews(reviews);
    }

    @PutMapping(value = "/reviews/{reviewId}/{isPositive}/{userId}")
    public Reviews updateReviews(@PathVariable Integer reviewId, @PathVariable String isPositive,
                              @PathVariable Integer userId) {
        return filmService.updateReviewsIsPositive(reviewId, isPositive, userId);
    }

    @DeleteMapping(value = "/reviews/{reviewId}")
    public void deleteReviews(@PathVariable Integer reviewId) {
        filmService.deleteReviews(reviewId);
    }
}
