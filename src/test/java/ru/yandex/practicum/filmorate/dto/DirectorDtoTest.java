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
import java.util.Set;

class DirectorDtoTest {
    private DirectorDto directorDto;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @BeforeEach
    void beforeEach() {
        directorDto = new DirectorDto();
        directorDto.setId(1);
        directorDto.setName("TestDirector #1");
    }

    @DisplayName("Name validation")
    @ParameterizedTest
    @CsvSource({
            "'', 1, name should not be blank",
            "'null', 2, не должно равняться null",
            "ivan, 0, OK",
    })
    void checkNameValidation(String testLogin, int expectSize, String expected) {
        if ("null".equals(testLogin)) {
            directorDto.setName(null);
        } else {
            directorDto.setName(testLogin);
        }
        Set<ConstraintViolation<DirectorDto>> violations = validator.validate(directorDto);

        Assertions.assertEquals(expectSize, violations.size());
    }

}