package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface RecommendationStorage {
    List<Integer[]> getAllLikes();
    List<Film> getRecommendationsFilms(List<Integer> recommendedFilmsId);
}
