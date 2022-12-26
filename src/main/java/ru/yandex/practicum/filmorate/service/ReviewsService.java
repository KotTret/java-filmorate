package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectExistsException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Reviews;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewsService {
    private final ReviewsStorage reviewsStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    public Reviews getReviewById(Integer reviewId) {
        checkReview(reviewId);
        log.info("Запрошен отзыв: id:{}", reviewId);
        return reviewsStorage.getReviewsById(reviewId);
    }

    public List<Reviews> getReviewByFilmId(Integer filmId, Integer count) {
        checkFilm(filmId);
        log.info("Запрошены отзывы на фильм: id:{}", filmId);
        return reviewsStorage.getReviewByFilmId(filmId, count);
    }

    public List<Reviews> findAllReviews() {
        List<Reviews> reviews = reviewsStorage.findAllReviews();
        log.info("Текущее количество отзывов: {}", reviews.size());
        return reviews;
    }

    public Reviews addReviews(Reviews reviews) {
        checkUserAndFilm(reviews.getUserId(), reviews.getFilmId());
        if (reviewsStorage.checkReview(reviews)) {
            throw new ObjectExistsException("Отзыв уже существует, проверьте верно ли указан Id");
        }
        log.info("Пользователь: c id:{} оставил отзыв на фильм: id:{}", reviews.getUserId(), reviews.getFilmId());
        Reviews review = reviewsStorage.addReviews(reviews);
        feedStorage.newFeed(review.getReviewId(), reviews.getUserId(), EventType.REVIEW, Operation.ADD);
        return review;
    }

    public Reviews updateReviews(Reviews reviews) {
        checkReview(reviews.getReviewId());
        log.info("Пользователь: c id:{} обновил отзыв на фильм: id:{}", reviews.getUserId(), reviews.getFilmId());
        Reviews updatedReview =  reviewsStorage.updateReviews(reviews);
        feedStorage.newFeed(updatedReview.getReviewId(), updatedReview.getUserId(), EventType.REVIEW, Operation.UPDATE);
        return updatedReview;
    }

    public void updateReviewsIsPositive(Integer reviewId, String isPositive, Integer userId) {
        checkUser(userId);
        checkReview(reviewId);
        Reviews reviews = getReviewById(reviewId);
        if (isPositive.equals("like")) {
            checkLikeOrDislike(reviewId, userId, true);
            reviews.setUseful(reviews.getUseful() + 1);
            reviewsStorage.updateUseful(reviews);
            reviewsStorage.updateReviewsIsPositive(reviewId, true, userId);
        } else if (isPositive.equals("dislike")) {
            checkLikeOrDislike(reviewId, userId, false);
            reviews.setUseful(reviews.getUseful() - 1);
            reviewsStorage.updateUseful(reviews);
            reviewsStorage.updateReviewsIsPositive(reviewId, false, userId);
        } else {
            throw new RuntimeException("Введены неизвестные данные");
        }
        log.info("Отзыв: id:{} обновлен", reviewId);
    }

    public void deleteIsPositive(Integer reviewId, String isPositive, Integer userId) {
        checkUser(userId);
        checkReview(reviewId);
        Reviews reviews = getReviewById(reviewId);
        if (isPositive.equals("like")) {
            checkLikeOrDislike(reviewId, userId, true);
            reviews.setUseful(reviews.getUseful() - 1);
            reviewsStorage.updateUseful(reviews);
            reviewsStorage.deleteLike(reviewId, userId);
        } else if (isPositive.equals("dislike")) {
            checkLikeOrDislike(reviewId, userId, false);
            reviews.setUseful(reviews.getUseful() + 1);
            reviewsStorage.updateUseful(reviews);
            reviewsStorage.deleteDislike(reviewId, userId);
        } else {
            throw new RuntimeException("Введены неизвестные данные");
        }
        log.info("Отзыв: id:{} обновлен", reviewId);
    }

    public void deleteReviews(Integer reviewId) {
        checkReview(reviewId);
        feedStorage.newFeed(reviewId, reviewsStorage.getReviewsById(reviewId).getReviewId(), EventType.REVIEW, Operation.REMOVE);
        reviewsStorage.deleteReviews(reviewId);
        log.info("Отзыв: id:{} удален", reviewId);
    }

    private void checkReview(Integer reviewId) {
        if (!reviewsStorage.checkReview(reviewId)) {
            throw new ObjectNotFoundException("Отзыв не найден, проверьте верно ли указан Id");
        }
    }

    private void checkLikeOrDislike(Integer reviewId, Integer userId, boolean check) {
        if (reviewsStorage.checkLikeOrDislike(reviewId, userId, check)) {
            throw new ObjectExistsException("Отзыв уже находится с данной оценкой");
        }
    }

    private void checkFilm(Integer id) {
        if (!filmStorage.containsId(id)) {
            throw new FilmNotFoundException("Фильм не найден, проверьте верно ли указан Id");
        }
    }

    public void checkUserAndFilm(Integer idUser, Integer idFilm) {
        if (!userStorage.containsId(idUser)) {
            throw new UserNotFoundException("Пользователь не найден, проверьте верно ли указан Id");
        }
        checkFilm(idFilm);
    }

    public void checkUser(Integer id) {
        if (!userStorage.containsId(id)) {
            throw new UserNotFoundException("Пользователь не найден, проверьте верно ли указан Id");
        }
    }
}
