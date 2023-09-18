package ru.yandex.practicum.filmorate.storage.dao;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component("friendsDbStorage")
@Slf4j
@Data
public class FriendsDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = new UserDbStorage(jdbcTemplate);
    }

    public void addFriendRequest(User requestUser, User acceptUser) {
        String sql = "insert into friends_link(request_user_id, accept_user_id, status_code) values(?, ?, 1)";
        jdbcTemplate.update(sql, requestUser.getId(), acceptUser.getId());
        log.info(String.format("Добавлен запрос в друзья от пользователя %d пользователю %d",
                requestUser.getId(),
                acceptUser.getId()));
    }

    public int getFriendRequestStatus(User requestUser, User acceptUser) {
        String sql = "select status_code from friends_link where request_user_id = ? and accept_user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, requestUser.getId(), acceptUser.getId());
        if (!userRows.next()) {
            throw new UserNotFoundException(String.format(
                    "Не найдено запроса в друзья от пользователя %d к пользователю %d.",
                    requestUser.getId(), acceptUser.getId()));
        }
        return userRows.getInt("status_code");
    }

    public void acceptFriendRequest(User requestUser, User acceptUser) {
        String sql = "update friends_link set status_code = 2 where request_user_id = ? and accept_user_id = ?";
        jdbcTemplate.update(sql, requestUser.getId(), acceptUser.getId());
        log.info(String.format("Принят запрос в друзья от пользователя %d пользователю %d",
                requestUser.getId(), acceptUser.getId()));
    }

    public void deleteFriend(User requestUser, User acceptUser) {
        String sql = "delete friends_link where request_user_id = ? and accept_user_id = ?";
        jdbcTemplate.update(sql, requestUser.getId(), acceptUser.getId());
        log.info(String.format("Пользователь %d удален из друзей пользователя %d",
                requestUser.getId(), acceptUser.getId()));
    }

    public List<User> getFriendsList(long userId) {
        log.info(String.format("Поиск друзей у пользователя %d", userId));
        return userDbStorage.getFriendsByUserId(userId);
    }

    public List<User> getCommonFriendsList(long userId, long otherId)  {
        return userDbStorage.getCommonFriendsByUsersIds(userId, otherId);
    }
}
