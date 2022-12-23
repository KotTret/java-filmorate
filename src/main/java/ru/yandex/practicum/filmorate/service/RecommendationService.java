package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private final UserStorage userStorage;
    private final RecommendationStorage recommendationStorage;
    
    public List<Film> getRecommendedFilms(Integer userId) {
        checkUser(userId);
        List<Integer[]> allLikes = recommendationStorage.getAllLikes();
        if (allLikes.isEmpty()) {
            log.info("У пользователя: id:{} нет рекомендуемых фильмов", userId);
            return new ArrayList<>();
        }
        return recommendationList(allLikes, userId);
    }

    private List<Film> recommendationList(List<Integer[]> allLikes, Integer userId) {
        List<Integer> userLikes = likesUser(allLikes, userId);
        if (userLikes.isEmpty()) {
            log.info("Пользователь: id:{} еще не ставил лайки", userId);
            return new ArrayList<>();
        }
        List<Integer[]> allNoUserLikes = notUserLikes(allLikes, userId);
        List<Integer> likesUserEqualsUser = likesUserEqualsUser(userLikes, allNoUserLikes);
        if (likesUserEqualsUser.isEmpty()) {
            log.info("Для пользователя: id:{} нет рекомендуемых фильмов", userId);
            return new ArrayList<>();
        }
        return getFilms(likesUserEqualsUser, userLikes, allLikes);
    }

    private List<Integer> likesUser(List<Integer[]> allLikes, Integer userId) {
        List<Integer> userLikes = new ArrayList<>();
        for (Integer[] like : allLikes) {
            if (like[1].equals(userId)) {
                userLikes.add(like[0]);
            }
        }
        return userLikes;
    }

    private List<Integer[]> notUserLikes(List<Integer[]> allLikes, Integer userId) {
        List<Integer[]> notUserLikes = new ArrayList<>();
        for (Integer[] like : allLikes) {
            if (!like[1].equals(userId)) {
                notUserLikes.add(like);
            }
        }
        return notUserLikes;

    }

    private List<Integer> likesUserEqualsUser(List<Integer> userLikes, List<Integer[]> notUserLikes) {
        Map<Integer, Integer> crossingCounts = new HashMap<>();
        for (Integer[] like : notUserLikes) {
            if (userLikes.contains(like[0])) {
                if (crossingCounts.containsKey(like[1])) {
                    crossingCounts.put(like[1], (crossingCounts.get(like[1]) + 1));
                } else {
                    crossingCounts.put(like[1], 1);
                }
            }
        }
        return crossingCounts.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(toList());
    }

    private List<Film> getFilms (List<Integer> likesUserEqualsUser, List<Integer> userLikes, List<Integer[]> allLikes) {
        List<Integer> recommendedFilmsId = new ArrayList<>();
        for (Integer id : likesUserEqualsUser) {
            List<Integer> likesUser = likesUser(allLikes, id);
            for (Integer filmId : likesUser) {
                if (!userLikes.contains(filmId) && !recommendedFilmsId.contains(filmId)) {
                    recommendedFilmsId.add(filmId);
                }
            }
        }
        if (recommendedFilmsId.isEmpty()) {
            log.info("У пользователя нет рекомендуемых фильмов");
            return new ArrayList<>();
        }
        log.info("Количество рекомендуемых фильмов - {}", recommendedFilmsId.size());
        return recommendationStorage.getRecommendationsFilms(recommendedFilmsId);
    }

    public void checkUser(Integer userId) {
        if (!userStorage.containsId(userId)) {
            throw new UserNotFoundException("Пользователь не найден, проверьте верно ли указан Id");
        }
    }
}
