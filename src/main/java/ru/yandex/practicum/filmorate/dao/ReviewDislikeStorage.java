package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

public interface ReviewDislikeStorage {
    void addDislike(long reviewId, long userId) throws ReviewNotFoundException, UserNotFoundException;

    void removeDislike(long reviewId, long userId) throws ReviewNotFoundException, UserNotFoundException;

    long getDislikeCount(long reviewId);
}
