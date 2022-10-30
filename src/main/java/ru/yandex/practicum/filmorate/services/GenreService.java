package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.storages.GenreDbStorage;

import java.util.Collection;

@Service
public class GenreService {

    GenreDbStorage genreDbStorage;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Genre getGenreById(int id) {
        Genre genre = genreDbStorage.getGenreById(id);
        if (genre == null) {
            throw new NotFoundException("Не удалось найти жанр с id: " + id);
        }
        return genre;
    }

    public Collection<Genre> getAll() {
        return genreDbStorage.getAll();
    }


}
