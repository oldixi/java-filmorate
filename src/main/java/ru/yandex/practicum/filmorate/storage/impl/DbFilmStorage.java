package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "insert into films (name, description, release_date, duration, rating) values (?, ?, ?, ?, ?)";
//        Integer rating_id = jdbcTemplate.queryForObject(
//                "select id from ratings where name = ?",
//                Integer.class,
//                String.valueOf(film.getMpa().getName()));
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        List<Genre> filmGenres = film.getGenre();
        if (filmGenres != null) {
            filmGenres.forEach(genre -> jdbcTemplate.update(
                    "insert into film_genre (film_id, genre_id) values (?, ?)",
                    keyHolder.getKey().longValue(),
                    genre.getId()));
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    @Override
    public Film delete(Film film) {
        return null;
    }

    @Override
    public Film getById(Long filmId) throws SQLException {
        String sqlQuery = "select id, name, description, release_date, duration, rating from films where id=?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapper, filmId);
        sqlQuery = "select name from ratings where ";
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return List.of();
    }

    @Override
    public boolean isPresent(Long filmId) {
        return false;
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.queryForList(
                "select id, name from genres",
                Genre.class);
    }

    @Override
    public Genre getGenreById(int id) {
        return jdbcTemplate.queryForObject(
                "select id, name from genres where id = ?",
                Genre.class,
                id);
    }

    private Film mapper(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = jdbcTemplate.queryForObject(
                "select id, name from ratings where id = ?",
                Mpa.class,
                resultSet.getInt(6));
        List<Genre> genres = jdbcTemplate.queryForList(
                "select id, name from genres where id in (select genre_id from film_genre where film_id = ?)",
                Genre.class);
        return Film.builder()
                .id(resultSet.getLong(1))
                .name(resultSet.getString(2))
                .description(resultSet.getString(3))
                .releaseDate(resultSet.getDate(4).toLocalDate())
                .duration(resultSet.getInt(5))
                .mpa(mpa)
                .genre(genres)
                .build();
    }
}
