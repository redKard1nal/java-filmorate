package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.Storage;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    private final Storage<User> userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") Storage<User> userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long id, long friendId) {
        User user1 = userStorage.getById(id);
        User user2 = userStorage.getById(friendId);

        if (user2.getFriendRequests().contains(user1.getId())) {
            user1.addFriend(user2.getId());
            user2.addFriend(user1.getId());
            user2.removeFriendRequest(user1.getId());
        } else {
            user1.addFriendRequest(user2.getId());
        }

        updateUser(user1);
        updateUser(user2);
    }

    public void removeFriend(long id, long friendId) {
        User user1 = userStorage.getById(id);
        User user2 = userStorage.getById(friendId);

        user1.removeFriendById(user2.getId());
        user1.removeFriendRequest(user2.getId());
        user2.removeFriendById(user1.getId());
        user2.removeFriendRequest(user1.getId());
        updateUser(user1);
        updateUser(user2);
    }

    public Set<User> getUserFriends(long id) {
        User user = userStorage.getById(id);

        return Stream.concat(user.getFriends().stream(), user.getFriendRequests().stream())
                .map(userStorage::getById)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(long id, long otherId) {
        User user1 = userStorage.getById(id);
        User user2 = userStorage.getById(otherId);

        return Stream.concat(user1.getFriends().stream(), user1.getFriendRequests().stream())
                .filter(e -> user2.getFriends().contains(e) || user2.getFriendRequests().contains(e))
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
