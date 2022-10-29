package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storages.GenreDbStorage;

@Service
public class GenreService {

    GenreDbStorage genreDbStorage;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public String getGenreNameById(int id) {
        return genreDbStorage.getGenreNameById(id);
    }

    public int getGenresCount() {
        return genreDbStorage.getGenresCount();
    }
}
