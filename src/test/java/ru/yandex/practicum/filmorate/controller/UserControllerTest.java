package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.IdGenerator;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private static UserController userController;

    @BeforeAll
    static void beforeAll() {
        userController = new UserController(new IdGenerator());
    }


    @Test
    void validateWithCorrectData() {
        User user = User.builder()
                .login("Test")
                .name("create")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        assertDoesNotThrow(() -> userController.validate(user),
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
        assertThrows(ValidationException.class, () -> userController.validate(user),
                "email неверный, должно быть исключение");
        user.setEmail("");
        assertThrows(ValidationException.class, () -> userController.validate(user),
                "email неверный, должно быть исключение");
        user.setEmail(null);
        assertThrows(ValidationException.class, () -> userController.validate(user),
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
        userController.validate(user);
        assertEquals(user.getLogin(), user.getName());
        user.setName(null);
        userController.validate(user);
        assertEquals(user.getLogin(), user.getName());
    }
}