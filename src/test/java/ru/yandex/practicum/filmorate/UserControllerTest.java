package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {
    UserStorage userStorage = new InMemoryUserStorage();
    ValidationException exception;

    @Test
    void shouldAddUserNullName() {
        User user = User.builder()
                .email("testFilmoRateUser1@gmail.com")
                .login("TU1")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        userStorage.addUser(user);

        assertEquals(1, userStorage.getUsers().size(), "Не совпадает количество добавленных пользователей.");
        assertTrue(userStorage.getUsers().contains(user), "Пользователь не добавился.");
        assertEquals("TU1", userStorage.getUsers().get(0).getName(), "Не совпадает имя с логином при незаданном имени.");
    }

    @Test
    void shouldNotAddUserAfterNowBirthday() {
        User user = User.builder()
                .email("testFilmoRateUser1@gmail.com")
                .name("TestUser1")
                .login("TU1")
                .birthday(LocalDate.now().plusYears(1))
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userStorage.addUser(user);
                    }
                }
                );
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }

    @Test
    void shouldNotAddUserNullLogin() {
        User user = User.builder()
                .email("testFilmoRateUser1@gmail.com")
                .login("")
                .name("TestUser1")
                .birthday(LocalDate.now().minusYears(20))
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userStorage.addUser(user);
                    }
                }
                );
        assertEquals("Логин не может быть пустым и не должен содержать пробелы.", exception.getMessage());
}

    @Test
    void shouldNotAddUserBlancEmail() {
        User user = User.builder()
                .email("")
                .login("TU1")
                .name("TestUser1")
                .birthday(LocalDate.now().minusYears(20))
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                    new Executable() {
                        @Override
                        public void execute() {
                            userStorage.addUser(user);
                        }
                    }
                    );
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.", exception.getMessage());
    }
}