package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public interface LikeStorage {

    void addLike(Film film, User user);

    void deleteLike(Film film, User user);

/*    public List<User> getLikesByFilmId(Film film) {
        return userDbStorage.getLikesByFilmId(film);
    }

    public List<Film> getFilmsPopularList(int count) {
        return filmDbStorage.getFilmsPopularList(count);
    }*/
}
