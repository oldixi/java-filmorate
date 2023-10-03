package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/films")
    public List<Film> getAllFilms() throws SQLException {
        log.info("Get list of films.");
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable long id) {
        log.info("Requested film {}", id);
        return filmService.getFilmById(id);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") long count) throws SQLException {
        log.info("Requested most popular {} films", count);
        return filmService.getTopFilms(count);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return filmService.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpas() {
        return filmService.getAllMpas();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaBuId(@PathVariable int id) {
        return filmService.getMpaById(id);
    }

    @PostMapping("/films")
    public Film post(@Valid @RequestBody Film film) {
        log.info("Requested add film {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike(@PathVariable long id, @PathVariable long userId) throws SQLException {
        log.info("Requested add like to film {} from user {}", id, userId);
        filmService.addLike(userId, id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        log.info("Requested update film {}", film);
        return filmService.update(film);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) throws SQLException {
        log.info("Requested delete like to film {} from user {}", id, userId);
        filmService.deleteLike(userId, id);
    }

}
