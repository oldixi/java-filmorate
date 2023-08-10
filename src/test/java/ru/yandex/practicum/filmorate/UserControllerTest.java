package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.controller.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {
    UserController userController = new UserController();
    ValidationException exception;

    @Test
    void shouldAddUserNullName() {
        User user = User.builder()
                .email("testFilmoRateUser1@gmail.com")
                .login("TU1")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        userController.addUser(user);

        assertEquals(1, userController.getUsers().size(), "Не совпадает количество добавленных пользователей.");
        assertTrue(userController.getUsers().contains(user), "Пользователь не добавился.");
        assertEquals("TU1", userController.getUsers().get(0).getName(), "Не совпадает имя с логином при незаданном имени.");
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
                        userController.addUser(user);
                    }
                });
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());}

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
                        userController.addUser(user);
                    }
                });
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
                            userController.addUser(user);
                        }
                    });
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.", exception.getMessage());
    }
}
