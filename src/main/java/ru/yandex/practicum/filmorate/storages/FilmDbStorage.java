package ru.yandex.practicum.filmorate.storages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConflictException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements Storage<Film> {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage, MpaDbStorage mpaDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    @Override
    public TreeSet<Film> get() {
        return new TreeSet<Film>(jdbcTemplate.query("SELECT * FROM films", (rs, rowNum) -> loadFilm(rs)));
    }

    @Override
    public Film add(Film film) {
        if (isExist(film.getId())) {
            throw new ConflictException("Такой фильм уже существует в коллекции.");
        }
        loadGenresAndMpa(film);
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
        if (!isExist(film.getId())) {
            throw new NotFoundException("Не удалось найти фильм: " + film);
        }

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

        return getById(film.getId());
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
            film.setMpa(new Mpa(filmRows.getInt("mpa_id")));
            film.setGenres(new TreeSet<>(loadGenres(film.getId())));
            loadGenresAndMpa(film);
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
        film.setMpa(new Mpa(rs.getInt("mpa_id")));
        film.setGenres(new TreeSet<>(loadGenres(film.getId())));
        loadGenresAndMpa(film);
        return film;
    }

    private Collection<Long> loadLikes(long id) {
        String sql = "SELECT * FROM films_likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> findLike(rs), id);
    }

    private long findLike(ResultSet rs) throws SQLException {
        return rs.getLong("user_id");
    }

    private Collection<Genre> loadGenres(long id) {
        String sql = "SELECT genre_id FROM films_genres WHERE film_id = ? ORDER BY genre_id ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> loadGenre(rs), id);
    }

    private Genre loadGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("genre_id"));
    }

    private long getAvailableId() {
        return jdbcTemplate.queryForObject("SELECT coalesce(max(film_id), 0) FROM films", Integer.class) + 1;
    }

    private boolean isExist(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", id);
        return filmRows.next();
    }

    private void loadGenresAndMpa(Film film) {
        for (Genre g : film.getGenres()) {
            g.setName(genreDbStorage.getGenreNameById(g.getId()));
        }
        film.getMpa().setName(mpaDbStorage.getMpaNameById(film.getMpa().getId()));
    }
}
