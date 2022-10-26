package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre implements Comparable<Genre> {
    int id;
    @NotNull
    String name;

    public Genre(int id) {
        this.id = id;
        this.name = genres[id - 1];
    }

    public static final String[] genres = {
            "Комедия",
            "Драма",
            "Мультфильм",
            "Триллер",
            "Документальный",
            "Боевик" };

    @Override
    public int compareTo(Genre o) {
        return this.id - o.id;
    }
}