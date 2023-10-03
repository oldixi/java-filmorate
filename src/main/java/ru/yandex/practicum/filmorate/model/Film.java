package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class Film {
    private long id;

    @NotNull
    @NotBlank
    @NotEmpty
    private String name;

    @NotNull
    @Size(max = 200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private List<Genre> genres;
    private Mpa mpa;

    private Set<Long> likeIds;

    public Film addLike(Long userId) {
        likeIds.add(userId);
        return this;
    }

    public Film deleteLike(Long userId) {
        likeIds.remove(userId);
        return this;
    }

}
