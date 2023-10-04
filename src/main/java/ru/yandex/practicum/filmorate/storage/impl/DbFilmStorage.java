package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DbGenreStorage dbGenreStorage;

    @Override
    public Film add(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "insert into films (name, description, release_date, duration, rating) values (?, ?, ?, ?, ?)";

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

        List<Genre> filmGenres = film.getGenres();
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
        jdbcTemplate.update("update films set name = ?, description = ?, release_date = ?, duration = ?, rating = ?" +
                        "where id = ?",
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        jdbcTemplate.update("delete from film_genre where film_id = ?", film.getId());
        List<Genre> filmGenres = film.getGenres();
        if (filmGenres != null) {
            filmGenres.stream().distinct().forEach(genre -> jdbcTemplate.update(
                    "insert into film_genre (film_id, genre_id) values (?, ?)",
                    film.getId(),
                    genre.getId()));
            film.setGenres(filmGenres.stream().distinct().collect(Collectors.toList()));
        }
        Set<Long> likeIds = film.getLikeIds();
        if (likeIds != null) {
            likeIds.forEach(userId -> jdbcTemplate.update(
                    "insert into film_like (film_id, user_id) values (?, ?)",
                    film.getId(),
                    userId));
        }
        return film;
    }

    @Override
    public Film delete(Film film) {
        jdbcTemplate.update("delete from films where id = ? cascade", film.getId());
        return film;
    }

    @Override
    public Film getById(Long filmId) throws SQLException {
        if (!isPresent(filmId)) {
            throw new WrongFilmIdException("No film with such id=" + filmId);
        }
        String sqlQuery = "select id, name, description, release_date, duration, rating from films where id=?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapper, filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(
                "select id, name, description, release_date, duration, rating from films",
                this::mapper
        );
    }

    @Override
    public boolean isPresent(Long filmId) {
        return jdbcTemplate.queryForObject("select count(id) from films where id = ?",
                Integer.class,
                filmId) != 0;
    }

    @Override
    public List<Genre> getAllGenres() {
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
    public Genre getGenreById(int id) {
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

    @Override
    public List<Mpa> getAllMpas() {
        return jdbcTemplate.query(
                "select id, name from ratings",
                (resultSetGenre, rowNumGenre) -> {
                    Mpa mpa = new Mpa();
                    mpa.setId(resultSetGenre.getInt(1));
                    mpa.setName(resultSetGenre.getString(2));
                    return mpa;
                });
    }

    @Override
    public Mpa getMpaById(int id) {
        if (jdbcTemplate.queryForObject("select count(id) from ratings where id = ?",
                Integer.class,
                id) == 0) {
            throw new WrongFilmIdException("No such mpa with id=" + id);
        }
        return jdbcTemplate.queryForObject(
                "select id, name from ratings where id = ?",
                (resultSetMpa, rowNumMpa) -> {
                    Mpa mpa = new Mpa();
                    mpa.setId(resultSetMpa.getInt(1));
                    mpa.setName(resultSetMpa.getString(2));
                    return mpa;
                }, id);
    }

    private Film mapper(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = jdbcTemplate.queryForObject(
                "select id, name from ratings where id = ?",
                (resultSetMpa, rowNumMpa) -> {
                    Mpa mpa1 = new Mpa();
                    mpa1.setId(resultSetMpa.getInt(1));
                    mpa1.setName(resultSetMpa.getString(2));
                    return mpa1;
                }, resultSet.getInt(6));
        List<Genre> genres = jdbcTemplate.query(
                "select id, name from genres where id in (select genre_id from film_genre where film_id = ?)",
                (resultSetGenre, rowNumGenre) -> {
                    Genre genre = new Genre();
                    genre.setId(resultSetGenre.getInt(1));
                    genre.setName(resultSetGenre.getString(2));
                    return genre;
                }, resultSet.getLong(1));
        Set<Long> likeIds = new HashSet<>(jdbcTemplate.query(
                "select user_id from film_like where film_id = ?",
                (resultSetLike, rowNumLike) -> resultSetLike.getLong(1),
                resultSet.getLong(1)
        ));
        return Film.builder()
                .id(resultSet.getLong(1))
                .name(resultSet.getString(2))
                .description(resultSet.getString(3))
                .releaseDate(resultSet.getDate(4).toLocalDate())
                .duration(resultSet.getInt(5))
                .mpa(mpa)
                .genres(genres)
                .likeIds(likeIds)
                .build();
    }
}
