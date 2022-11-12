package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UpdateDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements  UserStorage{

    private final Map<Integer, User> users = new HashMap<>();
    private final IdGenerator idGenerator;

    @Override
    public void addUser(User user) {
        user.setId(idGenerator.getId());
        users.put(user.getId(), user);
    }

    @Override
    public void upDateUser(User user) {
        if (user.getId() == null) {
            throw  new UpdateDataException("Идентификатор пользователя отсутствует, невозможно обновить данные. " +
                    "Пользователь не найден");
        }
        if (!users.containsKey(user.getId())) {
            throw  new UpdateDataException("Такого пользователя ещё нет, невозможно обновить!");
        }

        users.put(user.getId(), user);
    }

    @Override
    public void delete(Integer id) {
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int getNumberUsers() {
        return users.size();
    }

    @Override
    public User getUser(Integer id) {
        return users.get(id);
    }

    @Override
    public boolean containsId(Integer id) {
        return users.containsKey(id);
    }
}
