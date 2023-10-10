package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WrongUserIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private static final int DEFAULT_VALUE_FOR_TOP_FILMS = 10;
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final LikeStorage likeStorage;
    private final FilmStorage filmStorage;

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

        friendStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        if (isIncorrectId(userId) || isIncorrectId(friendId)) {
            throw new WrongUserIdException("Param must be more then 0");
        }

        friendStorage.deleteFriend(userId, friendId);
    }

    public List<User> findCommonFriends(long userId, long otherId) {
        if (isIncorrectId(userId) || isIncorrectId(otherId)) {
            throw new WrongUserIdException("Param must be more then 0");
        }

        return userStorage.getCommonFriendsByUserId(userId, otherId);
    }

    public User getById(long userId) {
        if (isIncorrectId(userId)) {
            throw new WrongUserIdException("Param must be more then 0");
        }

        return userStorage.getById(userId);
    }

    public List<User> getFriends(long userId) {
        if (isIncorrectId(userId)) {
            throw new WrongUserIdException("Param must be more then 0");
        }

        return friendStorage.getFriendsByUserId(userId).stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public List<Film> getRecommendations(long id) {
        User user = userStorage.getById(id);
        Set<Long> likedFilms = likeStorage.getLikesByUserId(id);
        int maxSize = 0;
        List<User> commonUsers = new ArrayList<>();
        if (likedFilms.isEmpty()) {
            return filmStorage.getPopular(DEFAULT_VALUE_FOR_TOP_FILMS);
        }
        for (User anotherUser : userStorage.getAll()) {
            int filmSize = getCommonFilmLikes(user, anotherUser).size();
            if (filmSize > maxSize && !user.equals(anotherUser)) {
                maxSize = filmSize;
                commonUsers.add(user);
            }
        }
        int finalMaxSize = maxSize;
        List<User> sortedCommonUsers = commonUsers.stream()
                .filter(u -> getCommonFilmLikes(user, u).size() == finalMaxSize).collect(Collectors.toList());
        List<Film> recommendedFilms = new ArrayList<>();
        for (User u : sortedCommonUsers) {
            for (long filmId : likeStorage.getLikesByUserId(u.getId())) {
                if (!likedFilms.contains(filmId)) {
                    recommendedFilms.add(filmStorage.getById(filmId));
                }
            }
        }
        return recommendedFilms;
    }

    private Set<Long> getCommonFilmLikes(User user, User anotherUser) {
        Set<Long> likedFilms = likeStorage.getLikesByUserId(user.getId());
        likedFilms.retainAll(likeStorage.getLikesByUserId(anotherUser.getId()));
        return likedFilms;
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

    private boolean isIncorrectId(long id) {
        return id <= 0;
    }

}
