package ru.yandex.practicum.filmorate.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

class DtoFilmTest {
    private DtoFilm dtoFilm;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @BeforeEach
    void beforeEach() {
        dtoFilm = new DtoFilm();
        dtoFilm.setId(1);
        dtoFilm.setName("Ivan");
        dtoFilm.setDescription("Description");
        dtoFilm.setReleaseDate(LocalDate.of(1977, 7, 10));
        dtoFilm.setDuration(1);
    }

    @DisplayName("Name validation")
    @ParameterizedTest
    @CsvSource({
            "'', 1, name should not be blank",
            "null, 1, name should not be blank",
            "'Stars wars', 0, OK"
    })
    void checkNameValidation(String testName, int expectSize, String expected) {
        if ("null".equals(testName)) {
            testName = null;
        }
        dtoFilm.setName(testName);
        Set<ConstraintViolation<DtoFilm>> violations = validator.validate(dtoFilm);

        Assertions.assertEquals(expectSize, violations.size());
        if (!violations.isEmpty()) {
            Assertions.assertEquals(expected, violations.iterator().next().getMessage());
        }
    }

    @DisplayName("Description validation")
    @ParameterizedTest
    @CsvSource({
            "0, 0, OK",
            "1, 0, OK",
            "199, 0, OK",
            "200, 0, OK",
            "201, 1, Description length must be less then 200"
    })
    void checkDescriptionValidation(int lengthDiscription, int expectSize, String expected) {
        String testDiscription = "a".repeat(lengthDiscription);

        dtoFilm.setDescription(testDiscription);
        Set<ConstraintViolation<DtoFilm>> violations = validator.validate(dtoFilm);

        Assertions.assertEquals(expectSize, violations.size());
        if (!violations.isEmpty()) {
            Assertions.assertEquals(expected, violations.iterator().next().getMessage());
        }
    }

    @DisplayName("ReleaseDate")
    @ParameterizedTest
    @CsvSource({
            "1800-10-10, 1, Data must be after 1895-12-28",
            "1895-12-27, 1, Data must be after 1895-12-28",
            "1895-12-28, 1, Data must be after 1895-12-28",
            "1895-12-29, 0, OK",
            "2021-12-29, 0, OK",
    })
    void checkReleaseDateValidation(LocalDate releaseDate, int expectSize, String expected) {
        dtoFilm.setReleaseDate(releaseDate);
        Set<ConstraintViolation<DtoFilm>> violations = validator.validate(dtoFilm);

        Assertions.assertEquals(expectSize, violations.size());
        if (!violations.isEmpty()) {
            Assertions.assertEquals(expected, violations.iterator().next().getMessage());
        }
    }

}