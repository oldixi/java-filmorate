package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        if (isNotValid(film)) {
            log.warn("Film is not valid. {}", film);
            throw new ValidationException("Film validation has been failed");
        }

        if (film.getId() == 0) {
            film.setId(generateId());
        }

        films.put(uniqueId, film);
    }

    public void addLike(Long userId, Long filmId) {
        filmStorage.getById(filmId).addLike(userId);
    }

    public void deleteLike(Long userId, Long filmId) {
        filmStorage.getById(filmId).deleteLike(userId);
    }

    public Film getFilmById(long filmId) {
        return filmStorage.getById(filmId);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public List<Film> getTopFilms(long count) {
        if (count == 0) {
            count = 10;
        }

        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

}
