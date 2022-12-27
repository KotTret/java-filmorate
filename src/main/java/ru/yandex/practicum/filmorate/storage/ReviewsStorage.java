package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Reviews;

import java.util.List;

public interface ReviewsStorage {

    Reviews getById(Integer id);

    List<Reviews> getByFilmId(Integer filmId, Integer count);

    List<Reviews> findAll();

    Reviews add(Reviews reviews);

    Reviews update(Reviews reviews);

    void updateIsPositive(Integer reviewId, Integer isPositive, Integer userId);

    void delete(Integer reviewId);

    boolean checkReview(Reviews reviews);

    boolean checkReview(Integer reviewId);

    boolean checkLikeOrDislike(Integer reviewId, Integer userId, Integer check);

    void deleteLikeOrDislike(Integer reviewId, Integer userId);
}
