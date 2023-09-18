package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    @Autowired
    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @PostMapping
    public Mpa addMpa(@RequestBody Mpa mpa) {
        return mpaService.addMpa(mpa);
    }

    @PutMapping
    public Mpa updateMpa(@RequestBody Mpa mpa) {
        return mpaService.updateMpa(mpa);
    }

    @GetMapping
    public List<Mpa> getAllMpa() {
        return mpaService.getAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        return mpaService.getMpaById(id);
    }
}
