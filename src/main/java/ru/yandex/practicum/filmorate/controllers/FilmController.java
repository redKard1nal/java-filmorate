package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Set<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmStorage.getFilmById(id);
    }

    @GetMapping("/films/popular")
    public List<Film> getTopRated(@RequestParam(defaultValue = "10") String count) {
        return filmService.getTopRated(Integer.parseInt(count));
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) {
        return filmStorage.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }

}
