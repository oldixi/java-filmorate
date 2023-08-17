package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private long uniqueId;

    @GetMapping(value = "films")
    public List<Film> getFilms() {
        log.info("Get list of films.");
        return List.copyOf(films.values());
    }

    @PostMapping(value = "films")
    public Film post(@RequestBody Film film) {
        if (isNotValid(film)) {
            log.info("Film is not valid. {}", film);
            throw new ValidationException("Film validation has been failed");
        }

        if (film.getId() == 0) {
            film.setId(generateId());
        }

        films.put(uniqueId, film);
        log.info("Film added {}. Total films quantity {}", film, films.size());
        return film;
    }

    @PutMapping(value = "films")
    public Film update(@RequestBody Film film) {
        if (isNotValid(film)) {
            log.info("Film is not valid. {}", film);
            throw new ValidationException("Film validation has been failed");
        }

        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Film updated {}", film);
            return film;
        }

        throw new ValidationException("Can't find the film to update");
    }

    private boolean isNotValid(Film film) {
        return film.getName().isEmpty()
                || film.getName().isBlank()
                || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))
                || film.getDuration() <= 0;
    }

    private long generateId() {
        return ++uniqueId;
    }
}
