package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Reviews;

import java.util.List;

public interface ReviewsStorage {

    Reviews getReviewsById(Integer id);

    List<Reviews> getReviewByFilmId(Integer filmId, Integer count);

    List<Reviews> findAllReviews(Integer count);

    Reviews addReviews(Reviews reviews);

    Reviews updateReviews(Reviews reviews);

    Reviews updateReviewsIsPositive(Integer reviewId, Boolean isPositive, Integer userId);

    void deleteReviews(Integer reviewId);

    boolean checkReview(Integer reviewId);

    boolean checkReviewUserFilm(Integer userId, Integer filmId);

    boolean checkReviewOnFilm(Integer filmId);

    boolean checkLikeOrDislike(Integer reviewId, boolean check);
}
