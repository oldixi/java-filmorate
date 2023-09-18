package ru.yandex.practicum.filmorate.storage.dao;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Component("userDbStorage")
@Slf4j
@Data
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private boolean isValid(User user) {
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return true;
    }

    public User addUser(User user) {
        if (isValid(user)) {
            String sqlInsertUser = "insert into users(name, login, email, birthday) " +
                    "values(?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sqlInsertUser, new String[] { "id" });
                ps.setString(1, user.getName());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getEmail());
                ps.setDate(4, Date.valueOf(user.getBirthday()));
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                user.setId(keyHolder.getKey().longValue());
                log.info(String.format("Добавлен новый пользователь %d", user.getId()));
            }
            return user;
        }
        throw new ValidationException("Пользователь не прошел валидацию.");
    }

    public User updateUser(User user) {
        if (user.getId() != 0 && isValid(user) && getUserById(user.getId()) != null) {
            String sql = "update users set name = ?, login = ?, email = ?, birthday = ? " +
                    "where id = ?";
            jdbcTemplate.update(sql,
                    user.getName(),
                    user.getLogin(),
                    user.getEmail(),
                    user.getBirthday(),
                    user.getId());
            log.info(String.format("Изменен пользователь %d", user.getId()));
            return user;
        }
        throw new UserNotFoundException(String.format("Не существует пользователя с заданным id %d.", user.getId()));
    }

    public List<User> getUsers() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    public User getUserById(Long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
        if (!userRows.next()) {
            throw new UserNotFoundException(String.format("Не найден пользователь с заданным id %d.", id));
        }

        return User.builder()
                .id(id)
                .name(userRows.getString("name"))
                .birthday(userRows.getDate("birthday").toLocalDate())
                .login(userRows.getString("login"))
                .email(userRows.getString("email"))
                .build();
    }

    public List<User> getLikesByFilmId(Film film) {
        String sql = "select u.* from likes_link ll join users u on ll.user_id = u.id where ll.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), film.getId());
    }

    public List<User> getFriendsByUserId(long userId) {
        String sql = "select u.* from friends_link fl join users u on fl.request_user_id = u.id " +
                "where fl.accept_user_id = ? and fl.status_code = 2";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId);
    }

    public List<User> getCommonFriendsByUsersIds(long userId, long otherId) {
        String sql = "select u.* " +
                "from friends_link fl1 join friends_link fl2 on fl1.request_user_id = fl2.request_user_id " +
                "join users u on fl2.request_user_id = u.id " +
                "where fl1.accept_user_id = ? and fl2.accept_user_id = ?" +
                "and fl1.status_code = 2 and fl2.status_code = 2";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId, otherId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String login = rs.getString("login");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        String email = rs.getString("email");
        log.info("Пользователь с id = {}", id);

        return User.builder()
                .id(id)
                .name(name)
                .birthday(birthday)
                .login(login)
                .email(email)
                .build();
    }
}