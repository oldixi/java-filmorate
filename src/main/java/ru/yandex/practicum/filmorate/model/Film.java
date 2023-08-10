package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.ValidationException;

import java.time.LocalDate;

@Builder
@Data
public class Film {
    private static final int DESCRIPTION_LENGTH = 200;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    public boolean isValid() {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Наименование фильма - обязательное поле.");
        }
        if (description != null) {
            if (description.length() > DESCRIPTION_LENGTH) {
                throw new ValidationException("Описание фильма ограничено 200 символами.");
            }
        }
        if (duration <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть больше 0.");
        }
        if (releaseDate == null || releaseDate.isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Система поддерживает загрузку фильмов с датой выхода после 28 декабря 1895.");
        }
        return true;
    }
}
