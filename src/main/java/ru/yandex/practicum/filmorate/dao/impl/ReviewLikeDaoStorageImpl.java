package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.dao.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewLikeDaoStorageImpl implements ReviewLikeStorage {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(final long reviewId, final long userId) throws ReviewNotFoundException, UserNotFoundException {
        if (checkLike(reviewId, userId) >= 0) {
            String sql = "INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID) VALUES (?, ?)";
            jdbcTemplate.update(sql, reviewId, userId);
            reviewStorage.addUseful(reviewId);
        }
    }

    @Override
    public void removeLike(final long reviewId, final long userId) throws ReviewNotFoundException, UserNotFoundException {
        if (checkLike(reviewId, userId) > 0) {
            String sql = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ? ;";
            jdbcTemplate.update(sql, reviewId, userId);
            reviewStorage.subUseful(reviewId);
        }
    }

    private int checkLike(final long reviewId, final long userId) throws ReviewNotFoundException, UserNotFoundException {
        if (!reviewStorage.isReviewExist(reviewId)) {
            throw new ReviewNotFoundException("Review Id for like not found.");
        }
        if (!userStorage.isUserExist(userId)) {
            throw new UserNotFoundException("User Id for like not found.");
        }
        String sql = "SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ? LIMIT 1;";

        return jdbcTemplate.queryForObject(sql, Integer.class, reviewId, userId);
    }

    @Override
    public long getLikeCount(final long reviewId) {
        String sql = "SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = ? ;";

        return jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
    }
}
