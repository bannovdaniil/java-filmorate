package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewDislikeStorage;
import ru.yandex.practicum.filmorate.dao.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDisLikeDaoStorageImpl implements ReviewDislikeStorage {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addDislike(final long reviewId, final long userId) throws ReviewNotFoundException, UserNotFoundException {
        if (checkDislike(reviewId, userId) >= 0) {
            String sql = "INSERT INTO REVIEW_DISLIKES (REVIEW_ID, USER_ID) VALUES (?, ?)";
            jdbcTemplate.update(sql, reviewId, userId);
            reviewStorage.subUseful(reviewId);
        }
    }

    @Override
    public void removeDislike(final long reviewId, final long userId) throws ReviewNotFoundException, UserNotFoundException {
        if (checkDislike(reviewId, userId) > 0) {
            String sql = "DELETE FROM REVIEW_DISLIKES WHERE REVIEW_ID = ? AND USER_ID = ? ;";
            jdbcTemplate.update(sql, reviewId, userId);
            reviewStorage.addUseful(reviewId);
        }
    }

    private int checkDislike(final long reviewId, final long userId) throws ReviewNotFoundException, UserNotFoundException {
        if (!reviewStorage.isReviewExist(reviewId)) {
            throw new ReviewNotFoundException("Review Id for like not found.");
        }
        if (!userStorage.isUserExist(userId)) {
            throw new UserNotFoundException("User Id for like not found.");
        }
        String sql = "SELECT COUNT(*) FROM REVIEW_DISLIKES WHERE REVIEW_ID = ? AND USER_ID = ? LIMIT 1;";

        return jdbcTemplate.queryForObject(sql, Integer.class, reviewId, userId);
    }

    @Override
    public long getDislikeCount(final long reviewId) {
        String sql = "SELECT COUNT(*) FROM REVIEW_DISLIKES WHERE REVIEW_ID = ? ;";

        return jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
    }
}
