package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DbLikesStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventStorage eventStorage;

    @Override
    public void addLike(Film film, User user) {
        String sql = "insert into likes_link(user_id, film_id) values(?, ?)";
        jdbcTemplate.update(sql, user.getId(), film.getId());
        eventStorage.addLike(film.getId(), film.getName(), user.getId(), user.getLogin());
        log.info(String.format("Добавлен новый лайк от пользователя %d фильму %d", user.getId(), film.getId()));
    }

    @Override
    public void deleteLike(Film film, User user) {
        String sql = "delete likes_link where user_id = ? and film_id = ?";
        jdbcTemplate.update(sql, user.getId(), film.getId());
        eventStorage.deleteLike(film.getId(), film.getName(), user.getId(), user.getLogin());
        log.info(String.format("Удален лайк от пользователя %d фильму %d", user.getId(), film.getId()));
    }

}
