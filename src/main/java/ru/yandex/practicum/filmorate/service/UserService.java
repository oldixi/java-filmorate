package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final FeedStorage feedStorage;

    public User create(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public void delete(Long userId) {
        userStorage.delete(userId);
    }

    public void addFriend(long userId, long friendId) {
        friendStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        friendStorage.deleteFriend(userId, friendId);
    }

    public void updateFriendRequest(long userId, long friendId) {
        friendStorage.acceptFriendRequest(userId, friendId);
    }

    public List<User> findCommonFriends(long userId, long otherId) {
        return userStorage.getCommonFriendsByUserId(userId, otherId);
    }

    public List<Feed> getEventsList(long userId) {
        return feedStorage.getFeedList(userId);
    }

    public User getById(long userId) {
        return userStorage.getById(userId);
    }

    public List<User> getFriends(long userId) {
        return friendStorage.getFriendsByUserId(userId).stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}
