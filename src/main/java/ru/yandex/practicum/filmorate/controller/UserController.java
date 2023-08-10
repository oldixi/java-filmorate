package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    Map<Long, User> users = new HashMap<>();

    private long idNum;

    private long generateId() {
        return ++idNum;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (user.isValid()) {
            user.setId(generateId());
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь. ", user);
            return user;
        }
        return null;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() != 0 && users.containsKey(user.getId()) && user.isValid()) {
            users.replace(user.getId(), user);
            log.info("Изменен пользователь. ", user);
            return user;
        }
        throw new ValidationException("Не существует пользователя с заданным id " + user.getId() + ".");
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}