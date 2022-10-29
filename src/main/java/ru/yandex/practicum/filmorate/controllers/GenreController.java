package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.services.GenreService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        List<Genre> response = new ArrayList<>();
        for (int i = 1; i <= genreService.getGenresCount(); i++) {
            Genre genre = new Genre(i);
            genre.setName(genreService.getGenreNameById(i));
            response.add(genre);
        }
        return response;
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        Genre genre = new Genre(id);
        genre.setName(genreService.getGenreNameById(id));
        return genre;
    }
}
