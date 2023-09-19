package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
@Slf4j
public class LikesDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = new UserDbStorage(jdbcTemplate);
        this.filmDbStorage = new FilmDbStorage(jdbcTemplate);
    }

    public void addLike(Film film, User user) {
        String sql = "insert into likes_link(user_id, film_id) values(?, ?)";
        jdbcTemplate.update(sql, user.getId(), film.getId());
        log.info(String.format("Добавлен новый лайк от пользователя %d фильму %d", user.getId(), film.getId()));
    }

    public void deleteLike(Film film, User user) {
        String sql = "delete likes_link where user_id = ? and film_id = ?";
        jdbcTemplate.update(sql, user.getId(), film.getId());
        log.info(String.format("Удален лайк от пользователя %d фильму %d", user.getId(), film.getId()));
    }

    public List<User> getLikesByFilmId(Film film) {
        return userDbStorage.getLikesByFilmId(film);
    }

    public List<Film> getFilmsPopularList(int count) {
        return filmDbStorage.getFilmsPopularList(count);
    }
}