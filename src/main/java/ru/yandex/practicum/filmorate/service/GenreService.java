package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    @Autowired
    private final GenreDbStorage genreDbStorage;

    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Genre addGenre(Genre genre) {
        return genreDbStorage.addGenre(genre);
    }

    public Genre updateGenre(Genre genre) {
        return genreDbStorage.updateGenre(genre);
    }

    public List<Genre> getAllGenre() {
        return genreDbStorage.findAllGenre();
    }

    public Genre getGenreById(int code) {
        return genreDbStorage.findGenreByCode(code);
    }
}
