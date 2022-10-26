package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConflictException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

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
        return new TreeSet<Film>(jdbcTemplate.query("select * from films", (rs, rowNum) -> loadFilm(rs)));
    }

    @Override
    public Film add(Film film) {
        if (isExist(film.getId())) {
            throw new ConflictException("Такой фильм уже существует в коллекции.");
        }

        film.setId(getAvailableId());
        jdbcTemplate.update("insert into films (film_id, film_name, description, release_date, duration, rating, " +
                        "mpa)" +
                        "values (?, ?, ?, ?, ?, ?, ?)",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRate(), film.getMpa().getId());

        for (Genre g : film.getGenres()) {
            jdbcTemplate.update("insert into films_genres values (?, ?)", film.getId(), g.getId());
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!isExist(film.getId())) {
            throw new NotFoundException("Не удалось найти фильм: " + film);
        }

        jdbcTemplate.update("update films set film_name = ?, description = ?, release_date = ?, duration = ?," +
                        "rating = ?, mpa = ?" +
                        "WHERE film_id = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRate(), film.getMpa().getId(), film.getId());

        jdbcTemplate.update("delete from films_likes where film_id = ?", film.getId());
        for (long l : film.getLikes()) {
            jdbcTemplate.update("insert into films_likes values (?, ?)", film.getId(), l);
        }

        jdbcTemplate.update("delete from films_genres where film_id = ?", film.getId());
        for (Genre g : film.getGenres()) {
            jdbcTemplate.update("insert into films_genres values (?, ?)", film.getId(), g.getId());
        }
        return film;
    }

    @Override
    public Film getById(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where film_id = ?", id);

        if (filmRows.next()) {
            Film film = new Film();
            film.setId(filmRows.getLong("film_id"));
            film.setName(filmRows.getString("film_name"));
            film.setDescription(filmRows.getString("description"));
            film.setReleaseDate(Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate());
            film.setDuration(filmRows.getLong("duration"));
            film.setRate(filmRows.getInt("rating"));
            film.setLikes(new TreeSet<>(loadLikes(film.getId())));
            film.setMpa(new MPA(filmRows.getInt("mpa")));
            film.setGenres(new TreeSet<>(loadGenres(film.getId())));
            return film;
        }
        throw new NotFoundException("Нет фильма с id " + id);

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
        film.setMpa(new MPA(rs.getInt("mpa")));
        film.setGenres(new TreeSet<>(loadGenres(film.getId())));
        return film;
    }

    private Collection<Long> loadLikes(long id) {
        String sql = "select * from films_likes where film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> findLike(rs), id);
    }

    private long findLike(ResultSet rs) throws SQLException {
        return rs.getLong("user_id");
    }

    private Collection<Genre> loadGenres(long id) {
        String sql = "select genre_id from films_genres where film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> loadGenre(rs), id);
    }

    private Genre loadGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("genre_id"));
    }

    private long getAvailableId() {
        return jdbcTemplate.queryForObject("select coalesce(max(film_id), 0) from films", Integer.class) + 1;
    }

    private boolean isExist(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);
        return filmRows.next();
    }
}
