package ru.yandex.practicum.filmorate.storages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;


@Component
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Genre> getAll() {
        String sql = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sql, (rs, rowNum) -> loadGenre(rs));
    }

    public Collection<Genre> getGenresOfFilm(long filmId) {
        String sql = "SELECT FG.GENRE_ID, G.GENRE_NAME FROM GENRES AS G " +
                "JOIN FILMS_GENRES AS FG on G.GENRE_ID = FG.GENRE_ID " +
                "WHERE FG.FILM_ID = " + filmId;
        return jdbcTemplate.query(sql, (rs, rowNum) -> loadGenre(rs));
    }

    public Genre getGenreById(int id) {
        String sql = "SELECT * FROM GENRES WHERE genre_id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, id);

        if (genreRows.next()) {
            return new Genre(genreRows.getInt("genre_id"), genreRows.getString("genre_name"));
        }
        throw new NotFoundException("Не удалось загрузить Жанр с id: " + id);
    }

    private Genre loadGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("genre_name"));
        return genre;
    }
}
