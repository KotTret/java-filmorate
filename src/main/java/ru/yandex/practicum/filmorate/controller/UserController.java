package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.ValidatorException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> findAll() {
        log.info("Текущее количество пользователей: {}", users.size());

        return new ArrayList<User>(users.values());
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) throws ValidatorException {
        validate(user);
        user.setId(User.idCounter++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user.getEmail());
        return user;

    }

    @PutMapping(value = "/users")
    public User put(@RequestBody User user) throws ValidatorException {
        validate(user);
        users.put(user.getId(), user);
        log.info("Информация о пользователе обнолвена: {}", user.getEmail());
        return user;
    }


    public void validate(User user) throws ValidatorException {

        if (user.getId() != null && !users.containsKey(user.getId())) {
            throw new ValidatorException("Пользователь не найден");
        }

        if(user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidatorException("Адрес электронной почты не может быть пустым.");
        }
        if (!EmailValidator.getInstance().isValid(user.getEmail())) {
            throw new ValidatorException("Неверно указан адрес электронной почты.");
        }
/*        if(users.containsKey(user.getEmail())) {
            throw new ValidatorException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }*/
        if(user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            throw new ValidatorException("Логин не может быть пустым и содержать пробелы.");
        }

        if (user.getBirthday().isAfter(LocalDate.now()) ||
                user.getBirthday().isBefore(LocalDate.now().minusYears(100))) {
            throw new ValidatorException("Дата рождения указана не верно");
        }

        if(user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
