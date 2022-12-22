package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Reviews;

import java.util.List;

public interface ReviewsStorage {

    Reviews getReviewsById(Integer id);

    List<Reviews> getReviewByFilmId(Integer filmId, Integer count);

    List<Reviews> findAllReviews();

    Reviews addReviews(Reviews reviews);

    Reviews updateReviews(Reviews reviews);

    void updateReviewsIsPositive(Integer reviewId, Boolean isPositive, Integer userId);

    void deleteReviews(Integer reviewId);

    boolean checkReview(Reviews reviews);

    boolean checkReview(Integer reviewId);

    boolean checkLikeOrDislike(Integer reviewId, Integer userId, boolean check);

    void updateUseful(Reviews review);

    void deleteLike(Integer reviewId, Integer userId);

    void deleteDislike(Integer reviewId, Integer userId);
}
