package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Set<Film> films = new HashSet<>();
    private static final LocalDate RELEASE_DATE_ALLOWED = LocalDate.of(1895, 12, 28);
    private int id = 0;

    @GetMapping
    public Set<Film> getFilms() {
        return films;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validateFilm(film);
        film.setId(generateId());
        films.add(film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if(!films.contains(film)) {
            throw new FilmNotFoundException("Не удалось найти пользователя: " + film);
        }

        validateFilm(film);
        films.remove(film);
        films.add(film);
        log.info("Фильм обновлен: {}", film);
        return film;
    }

    private void validateFilm(Film film) {
        if(film.getName() == null || film.getName().isEmpty()) {
            failValidation(film, "Название фильма не должно быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            failValidation(film, "Описание фильма превышает допустимо в 200 символов.");
        }
        if (film.getReleaseDate().isBefore(RELEASE_DATE_ALLOWED)) {
            failValidation(film, "Фильм слишком старый.");
        }
        if (film.getDuration().isNegative()) {
            failValidation(film, "Указана отрицательная продолжительность фильма.");
        }
    }

    private void failValidation(Film film, String reason) throws FilmValidationException {
        log.error("Ошибка валидации: '{}' для фильма: {}", reason, film);
        throw new FilmValidationException(reason);
    }

    private int generateId() {
        return ++id;
    }
}
