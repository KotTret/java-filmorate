package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.IdGenerator;
import ru.yandex.practicum.filmorate.model.User;
import org.apache.commons.validator.routines.EmailValidator;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private final IdGenerator idGenerator;
    private final static EmailValidator  EMAIL_VALIDATOR =  EmailValidator.getInstance();
    public UserController() {
        this.idGenerator = new IdGenerator();
    }

    @GetMapping("/users")
    public List<User> findAll() {
        log.info("Текущее количество пользователей: {}", users.size());

        return new ArrayList<User>(users.values());
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user)  {
        validate(user);
        user.setId(idGenerator.getId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user.getEmail());
        return user;

    }

    @PutMapping(value = "/users")
    public User put(@Valid @RequestBody User user) {
        validate(user);
        users.put(user.getId(), user);
        log.info("Информация о пользователе обнолвена: {}", user.getEmail());
        return user;
    }


    public void validate(User user)  {

        if (user.getId() != null && !users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь не найден");
        }

        if (!EMAIL_VALIDATOR.isValid(user.getEmail())) {
            throw new ValidationException("Неверно указан адрес электронной почты.");
        }

        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
