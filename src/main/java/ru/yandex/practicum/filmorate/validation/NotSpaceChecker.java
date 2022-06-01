package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.constant.FilmCheckDate;

import javax.validation.Payload;

public @interface NotSpaceChecker {
    String message() default "Can't use space";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
