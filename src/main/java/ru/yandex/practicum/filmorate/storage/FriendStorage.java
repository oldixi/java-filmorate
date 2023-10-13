package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface FriendStorage {
    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    void acceptFriendRequest(long userId, long friendId);
}
