package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage{
    @Override
    public void add(User user) {

    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public User get(Integer id) {
        return null;
    }

    @Override
    public boolean containsId(Integer id) {
        return false;
    }
}
