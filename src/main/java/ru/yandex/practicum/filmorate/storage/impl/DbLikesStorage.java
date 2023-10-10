package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DbLikesStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FeedStorage feedStorage;

    private Long getLikesId(Film film, User user) {
        return jdbcTemplate.queryForObject("select id from film_like where user_id = ? and film_id = ?",
                Long.class, user.getId(), film.getId());
    }

    @Override
    public void addLike(Film film, User user) {
        String sql = "insert into film_like(user_id, film_id) values(?, ?)";
        jdbcTemplate.update(sql, user.getId(), film.getId());
        feedStorage.addLike(user.getId(), getLikesId(film, user));
        log.info(String.format("Добавлен новый лайк от пользователя %d фильму %d", user.getId(), film.getId()));
    }

    @Override
    public void deleteLike(Film film, User user) {
        String sql = "delete likes_link where user_id = ? and film_id = ?";
        jdbcTemplate.update(sql, user.getId(), film.getId());
        feedStorage.deleteLike(user.getId(), getLikesId(film, user));
        log.info(String.format("Удален лайк от пользователя %d фильму %d", user.getId(), film.getId()));
    }

}
