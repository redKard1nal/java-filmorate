package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

public class FilmControllerTest {

    private static FilmController controller;
    private Film film;

    @BeforeAll
    public static void createController() {
        controller = new FilmController();
    }

    @BeforeEach
    public void createNormalFilm() {
        film = new Film();
        film.setName("Normal name");
        film.setDescription("Normal desc.");
        film.setReleaseDate(LocalDate.of(2020, 12, 28));
        film.setDuration(Duration.ofHours(2));
    }

    @Test
    public void shouldNotThrowAnyWhenItIsNormalFim() {
        Assertions.assertDoesNotThrow(() ->controller.addFilm(film));
    }

    @Test
    public void shouldThrowAnExceptionWhenFilmsNameIsEmpty() {
        film.setName("");

        Assertions.assertThrows(FilmValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    public void shouldThrowAnExceptionWhenFilmsDescIsToLarge() {
        film.setDescription("To large description over 200 symbols".repeat(1000));

        Assertions.assertThrows(FilmValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    public void shouldNotThrowAnExceptionWhenFilmsDescIs200Symbols() {
        film.setDescription("J".repeat(200));

        Assertions.assertDoesNotThrow(() -> controller.addFilm(film));
    }

    @Test
    public void shouldNotThrowAnExceptionWhenFilmsDescIsReallyShort() {
        film.setDescription("J");

        Assertions.assertDoesNotThrow(() -> controller.addFilm(film));
    }

    @Test
    public void shouldThrowAnExceptionWhenFilmIsTooOld() {
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        Assertions.assertThrows(FilmValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    public void shouldNotThrowAnExceptionWhenFilmIsReleasedExactlyAtAllowedDay() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Assertions.assertDoesNotThrow(() -> controller.addFilm(film));
    }

    @Test
    public void shouldNotThrowAnExceptionWhenFilmIsReleasedNow() {
        film.setReleaseDate(LocalDate.now());

        Assertions.assertDoesNotThrow(() -> controller.addFilm(film));
    }

    @Test
    public void shouldThrowAnExceptionWhenFilmDurationIsNegative() {
        film.setDuration(Duration.ofSeconds(-1));

        Assertions.assertThrows(FilmValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    public void shouldNotThrowAnExceptionWhenFilmDurationIsZero() {
        film.setDuration(Duration.ofSeconds(0));

        Assertions.assertDoesNotThrow(() -> controller.addFilm(film));
    }

    @Test
    public void shouldNotThrowAnExceptionWhenFilmDurationIsPositive() {
        film.setDuration(Duration.ofSeconds(1));

        Assertions.assertDoesNotThrow(() -> controller.addFilm(film));
    }
}
