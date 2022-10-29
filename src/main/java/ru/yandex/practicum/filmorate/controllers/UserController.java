package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Set<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/users/{id}/friends")
    public Set<User> getUsersFriends(@PathVariable long id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        validateUser(user);
        return userService.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        validateUser(user);
        return userService.updateUser(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.removeFriend(id, friendId);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            failValidation(user, "Не указан адрес электронной почты.");
        }
        if (!user.getEmail().contains("@")) {
            failValidation(user, "В адресе электронной почты не содержится символ '@'.");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty()) {
            failValidation(user, "Не указано имя пользователя.");
        }
        if (user.getLogin().contains(" ")) {
            failValidation(user, "Имя пользователя не может содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            failValidation(user, "Дата рождения не может быть в будущем.");
        }
    }

    private void failValidation(User user, String reason) throws ValidationException {
        log.error("Ошибка валидации: '{}' для пользователя: {}", reason, user);
        throw new ValidationException(reason);
    }
}
