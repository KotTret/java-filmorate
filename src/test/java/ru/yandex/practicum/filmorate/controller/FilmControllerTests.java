package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDbStorage;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTests {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    private final GenreStorage genreDAO;
    private final LikesStorage likesDAO;

    private User getUser(String email, String login) {
        return User.builder()
                .email(email)
                .login(login)
                .name("kot")
                .birthday(Date.valueOf("2022-02-02").toLocalDate())
                .build();
    }

    private Film getFilm() {
        return Film.builder()
                .name("testName")
                .releaseDate(Date.valueOf("1979-04-17").toLocalDate())
                .duration(30)
                .description("testDescription").duration(100)
                .mpa(new Mpa(2, "PG"))
                .build();
    }

    @AfterEach
    void afterEach() {
        jdbcTemplate.update("DELETE FROM FILM_LIKES");
        jdbcTemplate.update("DELETE FROM FILM_GENRES");
        jdbcTemplate.update("DELETE FROM FILMS");
        jdbcTemplate.update("DELETE FROM USERS");
        jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1");
    }

    @Test
    void testSaveAndGetFilmAndDelete() {
        Film film1 = getFilm();
        filmDbStorage.add(film1);
        assertEquals(film1, filmDbStorage.get(1));
        filmDbStorage.delete(1);
        assertThrows(FilmNotFoundException.class, () -> filmDbStorage.get(1));
    }


    @Test
    void testUpdateFilm() {
        Film film = getFilm();
        filmDbStorage.add(film);
        film.setName("XXX");
        film.setDuration(3333);
        filmDbStorage.update(film);
        assertEquals(film, filmDbStorage.get(1));
    }

    @Test
    void testContainsIdFilm() {
        assertFalse(filmDbStorage.containsId(9999),
                "Фильм с id " + 9999 + " не найден.");
    }

    @Test
    void testFindAllFilms() {
        Film film1 = getFilm();
        Film film2 = getFilm();
        film2.setName("Ololololo");
        filmDbStorage.add(film1);
        filmDbStorage.add(film2);
        List<Film> films = List.of(film1, film2);
        assertEquals(films, filmDbStorage.getFilms());
    }


    @Test
    void testSaveLikeAndDelete() {
        userDbStorage.add(getUser("kot@yandex.ru", "login"));
        userDbStorage.add(getUser("kot2@yandex.ru", "login2"));
        filmDbStorage.add(getFilm());
        likesDAO.putLike(1, 1);
        likesDAO.putLike(1, 2);
        assertEquals(2, filmDbStorage.get(1).getRate());
        likesDAO.deleteLike(1, 1);
        assertEquals(1, filmDbStorage.get(1).getRate());
    }

    @Test
    void testSaveLike() {
        filmDbStorage.add(getFilm());
        assertThrows(ObjectNotFoundException.class, () -> likesDAO.deleteLike(1, 2),
                "Пользователь с id=-2 не ставил лайк фильму с id=1");
    }

    @Test
    void testFindEmptyPopularFilms() {
        assertEquals(new ArrayList<>(), filmDbStorage.findPopular(10));
    }

    @Test
    void testFindOnePopularFilm() {
        userDbStorage.add(getUser("kot@yandex.ru", "login"));
        userDbStorage.add(getUser("kot2@yandex.ru", "login2"));
        filmDbStorage.add(getFilm());
        filmDbStorage.add(getFilm());
        likesDAO.putLike(1, 1);
        likesDAO.putLike(1, 2);
        likesDAO.putLike(2, 2);
        assertEquals(1, filmDbStorage.findPopular(1).get(0).getId());
    }


    @Test
    void testUpdateFilmWithGenre() {
        Film film = getFilm();
        filmDbStorage.add(film);
        film.setGenres(List.of(Genre.builder().id(2).name("Драма").build()));
        filmDbStorage.update(film);
        film.setGenres(null);
        genreDAO.findGenresForFilm(film);
        assertEquals(List.of(Genre.builder().id(2).name("Драма").build()), film.getGenres());
    }

    @Test
    void testFindFilmWithThreeGenres() {
        Film film = getFilm();
        List<Genre> genres = List.of(Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build(), Genre.builder().id(3).name("Мультфильм").build());
        film.setGenres(genres);
        filmDbStorage.add(film);
        film.setGenres(null);
        genreDAO.findGenresForFilm(film);
        assertEquals(genres, film.getGenres());
    }

    @Test
    void testUpdateFilmWithRepeatedGenres() {
        Film film = getFilm();
        filmDbStorage.add(film);
        film.setGenres(List.of(Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build(), Genre.builder().id(1).name("Комедия").build()));
        filmDbStorage.update(film);
        film.setGenres(List.of(Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build()));
        assertEquals(film, filmDbStorage.get(1));
    }
}