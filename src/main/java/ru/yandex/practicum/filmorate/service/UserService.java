package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final UserValidator userValidator;

    public List<User> findAll() {
        log.info("Текущее количество пользователей: {}", userStorage.getNumberUsers());
        return userStorage.getUsers();
    }

    public User create(User user)  {
        userValidator.validate(user);
        userStorage.addUser(user);
        log.info("Добавлен пользователь: {}", user.getEmail());
        return user;
    }

    public User updateUser(User user) {
        userValidator.validate(user);
        userStorage.upDateUser(user);
        log.info("Информация о пользователе обнолвена: {}", user.getEmail());
        return user;
    }

    public void addToFriends(User user, Integer friendId) {
        if (userStorage.containsId(friendId)) {

        }
        user.getFriends().add(friendId);
    }
}
