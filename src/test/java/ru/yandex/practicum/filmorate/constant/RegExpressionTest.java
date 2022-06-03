package ru.yandex.practicum.filmorate.constant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RegExpressionTest {

    @DisplayName("RegExpression NoSpace")
    @ParameterizedTest
    @CsvSource(
            {"' ' , true",
                    "'\t', true",
                    "' Ivan', true",
                    "'Ivan ', true",
                    "'I van', true",
                    "'I\tvan', true",
                    "'Ivan', false"
            }
    )
    void regExFoundSpace(String s, boolean expected) {
        Assertions.assertEquals(expected, s.matches(RegExpression.ANY_SPACE_REGEX));
    }

}