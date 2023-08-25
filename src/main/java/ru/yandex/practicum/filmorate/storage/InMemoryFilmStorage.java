package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Data
public class InMemoryFilmStorage implements FilmStorage{
    private static final int DESCRIPTION_LENGTH = 200;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    Map<Long, Film> films = new HashMap<>();
    private long idNum;

    private long generateId() {
        return ++idNum;
    }

    private boolean isValid(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Наименование фильма - обязательное поле.");
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() > DESCRIPTION_LENGTH) {
                throw new ValidationException("Описание фильма ограничено 200 символами.");
            }
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть больше 0.");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Система поддерживает загрузку фильмов с датой выхода после 28 декабря 1895.");
        }
        return true;
    }

    public Film addFilm(Film film) {
        if (isValid(film)) {
            film.setId(generateId());
            films.put(film.getId(), film);
            log.info(String.format("Добавлен новый фильм %d", film.getId()));
            return film;
        }
        throw new ValidationException("Фильм не прошел валидацию.");
    }

    public Film updateFilm(Film film) {
        if (film.getId() != 0 && films.containsKey(film.getId()) && isValid(film)) {
            films.replace(film.getId(), film);
            log.info(String.format("Изменена информация о фильме %d", film.getId()));
            return film;
        }
        throw new FilmNotFoundException(String.format("Не существует фильм с заданным id %d.", film.getId()));
    }

    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public Film getFilmById(Long id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException(String.format("Не найден фильм с заданным id %d.", id));
        }
        return films.get(id);
    }
}
