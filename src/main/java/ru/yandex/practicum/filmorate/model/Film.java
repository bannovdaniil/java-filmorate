package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *     целочисленный идентификатор — id;
 *     название — name;
 *     описание — description;
 *     дата релиза — releaseDate;
 *     продолжительность фильма — duration.
 */

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDateTime releaseDate;
    private long duration;

}
