package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final int DESCRIPTION_LENGTH = 200;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    Map<Long, Film> films = new HashMap<>();
    private long idNum;

    private long generateId() {
        return ++idNum;
    }

    private boolean isValid(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Наименование фильма - обязательное поле.");
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() > DESCRIPTION_LENGTH) {
                throw new ValidationException("Описание фильма ограничено 200 символами.");
            }
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть больше 0.");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Система поддерживает загрузку фильмов с датой выхода после 28 декабря 1895.");
        }
        return true;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (isValid(film)) {
            film.setId(generateId());
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм. ", film);
            return film;
        }
        throw new ValidationException("Фильм не прошел валидацию.");
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getId() != 0 && films.containsKey(film.getId()) && isValid(film)) {
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