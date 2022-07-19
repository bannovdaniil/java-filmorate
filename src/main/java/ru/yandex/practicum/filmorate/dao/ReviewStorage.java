package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {


    List<Review> findAll(Long filmId, Long count);

    Review create(Review review);

    Review update(Review review);

    void remove(Review review);

    Review getReviewById(Long reviewId);
}
