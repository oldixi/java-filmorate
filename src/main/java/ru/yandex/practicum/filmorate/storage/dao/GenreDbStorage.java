package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre addGenre(Genre genre) {
        jdbcTemplate.update("insert into genre_dic(genre_code, genre) values(?, ?)", genre.getId(), genre.getName());
        return genre;
    }

    public Genre updateGenre(Genre genre) {
        jdbcTemplate.update("update genre_dic set genre = ? where genre_code = ?", genre.getName(), genre.getId());
        return genre;
    }

    public Genre findGenreByCode(int code) {
        SqlRowSet genreDicRows = jdbcTemplate.queryForRowSet("select * from genre_dic where genre_code = ?", code);
        if (genreDicRows.next()) {
            log.info("Найден жанр: {} {}", genreDicRows.getInt("genre_code"), genreDicRows.getString("genre"));
            return new Genre(genreDicRows.getInt("genre_code"), genreDicRows.getString("genre"));
        } else {
            log.info("Термин жанра с кодом {} не найден.", code);
            throw new GenreNotFoundException(String.format("Термин жанра с кодом %d не найден.", code));
        }
    }

    public List<Genre> findGenreByFilmId(long filmId) {
        String sql = "select gd.* from genre_link gl JOIN genre_dic gd ON gl.genre_code = gd.genre_code where gl.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
    }

    public List<Genre> findAllGenre() {
        String sql = "select * from genre_dic";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_code");
        String name = rs.getString("genre");
        log.info("Жанр: {} имеет код: {}", name, id);

        return new Genre(id, name);
    }
}
