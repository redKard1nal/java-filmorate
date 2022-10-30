package ru.yandex.practicum.filmorate.storages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements Storage<Film> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TreeSet<Film> get() {
        return new TreeSet<>(jdbcTemplate.query("SELECT * FROM films", (rs, rowNum) -> loadFilm(rs)));
    }

    @Override
    public Film add(Film film) {
        film.setId(getAvailableId());
        jdbcTemplate.update("INSERT INTO films (film_id, film_name, description, release_date, duration, rating, " +
                        "mpa_id)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRate(), film.getMpa().getId());

        for (Genre g : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO films_genres VALUES (?, ?)", film.getId(), g.getId());
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update("UPDATE films SET film_name = ?, description = ?, release_date = ?, duration = ?," +
                        "rating = ?, mpa_id = ?" +
                        "WHERE film_id = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRate(), film.getMpa().getId(), film.getId());

        jdbcTemplate.update("DELETE FROM films_likes WHERE film_id = ?", film.getId());
        for (long l : film.getLikes()) {
            jdbcTemplate.update("INSERT INTO films_likes VALUES (?, ?)", film.getId(), l);
        }

        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?", film.getId());
        for (Genre g : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO films_genres VALUES (?, ?)", film.getId(), g.getId());
        }

        return film;
    }

    @Override
    public Film getById(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE film_id = ?", id);

        if (filmRows.next()) {
            Film film = new Film();
            film.setId(filmRows.getLong("film_id"));
            film.setName(filmRows.getString("film_name"));
            film.setDescription(filmRows.getString("description"));
            film.setReleaseDate(Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate());
            film.setDuration(filmRows.getLong("duration"));
            film.setRate(filmRows.getInt("rating"));
            film.setLikes(new TreeSet<>(loadLikes(film.getId())));
            return film;
        }
        throw new NotFoundException("Нет фильма с id " + id);

    }

    @Override
    public boolean isExist(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", id);
        return filmRows.next();
    }

    private Film loadFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("film_name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getLong("duration"));
        film.setRate(rs.getInt("rating"));
        film.setLikes(new TreeSet<>(loadLikes(film.getId())));
        return film;
    }

    private Collection<Long> loadLikes(long id) {
        String sql = "SELECT * FROM films_likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> findLike(rs), id);
    }

    private long findLike(ResultSet rs) throws SQLException {
        return rs.getLong("user_id");
    }
    
    private long getAvailableId() {
        return jdbcTemplate.queryForObject("SELECT coalesce(max(film_id), 0) FROM films", Integer.class) + 1;
    }
}
