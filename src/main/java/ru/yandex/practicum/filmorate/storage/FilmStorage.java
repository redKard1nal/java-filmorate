package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Set<Film> getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int id);
}
