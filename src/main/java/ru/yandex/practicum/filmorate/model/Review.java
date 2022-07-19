package ru.yandex.practicum.filmorate.model;

/**
 * {
 *     “reviewId”: 123,
 *     “content”: “This film is sooo baad.”,
 *     “isPositive”: false,
 *     “userId”: 123, // Пользователь
 *     “filmId”: 2, // Фильм
 *     “useful”: 20 // рейтинг полезности
 * }
 */
public class Review {
    private long reviewId;
    private String content;
    private boolean isPositive;
    private long userId;
    private long filmId;
    private int useful;
}
