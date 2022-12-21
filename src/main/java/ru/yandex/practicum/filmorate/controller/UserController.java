package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private  final UserService userService;


    @GetMapping("/users")
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/users/{userId}")
    public User get(@PathVariable Integer userId) {
        return userService.get(userId);
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user)  {
        return userService.create(user);
    }

    @PutMapping(value = "/users")
    public User put(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addToFriends(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFromFriends(id, friendId);
    }

    @DeleteMapping(value = "/users/{userId}")
    public void delete(@PathVariable Integer userId) {
        userService.delete(userId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        return userService.getFriends(id);
    }
    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

}
