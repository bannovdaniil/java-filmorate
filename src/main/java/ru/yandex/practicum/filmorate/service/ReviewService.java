package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDislikeStorage;
import ru.yandex.practicum.filmorate.dao.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.dao.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final ReviewDislikeStorage reviewDislikeStorage;

    public List<Review> findAll(Long filmId, Long count) {
        return reviewStorage.findAll(filmId, count);
    }

    public Review create(Review review) {
        return reviewStorage.create(review);
    }

    public Review update(Review review) throws ReviewNotFoundException {
        return reviewStorage.update(review);
    }

    public void remove(Review review) throws ReviewNotFoundException {
        reviewStorage.remove(review);
    }

    public Review getReviewById(Long reviewId) throws ReviewNotFoundException {
        return reviewStorage.getReviewById(reviewId);
    }

    public void addLike(Long reviewId, Long userId) throws ReviewNotFoundException, UserNotFoundException {
        reviewLikeStorage.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) throws ReviewNotFoundException, UserNotFoundException {
        reviewDislikeStorage.addDislike(reviewId, userId);
    }

    public void removeLike(Long reviewId, Long userId) throws ReviewNotFoundException, UserNotFoundException {
        reviewLikeStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) throws ReviewNotFoundException, UserNotFoundException {
        reviewDislikeStorage.removeDislike(reviewId, userId);
    }
}
