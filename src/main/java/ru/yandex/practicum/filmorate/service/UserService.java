package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final UserValidator userValidator;
    private final FriendsStorage friendsStorage;
    private final FeedStorage feedStorage;

    public List<User> findAll() {
        List<User> users = userStorage.findAll();
        log.info("Текущее количество пользователей: {}", users.size());
        return users;
    }

    public User get(Integer userId) {
        User user = userStorage.get(userId);
        log.info("Запрошена информация о пользователе: {}", user.getEmail());
        return user;
    }

    public User create(User user)  {
        userValidator.validate(user);
        userStorage.add(user);
        log.info("Добавлен пользователь: {}", user.getEmail());
        return user;
    }

    public User update(User user) {
        userValidator.validate(user);

        userStorage.update(user);
        log.info("Информация о пользователе обнолвена: {}", user.getEmail());
        return user;
    }

    public void addToFriends(Integer id, Integer friendId) {
        if (Objects.equals(id, friendId)) {
            throw new ValidationException("Вы не можете добавить сами себя");
        }
        checkUser(id, friendId);
        friendsStorage.addToFriends(id, friendId);
        feedStorage.add(friendId, id, EventType.FRIEND, Operation.ADD);
        log.info("Пользователи: c id:{} добавил в друзья id:{}", id, friendId);
    }

    public void deleteFromFriends(Integer id, Integer friendId) {
        checkUser(id, friendId);

        friendsStorage.deleteFromFriends(id, friendId);
        feedStorage.add(friendId, id, EventType.FRIEND, Operation.REMOVE);
        log.info("Пользователь: с id:{} удалил из друзей пользователя id:{}", id, friendId);
    }

    public List<User> getFriends(Integer id) {
        checkUser(id);
        return friendsStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        checkUser(id, otherId);
        return friendsStorage.getCommonFriends(id, otherId);
    }

    public void delete(Integer id) {
        checkUser(id);
        userStorage.delete(id);
    }

    public List<Event> getFeed(Integer id) {
        if(!userStorage.containsId(id)) {
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден.", id));
        }
        return feedStorage.get(id);
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
