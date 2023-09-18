package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDBStorageTest {
    private final GenreDbStorage genreDbStorage;
    Genre genre1 = new Genre(1, "Комедия");
    Genre genre2 = new Genre(2, "Драма");
    Genre genre3 = new Genre(3, "Мультфильм");

    @Test
    public void testAddGenre() {
        Genre finGenreByCode = genreDbStorage.findGenreByCode(1);
        assertNotNull(finGenreByCode);
        assertEquals(genre1.getName(), finGenreByCode.getName());

        List<Genre> allGenreList = genreDbStorage.findAllGenre();
        assertNotNull(allGenreList);
        assertEquals(6, allGenreList.size());
    }
}
