package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    Mpa mpa1 = new Mpa(1,"G");
    Mpa mpa2 = new Mpa(4,"R");
    Genre genre1 = new Genre(1, "Комедия");
    Film film1 = Film.builder()
            .name("nisi eiusmod")
            .description("adipisicing")
            .releaseDate(LocalDate.parse("25.03.1967", Constants.FORMATTER))
            .duration(100)
            .mpa(mpa1)
            .build();
    Film updateFilm1 = Film.builder()
            .id(1)
            .name("nisi eiusmod update")
            .description("adipisicing")
            .releaseDate(LocalDate.parse("25.03.1967", Constants.FORMATTER))
            .duration(100)
            .mpa(mpa1)
            .build();
    Film film2 = Film.builder()
            .name("New film")
            .description("New film about friends")
            .releaseDate(LocalDate.parse("30.04.1999", Constants.FORMATTER))
            .duration(120)
            .mpa(mpa2)
            .genres(List.of(genre1))
            .build();

    @Test
    public void testAddFilm() {
        Film film = filmStorage.addFilm(film1);

        assertNotNull(film);
        assertEquals(1, film.getId());

        filmStorage.addFilm(film2);
        List<Film> filmList = filmStorage.getFilms();

        assertNotNull(filmList);
        assertEquals(2, filmList.size());
    }

    @Test
    public void testUpdateFilm() {
        filmStorage.addFilm(film1);
        Film film = filmStorage.updateFilm(updateFilm1);

        assertNotNull(film);
        assertEquals(updateFilm1.getName(), film.getName());
    }

    @Test
    public void testFindFilmById() {
        Film film = filmStorage.getFilmById(1L);

        assertNotNull(film);
        assertEquals(1, film.getId());
    }
}
