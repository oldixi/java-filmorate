package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DbFeedStorage implements FeedStorage {
    private static String INSERT_SQL =
            "insert into events(timestamp, operation, event_type, user_id, entity_id) values(?, ?, ?, ?, ?)";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addReview(long userId, long entityId) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.ADD, Feed.EventType.REVIEW,
                userId, entityId);
    }

    @Override
    public void deleteReview(long userId, long entityId) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.REMOVE, Feed.EventType.REVIEW,
                userId, entityId);
    }

    @Override
    public void updateReview(long userId, long entityId) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.UPDATE, Feed.EventType.REVIEW,
                userId, entityId);
    }

    @Override
    public void addLike(long userId, long entityId) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.ADD, Feed.EventType.LIKE,
                userId, entityId);
    }

    @Override
    public void deleteLike(long userId, long entityId) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.REMOVE, Feed.EventType.LIKE,
                userId, entityId);
    }

    @Override
    public void addFriendRequest(long userId, long entityId) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.ADD, Feed.EventType.FRIEND,
                userId, entityId);
    }

    @Override
    public void deleteFriendRequest(long userId, long entityId) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.REMOVE, Feed.EventType.FRIEND,
                userId, entityId);
    }

    @Override
    public void acceptFriendRequest(long userId, long entityId) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.UPDATE, Feed.EventType.FRIEND,
                userId, entityId);
    }

    @Override
    public List<Feed> getFeedList(long userId, int count, Feed.Operation operation, Feed.EventType eventType) {
        String strOperation = operation.toString();
        String strEventType = eventType.toString();
        String sql = "select e.* " +
                "from events e " +
                "where e.user_id = ? " +
                "and e.operation = nvl(?, e.operation) " +
                "and e.event_type = nvl(?, e.event_type) " +
                "order by e.timestamp desc";
        if (count > 0) {
            sql += "limit ?";
        }
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapper(rs), userId, strOperation, strEventType, count);
    }

    private Feed mapper(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        Timestamp timestamp = resultSet.getTimestamp("timestamp");
        Feed.Operation operation = Feed.Operation.valueOf(resultSet.getString("operation"));
        Feed.EventType eventType = Feed.EventType.valueOf(resultSet.getString("event_type"));
        long userId = resultSet.getLong("user_id");
        long entityId = resultSet.getLong("entity_id");

        return Feed.builder()
                .eventId(id)
                .timestamp(timestamp)
                .operation(operation)
                .eventType(eventType)
                .userId(userId)
                .entityId(entityId)
                .build();
    }
}
