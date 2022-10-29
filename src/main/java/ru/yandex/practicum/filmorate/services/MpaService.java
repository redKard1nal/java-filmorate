package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storages.MpaDbStorage;

@Service
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public int getMpaCount() {
        return mpaDbStorage.getMpaCount();
    }

    public String getMpaNameById(int id) {
        return mpaDbStorage.getMpaNameById(id);
    }
}
