package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikesDbStorageTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final LikesDbStorage likesStorage;
    User user1 = User.builder()
            .name("dolore")
            .login("NickName")
            .email("mail@mail.ru")
            .birthday(LocalDate.parse("20.08.1946", Constants.FORMATTER))
            .build();
    User user2 = User.builder()
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
            .build();
    Film film2 = Film.builder()
            .name("New film")
            .description("New film about friends")
            .releaseDate(LocalDate.parse("30.04.1999", Constants.FORMATTER))
            .duration(120)
            .build();

    @Test
    public void testAddLike() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        user1.setId(1);
        user2.setId(2);
        user3.setId(3);
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        film1.setId(1);
        film2.setId(2);
        likesStorage.addLike(film1, user1);
        likesStorage.addLike(film1, user2);
        List<User> userList = likesStorage.getLikesByFilmId(film1);

        assertNotNull(userList);
        assertEquals(2, userList.size());

        likesStorage.deleteLike(film1, user1);
        userList = likesStorage.getLikesByFilmId(film1);

        assertNotNull(userList);
        assertEquals(1, userList.size());

        likesStorage.addLike(film2, user1);
        likesStorage.addLike(film2, user2);
        likesStorage.addLike(film2, user3);
        List<Film> filmList = likesStorage.getFilmsPopularList(1);

        assertNotNull(userList);
        assertEquals(1, userList.size());
    }
}
