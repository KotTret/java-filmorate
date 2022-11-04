package ru.yandex.practicum.filmorate.controller;

import org.apache.commons.validator.ValidatorException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private static UserController userController;

    @BeforeAll
    static void beforeAll() {
        userController = new UserController();
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
        assertThrows(ValidatorException.class, () -> userController.validate(user),
                "email неверный, должно быть исключение");
        user.setEmail("");
        assertThrows(ValidatorException.class, () -> userController.validate(user),
                "email неверный, должно быть исключение");
        user.setEmail(null);
        assertThrows(ValidatorException.class, () -> userController.validate(user),
                "email неверный, должно быть исключение");
    }

    @Test
    void validateShouldThrowExceptionWhenLoginIsIncorrect() {
        User user = User.builder()
                .login("")
                .name("create")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        assertThrows(ValidatorException.class, () -> userController.validate(user),
                "Логин пустой, должно быть исключение");
        user.setLogin(null);
        assertThrows(ValidatorException.class, () -> userController.validate(user),
                "Логин отсутствует, должно быть исключение");
        user.setLogin("Test test");
        assertThrows(ValidatorException.class, () -> userController.validate(user),
                "Логин содержит пробелы, должно быть исключение");
        user.setLogin("      Test");
        assertThrows(ValidatorException.class, () -> userController.validate(user),
                "Логин содержит пробелы, должно быть исключение");
        user.setLogin("Test       ");
        assertThrows(ValidatorException.class, () -> userController.validate(user),
                "Логин содержит пробелы, должно быть исключение");
    }

    @Test
    void validateShouldThrowExceptionWhenBirthdayIsIncorrect() {
        User user = User.builder()
                .login("Test")
                .name("create")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2023, 1, 1))
                .build();
        assertThrows(ValidatorException.class, () -> userController.validate(user),
                "День рождения не может быть в будущем, должно быть исключение");
        user.setBirthday(LocalDate.of(1666, 1,1));
        assertThrows(ValidatorException.class, () -> userController.validate(user),
                "КОГДА???, должно быть исключение");
    }

    @Test
    void validateShouldThrowExceptionWhenNameIsEmpty() throws ValidatorException {
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