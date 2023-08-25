package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long userId, long filmId) {
        log.info(String.format("Добавление лайка для фильма %d от пользователя %d", filmId, userId));
        Set<Long> filmLikes = new HashSet<>();
        if (userStorage.getUserById(userId) != null) {
            Film film = filmStorage.getFilmById(filmId);
            if (film.getUsersLikes() != null) {
                filmLikes = film.getUsersLikes();
            }
            filmLikes.add(userId);
            film.setUsersLikes(filmLikes);
            filmStorage.updateFilm(film);
        }
    }

    public void deleteLike(long userId, long filmId) {
        log.info(String.format("Удаление лайка для фильма %d от пользователя %d", filmId, userId));
        if (userStorage.getUserById(userId) != null) {
            Film film = filmStorage.getFilmById(filmId);
            if (film.getUsersLikes() != null) {
                Set<Long> filmLikes = film.getUsersLikes();
                if (filmLikes.contains(userId)) {
                    filmLikes.remove(userId);
                    film.setUsersLikes(filmLikes);
                    filmStorage.updateFilm(film);
                }
            }
        }
    }

    public List<Film> getFilmsPopularList(int count) {
        log.info(String.format("Список %d самых популярных фильмов", count));
        List<Film> filmsPopularList = filmStorage.getFilms();
        if (filmsPopularList != null) {
            filmsPopularList = filmsPopularList
                    .stream()
                    .sorted((film1, film2) -> compareLikesCount(film1, film2))
                    .limit(count)
                    .collect(Collectors.toList());
        }
        return filmsPopularList;
    }

    private int compareLikesCount(Film film1, Film film2) {
        int film1LikesCount;
        int film2LikesCount;
        if (film1.getUsersLikes() == null) {
            film1LikesCount = 0;
        } else {
            film1LikesCount = film1.getUsersLikes().size();
        }
        if (film2.getUsersLikes() == null) {
            film2LikesCount = 0;
        } else {
            film2LikesCount = film2.getUsersLikes().size();
        }
        return -1 * (film1LikesCount - film2LikesCount);
    }
}
