package ru.yandex.practicum.filmorate.storages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConflictException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements Storage<User> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Set<User> get() {
        String sql = "SELECT user_id, user_email, login, name, birthday FROM users";
        return Set.copyOf(jdbcTemplate.query(sql, (rs, rowNum) -> loadUser(rs)));
    }

    @Override
    public User add(User user) {

        if (isExist(user.getId())) {
            throw new ConflictException("Такой пользователь уже существует.");
        }
        user.setId(getAvailableId());
        jdbcTemplate.update("INSERT INTO users (user_id, user_email, login, name, birthday) VALUES (?, ?, ?, ?, ?)",
                user.getId(), user.getEmail(),
                user.getLogin(), user.getName(),
                user.getBirthday());

        return user;
    }

    @Override
    public User update(User user) {
        if (!isExist(user.getId())) {
            throw new NotFoundException("Не удалось найти пользователя: " + user);
        }

        jdbcTemplate.update("UPDATE users SET " +
                        "user_email = ?," +
                        "login = ?," +
                        "name = ?," +
                        "birthday = ?" +
                        "WHERE user_id = ?",
                user.getEmail(),
                user.getLogin(), user.getName(),
                user.getBirthday(),
                user.getId());

        jdbcTemplate.update("DELETE FROM users_friends WHERE user_id = ?", user.getId());
        for (long l : user.getFriends()) {
            jdbcTemplate.update("INSERT INTO users_friends VALUES (?, ?, true)", user.getId(), l);
        }
        for (long l : user.getFriendRequests()) {
            jdbcTemplate.update("INSERT INTO users_friends VALUES (?, ?, false)", user.getId(), l);
        }
        return user;
    }

    @Override
    public User getById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", id);
        if (userRows.next()) {
            User user = new User();
            user.setId(id);
            user.setEmail(userRows.getString("user_email"));
            user.setLogin(userRows.getString("login"));
            user.setName(userRows.getString("name"));
            user.setBirthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate());

            user.setFriends(new HashSet<>(findUserFriends(id)));
            user.setFriendRequests(new HashSet<>(findUserFriendRequests(id)));
            return user;
        }
        throw new NotFoundException("Нет пользователя с id " + id);
    }

    private Collection<Long> findUserFriends(long id) {
        String sql = "SELECT * FROM users_friends WHERE user_id = ? AND is_accepted = true";
        return jdbcTemplate.query(sql, (rs, rowNum) -> findFriend(rs), id);
    }

    private Collection<Long> findUserFriendRequests(long id) {
        String sql = "SELECT * FROM users_friends WHERE user_id = ? AND is_accepted = false";
        return jdbcTemplate.query(sql, (rs, rowNum) -> findFriend(rs), id);
    }

    private long findFriend(ResultSet rs) throws SQLException {
        return rs.getLong("friend_id");
    }

    private User loadUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("user_email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate());

        user.setFriends(new HashSet<>(findUserFriends(user.getId())));
        user.setFriendRequests(new HashSet<>(findUserFriendRequests(user.getId())));
        return user;
    }

    private long getAvailableId() {
        return jdbcTemplate.queryForObject("SELECT coalesce(max(user_id), 0) FROM users", Integer.class) + 1;
    }

    private boolean isExist(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", id);
        return userRows.next();
    }
}
