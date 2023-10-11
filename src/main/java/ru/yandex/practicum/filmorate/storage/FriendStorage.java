package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

public interface FriendStorage {
    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    void acceptFriendRequest(long userId, long friendId);

    Set<Long> getFriendsByUserId(long id);
}
