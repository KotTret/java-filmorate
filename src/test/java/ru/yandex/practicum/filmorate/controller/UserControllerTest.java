package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private static UserValidator userValidator;

    @BeforeAll
    static void beforeAll() {
        userValidator = new UserValidator();
    }


    @Test
    void validateWithCorrectData() {
        User user = User.builder()
                .login("Test")
                .name("create")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        assertDoesNotThrow(() -> userValidator.validate(user),
                "Не должно быть исключений");
    }

    @Test
    void validateShouldThrowExceptionWhenEmailIsIncorrect() {
        User user = User.builder()
                .login("Test")
                .name("create")
                .email("@mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        assertThrows(ValidationException.class, () -> userValidator.validate(user),
                "email неверный, должно быть исключение");
        user.setEmail("");
        assertThrows(ValidationException.class, () -> userValidator.validate(user),
                "email неверный, должно быть исключение");
        user.setEmail(null);
        assertThrows(ValidationException.class, () -> userValidator.validate(user),
                "email неверный, должно быть исключение");
    }

    @Test
    void validateShouldThrowExceptionWhenNameIsEmpty()  {
        User user = User.builder()
                .login("Test")
                .name("")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2021, 1, 1))
                .build();
        userValidator.validate(user);
        assertEquals(user.getLogin(), user.getName());
        user.setName(null);
        userValidator.validate(user);
        assertEquals(user.getLogin(), user.getName());
    }
}