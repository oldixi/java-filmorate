package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.WrongDirectorIdException;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class DbDirectorStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director getDirectorById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM directors WHERE id = ?",
                    (rs, RowNum) -> new Director(rs.getInt("id"), rs.getString("name")), id);
        } catch (EmptyResultDataAccessException e) {
            throw new WrongDirectorIdException("There is no director in DB with id = " + id);
        }
    }

    @Override
    public Director addDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO directors (name) VALUES (?)";

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, director.getName());
            return statement;
        }, keyHolder);
        return getDirectorById(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query("SELECT * FROM directors",
                (rs, RowNum) -> new Director(rs.getInt("id"), rs.getString("name")));
    }

    @Override
    public Director updateDirector(Director director) {
        int response = jdbcTemplate.update("UPDATE directors SET name = ? WHERE id = ?",
                director.getName(),
                director.getId());

        if (response == 0) {
            throw new WrongDirectorIdException("No such director in DB with id = " + director.getId() +
                    " was found. Update failed");
        }

        return getDirectorById(director.getId());
    }

    @Override
    public long deleteDirector(long id) {
        jdbcTemplate.update("DELETE FROM directors WHERE id = ?", id);
        return id;
    }

    @Override
    public List<Director> getByFilmId(long filmId) {
        return jdbcTemplate.query(
                "SELECT * FROM directors WHERE id IN (SELECT director_id FROM film_director WHERE film_id = ?)",
                (rs, RowNum) -> mapper(rs),
                filmId);
    }

    private Director mapper(ResultSet resultSet) throws SQLException {
        try {
            return new Director(resultSet.getInt("id"), resultSet.getString("name"));
        } catch (SQLException e) {
            throw new WrongFilmIdException("Can't unwrap director from DB response");
        }
    }
}