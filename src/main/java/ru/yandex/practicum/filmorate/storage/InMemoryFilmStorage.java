package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Set<Film> films;
    private int id;
    private static final LocalDate RELEASE_DATE_ALLOWED = LocalDate.of(1895, 12, 28);

    public InMemoryFilmStorage() {
        films = new HashSet<>();
        id = 0;
    }

    @Override
    public Set<Film> getFilms() {
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        film.setId(generateId());
        films.add(film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.contains(film)) {
            throw new NotFoundException("Не удалось найти пользователя: " + film);
        }

        validateFilm(film);
        films.remove(film);
        films.add(film);
        log.info("Фильм обновлен: {}", film);
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        Optional<Film> film = films.stream()
                .filter(e -> e.getId() == id)
                .findFirst();
        if (film.isPresent()) {
            return film.get();
        } else {
            throw new NotFoundException("Нет фильма с id " + id);
        }
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

    private int generateId() {
        return ++id;
    }
}
