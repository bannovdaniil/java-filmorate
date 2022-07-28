package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmLikeStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmLikeDaoStorageImpl implements FilmLikeStorage {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(final long filmId, final long userId, final int rate) throws FilmNotFoundException, UserNotFoundException {
        if (checkLike(filmId, userId) == 0) {
            String sql = "INSERT INTO LIKES (FILM_ID, USER_ID, RATE) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, filmId, userId, rate);
        }
    }

    @Override
    public void removeLike(final long filmId, final long userId) throws FilmNotFoundException, UserNotFoundException {
        if (checkLike(filmId, userId) > 0) {
            String sql = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ? ;";
            jdbcTemplate.update(sql, filmId, userId);
        }
    }

    private int checkLike(final long filmId, final long userId) throws FilmNotFoundException, UserNotFoundException {
        if (!filmStorage.isFilmExist(filmId)) {
            throw new FilmNotFoundException("Film Id for like not found.");
        }
        if (!userStorage.isUserExist(userId)) {
            throw new UserNotFoundException("User Id for like not found.");
        }
        String sql = "SELECT COUNT(*) FROM LIKES WHERE FILM_ID = ? AND USER_ID = ? LIMIT 1;";

        return jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
    }

    @Override
    public int getUserLikeRate(long filmId, long userId) throws FilmNotFoundException, UserNotFoundException {
        if (checkLike(filmId, userId) > 0) {
            String sql = "SELECT RATE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ? ;";
            return jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
        }
        return 0;
    }

    @Override
    public int getUserLikeCount(long filmId, long userId) throws FilmNotFoundException, UserNotFoundException {
        return checkLike(filmId, userId);
    }
}
