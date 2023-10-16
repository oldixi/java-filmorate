package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.WrongIdException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DbDirectorStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Director> getDirectorById(int id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM directors WHERE id = ?",
                    (rs, RowNum) -> new Director(rs.getInt("id"), rs.getString("name")), id));
        } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
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

        if (keyHolder.getKey() != null) {
            log.info("director {} added", Objects.requireNonNull(keyHolder.getKey()).intValue());
            director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        }
        return director;
    }

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query("SELECT * FROM directors",
                (rs, RowNum) -> new Director(rs.getInt("id"), rs.getString("name")));
    }

    @Override
    public Director updateDirector(Director director) {
        jdbcTemplate.update("UPDATE directors SET name = ? WHERE id = ?",
                director.getName(),
                director.getId());
        log.info("Director {} updated", director.getId());
        return getDirectorById(director.getId()).orElse(null);
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

    @Override
    public boolean isLegalId(long Id) {
        try {
            return jdbcTemplate.queryForObject("select 1 from directors where id=?", Integer.class, Id) != null;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private Director mapper(ResultSet resultSet) throws SQLException {
        try {
            return new Director(resultSet.getInt("id"), resultSet.getString("name"));
        } catch (SQLException e) {
            throw new WrongIdException("Can't unwrap director from DB response");
        }
    }
}