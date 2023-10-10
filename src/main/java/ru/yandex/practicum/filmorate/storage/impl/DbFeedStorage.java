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
            "insert into feeds(timestamp, operation, event_type, user_id, entity_id) values(?, ?, ?, ?, ?)";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addReview(long user_id, long entity_id) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.ADD, Feed.EventType.REVIEW,
                user_id, entity_id);
    }

    @Override
    public void deleteReview(long user_id, long entity_id) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.REMOVE, Feed.EventType.REVIEW,
                user_id, entity_id);
    }

    @Override
    public void updateReview(long user_id, long entity_id) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.UPDATE, Feed.EventType.REVIEW,
                user_id, entity_id);
    }

    @Override
    public void addLike(long user_id, long entity_id) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.ADD, Feed.EventType.LIKE,
                user_id, entity_id);
    }

    @Override
    public void deleteLike(long user_id, long entity_id) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.REMOVE, Feed.EventType.LIKE,
                user_id, entity_id);
    }

    @Override
    public void addFriendRequest(long user_id, long entity_id) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.ADD, Feed.EventType.FRIEND,
                user_id, entity_id);
    }

    @Override
    public void deleteFriendRequest(long user_id, long entity_id) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.REMOVE, Feed.EventType.FRIEND,
                user_id, entity_id);
    }

    @Override
    public void acceptFriendRequest(long user_id, long entity_id) {
        jdbcTemplate.update(INSERT_SQL, Timestamp.from(Instant.now()), Feed.Operation.UPDATE, Feed.EventType.FRIEND,
                user_id, entity_id);
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
        Timestamp timestamp = resultSet.getTimestamp("date");
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
