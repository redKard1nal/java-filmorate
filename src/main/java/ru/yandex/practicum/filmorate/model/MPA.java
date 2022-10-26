package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MPA {

    int id;
    String name;

    public MPA(int id) {
        this.id = id;
        this.name = MPAs[id - 1];
    }

    public static final String[] MPAs = {
            "G",
            "PG",
            "PG-13",
            "R",
            "NC-17"
    };


}
