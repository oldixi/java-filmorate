package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.ValidationException;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Builder
@Data
public class User {
    private long id;
    @Email(message = "Электронная почта не прошла валидацию.")
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
