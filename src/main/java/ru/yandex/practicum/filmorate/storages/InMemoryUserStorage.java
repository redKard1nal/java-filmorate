package ru.yandex.practicum.filmorate.storages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConflictException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class InMemoryUserStorage implements Storage<User> {
    private final Set<User> users;
    private long id;

    public InMemoryUserStorage() {
        users = new HashSet<>();
        id = 0;
    }

    public Set<User> get() {
        return users;
    }

    public User add(@Valid User user) {
        if (users.contains(user)) {
            throw new ConflictException("Такой пользователь уже существует.");
        }

        user.setId(generateId());
        users.add(user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    public User update(@Valid User user) {
        if (!users.contains(user)) {
            throw new NotFoundException("Не удалось найти пользователя: " + user);
        }

        users.remove(user);
        users.add(user);
        log.info("Пользователь обновлен: {}", user);
        return user;
    }

    @Override
    public User getById(long id) {
        return users.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Нет пользователя с id " + id));
    }

    private long generateId() {
        return ++id;
    }
}
