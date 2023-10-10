package ru.yandex.practicum.filmorate.exception;

public class WrongDirectorIdException extends RuntimeException {

    public WrongDirectorIdException(String message) {
        super(message);
    }
}
