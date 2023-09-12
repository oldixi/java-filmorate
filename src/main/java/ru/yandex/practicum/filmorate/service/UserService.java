package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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
        log.info(String.format("Добавление друга %d для пользователя %d", friendId, userId));
        Set<Long> friendsOfUser = new HashSet<>();
        Set<Long> friendsOfFriend = new HashSet<>();
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriends() != null) {
            friendsOfUser = user.getFriends();
        }
        if (friend.getFriends() != null) {
            friendsOfFriend = friend.getFriends();
        }
        friendsOfUser.add(friendId);
        friendsOfFriend.add(userId);
        user.setFriends(friendsOfUser);
        friend.setFriends(friendsOfFriend);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void deleteFriend(long userId, long friendId) {
        log.info(String.format("Удаление друга %d для пользователя %d", friendId, userId));
        Set<Long> friendsOfUser = new HashSet<>();
        Set<Long> friendsOfFriend = new HashSet<>();
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriends() != null) {
            friendsOfUser = user.getFriends();
        }
        if (friend.getFriends() != null) {
            friendsOfFriend = friend.getFriends();
        }
        if (friendsOfUser.contains(friendId)) {
            friendsOfUser.remove(friendId);
        }
        if (friendsOfFriend.contains(userId)) {
            friendsOfFriend.remove(userId);
        }
        user.setFriends(friendsOfUser);
        friend.setFriends(friendsOfFriend);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public List<User> getFriendsList(long userId) {
        List<User> friendsList = new ArrayList<>();
        log.info(String.format("Список друзей для пользователя %d.", userId));
        Set<Long> friendsOfUserId = userStorage.getUserById(userId).getFriends();
        if (friendsOfUserId != null) {
            for (Long friendId : friendsOfUserId) {
                friendsList.add(userStorage.getUserById(friendId));
            }
        }
        return friendsList;
    }

    public List<User> getCommonFriendsList(long userId, long otherId) {
        final List<User> friendsCommonList = new ArrayList<>();
        log.info(String.format("Список общих друзей для пользователя %d и %d", userId, otherId));
        Set<Long> friendsOfUserId = userStorage.getUserById(userId).getFriends();
        Set<Long> friendsOfFriendId = userStorage.getUserById(otherId).getFriends();
        if (friendsOfUserId == null || friendsOfFriendId == null) {
            return friendsCommonList;
        }
        friendsOfUserId
                .stream()
                .filter(friendsOfFriendId::contains)
                .forEach(friend -> friendsCommonList.add(userStorage.getUserById(friend)));
        return friendsCommonList;
    }
}
