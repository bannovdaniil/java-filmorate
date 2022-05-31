package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.constant.DateTimeFormatString;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FilmDataCheckerValidation implements ConstraintValidator<FilmDataChecker, LocalDate> {
    private LocalDate startDate;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormatString.DATE_TIME_FORMATTER);

    @Override
    public void initialize(FilmDataChecker filmDataChecker) {
        ConstraintValidator.super.initialize(filmDataChecker);
        this.startDate = LocalDate.parse(filmDataChecker.value(), dateTimeFormatter);
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return false;
        }
        return localDate.isAfter(this.startDate);
    }
}
