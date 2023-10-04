package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DbMpaStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAll() {
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
    public Mpa getById(int id) {
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
}
