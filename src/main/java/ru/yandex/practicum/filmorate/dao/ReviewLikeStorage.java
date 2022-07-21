package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.LikeStatus;

public interface ReviewLikeStorage {
    void addLike(long reviewId, long userId, LikeStatus status) throws ReviewNotFoundException, UserNotFoundException;

    void removeLike(long reviewId, long userId, LikeStatus status) throws ReviewNotFoundException, UserNotFoundException;

}
