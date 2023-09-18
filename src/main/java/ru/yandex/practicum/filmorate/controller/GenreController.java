package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    @Autowired
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public Genre addGenre(@RequestBody Genre genre) {
        return genreService.addGenre(genre);
    }

    @PutMapping
    public Genre updateGenre(@RequestBody Genre genre) {
        return genreService.updateGenre(genre);
    }

    @GetMapping
    public List<Genre> getAllGenre() {
        return genreService.getAllGenre();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return genreService.getGenreById(id);
    }
}
