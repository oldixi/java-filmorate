package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    FilmController filmController = new FilmController();
    ValidationException exception;

    @Test
    void shouldAddFilm() {
        Film film = Film.builder()
                .name("Служебный роман")
                .description("Комедия про трудовые будни Статистической организации")
                .releaseDate(LocalDate.of(1977, 10, 26))
                .duration(159)
                .build();
        filmController.addFilm(film);

        assertEquals(1, filmController.getFilms().size(), "Не совпадает количество добавленных фильмов.");
        assertTrue(filmController.getFilms().contains(film), "Фильм не добавился.");
    }

    @Test
    void shouldNotAddFilm0Duration() {
        Film film = Film.builder()
                .name("Служебный роман")
                .description("Комедия про трудовые будни Статистической организации")
                .releaseDate(LocalDate.of(1977, 10, 26))
                .duration(0)
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.addFilm(film);
                    }
                }
                );
        assertEquals("Продолжительность фильма должна быть больше 0.", exception.getMessage());
    }

    @Test
    void shouldNotAddFilmBeforeMinReleaseDate() {
        Film film = Film.builder()
                .name("Служебный роман")
                .description("Комедия про трудовые будни Статистической организации")
                .releaseDate(Film.MIN_RELEASE_DATE.minusDays(1))
                .duration(159)
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.addFilm(film);
                    }
                }
                );
        assertEquals("Система поддерживает загрузку фильмов с датой выхода после 28 декабря 1895.", exception.getMessage());
    }

    @Test
    void shouldNotAddFilmWithoutName() {
        Film film = Film.builder()
                .description("Комедия про трудовые будни Статистической организации")
                .releaseDate(LocalDate.of(1977, 10, 26))
                .duration(159)
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.addFilm(film);
                    }
                }
                );
        assertEquals("Наименование фильма - обязательное поле.", exception.getMessage());
    }

    @Test
    void shouldNotAddFilmDescription201() {
        Film film = Film.builder()
                .name("Служебный роман")
                .description(Stream.generate(() -> String.valueOf("!"))
                        .limit(201)
                        .collect(Collectors.joining()))
                .releaseDate(LocalDate.of(1977, 10, 26))
                .duration(159)
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.addFilm(film);
                    }
                }
                );
        assertEquals("Описание фильма ограничено 200 символами.", exception.getMessage());
    }
}