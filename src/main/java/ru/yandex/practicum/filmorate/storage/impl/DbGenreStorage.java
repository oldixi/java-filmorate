package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

@Repository
@RequiredArgsConstructor
public class DbGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    @Override
    public Genre getById(int id) {
        return jdbcTemplate.queryForObject(
                "select id, name from genres where id=?",
                (resultSet, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(resultSet.getInt(1));
                    genre.setName(resultSet.getString(2));
                    return genre;
                }, id
        );
    }
}
