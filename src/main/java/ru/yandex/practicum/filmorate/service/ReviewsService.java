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

    public Reviews getById(Integer reviewId) {
        checkReview(reviewId);
        log.info("Запрошен отзыв: id:{}", reviewId);
        return reviewsStorage.getById(reviewId);
    }

    public List<Reviews> getByFilmId(Integer filmId, Integer count) {
        checkFilm(filmId);
        log.info("Запрошены отзывы на фильм: id:{}", filmId);
        return reviewsStorage.getByFilmId(filmId, count);
    }

    public List<Reviews> findAll() {
        List<Reviews> reviews = reviewsStorage.findAll();
        log.info("Текущее количество отзывов: {}", reviews.size());
        return reviews;
    }

    public Reviews add(Reviews reviews) {
        checkUserAndFilm(reviews.getUserId(), reviews.getFilmId());
        if (reviewsStorage.checkReview(reviews)) {
            throw new ObjectExistsException("Отзыв уже существует, проверьте верно ли указан Id");
        }
        log.info("Пользователь: c id:{} оставил отзыв на фильм: id:{}", reviews.getUserId(), reviews.getFilmId());
        Reviews review = reviewsStorage.add(reviews);
        feedStorage.add(review.getReviewId(), reviews.getUserId(), EventType.REVIEW, Operation.ADD);
        return review;
    }

    public Reviews update(Reviews reviews) {
        checkReview(reviews.getReviewId());
        log.info("Пользователь: c id:{} обновил отзыв на фильм: id:{}", reviews.getUserId(), reviews.getFilmId());
        Reviews updatedReview =  reviewsStorage.update(reviews);
        feedStorage.add(updatedReview.getReviewId(), updatedReview.getUserId(), EventType.REVIEW, Operation.UPDATE);
        return updatedReview;
    }

    public void updateIsPositive(Integer reviewId, String isPositive, Integer userId, Integer like) {
        checkUser(userId);
        checkReview(reviewId);
        if (isPositive.equals("like") || isPositive.equals("dislike")) {
            checkLikeOrDislike(reviewId, userId, like);
            reviewsStorage.updateIsPositive(reviewId, like, userId);
        } else {
            throw new RuntimeException("Введены неизвестные данные");
        }
        log.info("Отзыв: id:{} обновлен", reviewId);
    }

    public void deleteIsPositive(Integer reviewId, String isPositive, Integer userId, Integer like) {
        checkUser(userId);
        checkReview(reviewId);
        if (isPositive.equals("like") || isPositive.equals("dislike")) {
            checkLikeOrDislike(reviewId, userId, like);
            reviewsStorage.deleteLikeOrDislike(reviewId, userId);
        } else {
            throw new RuntimeException("Введены неизвестные данные");
        }
        log.info("Отзыв: id:{} обновлен", reviewId);
    }

    public void delete(Integer reviewId) {
        checkReview(reviewId);
        feedStorage.add(reviewId, reviewsStorage.getById(reviewId).getReviewId(), EventType.REVIEW, Operation.REMOVE);
        reviewsStorage.delete(reviewId);
        log.info("Отзыв: id:{} удален", reviewId);
    }

    private void checkReview(Integer reviewId) {
        if (!reviewsStorage.checkReview(reviewId)) {
            throw new ObjectNotFoundException("Отзыв не найден, проверьте верно ли указан Id");
        }
    }

    private void checkLikeOrDislike(Integer reviewId, Integer userId, Integer check) {
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
