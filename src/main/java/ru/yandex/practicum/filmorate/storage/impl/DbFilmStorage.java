package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DbFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final MpaStorage mpaStorage;

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

        if (film.getGenres() != null) {
            film.setGenres(film.getGenres().stream().distinct().collect(Collectors.toList()));
            genreUpdate(film);
        }

        if (film.getDirectors() != null) {
            film.setDirectors(film.getDirectors().stream().distinct().collect(Collectors.toList()));
            directorUpdate(film);
        }

        return getById(film.getId());
    }

    @Override
    public Film update(Film film) {
        int response = jdbcTemplate.update("update films set name = ?, description = ?, release_date = ?, duration = ?, rating = ?" +
                        "where id = ?",
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (response == 0) {
            throw new WrongFilmIdException("No such film in DB with id = " + film.getId() + ". Update failed");
        }

        jdbcTemplate.update("delete from film_genre where film_id = ?", film.getId());

        if (film.getGenres() != null) {
            film.setGenres(film.getGenres().stream().distinct().collect(Collectors.toList()));
            genreUpdate(film);
        }

        jdbcTemplate.update("delete from film_director where film_id = ?", film.getId());

        if (film.getDirectors() != null) {
            film.setDirectors(film.getDirectors().stream().distinct().collect(Collectors.toList()));
            directorUpdate(film);
        }

        return getById(film.getId());
    }

    @Override
    public Film delete(Film film) {
        jdbcTemplate.update("delete from films where id = ?", film.getId());
        return film;
    }

    @Override
    public Film getById(Long filmId) {
        String sqlQuery = "select id, name, description, release_date, duration, rating from films where id=?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapper, filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new WrongFilmIdException("There is no film in DB with id = " + filmId);
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(
                "select id, name, description, release_date, duration, rating from films",
                this::mapper
        );
    }

    @Override
    public List<Film> getPopular(int count, int genreId, int year) {
        log.info("Поиск {} самых популярных фильмов по жанру {} за {} год", count, genreId, year);
        return jdbcTemplate.query("select f.* " +
                "from films f " +
                "left join (select fl.film_id, count(fl.user_id) cnt from film_like fl group by fl.film_id) l " +
                "on f.id = l.film_id " +
                "left join (select fg.film_id from film_genre fg " +
                "where fg.genre_id = decode(?, -9999, fg.genre_id, ?) group by fg.film_id) g " +
                "on f.id = g.film_id " +
                "where f.id = decode(?, -9999, f.id, g.film_id) " +
                "and year(f.release_date) = decode(?, -9999, year(f.release_date), ?) " +
                "order by l.cnt desc " +
                "limit ?", this::mapper, genreId, genreId, genreId, year, year, count);
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return jdbcTemplate.query("select f.*, count(1) cnt " +
                "from films f join film_like fl on f.id = fl.film_id " +
                "where fl.user_id in (?, ?) " +
                "group by f.id " +
                "having cnt > 1", this::mapper, userId, friendId);
    }

    @Override
    public List<Film> getTopByDirector(int id, String sortBy) {
        String sqlRequest = "SELECT f.* FROM films f LEFT JOIN " +
                "(SELECT fl.film_id, COUNT(fl.user_id) cnt FROM film_like fl GROUP BY fl.film_id) l " +
                "on f.id = l.film_id " +
                "WHERE f.id IN (SELECT film_id FROM film_director WHERE director_id = ?)";
        switch (sortBy) {
            case "year":
                sqlRequest = sqlRequest + "ORDER BY f.release_date";
                break;
            case "likes":
                sqlRequest = sqlRequest + "ORDER BY cnt";
                break;
            default:
                throw new ValidationException("No such sort was found");
        }

        return jdbcTemplate.query(sqlRequest, this::mapper, id);
    }

    private Film mapper(ResultSet resultSet, int rowNum) {
        log.info("mapper.rowNum = {}", rowNum);
        try {
            Mpa mpa = mpaStorage.getById(resultSet.getInt("rating"));
            List<Genre> genres = genreStorage.getByFilmId(resultSet.getLong("id"));
            List<Director> directors = directorStorage.getByFilmId(resultSet.getLong("id"));

            return Film.builder()
                    .id(resultSet.getLong("id"))
                    .name(resultSet.getString("name"))
                    .description(resultSet.getString("description"))
                    .releaseDate(resultSet.getDate("release_date").toLocalDate())
                    .duration(resultSet.getInt("duration"))
                    .mpa(mpa)
                    .genres(genres)
                    .directors(directors)
                    .build();
        } catch (SQLException e) {
            throw new WrongFilmIdException("Can't unwrap film from DB response");
        }
    }

    private void genreUpdate(Film film) {
        jdbcTemplate.batchUpdate("insert into film_genre (film_id, genre_id) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        int genreId = film.getGenres().get(i).getId();
                        ps.setLong(1, film.getId());
                        ps.setInt(2, genreId);
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                }
        );
    }

    private void directorUpdate(Film film) {
        jdbcTemplate.batchUpdate("INSERT INTO film_director (film_id, director_id) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        long directorId = film.getDirectors().get(i).getId();
                        ps.setLong(1, film.getId());
                        ps.setLong(2, directorId);
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getDirectors().size();
                    }
                }
        );
    }
}