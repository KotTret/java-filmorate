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
import java.util.Optional;

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
    public List<Film> getPopularFilms(@Positive @RequestParam(defaultValue = "10") Integer count,
                                      @RequestParam(required = false) Integer genreId,
                                      @RequestParam(required = false) Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
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


    @GetMapping("/reviews/{reviewId}")
    public Reviews getReviewsByUrlId(@PathVariable Integer reviewId) {
        return filmService.getReviewById(reviewId);
    }

    @GetMapping("/reviews")
    public List<Reviews> findAllReviews(@RequestParam(required = false) Optional<Integer> filmId,
                                        @RequestParam(required = false, defaultValue = "10") int count) {
        if (filmId.isPresent()) {
            return filmService.getReviewByFilmId(filmId.get(), count);
        } else {
            return filmService.findAllReviews();
        }
    }

    @PostMapping("/reviews")
    public Reviews addReviews(@Valid @RequestBody Reviews reviews) {
        return filmService.addReviews(reviews);
    }

    @PutMapping("/reviews")
    public Reviews updateReviews(@Valid @RequestBody Reviews reviews) {
        return filmService.updateReviews(reviews);
    }

    @PutMapping(value = "/reviews/{reviewId}/{isPositive}/{userId}")
    public void updateReviews(@PathVariable Integer reviewId, @PathVariable String isPositive,
                                 @PathVariable Integer userId) {
        filmService.updateReviewsIsPositive(reviewId, isPositive, userId);
    }

    @DeleteMapping(value = "/reviews/{reviewId}")
    public void deleteReviews(@PathVariable Integer reviewId) {
        filmService.deleteReviews(reviewId);
    }

    @DeleteMapping("/reviews/{reviewId}/{isPositive}/{userId}")
    public void deleteIsPositive(@PathVariable Integer reviewId, @PathVariable String isPositive,
                           @PathVariable Integer userId) {
        filmService.deleteIsPositive(reviewId, isPositive, userId);
    }

    @GetMapping("/films/search")
    public List<Film> searchFilms(@RequestParam("query") String query, @RequestParam("by") String [] searchBy) {
        return filmService.searchFilms(query,searchBy);
    }
}
