package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendsDbStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final FriendsDbStorage friendsDbStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendsDbStorage friendsDbStorage) {
        this.userStorage = userStorage;
        this.friendsDbStorage = friendsDbStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(long userId, long friendId) {
        log.info(String.format("Добавление друга %d для пользователя %d.", friendId, userId));
        User requestUser = userStorage.getUserById(friendId);
        User acceptUser = userStorage.getUserById(userId);
        if (requestUser != null && acceptUser != null) {
            friendsDbStorage.addFriendRequest(requestUser, acceptUser);
            friendsDbStorage.acceptFriendRequest(requestUser, acceptUser);
        }
    }

    public void deleteFriend(long userId, long friendId) {
        log.info(String.format("Удаление друга %d для пользователя %d.", friendId, userId));
        User requestUser = userStorage.getUserById(friendId);
        User acceptUser = userStorage.getUserById(userId);
        if (requestUser != null && acceptUser != null) {
            friendsDbStorage.deleteFriend(requestUser, acceptUser);
        }
    }

    public List<User> getFriendsList(long userId) {
        return friendsDbStorage.getFriendsList(userId);
    }

    public List<User> getCommonFriendsList(long userId, long otherId) {
        return friendsDbStorage.getCommonFriendsList(userId, otherId);
    }


}
