package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.GenreDbStorage;
import ru.yandex.practicum.filmorate.storages.Storage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final Storage<Film> filmStorage;
    private final Storage<User> userStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") Storage<Film> filmStorage,
                       @Qualifier("UserDbStorage") Storage<User> userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long id, long userId) {
        Film film = filmStorage.getById(id);
        User user = userStorage.getById(userId);

        film.addLike(user.getId());
        updateFilm(film);
    }

    public void removeLike(long id, long userId) {
        Film film = filmStorage.getById(id);
        User user = userStorage.getById(userId);

        film.removeLike(user.getId());
        updateFilm(film);
    }

    public List<Film> getTopRated(int count) {
        return filmStorage.get().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Set<Film> getFilms() {
        return filmStorage.get();
    }

    public Film getFilmById(long id) {
        return filmStorage.getById(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }
}
