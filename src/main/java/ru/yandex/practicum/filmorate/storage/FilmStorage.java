package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Film delete(Film film);

    Film getById(Long filmId);

    List<Film> getAllFilms();

    List<Film> getTopByDirector(int id, String sortBy);

    List<Film> getPopular(int count, int genreId, int year);

    List<Film> getCommonFilms(long userId, long friendId);
}
