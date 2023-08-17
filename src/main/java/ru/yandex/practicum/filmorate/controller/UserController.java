package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@RestController
public class UserController {
    private final static String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@.+$";
    private final Map<Long, User> users = new HashMap<>();
    private long uniqueId;


    @GetMapping(value = "users")
    public List<User> getUsers() {
        return List.copyOf(users.values());
    }

    @PostMapping(value = "users")
    public User post(@RequestBody User user) {
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

    @PutMapping(value = "users")
    public User update(@RequestBody User user) {
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

        return user.getLogin().isEmpty()
                || user.getLogin().isBlank()
                || user.getLogin().contains(" ")
                || !Pattern.matches(EMAIL_REGEX, user.getEmail())
                || user.getBirthday().isAfter(LocalDate.now());
    }

    private long generateId() {
        return ++uniqueId;
    }
}
