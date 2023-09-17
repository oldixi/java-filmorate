package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WrongUserIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User user) {

        changeNameToLogin(user);

        if (isNotValid(user)) {
            throw new ValidationException("Can't create new user. Check your data.");
        }

        userStorage.add(user);
        log.info("New user added {}", user);
        return user;
    }

    public User update(User user) {

        changeNameToLogin(user);

        if (isNotValid(user)) {
            throw new ValidationException("Can't update user. Check your data.");
        }

        if (!userStorage.isPresent(user.getId())) {
            throw new WrongUserIdException("Can't find user to update.");
        }

        return userStorage.update(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User delete(User user) {
        userStorage.delete(user);
        return user;
    }

    public void addFriend(long userId, long friendId) {
        if (isIncorrectId(userId) || isIncorrectId(friendId)) {
            throw new WrongUserIdException("Param must be more then 0");
        }

//        Set<Long> incomingRequests = userStorage.getById(userId).getIncomingFriendRequest();
//
//        if (incomingRequests.contains(friendId)) {
//            userStorage.getById(userId).acceptFriendship(friendId);
//            userStorage.getById(friendId).getFriendConformation(userId);
//        } else {
//            userStorage.getById(userId).sendFriendRequest(friendId);
//            userStorage.getById(friendId).getFriendRequest(userId);
//        }

        userStorage.getById(userId).addFriend(friendId);
        userStorage.getById(friendId).addFriend(userId);
    }

    public void deleteFriend(long userId, long friendId) {
        if (isIncorrectId(userId) || isIncorrectId(friendId)) {
            throw new WrongUserIdException("Param must be more then 0");
        }

        userStorage.getById(userId).removeFriend(friendId);
        userStorage.getById(friendId).removeFriend(userId);
    }

    public List<User> findCommonFriends(long userId, long otherId) {
        if (isIncorrectId(userId) || isIncorrectId(otherId)) {
            throw new WrongUserIdException("Param must be more then 0");
        }

        Set<Long> friendIds = userStorage.getById(otherId).getFriends();
        return userStorage.getById(userId).getFriends().stream()
                .filter(friendIds::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    private boolean isNotValid(User user) {
        return user.getLogin().contains(" ")
                || user.getBirthday().isAfter(LocalDate.now());
    }

    public User getById(long userId) {
        if (isIncorrectId(userId)) {
            throw new WrongUserIdException("Param must be more then 0");
        }

        if (!userStorage.isPresent(userId)) {
            throw new WrongUserIdException("User with such id doesn't exist.");
        }

        return userStorage.getById(userId);
    }

    public List<User> getFriends(long userId) {
        if (isIncorrectId(userId)) {
            throw new WrongUserIdException("Param must be more then 0");
        }

        if (!userStorage.isPresent(userId)) {
            throw new WrongUserIdException("User with such id doesn't exist.");
        }

        return userStorage.getById(userId).getFriends().stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    private void changeNameToLogin(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("Changed user name to user login");
            user.setName(user.getLogin());
        }
    }

    private boolean isIncorrectId(long id) {
        return id <= 0;
    }

}
