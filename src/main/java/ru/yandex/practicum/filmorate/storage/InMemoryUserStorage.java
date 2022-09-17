package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Set<User> users;
    private int id;

    public InMemoryUserStorage() {
        users = new HashSet<>();
        id = 0;
    }

    public Set<User> getUsers() {
        return users;
    }

    public User addUser(@Valid User user) {
        validateUser(user);
        user.setId(generateId());
        users.add(user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    public User updateUser(@Valid User user) {
        if (!users.contains(user)) {
            throw new NotFoundException("Не удалось найти пользователя: " + user);
        }

        validateUser(user);
        users.remove(user);
        users.add(user);
        log.info("Пользователь обновлен: {}", user);
        return user;
    }

    @Override
    public User getUserById(long id) {
        Optional<User> user = users.stream()
                .filter(e -> e.getId() == id)
                .findFirst();
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("Нет пользователя с id " + id);
        }
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

    private int generateId() {
        return ++id;
    }
}
