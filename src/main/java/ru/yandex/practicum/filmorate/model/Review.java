package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Review {
    private long reviewId;

    @NotNull
    @NotBlank
    @Size(max = 500)
    private String content;

    @NotNull
    @JsonProperty("isPositive")
    private Boolean positive;

    @NotNull
    private Long userId;

    @NotNull
    private Long filmId;
    private int useful;
}
