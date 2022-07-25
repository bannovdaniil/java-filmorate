package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDaoStorageImpl implements ReviewStorage {
    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getLong("REVIEW_ID"));
        review.setContent(rs.getString("CONTENT"));
        review.setIsPositive(rs.getBoolean("POSITIVE"));
        review.setUseful(rs.getInt("USEFUL"));
        review.setUserId(rs.getLong("USER_ID"));
        review.setFilmId(rs.getLong("FILM_ID"));
        return review;
    }

    @Override
    public List<Review> findAll() {
        String sql = "SELECT * FROM REVIEWS ORDER BY USEFUL DESC;";

        List<Review> reviews = jdbcTemplate.query(sql, this::makeReview);

        log.info("Get Review list from DB count: {}.", reviews.size());

        return reviews;
    }

    @Override
    public List<Review> getReviewTopForFilmId(Long filmId, Long count) throws FilmNotFoundException {
        if (filmStorage.isFilmExist(filmId)) {
            String sql = "SELECT * FROM REVIEWS " +
                    " WHERE FILM_ID = ? " +
                    " ORDER BY USEFUL DESC " +
                    " LIMIT ?;";

            List<Review> reviews = jdbcTemplate.query(sql, this::makeReview, filmId, count);

            log.info("Get Review list from DB count: {}.", reviews.size());

            return reviews;
        } else {
            throw new FilmNotFoundException("Film ID not found then Get Reviews Top.");
        }
    }

    @Override
    public Review create(Review review) throws UserNotFoundException, FilmNotFoundException {
        if (!userStorage.isUserExist(review.getUserId())) {
            throw new UserNotFoundException("User Id for review not found.");
        }
        if (!filmStorage.isFilmExist(review.getFilmId())) {
            throw new FilmNotFoundException("Film Id for review not found.");
        }
        String sql = "INSERT INTO REVIEWS (CONTENT, POSITIVE, USEFUL, USER_ID, FILM_ID) " +
                " VALUES(? , ? , ? , ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement prSt = connection.prepareStatement(
                            sql
                            , new String[]{"review_id"});
                    prSt.setString(1, review.getContent());
                    prSt.setBoolean(2, review.getIsPositive());
                    prSt.setLong(3, review.getUseful());
                    prSt.setLong(4, review.getUserId());
                    prSt.setLong(5, review.getFilmId());
                    return prSt;
                }
                , keyHolder);

        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        log.info("Review create: {}.", review.getReviewId());

        return review;
    }

    @Override
    public Review update(Review review) throws ReviewNotFoundException {
        if (isReviewExist(review.getReviewId())) {
            String sql = "UPDATE REVIEWS " +
                    " SET CONTENT = ?, POSITIVE = ? " +
                    " WHERE REVIEW_ID = ? ;";

            jdbcTemplate.update(sql
                    , review.getContent()
                    , review.getIsPositive()
                    , review.getReviewId()
            );
        } else {
            throw new ReviewNotFoundException("Update failed, review not found.");
        }

        return review;
    }

    @Override
    public void remove(Long reviewId) throws ReviewNotFoundException {
        if (isReviewExist(reviewId)) {
            String sql = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? ;";
            jdbcTemplate.update(sql, reviewId);

            sql = "DELETE FROM REVIEWS WHERE REVIEW_ID = ? ;";
            jdbcTemplate.update(sql, reviewId);
        } else {
            throw new ReviewNotFoundException("Review for delete not found.");
        }

    }

    @Override
    public Review getReviewById(Long reviewId) throws ReviewNotFoundException {
        if (isReviewExist(reviewId)) {
            String sql = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?;";

            List<Review> reviews = jdbcTemplate.query(sql, this::makeReview, reviewId);

            return reviews.get(0);
        } else {
            throw new ReviewNotFoundException("Review ID not found.");
        }
    }

    @Override
    public void addUseful(long reviewId) {
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?; ";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public void subUseful(long reviewId) {
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?; ";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public boolean isReviewExist(long reviewId) {
        String sql = "SELECT COUNT(*) FROM REVIEWS WHERE REVIEW_ID = ? ;";
        int reviewCount = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);

        return reviewCount > 0;
    }
}
