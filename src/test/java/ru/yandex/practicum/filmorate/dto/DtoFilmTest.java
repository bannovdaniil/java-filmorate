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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class DtoFilmTest {
    private DtoFilm dtoFilm;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @BeforeEach
    void beforeEach() {
        dtoFilm = new DtoFilm();
        dtoFilm.setId(1L);
        dtoFilm.setName("Ivan");
        dtoFilm.setDescription("Description");
        dtoFilm.setReleaseDate(LocalDate.of(1977, 7, 10));
        dtoFilm.setDuration(1L);
    }

    @DisplayName("Name validation")
    @ParameterizedTest
    @CsvSource({
            "'', name should not be blank",
            "null, name should not be blank",
            "'Stars wars', OK"
    })
    void checkNameValidation(String testName, String expectedErrorMessage) {
        if ("null".equals(testName)) {
            testName = null;
        }
        dtoFilm.setName(testName);
        List<String> violations = validator.validate(dtoFilm)
                .stream().map(ConstraintViolation::getMessage)
                .filter(expectedErrorMessage::equals)
                .collect(Collectors.toList());

        if (!violations.isEmpty()) {
            Assertions.assertEquals(expectedErrorMessage, violations.get(0));
        }
    }

    @DisplayName("Description validation")
    @ParameterizedTest
    @CsvSource({
            "0, OK",
            "1, OK",
            "199, OK",
            "200, OK",
            "201, Description length must be less then 200"
    })
    void checkDescriptionValidation(int lengthDiscription, String expectedErrorMessage) {
        String testDiscription = "a".repeat(lengthDiscription);

        dtoFilm.setDescription(testDiscription);
        List<String> violations = validator.validate(dtoFilm)
                .stream().map(ConstraintViolation::getMessage)
                .filter(expectedErrorMessage::equals)
                .collect(Collectors.toList());

        if (!violations.isEmpty()) {
            Assertions.assertEquals(expectedErrorMessage, violations.get(0));
        }
    }

    @DisplayName("ReleaseDate")
    @ParameterizedTest
    @CsvSource({
            "1800-10-10, Data must be after 1895-12-28",
            "1895-12-27, Data must be after 1895-12-28",
            "1895-12-28, Data must be after 1895-12-28",
            "1895-12-29, OK",
            "2021-12-29, OK",
    })
    void checkReleaseDateValidation(LocalDate releaseDate, String expectedErrorMessage) {
        dtoFilm.setReleaseDate(releaseDate);
        List<String> violations = validator.validate(dtoFilm)
                .stream().map(ConstraintViolation::getMessage)
                .filter(expectedErrorMessage::equals)
                .collect(Collectors.toList());

        if (!violations.isEmpty()) {
            Assertions.assertEquals(expectedErrorMessage, violations.get(0));
        }
    }

}