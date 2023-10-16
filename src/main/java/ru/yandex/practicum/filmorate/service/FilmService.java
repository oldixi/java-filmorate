package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WrongIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate EARLIEST_FILM_RELEASE = LocalDate.of(1895, 12, 5);
    private static final int DEFAULT_FILMS_COUNT = 10;
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final FeedStorage feedStorage;
    private final DirectorService directorService;
    private final UserService userService;
    private final GenreService genreService;
    private final MpaService mpaService;

    public Film addFilm(Film film) {
        if (isNotValid(film)) {
            throw new ValidationException("Film validation has been failed");
        }
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        if (isNotValid(film)) {
            throw new ValidationException("Film validation has been failed");
        }
        if (!isLegalFilmId(film.getId())) {
            return film;
        }
        return filmStorage.update(film);
    }

    public void addLike(long userId, long filmId) {
        if (isLegalFilmId(filmId) && userService.isLegalUserId(userId)) {
            if (likeStorage.getLikesByFilmId(filmId).contains(userId)) {
                feedStorage.addLike(userId, filmId);
                return;
            }
            likeStorage.addLike(userId, filmId);
            feedStorage.addLike(userId, filmId);
        }
    }

    public void deleteLike(long userId, long filmId) {
        if (isLegalFilmId(filmId) && userService.isLegalUserId(userId)) {
            likeStorage.deleteLike(userId, filmId);
            feedStorage.deleteLike(userId, filmId);
        }
    }

    public Film getFilmById(long filmId) {
        if (isIncorrectId(filmId)) {
            throw new WrongIdException("Param must be more then 0");
        }
        Optional<Film> filmOpt = filmStorage.getById(filmId);
        if (filmOpt.isEmpty()) {
            throw new WrongIdException("No film with id = " + filmId + " in DB was found.");
        }
        return filmOpt.get();
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public List<Film> getTopFilms(int count, Optional<Integer> genreId, Optional<String> year) {
        if (count <= 0) {
            count = DEFAULT_FILMS_COUNT;
        }
        return filmStorage.getPopular(count, genreId, year);
    }

    public List<Film> getTopByDirector(int id, String sortBy) {
        if (!directorService.isLegalDirectorId(id)) {
            return new ArrayList<>();
        }
        return filmStorage.getTopByDirector(id, sortBy);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        if (!userService.isLegalUserId(userId) || !userService.isLegalUserId(friendId)) {
            return new ArrayList<>();
        }
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public void deleteFilmById(long id) {
        if (isIncorrectId(id)) {
            throw new WrongIdException("Param must be more then 0");
        }
        filmStorage.delete(id);
    }

    public List<Film> searchFilms(String query, String by) {
        return filmStorage.searchFilms(query, by);
    }

    public boolean isLegalFilmId(Long filmId) {
        return getFilmById(filmId) != null;
    }

    private boolean isIncorrectId(Long id) {
        return id == null || id <= 0;
    }

    private boolean isNotValid(Film film) {
        return film.getReleaseDate().isBefore(EARLIEST_FILM_RELEASE);
    }
}