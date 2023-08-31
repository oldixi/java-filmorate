package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

@Component
public class InMemoryUserStorage implements UserStorage{
    private final HashMap<Long, User> users = new HashMap<>();
    @Override
    public User add(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.replace(user.getId(), user);
        return user;
    }

    @Override
    public User delete(User user) {
        users.remove(user.getId());
        return user;
    }

    @Override
    public User getById(Long userId) {
        return users.get(userId);
    }
}
