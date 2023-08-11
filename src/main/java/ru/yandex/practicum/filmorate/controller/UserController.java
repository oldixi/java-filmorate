package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private static final LocalDate NOW = LocalDate.now();

    Map<Long, User> users = new HashMap<>();
    private long idNum;

    private long generateId() {
        return ++idNum;
    }

    private boolean isValid(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(NOW)) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        return true;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (isValid(user)) {
            user.setId(generateId());
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь. ", user);
            return user;
        }
        throw new ValidationException("Пользователь не прошел валидацию.");
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() != 0 && users.containsKey(user.getId()) && isValid(user)) {
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