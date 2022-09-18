package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Storage<User> userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long id, long friendId) {
        User user1 = userStorage.getById(id);
        User user2 = userStorage.getById(friendId);

        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());
    }

    public void removeFriend(long id, long friendId) {
        User user1 = userStorage.getById(id);
        User user2 = userStorage.getById(friendId);

        user1.removeFriendById(user2.getId());
        user2.removeFriendById(user1.getId());
    }

    public Set<User> getUserFriends(long id) {
        return userStorage.getById(id).getFriends().stream()
                .map(userStorage::getById)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(long id, long otherId) {
        User user1 = userStorage.getById(id);
        User user2 = userStorage.getById(otherId);

        return user1.getFriends().stream()
                .filter(e -> user2.getFriends().contains(e))
                .map(userStorage::getById)
                .collect(Collectors.toSet());
    }

    public Set<User> getUsers() {
        return userStorage.get();
    }

    public User getUserById(long id) {
        return userStorage.getById(id);
    }

    public User addUser(User user) {
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }
}
