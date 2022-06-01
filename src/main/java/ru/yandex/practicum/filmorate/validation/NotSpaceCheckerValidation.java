package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.constant.RegExpression;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class NotSpaceCheckerValidation implements ConstraintValidator<NotSpaceChecker, String> {


    @Override
    public void initialize(NotSpaceChecker constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String spaceCheckString, ConstraintValidatorContext constraintValidatorContext) {
        return spaceCheckString.matches(RegExpression.ANY_SPACE_REGEX);
    }
}
