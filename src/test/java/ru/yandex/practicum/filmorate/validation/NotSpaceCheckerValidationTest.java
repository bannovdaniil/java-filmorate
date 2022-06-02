package ru.yandex.practicum.filmorate.validation;

import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.hibernate.validator.internal.util.annotation.AnnotationFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;

import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.Map;

class NotSpaceCheckerValidationTest {

    private NotSpaceCheckerValidation notSpaceCheckerValidation;
    private NotSpaceChecker notSpaceChecker;
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    private NotSpaceChecker createAnnotation(String message) {
        final Map<String, Object> attrs = new HashMap<>();
        if (null != message) {
            attrs.put("message", message);
        }
        var desc = new AnnotationDescriptor
                .Builder<>(NotSpaceChecker.class, attrs)
                .build();
        return AnnotationFactory.create(desc);
    }

    @BeforeEach
    void beforeEachTest() {
        notSpaceCheckerValidation = new NotSpaceCheckerValidation();
        notSpaceChecker = createAnnotation("Can't use space");
    }

    @DisplayName("Space Validation")
    @ParameterizedTest
    @CsvSource({
            "' ' , false",
            "'\t', false",
            "' Ivan', false",
            "'Ivan ', false",
            "'I van', false",
            "'I\tvan', false",
            "'Ivan', true"
    }
    )
    void spaceValidation(String testName, boolean isCorrect) {
        notSpaceCheckerValidation.initialize(notSpaceChecker);

        Assertions.assertEquals(isCorrect
                , notSpaceCheckerValidation.isValid(testName, constraintValidatorContext));
    }
}