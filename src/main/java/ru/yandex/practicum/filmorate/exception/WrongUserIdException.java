package ru.yandex.practicum.filmorate.exception;

public class WrongUserIdException extends RuntimeException {
    public WrongUserIdException(String message) {
        super(message);
    }
}
