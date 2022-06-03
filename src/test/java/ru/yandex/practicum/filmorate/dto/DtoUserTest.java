package ru.yandex.practicum.filmorate.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

class DtoUserTest {
    private DtoUser dtoUser;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @BeforeEach
    void beforeEach() {
        dtoUser = new DtoUser();
        dtoUser.setId(1);
        dtoUser.setEmail("a@mail.ru");
        dtoUser.setLogin("ivan1977");
        dtoUser.setName("Ivan");
        dtoUser.setBirthday(LocalDate.of(1977, 7, 10));
    }

    @DisplayName("Email validation")
    @ParameterizedTest
    @CsvSource({
            "'', Can not be blank",
            "null, Can not be blank",
            "email, Is not correct email",
            "@email, Is not correct email",
            "email@, Is not correct email",
            "email@@dd, Is not correct email",
            "'ema il@dd', Is not correct email"
    })
    void checkEmailValidation(String testEmail, String expected) {
        if ("null".equals(testEmail)) {
            testEmail = null;
        }
        dtoUser.setEmail(testEmail);
        Set<ConstraintViolation<DtoUser>> violations = validator.validate(dtoUser);

        Assertions.assertEquals(expected, violations.iterator().next().getMessage());
    }

    @DisplayName("Login validation")
    @ParameterizedTest
    @CsvSource({
            "'', 1, Can not be blank",
            "' ivan', 1, Can't use space",
            "'ivan ', 1, Can't use space",
            "'i\tvan', 1, Can't use space",
            "i van, 1, Can't use space",
            "ivan, 0, OK",
    })
    void checkLoginValidation(String testLogin, int expectSize, String expected) {
        dtoUser.setLogin(testLogin);
        Set<ConstraintViolation<DtoUser>> violations = validator.validate(dtoUser);

        Assertions.assertEquals(expectSize, violations.size());
        if (!violations.isEmpty()) {
            Assertions.assertEquals(expected, violations.iterator().next().getMessage());
        }
    }

    @DisplayName("Birthday validation")
    @Test
    void checkBirthdayValidation() {
        LocalDate nowDate = LocalDate.now().plusDays(1);
        dtoUser.setBirthday(nowDate);
        Set<ConstraintViolation<DtoUser>> violations = validator.validate(dtoUser);

        Assertions.assertEquals("Not yet born", violations.iterator().next().getMessage());
    }

}