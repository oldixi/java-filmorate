package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface LikeStorage {
    void addLike(long userId, long filmId);

    void deleteLike(long userId, long filmId);

    Set<Long> getLikesByFilmId(Long filmId);

    Set<Long> getLikesByUserId(Long userId);
}
