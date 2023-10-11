package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {

    void addLike(long userId, long entityId);

    void deleteLike(long userId, long entityId);

    void addReview(long userId, long entityId);

    void deleteReview(long userId, long entityId);

    void updateReview(long userId, long entityId);

    void addFriendRequest(long userId, long entityId);

    void deleteFriendRequest(long userId, long entityId);

    void acceptFriendRequest(long userId, long entityId);

    List<Feed> getFeedList(long userId);

}
