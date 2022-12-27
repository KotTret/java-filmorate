package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationsController {
    private final RecommendationService recommendationService;

    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendedFilms(@PathVariable Integer id) {
        return recommendationService.getRecommendedFilms(id);
    }
}
