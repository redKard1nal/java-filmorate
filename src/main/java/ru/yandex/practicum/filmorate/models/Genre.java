package ru.yandex.practicum.filmorate.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre implements Comparable<Genre> {
    int id;
    String name;

    public Genre(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Genre o) {
        return this.id - o.id;
    }
}