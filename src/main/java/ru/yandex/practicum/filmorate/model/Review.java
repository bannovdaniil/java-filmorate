package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

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
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    private int useful;
}
