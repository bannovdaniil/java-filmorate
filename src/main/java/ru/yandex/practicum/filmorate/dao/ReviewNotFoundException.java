package ru.yandex.practicum.filmorate.dao;

public class ReviewNotFoundException extends Exception {
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
