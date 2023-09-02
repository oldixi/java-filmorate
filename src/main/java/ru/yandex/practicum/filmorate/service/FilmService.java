package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidPathVariableException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final LocalDate EARLIEST_FILM_RELEASE = LocalDate.of(1895, 12,5);
    private long uniqueId;
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

        log.info("Film added {}.", film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        if (isNotValid(film)) {
            log.warn("Film is not valid. {}", film);
            throw new ValidationException("Film validation has been failed");
        }

        if (filmStorage.isPresent(film.getId())) {
            log.info("Film updated {}", film);
            return filmStorage.update(film);
        }

        log.warn("Requested non-existent film {}", film);
        throw new WrongFilmIdException("Can't find the film to update");
    }

    public void addLike(String userId, String filmId) {
        long parsedFilmId = parsePathParam(filmId);
        long parsedUserId = parsePathParam(userId);

        if (filmStorage.isPresent(parsedFilmId)) {
            filmStorage.getById(parsedFilmId).addLike(parsedUserId);
            return;
        }

        log.warn("Requested non-existent film. Id {}", filmId);
        throw new WrongFilmIdException("There is no film with such id.");
    }

    public void deleteLike(String userId, String filmId) {
        long parsedFilmId = parsePathParam(filmId);
        long parsedUserId = parsePathParam(userId);

        if (filmStorage.isPresent(parsedFilmId)) {
            filmStorage.getById(parsedFilmId).deleteLike(parsedUserId);
            return;
        }

        log.warn("Requested non-existent film. Id {}", filmId);
        throw new WrongFilmIdException("There is no film with such id.");
    }

    public Film getFilmById(String filmId) {
        long parsedFilmId = parsePathParam(filmId);

        if (filmStorage.isPresent(parsedFilmId)) {
            return filmStorage.getById(parsedFilmId);
        }

        log.warn("Requested non-existent film. Id {}", filmId);
        throw new WrongFilmIdException("Film with such id doesn't exist");
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public List<Film> getTopFilms(String count) {
        long filmsCount = 10;

        if (count != null) {
            filmsCount = parsePathParam(count);
        }

        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing(film -> -film.getLikes().size()))
                .limit(filmsCount)
                .collect(Collectors.toList());
    }

    private boolean isNotValid(Film film) {
        return film.getReleaseDate().isBefore(EARLIEST_FILM_RELEASE);
    }

    private long generateId() {
        return ++uniqueId;
    }

    private Long parsePathParam(String pathId) {
        long pathVariable;
        try {
            pathVariable = Long.parseLong(pathId);
        } catch (NumberFormatException e) {
            log.warn("Parser. Path variable has wrong format {}", pathId);
            throw new InvalidPathVariableException("Incorrect film id or count parameter format.");
        }
        if (pathVariable < 0) {
            log.warn("Parser. Requested film with wrong id {}", pathVariable);
            throw new WrongFilmIdException("Film with such id doesn't exist.");
        }

        return pathVariable;
    }

}
