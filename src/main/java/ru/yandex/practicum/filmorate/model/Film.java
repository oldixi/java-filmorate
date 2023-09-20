package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class Film {
    private long id;
    @NotBlank(message = "Наименование фильма - обязательное поле.")
    private String name;
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть больше 0.")
    private int duration;
    private Mpa mpa;
    private List<Genre> genres;
}
