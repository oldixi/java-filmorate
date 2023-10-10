package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.exception.WrongUserIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate EARLIEST_FILM_RELEASE = LocalDate.of(1895, 12, 5);
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    public Film addFilm(Film film) {
        if (isNotValid(film)) {
            throw new ValidationException("Film validation has been failed");
        }

        log.info("Film added {}.", film);
        Film film1 = filmStorage.add(film);
        film1.setGenres(genreStorage.getByFilmId(film1.getId()));
        film1.setDirectors(directorStorage.getByFilmId(film1.getId()));
        return film1;
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
        likeStorage.addLike(userId, filmId);
    }

    public void deleteLike(long userId, long filmId) {
        if (isIncorrectId(userId)) {
            throw new WrongUserIdException("Param must be more then 0");
        }

        if (isIncorrectId(filmId)) {
            throw new WrongFilmIdException("Param must be more then 0");
        }

        log.info("Like deleted from film {} from user {}", filmId, userId);
        likeStorage.deleteLike(userId, filmId);
    }

    public Film getFilmById(long filmId) {
        if (isIncorrectId(filmId)) {
            throw new WrongFilmIdException("Param must be more then 0");
        }

        Film film = filmStorage.getById(filmId);
        film.setGenres(genreStorage.getByFilmId(filmId));
        film.setDirectors(directorStorage.getByFilmId(filmId));
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        films.forEach(film -> film.setGenres(genreStorage.getByFilmId(film.getId())));
        films.forEach(film -> film.setDirectors(directorStorage.getByFilmId(film.getId())));
        return films;
    }

    public List<Film> getTopFilms(long count) {
        if (isIncorrectId(count)) {
            throw new WrongFilmIdException("Param must be more then 0");
        }

        List<Film> films = filmStorage.getPopular(count);
        films.forEach(film -> film.setGenres(genreStorage.getByFilmId(film.getId())));
        films.forEach(film -> film.setDirectors(directorStorage.getByFilmId(film.getId())));
        return films;
    }

    public List<Film> getTopByDirector(int id, String sortBy) {
        directorStorage.getDirectorById(id);
        return filmStorage.getTopByDirector(id, sortBy);
    }

    private boolean isNotValid(Film film) {
        return film.getReleaseDate().isBefore(EARLIEST_FILM_RELEASE);
    }

    private boolean isIncorrectId(long id) {
        return id <= 0;
    }
}
