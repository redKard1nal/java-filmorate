package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface Storage<T> {

    Set<T> get();

    T add(T t);

    T update(T t);

    T getById(long id);
}
