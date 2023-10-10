package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director getDirectorById(int id);

    Director addDirector(Director director);

    List<Director> getDirectors();

    Director updateDirector(Director director);

    long deleteDirector(long id);

    List<Director> getByFilmId(long filmId);
}
