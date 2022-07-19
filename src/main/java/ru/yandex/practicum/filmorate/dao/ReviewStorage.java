package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    List<Review> findAll();

    List<Review> getReviewTopForFilmId(Long filmId, Long count) throws FilmNotFoundException;

    Review create(Review review) throws UserNotFoundException, FilmNotFoundException;

    Review update(Review review) throws ReviewNotFoundException;

    void remove(Long reviewId) throws ReviewNotFoundException;

    Review getReviewById(Long reviewId) throws ReviewNotFoundException;

    void addUseful(long reviewId);

    void subUseful(long reviewId);

    boolean isReviewExist(long reviewId);
}
