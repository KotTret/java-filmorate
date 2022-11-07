package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.IdGenerator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private static  FilmController filmController;

    @BeforeAll
    static void beforeAll() {
        filmController = new FilmController(new IdGenerator());
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
    void validateShouldThrowExceptionWhenReleaseDateIsIncorrect() {
        Film film = Film.builder()
                .name("Test")
                .description("TestTestTest")
                .releaseDate(LocalDate.of(1666, 1, 1))
                .duration(130)
                .build();
        assertThrows(ValidationException.class, () -> filmController.validate(film),
                "дата релиза — не раньше 28 декабря 1895 года, должно быть исключение");
    }
}