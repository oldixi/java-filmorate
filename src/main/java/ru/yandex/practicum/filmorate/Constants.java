package ru.yandex.practicum.filmorate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Constants {
    public static final int DESCRIPTION_LENGTH = 200;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public static final LocalDate NOW = LocalDate.now();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
}
