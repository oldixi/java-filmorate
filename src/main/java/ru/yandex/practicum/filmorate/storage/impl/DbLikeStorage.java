package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class DbLikeStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(long userId, long filmId) {
        jdbcTemplate.update("insert into film_like (film_id, user_id) values (?, ?)",
                filmId,
                userId);
    }

    @Override
    public void deleteLike(long userId, long filmId) {
        jdbcTemplate.update("delete from film_like where film_id = ? and user_id = ?",
                filmId,
                userId);
    }

    @Override
    public Set<Long> getLikesByFilmId(Long filmId) {

        return new HashSet<>(jdbcTemplate.query(
                "select user_id from film_like where film_id = ?",
                (resultSetLike, rowNumLike) -> resultSetLike.getLong("film_like.user_id"),
                filmId));
    }
}
