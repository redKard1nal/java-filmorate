package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Set;

public interface UserStorage {

    Set<User> getUsers();

    User addUser(@Valid User user);

    User updateUser(@Valid User user);

    User getUserById(long id);
}
