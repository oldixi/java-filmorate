package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User add(User user);

    User update(User user);

    User delete(User user);

    User getById(Long userID);

    List<User> getAll();

    List<User> getCommonFriendsByUsersIds(long userId, long otherId);

    void acceptFriendRequest(User requestUser, User acceptUser);

}
