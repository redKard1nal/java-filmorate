package ru.yandex.practicum.filmorate.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mpa {

    int id;
    String name;

    public Mpa(int id) {
        this.id = id;
    }
}
