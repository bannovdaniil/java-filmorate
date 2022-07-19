package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public List<Review> getReviewList(
            @RequestParam(defaultValue = "0", required = false) Long filmId
            , @RequestParam(defaultValue = "10", required = false) Long count) {
        return reviewService.findAll(filmId, count);
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) throws ReviewNotFoundException {
        return reviewService.update(review);
    }

    @DeleteMapping
    public void remove(@Valid @RequestBody Review review) throws ReviewNotFoundException {
        reviewService.remove(review);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable("id") Long reviewId) throws ReviewNotFoundException {
        return reviewService.getReviewById(reviewId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable("id") Long reviewId,
            @PathVariable("userId") Long userId) throws ReviewNotFoundException, UserNotFoundException {
        reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(
            @PathVariable("id") Long reviewId,
            @PathVariable("userId") Long userId) throws ReviewNotFoundException, UserNotFoundException {
        reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable("id") Long reviewId,
            @PathVariable("userId") Long userId) throws ReviewNotFoundException, UserNotFoundException {
        reviewService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(
            @PathVariable("id") Long reviewId,
            @PathVariable("userId") Long userId) throws ReviewNotFoundException, UserNotFoundException {
        reviewService.removeDislike(reviewId, userId);
    }

}
