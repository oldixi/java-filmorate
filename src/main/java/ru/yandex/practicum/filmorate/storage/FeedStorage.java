package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {

    void addLike(long user_id, long entity_id);

    void deleteLike(long user_id, long entity_id);

    void addReview(long user_id, long entity_id);

    void deleteReview(long user_id, long entity_id);

    void updateReview(long user_id, long entity_id);

    void addFriendRequest(long user_id, long entity_id);

    void deleteFriendRequest(long user_id, long entity_id);

    void acceptFriendRequest(long user_id, long entity_id);

    List<Feed> getFeedList(long userId, int count, Feed.Operation operation, Feed.EventType eventType);

}
