package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Repository
@Slf4j
public class FriendshipStatusDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendshipStatusDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public FriendshipStatus addFriendshipStatus(FriendshipStatus friendshipStatus) {
        jdbcTemplate.update("insert into friends_status_dic(status_code, status) values(?, ?)",
                friendshipStatus.getId(), friendshipStatus.getName());
        return friendshipStatus;
    }

    public FriendshipStatus updateFriendshipStatus(FriendshipStatus friendshipStatus) {
        jdbcTemplate.update("update friends_status_dic set status = ? where status_code = ?",
                friendshipStatus.getName(), friendshipStatus.getId());
        return friendshipStatus;
    }

    public Optional<FriendshipStatus> findFriendshipStatusByCode(int code) {
        SqlRowSet friendshipStatusDicRows = jdbcTemplate.queryForRowSet("select * from friends_status_dic where code = ?", code);
        if (friendshipStatusDicRows.next()) {
            log.info("Найден статус добавления в друзья: {} {}",
                    friendshipStatusDicRows.getInt("genre_code"),
                    friendshipStatusDicRows.getString("genre"));
            FriendshipStatus friendshipStatus = new FriendshipStatus(friendshipStatusDicRows.getInt("genre_code"),
                    friendshipStatusDicRows.getString("genre"));
            return Optional.of(friendshipStatus);
        } else {
            log.info("Термин статуса с кодом {} не найден.", code);
            return Optional.empty();
        }
    }

    public Collection<FriendshipStatus> findAllFriendshipStatus() {
        String sql = "select * from friends_status_dic";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFriendshipStatus(rs));
    }

    private FriendshipStatus makeFriendshipStatus(ResultSet rs) throws SQLException {
        int id = rs.getInt("status_code");
        String name = rs.getString("status");
        log.info("Статус добавления в друзья: {} имеет код: {}", name, id);

        return new FriendshipStatus(id, name);
    }
}
