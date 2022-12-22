package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectExistsException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Reviews;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final GenreStorage genreStorage;

    private final MpaStorage mpaStorage;
    private final LikesStorage likesStorage;

    private final DirectorStorage directorStorage;
    private final ReviewsStorage reviewsStorage;

    public List<Film> findAll() {
        List<Film> films = filmStorage.getFilms();
        genreStorage.findGenresForFilm(films);
        directorStorage.findDirectorsForFilm(films);
        log.info("Текущее количество фильмов: {}", films.size());
        return films;
    }

    public Film create(Film film) {
        filmStorage.add(film);
        setMpaAndGenres(film);
        setDirectors(film);
        log.info("Добавлен фильм: {}", film.getName());
        return film;
    }

    public Film put(Film film) {
        if (film.getId() == null) {
            throw new FilmNotFoundException("Идентификатор фильма отсутствует, невозможно обновить фильм. Фильм не найден");
        }
        filmStorage.update(film);
        setMpaAndGenres(film);
        setDirectors(film);
        log.info("Информация о фильме обнолвена: {}", film.getName());
        return film;
    }

    public Film get(Integer id) {
        log.info("Запрошена информация о фильме: {}", filmStorage.get(id).getName());
        Film film = filmStorage.get(id);
        genreStorage.findGenresForFilm(film);
        directorStorage.findDirectorsForFilm(film);
        return film;
    }

    public void putLike(Integer id, Integer userId) {
        checkUserAndFilm(userId, id);
        likesStorage.putLike(id, userId);
        log.info("Пользователю: c id:{} понравился фильм: id:{}", userId, id);
    }

    public void deleteLike(Integer id, Integer userId) {
        checkUserAndFilm(userId, id);
        likesStorage.deleteLike(id, userId);
        log.info("Пользователю: c id:{} удалил лайк у  фильмв: id:{}", userId, id);
    }

    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        if (year != null && year < 0) {
            throw new ObjectNotFoundException("Год не может быть отрицательным");
        }
        List<Film> films = filmStorage.getPopularFilms(count, genreId, year);
        genreStorage.findGenresForFilm(films);
        directorStorage.findDirectorsForFilm(films);
        return films;
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        checkUser(userId);
        checkUser(friendId);

        List<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);

        return commonFilms;
    }

    public void checkUser(Integer id) {
        if (!userStorage.containsId(id)) {
            throw new UserNotFoundException("Пользователь не найден, проверьте верно ли указан Id");
        }
    }


    public void delete(Integer id) {
        checkFilm(id);
        filmStorage.delete(id);
    }

    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        List<Film> films = directorStorage.getFilmsByDirector(directorId, sortBy);
        genreStorage.findGenresForFilm(films);
        directorStorage.findDirectorsForFilm(films);
        return films;
    }

    private void setMpaAndGenres(Film film) {
        film.setMpa(mpaStorage.findById(film.getMpa().getId()));
        genreStorage.findGenresForFilm(film);
    }

    private void setDirectors(Film film) {
        directorStorage.findDirectorsForFilm(film);
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
        return reviewsStorage.addReviews(reviews);
    }

    public Reviews updateReviews(Reviews reviews) {
        checkReview(reviews.getReviewId());
        log.info("Пользователь: c id:{} обновил отзыв на фильм: id:{}", reviews.getUserId(), reviews.getFilmId());
        return reviewsStorage.updateReviews(reviews);
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


    public List<Film> searchFilms(String query, String[] searchBy) {
        List<Film> films = new ArrayList<>();
        if (searchBy.length == 2) {
            log.info("Поиск фильмов по режиссеру и названию: {}", query);
            films.addAll(filmStorage.searchFilmsByDirector(query));
            films.addAll(filmStorage.searchFilmsByTitle(query));
        } else if (searchBy[0].equals("director")) {
            log.info("Поиск фильмов по режиссеру: {}", query);
            films.addAll(filmStorage.searchFilmsByDirector(query));
        } else if (searchBy[0].equals("title")) {
            log.info("Поиск фильмов по названию: {}", query);
            films.addAll(filmStorage.searchFilmsByTitle(query));
        }
        genreStorage.findGenresForFilm(films);
        directorStorage.findDirectorsForFilm(films);
        return films;
    }

}
