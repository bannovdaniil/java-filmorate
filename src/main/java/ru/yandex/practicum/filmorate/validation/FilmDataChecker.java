package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.constant.FilmCheckDate;

import javax.validation.Payload;

public @interface FilmDataChecker {
    String value() default FilmCheckDate.OLDEST_DATE;

    String message() default FilmCheckDate.OLDEST_DATE_ERROR_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
