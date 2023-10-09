package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {

    void addFilm(long film_id, String name);

    void updateFilm(long film_id, String name);

    void deleteFilm(long film_id, String name);

    void addUser(long user_id, String login);

    void updateUser(long user_id, String login);

    void deleteUser(long user_id, String login);

    void addLike(long film_id, String name, long user_id, String login);

    void deleteLike(long film_id, String name, long user_id, String login);

    void addFeedback(long film_id, String name, long user_id, String login);

    void deleteFeedback(long film_id, String name, long user_id, String login);

    void updateFeedback(long film_id, String name, long user_id, String login);

    void addFriendRequest(long friend_id, String friend_login, long user_id, String login);

    void deleteFriendRequest(long friend_id, String friend_login, long user_id, String login);

    void acceptFriendRequest(long friend_id, String friend_login, long user_id, String login);

    void addRecommendation(long film_id, String name, long friend_id, String friend_login, long user_id, String login);

    void deleteRecommendation(long film_id, String name,  long friend_id, String friend_login, long user_id, String login);

    List<Event> getEventsList(int cnt, Event.Operation operation, Event.Object object);

}
