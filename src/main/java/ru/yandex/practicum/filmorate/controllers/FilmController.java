package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping
@Slf4j
public class FilmController {
    private final FilmService filmService;
    private static final LocalDate RELEASE_DATE_ALLOWED = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Set<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/films/popular")
    public List<Film> getTopRated(@RequestParam(defaultValue = "10") String count) {
        return filmService.getTopRated(Integer.parseInt(count));
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) {
        validateFilm(film);
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        validateFilm(film);
        return filmService.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/genres")
    public GenreHelper[] getGenres() {
        GenreHelper[] response = new GenreHelper[Genre.genres.length];
        for (int i = 0; i < response.length; i++) {
            response[i] = new GenreHelper(i + 1);
        }
        return response;
    }

    @GetMapping("/mpa")
    public MPAHelper[] getRatings() {
        MPAHelper[] response = new MPAHelper[MPA.MPAs.length];
        for (int i = 0; i < response.length; i++) {
            response[i] = new MPAHelper(i + 1);
        }
        return response;
    }

    @GetMapping("/genres/{id}")
    public GenreHelper getGenreById(@PathVariable int id) {
        return new GenreHelper(id);
    }

    @GetMapping("/mpa/{id}")
    public MPAHelper getRatingById(@PathVariable int id) {
        return new MPAHelper(id);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            failValidation(film, "Название фильма не должно быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            failValidation(film, "Описание фильма превышает допустимо в 200 символов.");
        }
        if (film.getReleaseDate().isBefore(RELEASE_DATE_ALLOWED)) {
            failValidation(film, "Фильм слишком старый.");
        }
        if (film.getDuration() < 0) {
            failValidation(film, "Указана отрицательная продолжительность фильма.");
        }
    }

    private void failValidation(Film film, String reason) throws ValidationException {
        log.error("Ошибка валидации: '{}' для фильма: {}", reason, film);
        throw new ValidationException(reason);
    }

}

class GenreHelper {
    public final int id;
    public final String name;

    public GenreHelper(int id) {
        this.id = id;
        try {
            this.name = Genre.genres[id - 1];
        } catch (IndexOutOfBoundsException e) {
            throw new NotFoundException("Нет Жанра с id: " + id);
        }
    }
}

class MPAHelper {
    public final int id;
    public final String name;

    public MPAHelper(int id) {
        this.id = id;
        try {
            this.name = MPA.MPAs[id - 1];
        } catch (IndexOutOfBoundsException e) {
            throw new NotFoundException("Нет MPA с id: " + id);
        }

    }
}