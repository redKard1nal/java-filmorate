package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.services.MpaService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/mpa")
    public List<Mpa> getMpa() {
        List<Mpa> response = new ArrayList<>();
        for (int i = 1; i <= mpaService.getMpaCount(); i++) {
            Mpa mpa = new Mpa(i);
            mpa.setName(mpaService.getMpaNameById(i));
            response.add(mpa);
        }
        return response;
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        Mpa mpa = new Mpa(id);
        mpa.setName(mpaService.getMpaNameById(id));
        return mpa;
    }
}
