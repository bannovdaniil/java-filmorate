package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

public interface LikeStorage {
    void addLike(long film_id, long user_id) throws FilmNotFoundException, UserNotFoundException;

    void removeLike(long film_id, long user_id) throws FilmNotFoundException, UserNotFoundException;

    long getLikeCount(long filmId);
}
