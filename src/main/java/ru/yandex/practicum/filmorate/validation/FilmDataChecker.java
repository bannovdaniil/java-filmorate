package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.constant.FilmCheckDate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = FilmDataCheckerValidation.class)
public @interface FilmDataChecker {
    String value() default FilmCheckDate.OLDEST_DATE;

    String message() default FilmCheckDate.OLDEST_DATE_ERROR_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
