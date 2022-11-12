package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.IdGenerator;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private  final UserService userService;


    @GetMapping("/users")
    public List<User> findAll() {
        return userService.findAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user)  {
        return userService.create(user);
    }

    @PutMapping(value = "/users")
    public User put(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

}
