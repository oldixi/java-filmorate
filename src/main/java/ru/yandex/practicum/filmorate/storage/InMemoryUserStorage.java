package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Data
public class InMemoryUserStorage implements UserStorage {
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

    public User addUser(User user) {
        if (isValid(user)) {
            user.setId(generateId());
            users.put(user.getId(), user);
            log.info(String.format("Добавлен новый пользователь %d", user.getId()));
            return user;
        }
        throw new ValidationException("Пользователь не прошел валидацию.");
    }

    public User updateUser(User user) {
        if (user.getId() != 0 && users.containsKey(user.getId()) && isValid(user)) {
            users.replace(user.getId(), user);
            log.info(String.format("Изменен пользователь %d", user.getId()));
            return user;
        }
        throw new UserNotFoundException(String.format("Не существует пользователя с заданным id %d.", user.getId()));
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(Long id) {
        log.info(String.format("Ищем пользователя %d", id));
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(String.format("Не найден пользователь с заданным id %d.", id));
        }
        return users.get(id);
    }
}
