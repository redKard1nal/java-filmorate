package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

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