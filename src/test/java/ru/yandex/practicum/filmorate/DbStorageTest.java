package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbStorageTest {
    private final FilmDbStorage filmStorage;
    private final FriendsDbStorage friendsStorage;
    private final UserDbStorage userStorage;
    private final LikesDbStorage likesStorage;

    Mpa mpa1 = new Mpa(1, "G");
    Mpa mpa2 = new Mpa(4, "R");
    Genre genre1 = new Genre(1, "Комедия");

    User user1 = User.builder()
            .name("dolore")
            .login("NickName")
            .email("mail@mail.ru")
            .birthday(LocalDate.parse("20.08.1946", Constants.FORMATTER))
            .build();
    User updateUser1 = User.builder()
            .id(1)
            .name("Newdolore")
            .login("NickName")
            .email("mail@mail.ru")
            .birthday(LocalDate.parse("20.08.1946", Constants.FORMATTER))
            .build();
    User user2 = User.builder()
            .id(1)
            .name("Newdolore2")
            .login("NickName2")
            .email("mail2@mail.ru")
            .birthday(LocalDate.parse("20.08.1955", Constants.FORMATTER))
            .build();
    User user3 = User.builder()
            .name("Newdolore3")
            .login("NickName3")
            .email("mail3@mail.ru")
            .birthday(LocalDate.parse("20.08.1965", Constants.FORMATTER))
            .build();

    Film film1 = Film.builder()
            .name("nisi eiusmod")
            .description("adipisicing")
            .releaseDate(LocalDate.parse("25.03.1967", Constants.FORMATTER))
            .duration(100)
            .mpa(mpa1)
            .build();
    Film updateFilm1 = Film.builder()
            .id(1)
            .name("nisi eiusmod update")
            .description("adipisicing")
            .releaseDate(LocalDate.parse("25.03.1967", Constants.FORMATTER))
            .duration(100)
            .mpa(mpa1)
            .build();
    Film film2 = Film.builder()
            .name("New film")
            .description("New film about friends")
            .releaseDate(LocalDate.parse("30.04.1999", Constants.FORMATTER))
            .duration(120)
            .mpa(mpa2)
            .genres(List.of(genre1))
            .build();

    @Test
    public void addTest() {
        Film film = filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        film1.setId(1);
        film2.setId(2);

        assertNotNull(film);
        assertEquals(1, film.getId());

        Film filmId1 = filmStorage.getFilmById(1L);

        assertNotNull(filmId1);
        assertEquals(1, filmId1.getId());

        List<Film> filmList = filmStorage.getFilms();

        assertNotNull(filmList);
        assertEquals(2, filmList.size());

        User user = userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        user1.setId(1);
        user2.setId(2);
        user3.setId(3);

        assertNotNull(user);
        assertEquals(user.getName(), user1.getName());

        User userUpdate = userStorage.updateUser(updateUser1);

        assertNotNull(userUpdate);
        assertEquals(userUpdate.getName(), updateUser1.getName());

        User userId1 = userStorage.getUserById(1L);

        assertNotNull(userId1);
        assertEquals(userId1.getId(), 1);

        List<User> userListAll = userStorage.getUsers();

        assertNotNull(userListAll);
        assertEquals(userListAll.size(), 3);

        friendsStorage.addFriendRequest(user1, user2);
        int statusCode = friendsStorage.getFriendRequestStatus(user1, user2);
        assertEquals(1, statusCode);

        friendsStorage.acceptFriendRequest(user1, user2);
        statusCode = friendsStorage.getFriendRequestStatus(user1, user2);
        assertEquals(2, statusCode);

        List<User> userList = friendsStorage.getFriendsList(2);

        assertNotNull(userList);
        assertEquals(1, userList.size());

        friendsStorage.addFriendRequest(user1, user3);
        friendsStorage.acceptFriendRequest(user1, user3);
        friendsStorage.deleteFriend(user1, user3);

        assertThrows(UserNotFoundException.class, () -> friendsStorage.getFriendRequestStatus(user1, user3));

        friendsStorage.addFriendRequest(user2, user1);
        friendsStorage.acceptFriendRequest(user2, user1);
        friendsStorage.addFriendRequest(user2, user3);
        friendsStorage.acceptFriendRequest(user2, user3);
        friendsStorage.addFriendRequest(user3, user1);
        friendsStorage.acceptFriendRequest(user3, user1);
        List<User> userListNew = friendsStorage.getCommonFriendsList(user1.getId(), user3.getId());

        assertNotNull(userListNew);
        assertEquals(1, userListNew.size());

        likesStorage.addLike(film1, user1);
        likesStorage.addLike(film1, user2);
        List<User> userListLike = likesStorage.getLikesByFilmId(film1);

        assertNotNull(userListLike);
        assertEquals(2, userListLike.size());

        likesStorage.deleteLike(film1, user1);
        List<User> userListLikeAfterDel = likesStorage.getLikesByFilmId(film1);

        assertNotNull(userListLikeAfterDel);
        assertEquals(1, userListLikeAfterDel.size());

        likesStorage.addLike(film2, user1);
        likesStorage.addLike(film2, user2);
        likesStorage.addLike(film2, user3);
        List<Film> filmPopularList = likesStorage.getFilmsPopularList(1);

        assertNotNull(filmPopularList);
        assertEquals(1, filmPopularList.size());
    }
}
