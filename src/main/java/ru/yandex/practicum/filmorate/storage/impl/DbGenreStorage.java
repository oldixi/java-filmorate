package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DbGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query(
                "select id, name from genres",
                this::mapper);
    }

    @Override
    public Genre getById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select id, name from genres where id = ?",
                    this::mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new WrongFilmIdException("No such genre in DB with id = " + id + " was found.");
        }
    }

    private Genre mapper(ResultSet resultSet, int rowNum) throws SQLException {
            Genre genre = new Genre();
            genre.setId(resultSet.getInt("genres.id"));
            genre.setName(resultSet.getString("genres.name"));
            return genre;
    }
}
