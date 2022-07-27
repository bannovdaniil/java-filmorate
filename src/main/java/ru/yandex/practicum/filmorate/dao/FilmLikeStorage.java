package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

public interface FilmLikeStorage {
    void addLike(long filmId, long userId, int rate) throws FilmNotFoundException, UserNotFoundException;

    void removeLike(long filmId, long userId) throws FilmNotFoundException, UserNotFoundException;

    int getUserLikeRate(long filmId, long userId) throws FilmNotFoundException, UserNotFoundException;

    int getUserLikeCount(long filmId, long userId) throws FilmNotFoundException, UserNotFoundException;
}
