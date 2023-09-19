package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;

import java.util.List;

@Slf4j
@Service
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Mpa addMpa(Mpa mpa) {
        return mpaDbStorage.addMpa(mpa);
    }

    public Mpa updateMpa(Mpa mpa) {
        return mpaDbStorage.updateMpa(mpa);
    }

    public List<Mpa> getAllMpa() {
        return mpaDbStorage.findAllPma();
    }

    public Mpa getMpaById(int code) {
        return mpaDbStorage.findPmaByCode(code);
    }
}
