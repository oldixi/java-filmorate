package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dao.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipStatusDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendsDbStorageTest {
    private final UserDbStorage userStorage;
    private final FriendsDbStorage friendsStorage;
    private final FriendshipStatusDbStorage friendshipStatusStorage;
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
            .mpa(new Mpa(1,"G"))
            .build();
    Film film2 = Film.builder()
            .name("New film")
            .description("New film about friends")
            .releaseDate(LocalDate.parse("30.04.1999", Constants.FORMATTER))
            .duration(120)
            .mpa(new Mpa(4,"R"))
            .genres(List.of(new Genre(1, "Комедия")))
            .build();

    @Test
    public void testAddFriendRequest() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        user1.setId(1);
        user2.setId(2);
        user3.setId(3);
        friendsStorage.addFriendRequest(user1, user2);
        int statusCode = friendsStorage.getFriendRequestStatus(user1, user2);
        assertEquals(1, statusCode);

        friendsStorage.acceptFriendRequest(user1, user2);
        statusCode = friendsStorage.getFriendRequestStatus(user1, user2);
        assertEquals(2, statusCode);

        List<User> userList = friendsStorage.getFriendsList(2);

        assertNotNull(userList);
        assertEquals(1, userList.size());
    }

    @Test
    public void testDeleteFriend() {
        userStorage.addUser(user1);
        userStorage.addUser(user3);
        user1.setId(1);
        user2.setId(3);
        friendsStorage.addFriendRequest(user1, user3);
        friendsStorage.acceptFriendRequest(user1, user3);
        friendsStorage.deleteFriend(user1, user3);
        assertThrows(UserNotFoundException.class, () -> friendsStorage.getFriendRequestStatus(user1, user3));
    }

    @Test
    public void testGetCommonFriendsList() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        user1.setId(1);
        user2.setId(2);
        user3.setId(3);
        friendsStorage.addFriendRequest(user2, user1);
        friendsStorage.acceptFriendRequest(user2, user1);
        friendsStorage.addFriendRequest(user2, user3);
        friendsStorage.acceptFriendRequest(user2, user3);
        friendsStorage.addFriendRequest(user3, user1);
        friendsStorage.acceptFriendRequest(user3, user1);
        List<User> userList = friendsStorage.getCommonFriendsList(user1.getId(), user3.getId());

        assertNotNull(userList);
        assertEquals(1, userList.size());
    }
}
