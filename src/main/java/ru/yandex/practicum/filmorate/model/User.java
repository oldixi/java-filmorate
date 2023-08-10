package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.ValidationException;

import java.time.LocalDate;

@Builder
@Data
public class User {
    private static final LocalDate NOW = LocalDate.now();

    int id;
    String email;
    String login;
    String name;
    LocalDate birthday;

    public boolean isValid() {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
        if (login == null || login.isBlank() || login.contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелы.");
        }
        if (name == null || name.isBlank()) {
            name = login;
        }
        if (birthday == null || birthday.isAfter(NOW)) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        return true;
    }
}
