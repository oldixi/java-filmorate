package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DbReviewLikeStorage implements ReviewLikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(long id, long userId) {
        jdbcTemplate.update(
                "insert into review_like (review_id, user_id, useful) values (?, ?, 1)",
                id,
                userId
        );
    }

    @Override
    public void addDislike(long id, long userId) {
        jdbcTemplate.update(
                "insert into review_like (review_id, user_id, useful) values (?, ?, -1)",
                id,
                userId
        );
    }

    @Override
    public void deleteLikeOrDislike(long id, long userId) {
        jdbcTemplate.update(
                "delete from review_like where review_id = ? and user_id = ?",
                id,
                userId
        );
    }

    @Override
    public int getUsability(long id) {
        Optional<Integer> useful = Optional.ofNullable(jdbcTemplate.queryForObject(
                "select sum(useful) from review_like where review_id = ?",
                Integer.class,
                id
        ));

        if (useful.isEmpty()) {
            throw new WrongFilmIdException("No review with such id found. id = " + id);
        }

        return useful.get();
    }
}
