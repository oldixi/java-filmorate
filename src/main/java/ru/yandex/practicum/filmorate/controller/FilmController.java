package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.InvalidPathVariableException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate EARLIEST_FILM_RELEASE = LocalDate.of(1895, 12, 28);
    private final FilmService filmService;
    private long uniqueId;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Get list of films.");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable String id) {
        long filmId = parsePathParam(id);

        if (filmId == -1) {
            throw new InvalidPathVariableException("Incorrect film id format.");
        }

        Film film = filmService.getFilmById(filmId);

        if (film == null) {
            throw new WrongFilmIdException("Film with such id doesn't exist.");
        }

        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable String id, String userId) {
        long filmId = parsePathParam(id);
        long parsedUserId = parsePathParam(userId);

        if (filmId == -1 || parsedUserId == -1) {
            throw new InvalidPathVariableException("Incorrect film or user id format.");
        }

        filmService.addLike(parsedUserId, filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable String id, String userId) {
        long filmId = parsePathParam(id);
        long parsedUserId = parsePathParam(userId);

        if (filmId == -1 || parsedUserId == -1) {
            throw new InvalidPathVariableException("Incorrect film or user id format.");
        }

        filmService.deleteLike(parsedUserId, filmId);
    }

    @GetMapping("/popular?count={count}")
    public List<Film> getPopularFilms(@RequestParam(required = false) String count) {
        long filmCount = parsePathParam(count);

        if (filmCount == -1) {
            throw new InvalidPathVariableException("Incorrect film count parameter format.");
        }

        return filmService.getTopFilms(filmCount);
    }

    @PostMapping
    public Film post(@Valid @RequestBody Film film) {

        log.info("Film added {}. Total films quantity {}", film, films.size());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (isNotValid(film)) {
            log.warn("Film is not valid. {}", film);
            throw new ValidationException("Film validation has been failed");
        }

        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Film updated {}", film);
            return film;
        }

        throw new ValidationException("Can't find the film to update");
    }

    private long parsePathParam(String pathId) {
        try {
            return Long.parseLong(pathId);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
