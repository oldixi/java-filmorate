package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidPathVariableException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WrongFilmIdException;
import ru.yandex.practicum.filmorate.exception.WrongUserIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("New user added {}", user);
        return user;
    }

    public User update(User user) {

        changeNameToLogin(user);

        if (isNotValid(user)) {
            throw new ValidationException("Can't update user. Check your data.");
        }

        if (userStorage.isPresent(user.getId())) {
            return userStorage.update(user);
        }

        throw new WrongUserIdException("Can't find user to update.");
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User delete(User user) {
        userStorage.delete(user);
        return user;
    }

    public void addFriend(String userId, String friendId) {
        long parsedUserId = parsePathParam(userId);
        long parsedFriendId = parsePathParam(friendId);

        userStorage.getById(parsedUserId).addFriend(parsedFriendId);
        userStorage.getById(parsedFriendId).addFriend(parsedUserId);
    }

    public void deleteFriend(String userId, String friendId) {
        long parsedUserId = parsePathParam(userId);
        long parsedFriendId = parsePathParam(friendId);

        userStorage.getById(parsedUserId).removeFriend(parsedFriendId);
        userStorage.getById(parsedFriendId).removeFriend(parsedUserId);
    }

    public List<User> findCommonFriends(String userId, String otherId) {
        long parsedUserId = parsePathParam(userId);
        long parsedOtherId = parsePathParam(otherId);

        Set<Long> friendIds = userStorage.getById(parsedOtherId).getFriends();
        return userStorage.getById(parsedUserId).getFriends().stream()
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
            log.info("Changed user name to user login");
            user.setName(user.getLogin());
        }
    }

    private long generateId() {
        return ++uniqueId;
    }

    public User getById(String userId) {
        long parsedUserId = parsePathParam(userId);

        if (userStorage.isPresent(parsedUserId)) {
            return userStorage.getById(parsedUserId);
        }

        log.warn("Requested non-existent user. Id {}", userId);
        throw new WrongUserIdException("User with such id doesn't exist.");
    }

    public List<User> getFriends(String userId) {
        long parsedUserId = parsePathParam(userId);
        if (userStorage.isPresent(parsedUserId)) {
            return userStorage.getById(parsedUserId).getFriends().stream()
                    .map(userStorage::getById)
                    .collect(Collectors.toList());
        }

        log.warn("Requested non-existent user. Id {}", userId);
        throw new WrongUserIdException("User with such id doesn't exist.");
    }

    private Long parsePathParam(String pathId) {
        long pathVariable;
        try {
            pathVariable = Long.parseLong(pathId);
        } catch (NumberFormatException e) {
            log.warn("Parser. Path variable has wrong format {}", pathId);
            throw new InvalidPathVariableException("Incorrect user id parameter format.");
        }
        if (pathVariable < 0) {
            log.warn("Parser. Requested user with wrong id {}", pathVariable);
            throw new WrongFilmIdException("User with such id doesn't exist.");
        }

        return pathVariable;
    }
}
