package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

public interface ReviewLikeStorage {
    void addLike(long reviewId, long userId) throws ReviewNotFoundException, UserNotFoundException;

    void removeLike(long reviewId, long userId) throws ReviewNotFoundException, UserNotFoundException;

    long getLikeCount(long reviewId);
}
