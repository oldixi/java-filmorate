package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private long uniqueId;
    private final UserStorage userStorage;

    @Autowired
    UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {

        changeNameToLogin(user);

        if (isNotValid(user)) {
            throw new ValidationException("Can't create new user. Check your data.");
        }

        user.setId(generateId());

        userStorage.add(user);
        return user;
    }

    public User update(User user) {
        changeNameToLogin(user);

        if (isNotValid(user)) {
            throw new ValidationException("Can't update user. Check your data.");
        }

        userStorage.update(user);
        return user;
    }

    public User delete(User user) {
        userStorage.delete(user);
        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.getById(userId).addFriend(friendId);
        userStorage.getById(friendId).addFriend(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.getById(userId).removeFriend(friendId);
        userStorage.getById(friendId).removeFriend(userId);
    }

    public List<User> findCommonFriends(Long userId, Long friendId) {
        Set<Long> friendIds = userStorage.getById(friendId).getFriends();
        return userStorage.getById(userId).getFriends().stream()
                .filter(friendIds::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    private boolean isNotValid(User user) {
        return user.getLogin().contains(" ")
                || user.getBirthday().isAfter(LocalDate.now());
    }

    private void changeNameToLogin(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private long generateId() {
        return ++uniqueId;
    }

}
