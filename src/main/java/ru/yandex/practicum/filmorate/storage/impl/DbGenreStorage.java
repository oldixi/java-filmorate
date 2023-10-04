package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DbGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query(
                "select id, name from genres",
                (resultSetGenre, rowNumGenre) -> {
                    Genre genre = new Genre();
                    genre.setId(resultSetGenre.getInt(1));
                    genre.setName(resultSetGenre.getString(2));
                    return genre;
                });
    }

    @Override
    public Genre getById(int id) {
        if (jdbcTemplate.queryForObject("select count(id) from genres where id = ?",
                Integer.class,
                id) == 0) {
            throw new WrongFilmIdException("No such genre with id=" + id);
        }
        return jdbcTemplate.queryForObject(
                "select id, name from genres where id = ?",
                (resultSetGenre, rowNumGenre) -> {
                    Genre genre = new Genre();
                    genre.setId(resultSetGenre.getInt(1));
                    genre.setName(resultSetGenre.getString(2));
                    return genre;
                }, id);
    }
}
