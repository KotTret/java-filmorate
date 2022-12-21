package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Reviews;

import java.util.List;

public interface ReviewsStorage {

    List<Reviews> findAllReviews();

    Reviews getReviewsById(Integer id);

    List<Reviews> getReviewByFilmId(Integer id, Integer count);

    Reviews addReviews(Reviews reviews);

    Reviews updateReviews(Reviews reviews);

    Reviews updateReviewsIsPositive(Integer reviewId, Boolean isPositive, Integer userId);

    void deleteReviews(Integer reviewId);

    boolean checkReview(Integer reviewId);

}
