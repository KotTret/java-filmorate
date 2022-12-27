package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private  final UserService userService;


    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable Integer userId) {
        return userService.get(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user)  {
        return userService.create(user);
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addToFriends(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFromFriends(id, friendId);
    }

    @DeleteMapping(value = "/{userId}")
    public void delete(@PathVariable Integer userId) {
        userService.delete(userId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        return userService.getFriends(id);
    }
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }
    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable Integer id) {
        return userService.getFeed(id);
    }

}
