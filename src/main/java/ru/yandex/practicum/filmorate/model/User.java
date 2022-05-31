package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

/**
 *     целочисленный идентификатор — id;
 *     электронная почта — email;
 *     логин пользователя — login;
 *     имя для отображения — name;
 *     дата рождения — birthday.
 */

@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
