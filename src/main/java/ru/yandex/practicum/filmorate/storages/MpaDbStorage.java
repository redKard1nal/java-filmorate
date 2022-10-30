package ru.yandex.practicum.filmorate.storages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Mpa> getAll() {
        String sql = "SELECT * FROM MPA";
        return jdbcTemplate.query(sql, (rs, rowNum) -> loadMpa(rs));
    }

    public Mpa getMpaOfFilm(long filmId) {
        String sql = "SELECT M.mpa_id, M.mpa_name " +
                "FROM FILMS as F " +
                "JOIN MPA as M on F.MPA_ID = M.MPA_ID " +
                "WHERE F.film_id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, filmId);
        if (mpaRows.next()) {
            return new Mpa(mpaRows.getInt("mpa_id"), mpaRows.getString("mpa_name"));
        }
        throw new NotFoundException("Не удалось загрузить MPA для фильма с id: " + filmId);
    }

    public Mpa getMpaById(int id) {
        String sql = "SELECT * FROM MPA WHERE mpa_id = ?";
        SqlRowSet mpaRows =  jdbcTemplate.queryForRowSet(sql, id);

        if (mpaRows.next()) {
            return new Mpa(mpaRows.getInt("mpa_id"), mpaRows.getString("mpa_name"));
        }
        throw new NotFoundException("Не удалось загрузить MPA с id: " + id);
    }

    private Mpa loadMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        return mpa;
    }
}
