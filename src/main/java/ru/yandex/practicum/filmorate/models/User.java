package ru.yandex.practicum.filmorate.models;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

@Data
public class User {
    private long id;
    @Email
    private String email;
    @NotNull
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friendRequests = new HashSet<>();
    private Set<Long> friends = new HashSet<>();

    public void addFriendRequest(long id) {
        friendRequests.add(id);
    }

    public void removeFriendRequest(long id) {
        friendRequests.remove(id);
    }

    public void addFriend(long id) {
        friends.add(id);
        removeFriendRequest(id);
    }

    public void removeFriendById(long id) {
        friends.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
