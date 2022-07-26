package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.LikeStatus;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final EventService eventService;

    public List<Review> findAll(Long filmId, Long count) throws FilmNotFoundException {
        if (filmId > 0) {
            return reviewStorage.getReviewTopForFilmId(filmId, count);
        } else {
            return reviewStorage.findAll();
        }
    }

    public Review create(Review review) throws UserNotFoundException, FilmNotFoundException {
        Review newReview = reviewStorage.create(review);
        eventService.addEvent(newReview.getUserId(), EventType.REVIEW, EventOperation.ADD, newReview.getReviewId());
        return newReview;
    }

    public Review update(Review review) throws ReviewNotFoundException {
        Review newReview = reviewStorage.update(review);
        Review correctReview = reviewStorage.getReviewById(review.getReviewId());
        eventService.addEvent(correctReview.getUserId(),
                EventType.REVIEW,
                EventOperation.UPDATE,
                correctReview.getReviewId());
        return newReview;
    }

    public void remove(Long reviewId) throws ReviewNotFoundException {
        Review review = reviewStorage.getReviewById(reviewId);
        eventService.addEvent(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, reviewId);
        reviewStorage.remove(reviewId);
    }

    public Review getReviewById(Long reviewId) throws ReviewNotFoundException {
        return reviewStorage.getReviewById(reviewId);
    }

    public void addLike(Long reviewId, Long userId, LikeStatus likeStatus) throws ReviewNotFoundException, UserNotFoundException {
        reviewLikeStorage.addLike(reviewId, userId, likeStatus);
    }

    public void removeLike(Long reviewId, Long userId, LikeStatus likeStatus) throws ReviewNotFoundException, UserNotFoundException {
        reviewLikeStorage.removeLike(reviewId, userId, likeStatus);
    }

}
