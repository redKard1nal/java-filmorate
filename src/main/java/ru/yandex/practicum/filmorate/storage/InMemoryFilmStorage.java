package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConflictException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class InMemoryFilmStorage implements Storage<Film> {

    private final Set<Film> films;
    private long id;

    public InMemoryFilmStorage() {
        films = new HashSet<>();
        id = 0;
    }

    @Override
    public Set<Film> get() {
        return films;
    }

    @Override
    public Film add(Film film) {
        if (films.contains(film)) {
            throw new ConflictException("Такой фильм уже существует в коллекции.");
        }

        film.setId(generateId());
        films.add(film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.contains(film)) {
            throw new NotFoundException("Не удалось найти фильм: " + film);
        }

        films.remove(film);
        films.add(film);
        log.info("Фильм обновлен: {}", film);
        return film;
    }

    @Override
    public Film getById(long id) {
        Optional<Film> film = films.stream()
                .filter(e -> e.getId() == id)
                .findFirst();
        if (film.isPresent()) {
            return film.get();
        } else {
            throw new NotFoundException("Нет фильма с id " + id);
        }
    }


    private long generateId() {
        return ++id;
    }
}
