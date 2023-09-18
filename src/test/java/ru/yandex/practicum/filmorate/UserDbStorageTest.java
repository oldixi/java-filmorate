package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;
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

    @Test
    public void testAddUser() {
        User user = userStorage.addUser(user1);

        assertNotNull(user);
        assertEquals(user.getName(), user1.getName());
    }

    @Test
    public void testUpdateUser() {
        User user = userStorage.updateUser(updateUser1);

        assertNotNull(user);
        assertEquals(user.getName(), updateUser1.getName());
    }

    @Test
    public void testFindUserById() {
        User user = userStorage.getUserById(1L);

        assertNotNull(user);
        assertEquals(user.getId(), 1);
    }

    @Test
    public void testFindAllUsers() {
        userStorage.addUser(user2);
        List<User> userList = userStorage.getUsers();

        assertNotNull(userList);
        assertEquals(userList.size(), 2);
    }
}
