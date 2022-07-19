package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

public interface FilmLikeStorage {
    void addLike(long filmId, long userId) throws FilmNotFoundException, UserNotFoundException;

    void removeLike(long filmId, long userId) throws FilmNotFoundException, UserNotFoundException;

    long getLikeCount(long filmId);
}
