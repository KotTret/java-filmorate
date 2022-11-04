package ru.yandex.practicum.filmorate.controller;

import org.apache.commons.validator.ValidatorException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private static  FilmController filmController;

    @BeforeAll
    static void beforeAll() {
        filmController = new FilmController();
    }

    @Test
    void validateWithCorrectData() {
        Film film = Film.builder()
                .name("Test")
                .description("TestTestTest")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(130)
                .build();
        assertDoesNotThrow(() -> filmController.validate(film),
                "Не должно быть исключений");
    }

    @Test
    void validateShouldThrowExceptionWhenNameIsIncorrect() {
        Film film = Film.builder()
                .name("")
                .description("TestTestTest")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(130)
                .build();
        assertThrows(ValidatorException.class, () -> filmController.validate(film),
                "Name пустой, должно быть исключение");
        film.setName(null);
        assertThrows(ValidatorException.class, () -> filmController.validate(film),
                "Name отсутствует, должно быть исключение");
    }

    @Test
    void validateShouldThrowExceptionWhenReleaseDateIsIncorrect() {
        Film film = Film.builder()
                .name("Test")
                .description("TestTestTest")
                .releaseDate(LocalDate.of(1666, 1, 1))
                .duration(130)
                .build();
        assertThrows(ValidatorException.class, () -> filmController.validate(film),
                "дата релиза — не раньше 28 декабря 1895 года, должно быть исключение");
    }

    @Test
    void validateShouldThrowExceptionWhenDurationIsIncorrect() {
        Film film = Film.builder()
                .name("Test")
                .description("TestTestTest")
                .releaseDate(LocalDate.of(2013, 1, 1))
                .duration(0)
                .build();
        assertThrows(ValidatorException.class, () -> filmController.validate(film),
                "продолжительность фильма должна быть положительной, должно быть исключение");
        film.setDuration(-20);
        assertThrows(ValidatorException.class, () -> filmController.validate(film),
                "продолжительность фильма должна быть положительной, должно быть исключение");
    }
}