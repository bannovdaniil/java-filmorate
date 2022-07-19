package ru.yandex.practicum.filmorate.model;

import lombok.*;

/**
 * {
 * “reviewId”: 123,
 * “content”: “This film is sooo baad.”,
 * “isPositive”: false,
 * “userId”: 123, // Пользователь
 * “filmId”: 2, // Фильм
 * “useful”: 20 // рейтинг полезности
 * }
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private long reviewId;
    private String content;
    private boolean positive;
    private long userId;
    private long filmId;
    private int useful;
}
