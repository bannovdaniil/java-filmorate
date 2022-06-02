package ru.yandex.practicum.filmorate.validation;

import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.hibernate.validator.internal.util.annotation.AnnotationFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import ru.yandex.practicum.filmorate.constant.FilmCheckDate;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

class FilmDataCheckerValidationTest {
    private FilmDataCheckerValidation filmDataCheckerValidation;
    private FilmDataChecker filmDataChecker;
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    private FilmDataChecker createAnnotation(String value, String message) {
        final Map<String, Object> attrs = new HashMap<>();
        if (null != value) {
            attrs.put("value", value);
        }
        if (null != message) {
            attrs.put("message", message);
        }
        var desc = new AnnotationDescriptor
                .Builder<>(FilmDataChecker.class, attrs)
                .build();
        return AnnotationFactory.create(desc);
    }

    @BeforeEach
    void beforeEachTest() {
        filmDataCheckerValidation = new FilmDataCheckerValidation();
        filmDataChecker = createAnnotation(FilmCheckDate.OLDEST_DATE
                , FilmCheckDate.OLDEST_DATE_ERROR_MESSAGE);
    }


    @DisplayName("Date check")
    @ParameterizedTest
    @CsvSource(
            {
                    "1800-10-10, false, then before",
                    "1895-12-27, false, then before",
                    "1895-12-28, false, then equals",
                    "1895-12-29, true, then after",
                    "2021-12-29, true, then after",
            }
    )
    void dateCheck(LocalDate testDate, boolean isCorrect, String message) {
      //  LocalDate gameDate = LocalDateTime.parse(customDateTime, dateTimeFormatter);
        filmDataCheckerValidation.initialize(filmDataChecker);

        Assertions.assertEquals(isCorrect
                , filmDataCheckerValidation.isValid(testDate, constraintValidatorContext)
                , message);
    }

}