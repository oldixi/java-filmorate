package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class DbUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    @Override
    public User add(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "insert into users (name, login, email, birthday) values (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        Set<Long> friends = user.getFriends();
        if (friends != null) {
            friends.forEach(friendId -> jdbcTemplate.update(
                    "insert into friend (user_id, friend_id) values (?, ?)",
                    keyHolder.getKey().longValue(),
                    friendId));
        }
        return user;
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update("update users set name = ?, login = ?, email = ?, birthday = ?" +
                        "where id = ?",
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                java.sql.Date.valueOf(user.getBirthday()),
                user.getId());
        jdbcTemplate.update("delete from friends where user_id = ?", user.getId());
        Set<Long> friends = user.getFriends();
        if (friends != null) {
            friends.forEach(friendId -> jdbcTemplate.update(
                    "insert into friend (user_id, friend_id) values (?, ?)",
                    friendId));
        }
        return user;
    }

    @Override
    public User delete(User user) {
        jdbcTemplate.update("delete from users where id = ? cascade", user.getId());
        return user;
    }

    @Override
    public User getById(Long userID) {
        if (!isPresent(userID)) {
            throw new WrongFilmIdException("No user with such id=" + userID);
        }
        String sqlQuery = "select id, name, login, email, birthday from users where id=?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapper, userID);
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query(
                "select id, name, login, email, birthday from users",
                this::mapper
        );
        return users;
    }

    @Override
    public boolean isPresent(Long userId) {
        return jdbcTemplate.queryForObject("select count(id) from users where id = ?",
                Integer.class,
                userId) != 0;
    }

    private User mapper(ResultSet resultSet, int rowNum) throws SQLException {

        Set<Long> friendIds = new HashSet<>(jdbcTemplate.query(
                "select friend_id from friends where user_id = ?",
                (resultSetLike, rowNumLike) -> resultSetLike.getLong(1),
                resultSet.getLong(1)
        ));

        return User.builder()
                .id(resultSet.getLong(1))
                .name(resultSet.getString(2))
                .login(resultSet.getString(3))
                .email(resultSet.getString(4))
                .birthday(resultSet.getDate(5).toLocalDate())
                .friends(friendIds)
                .build();
    }
}
