package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.IdGenerator;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final IdGenerator idGenerator;

    @Override
    public void add(User user) {
        user.setId(idGenerator.getId());
        users.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void delete(Integer id) {
    }

    @Override
    public void addToFriends(Integer id, Integer friendId) {

    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteFromFriends(Integer id, Integer friendId) {

    }

    @Override
    public List<User> getFriends(Integer id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        return null;
    }

    @Override
    public User get(Integer id) {
        return users.get(id);
    }

    @Override
    public boolean containsId(Integer id) {
        return users.containsKey(id);
    }
}
