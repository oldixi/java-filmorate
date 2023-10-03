package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.SQLException;
import java.util.List;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Film delete(Film film);

    Film getById(Long filmId) throws SQLException;

    List<Film> getAllFilms() throws SQLException;

    boolean isPresent(Long filmId);

    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    List<Mpa> getAllMpas();

    Mpa getMpaById(int id);
}
