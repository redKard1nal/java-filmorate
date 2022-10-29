package ru.yandex.practicum.filmorate.storages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final String[] names;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        names = loadNames();
    }

    public String getGenreNameById(int id) {
        try {
            return names[id - 1];
        } catch (IndexOutOfBoundsException e) {
            throw new NotFoundException("Не удалось найти жанр с id: " + id);
        }
    }

    public int getGenresCount() {
        return names.length;
    }

    private String[] loadNames() {
        String sql = "SELECT genre_name FROM GENRES";
        return jdbcTemplate.query(sql, (rs, rowNum) -> loadName(rs)).toArray(new String[0]);
    }

    private String loadName(ResultSet rs) throws SQLException {
        return rs.getString("genre_name");
    }
}
