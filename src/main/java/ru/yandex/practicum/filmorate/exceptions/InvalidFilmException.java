package ru.yandex.practicum.filmorate.exceptions;

public class InvalidFilmException extends RuntimeException {
    public InvalidFilmException(String message) {
        super(message);
    }
}
