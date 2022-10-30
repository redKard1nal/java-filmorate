package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConflictException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.GenreDbStorage;
import ru.yandex.practicum.filmorate.storages.MpaDbStorage;
import ru.yandex.practicum.filmorate.storages.Storage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final Storage<Film> filmStorage;
    private final Storage<User> userStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") Storage<Film> filmStorage,
                       @Qualifier("UserDbStorage") Storage<User> userStorage,
                       GenreDbStorage genreDbStorage,
                       MpaDbStorage mpaDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    public void addLike(long id, long userId) {
        Film film = getFilmById(id);
        User user = userStorage.getById(userId);

        film.addLike(user.getId());
        updateFilm(film);
    }

    public void removeLike(long id, long userId) {
        Film film = getFilmById(id);
        User user = userStorage.getById(userId);

        film.removeLike(user.getId());
        updateFilm(film);
    }

    public List<Film> getTopRated(int count) {
        return getFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Set<Film> getFilms() {
        Set<Film> result = filmStorage.get();
        result.forEach(this::loadGenreAndMpa);
        return result;
    }

    public Film getFilmById(long id) {
        Film film = filmStorage.getById(id);
        loadGenreAndMpa(film);
        return film;
    }

    public Film addFilm(Film film) {
        if (isExist(film.getId())) {
            throw new ConflictException("Фильм с id: " + film.getId() + " уже существует.");
        }

        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        if (!isExist(film.getId())) {
            throw new NotFoundException("Не удалось найти фильм с id: " + film);
        }

        filmStorage.update(film);
        return getFilmById(film.getId());
    }

    private void loadGenreAndMpa(Film film) {
        Set<Genre> genres = new TreeSet<>(genreDbStorage.getGenresOfFilm(film.getId()));
        film.setGenres(genres);
        Mpa mpa = mpaDbStorage.getMpaOfFilm(film.getId());
        film.setMpa(mpa);
    }

    private boolean isExist(long id) {
        return filmStorage.isExist(id);
    }
}
