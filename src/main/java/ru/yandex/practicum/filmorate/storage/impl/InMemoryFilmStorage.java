package ru.yandex.practicum.filmorate.storage.impl;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
public class InMemoryFilmStorage implements FilmStorage {
    private long uniqueId;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film add(Film film) {
        film.setId(generateId());
        films.put(uniqueId, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.replace(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Film film) {
        films.remove(film.getId());
        return film;
    }

    @Override
    public Film getById(Long filmId) {
        return films.get(filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        return List.copyOf(films.values());
    }

    @Override
    public boolean isPresent(Long filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public List<Genre> getAllGenres() {
        return null;
    }

    @Override
    public Genre getGenreById(int id) {
        return null;
    }

    @Override
    public List<Mpa> getAllMpas() {
        return null;
    }

    @Override
    public Mpa getMpaById(int id) {
        return null;
    }

    private long generateId() {
        return ++uniqueId;
    }

}
