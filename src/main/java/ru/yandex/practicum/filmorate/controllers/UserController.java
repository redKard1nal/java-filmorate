package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Set<User> users = new HashSet<>();
    private int id = 0;

    @GetMapping
    public Set<User> getUsers() {
        return users;
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        validateUser(user);
        user.setId(generateId());
        users.add(user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if(!users.contains(user)) {
            throw new UserNotFoundException("Не удалось найти пользователя: " + user);
        }

        validateUser(user);
        users.remove(user);
        users.add(user);
        log.info("Пользователь обновлен: {}", user);
        return user;
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

    private void failValidation(User user, String reason) throws UserValidationException {
        log.error("Ошибка валидации: '{}' для пользователя: {}", reason, user);
        throw new UserValidationException(reason);
    }

    private int generateId() {
        return ++id;
    }
}
