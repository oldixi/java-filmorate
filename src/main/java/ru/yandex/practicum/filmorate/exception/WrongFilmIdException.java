package ru.yandex.practicum.filmorate.exception;

public class WrongFilmIdException extends RuntimeException {
    public WrongFilmIdException(String message) {
        super(message);
    }
}
