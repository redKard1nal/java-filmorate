package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
public class Film implements Comparable<Film> {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private int rate;
    private MPA mpa;
    private TreeSet<Genre> genres = new TreeSet<>();
    private TreeSet<Long> likes = new TreeSet<>();


    public void addLike(long id) {
        likes.add(id);
    }

    public void removeLike(long id) {
        likes.remove(id);
    }

    public int getLikesCount() {
        return likes.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Film o) {
        return (int) (this.id - o.getId());
    }
}
