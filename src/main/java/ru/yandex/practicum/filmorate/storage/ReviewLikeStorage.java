package ru.yandex.practicum.filmorate.storage;

public interface ReviewLikeStorage {
    void addLike(long id, long userId);

    void addDislike(long id, long userId);

    void deleteLikeOrDislike(long id, long userId);

    int getUsability(long id);
}
