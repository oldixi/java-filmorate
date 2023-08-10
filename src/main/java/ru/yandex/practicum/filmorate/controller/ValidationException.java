package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationException extends RuntimeException {
    public ValidationException(String msg){
        super(msg);
        log.debug("Во время выполнения программы произошла ошибка валидации. ", this);
    }
}
