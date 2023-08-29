package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class User {
    private long id;

    @Email
    private String email;

    @NotNull
    @NotEmpty
    @NotBlank
    private String login;

    private String name;
    private LocalDate birthday;
}
