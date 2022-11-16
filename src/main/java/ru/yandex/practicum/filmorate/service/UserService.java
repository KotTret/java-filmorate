package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final UserValidator userValidator;

    public List<User> findAll() {
        log.info("Текущее количество пользователей: {}", userStorage.getCount());
        return userStorage.getUsers();
    }

    public User get(Integer userId) {
        checkUser(userId);
        log.info("Запрошена информация о пользователе: {}", userStorage.get(userId).getEmail());
        return userStorage.get(userId);

    }

    public User create(User user)  {
        userValidator.validate(user);
        userStorage.add(user);
        log.info("Добавлен пользователь: {}", user.getEmail());
        return user;
    }

    public User update(User user) {
        userValidator.validate(user);
        if (user.getId() == null) {
            throw  new UserNotFoundException("Идентификатор пользователя отсутствует, невозможно обновить данные. " +
                    "Пользователь не найден");
        }
        if (!userStorage.containsId(user.getId())) {
            throw  new UserNotFoundException("Такого пользователя ещё нет, невозможно обновить!");
        }
        userStorage.update(user);
        log.info("Информация о пользователе обнолвена: {}", user.getEmail());
        return user;
    }

    public void addToFriends(Integer id, Integer friendId) {
        checkUser(id, friendId);
        User user = userStorage.get(id);
        user.getFriends().add(friendId);
        userStorage.get(friendId).getFriends().add(user.getId());
        log.info("Пользователи: {} и {} теперь друзья", user.getEmail(), userStorage.get(friendId).getEmail());
    }

    public void deleteFromFriends(Integer id, Integer friendId) {
        checkUser(id, friendId);
        User user = userStorage.get(id);
        user.getFriends().remove(friendId);
        userStorage.get(friendId).getFriends().remove(user.getId());
        log.info("Пользователи: {} и {} теперь не друзья", user.getEmail(), userStorage.get(friendId).getEmail());
    }

    public Set<User> getFriends(Integer id) {
        checkUser(id);
        return userStorage.get(id).getFriends().stream().map(userStorage::get).collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        checkUser(id, otherId);
        Set<Integer> friends = userStorage.get(id).getFriends();
        Set<Integer> otherFriends = userStorage.get(otherId).getFriends();
        return friends.stream().filter(otherFriends::contains).map(userStorage::get).collect(Collectors.toSet());
    }
    private void checkUser(Integer id) {
        if (!userStorage.containsId(id)) {
            throw new UserNotFoundException("Пользователь не найден, проверьте верно ли указан Id");
        }
    }

    private void checkUser(Integer id1, Integer id2) {
        if (!userStorage.containsId(id1) || !userStorage.containsId(id2)) {
            throw new UserNotFoundException("Пользователь не найден, проверьте верно ли указан Id");
        }
    }
}
