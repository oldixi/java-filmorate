package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class Review {
    private long id;

    @NotNull
    @NotBlank
    @Size(max = 500)
    private String content;

    @NotNull
    private boolean isPositive;

    private long userId;
    private long filmId;
    private int useful;

}
