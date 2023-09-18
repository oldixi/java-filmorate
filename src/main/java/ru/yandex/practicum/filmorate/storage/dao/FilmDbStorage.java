package ru.yandex.practicum.filmorate.storage.dao;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Constants;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@Slf4j
@Data
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = new MpaDbStorage(jdbcTemplate);
        this.genreDbStorage = new GenreDbStorage(jdbcTemplate);
    }

    private boolean isValid(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(Constants.MIN_RELEASE_DATE)) {
            throw new ValidationException("Система поддерживает загрузку фильмов с датой выхода после "
                    + Constants.MIN_RELEASE_DATE.format(Constants.FORMATTER) + ".");
        }
        if (film.getDescription() != null && film.getDescription().length() > Constants.DESCRIPTION_LENGTH) {
            throw new ValidationException("Длина описания должна быть меньше "
                    + Constants.MIN_RELEASE_DATE.format(Constants.FORMATTER) + ".");
        }
        return true;
    }

    public Film addFilm(Film film) {
        if (isValid(film)) {
            String sqlInsertFilmWithMpa = "insert into films(name, description, duration, mpa_code, release_date) " +
                    "values(?, ?, ?, ?, ?)";
            String sqlInsertFilmWithoutMpa = "insert into films(name, description, duration, release_date) " +
                    "values(?, ?, ?, ?)";
            KeyHolder keyHolderFilmId = new GeneratedKeyHolder();
            if (film.getMpa() != null) {
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sqlInsertFilmWithMpa, new String[]{"id"});
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setInt(3, film.getDuration());
                    ps.setInt(4, film.getMpa().getId());
                    ps.setDate(5, Date.valueOf(film.getReleaseDate()));
                    return ps;
                }, keyHolderFilmId);
            } else {
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sqlInsertFilmWithoutMpa, new String[]{"id"});
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setInt(3, film.getDuration());
                    ps.setDate(4, Date.valueOf(film.getReleaseDate()));
                    return ps;
                }, keyHolderFilmId);
            }
            if (keyHolderFilmId.getKey() != null) {
                film.setId(keyHolderFilmId.getKey().longValue());
                log.info(String.format("Добавлен новый фильм %d.", film.getId()));

                if (film.getGenres() != null) {
                    for (Genre genre : film.getGenres()) {
                        jdbcTemplate.update("insert into genre_link(film_id, genre_code) values (?, ?)"
                                , film.getId()
                                , genre.getId());
                    }
                }
            }
            return getFilmById(film.getId());
        }
        throw new ValidationException("Фильм не прошел валидацию.");
    }

    public Film updateFilm(Film film) {
        if (film.getId() != 0 && isValid(film) && getFilmById(film.getId()) != null) {
            String sqlUpdateFilmWithMpa = "update films " +
                    "set name = ?, description = ?, duration = ?, mpa_code = ?, release_date = ? " +
                    "where id = ?";
            String sqlUpdateFilmWithoutMpa = "update films " +
                    "set name = ?, description = ?, duration = ?, release_date = ? " +
                    "where id = ?";
            if (film.getMpa() != null) {
                jdbcTemplate.update(sqlUpdateFilmWithMpa,
                        film.getName(),
                        film.getDescription(),
                        film.getDuration(),
                        film.getMpa().getId(),
                        film.getReleaseDate(),
                        film.getId());
            } else {
                jdbcTemplate.update(sqlUpdateFilmWithoutMpa,
                        film.getName(),
                        film.getDescription(),
                        film.getDuration(),
                        film.getReleaseDate(),
                        film.getId());
            }

            if (film.getGenres() != null && film.getGenres().size() > 0) {
                List<Integer> genreListIdFromDb = genreDbStorage.findGenreByFilmId(film.getId())
                        .stream()
                        .map(Genre::getId)
                        .distinct()
                        .collect(Collectors.toList());
                List<Integer> genreListIdFromFilm = film.getGenres()
                        .stream()
                        .map(Genre::getId)
                        .distinct()
                        .collect(Collectors.toList());
                genreListIdFromFilm.forEach(genreIdFromFilm -> {
                    if (genreListIdFromDb.size() == 0 || !genreListIdFromDb.contains(genreIdFromFilm)) {
                        jdbcTemplate.update("insert into genre_link(film_id, genre_code) values (?, ?)"
                                , film.getId()
                                , genreIdFromFilm);
                    }
                });
                genreListIdFromDb.forEach(genreIdFromDb -> {
                    if (genreListIdFromFilm.size() == 0 || !genreListIdFromFilm.contains(genreIdFromDb)) {
                        jdbcTemplate.update("delete genre_link where film_id = ? and genre_code = ?"
                                , film.getId()
                                , genreIdFromDb);
                    }
                });
            } else if (genreDbStorage.findGenreByFilmId(film.getId()).size() > 0) {
                jdbcTemplate.update("delete genre_link where film_id = ?", film.getId());
            }
            log.info(String.format("Изменена информация о фильме %d.", film.getId()));
            return getFilmById(film.getId());
        }
        throw new FilmNotFoundException(String.format("Не существует фильм с заданным id %d.", film.getId()));
    }

    public List<Film> getFilms() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    public Film getFilmById(Long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where id = ?", id);
        if (!filmRows.next()) {
            throw new FilmNotFoundException(String.format("Не найден фильм с заданным id %d.", id));
        }
        List<Genre> genre = genreDbStorage.findGenreByFilmId(id);
        if (filmRows.getInt("mpa_code") > 0) {
            Mpa mpa  = mpaDbStorage.findPmaByCode(filmRows.getInt("mpa_code"));
            return Film.builder()
                    .id(id)
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .duration(filmRows.getInt("duration"))
                    .releaseDate(filmRows.getDate("release_date").toLocalDate())
                    .mpa(mpa)
                    .genres(genre)
                    .build();
        } else {
            return Film.builder()
                    .id(id)
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .duration(filmRows.getInt("duration"))
                    .releaseDate(filmRows.getDate("release_date").toLocalDate())
                    .genres(genre)
                    .build();
        }
    }

    public List<Film> getFilmsPopularList(int count) {
        String sql = "select f.* from films f left join " +
                "(select ll.film_id, count(ll.user_id) cnt from likes_link ll group by ll.film_id) l " +
                "on f.id = l.film_id " +
                "order by l.cnt desc " +
                "limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        List<Genre> genre = genreDbStorage.findGenreByFilmId(id);
        log.info("Фильм с id = {}", id);
        if (rs.getInt("mpa_code") > 0) {
            Mpa mpa  = mpaDbStorage.findPmaByCode(rs.getInt("mpa_code"));
            return Film.builder()
                    .id(id)
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .duration(rs.getInt("duration"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .mpa(mpa)
                    .genres(genre)
                    .build();
        } else {
            return Film.builder()
                    .id(id)
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .duration(rs.getInt("duration"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .genres(genre)
                    .build();
        }
    }
}