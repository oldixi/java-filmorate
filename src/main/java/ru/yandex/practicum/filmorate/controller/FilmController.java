package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    Map<Long, Film> films = new HashMap<>();

    private long idNum;
    private long generateId() {
        return ++idNum;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (film.isValid()) {
            film.setId(generateId());
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм. ", film);
            return film;
        }
        return null;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getId() != 0 && film.isValid() && films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
            log.info("Изменена информация о фильме. ", film);
            return film;
        }
        throw new ValidationException("Не существует фильма с заданным id " + film.getId() + ".");
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}
