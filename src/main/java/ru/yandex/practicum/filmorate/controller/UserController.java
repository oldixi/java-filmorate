package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long uniqueId;


    @GetMapping
    public List<User> getUsers() {
        return List.copyOf(users.values());
    }

    @PostMapping
    public User post(@Valid @RequestBody User user) {
        if (isNotValid(user)) {
            log.info("User is not valid {}", user);
            throw new ValidationException("User validation has been failed");
        }

        if (user.getId() == 0) {
            user.setId(generateId());
        }

        users.put(uniqueId, user);
        log.info("New user added {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (isNotValid(user)) {
            log.info("User is not valid {}", user);
            throw new ValidationException("User validation has been failed");
        }

        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("User {} has been updated", user.getLogin());
            return user;
        }

        throw new ValidationException("Can't find user to update");
    }

    private boolean isNotValid(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return user.getLogin().contains(" ")
                || user.getBirthday().isAfter(LocalDate.now());
    }

    private long generateId() {
        return ++uniqueId;
    }
}
