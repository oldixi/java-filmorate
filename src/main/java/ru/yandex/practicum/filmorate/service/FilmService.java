package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.exception.WrongUserIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    //Стас, пришлось криво называть константу - паттерн проверки кодстайла на гите не содержит '_'. В поддержку уже написал
    private static final LocalDate EARLIESTFILMRELEASE = LocalDate.of(1895, 12, 5);
    private final FilmStorage filmStorage;

    public Film addFilm(Film film) {
        if (isNotValid(film)) {
            throw new ValidationException("Film validation has been failed");
        }

        log.info("Film added {}.", film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        if (isNotValid(film)) {
            log.warn("Film is not valid. {}", film);
            throw new ValidationException("Film validation has been failed");
        }

        log.info("Film updated {}", film);
        return filmStorage.update(film);
    }

    public void addLike(long userId, long filmId) {
        if (isIncorrectId(filmId)) {
            throw new WrongFilmIdException("Param must be more then 0");
        }

        if (isIncorrectId(userId)) {
            throw new WrongUserIdException("Param must be more then 0");
        }

        log.info("Like added to film {} from user {}", filmId, userId);
        filmStorage.update(filmStorage.getById(filmId).addLike(userId));
    }

    public void deleteLike(long userId, long filmId) {
        if (isIncorrectId(userId)) {
            throw new WrongUserIdException("Param must be more then 0");
        }

        if (isIncorrectId(filmId)) {
            throw new WrongFilmIdException("Param must be more then 0");
        }

        filmStorage.update(filmStorage.getById(filmId).deleteLike(userId));
    }

    public Film getFilmById(long filmId) {
        if (isIncorrectId(filmId)) {
            throw new WrongFilmIdException("Param must be more then 0");
        }

        return filmStorage.getById(filmId);
    }

    public List<Film> getAllFilms() throws SQLException {
        return filmStorage.getAllFilms();
    }

    public List<Film> getTopFilms(long count) {
        if (isIncorrectId(count)) {
            throw new WrongFilmIdException("Param must be more then 0");
        }

        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing(film -> -film.getLikeIds().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean isNotValid(Film film) {
        return film.getReleaseDate().isBefore(EARLIESTFILMRELEASE);
    }

    private boolean isIncorrectId(long id) {
        return id <= 0;
    }
}
