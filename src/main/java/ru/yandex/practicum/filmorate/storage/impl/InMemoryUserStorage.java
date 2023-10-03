package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashMap;
import java.util.List;

//@Component
public class InMemoryUserStorage implements UserStorage {
    private long uniqueId;
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        user.setId(generateId());
        users.put(uniqueId, user);
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

    @Override
    public List<User> getAll() {
        return List.copyOf(users.values());
    }

    @Override
    public boolean isPresent(Long userId) {
        return users.containsKey(userId);
    }

    private long generateId() {
        return ++uniqueId;
    }

}
