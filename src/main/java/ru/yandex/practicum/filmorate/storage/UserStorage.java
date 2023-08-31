package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    User add(User user);

    User update(User user);

    User delete(User user);

    User getById(Long userID);
}
