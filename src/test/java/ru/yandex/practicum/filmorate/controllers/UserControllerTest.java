package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

public class UserControllerTest {

    private static UserController controller;
    private static User user;

    @BeforeAll
    public static void createController() {
        InMemoryUserStorage storage = new InMemoryUserStorage();
        controller = new UserController(new UserService(storage));
    }

    @BeforeEach
    public void createNormalUser() {
        user = new User();
        user.setEmail("just@normal.email");
        user.setLogin("John");
        user.setName("Mike");
        user.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void shouldNotThrowsAnyThenItIsNormalUser() {
        Assertions.assertDoesNotThrow(() -> controller.addUser(user));
    }

    @Test
    public void shouldThrowAnExceptionWhenUsersEmailIsEmpty() {
        user.setEmail("");

        Assertions.assertThrows(ValidationException.class, () -> controller.addUser(user));
    }

    @Test
    public void shouldThrowAnExceptionWhenUsersEmailDoesNotContainsAt() {
        user.setEmail("email.but.no.at.symbol");

        Assertions.assertThrows(ValidationException.class, () -> controller.addUser(user));
    }

    @Test
    public void shouldThrowAnExceptionWhenUserLoginIsEmpty() {
        user.setLogin("");

        Assertions.assertThrows(ValidationException.class, () -> controller.addUser(user));
    }

    @Test
    public void shouldThrowAnExceptionWhenUserLoginContainsSpace() {
        user.setLogin("not normal login with spaces");

        Assertions.assertThrows(ValidationException.class, () -> controller.addUser(user));
    }

    @Test
    public void shouldSetUsersNameAsUsersLogin() {
        user.setName("");
        controller.addUser(user);

        Assertions.assertEquals("John", user.getName());
    }

    @Test
    public void shouldNotChangeUsersName() {
        user.setName("Mike");
        controller.addUser(user);

        Assertions.assertEquals("Mike", user.getName());
    }

}
