package com.github.alien11689.integercalculator;

import static com.github.alien11689.integercalculator.IntegerCalculator.calculate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

public class IntegerCalculatorTest {
    @ParameterizedTest
    @MethodSource("validExpressionsProvider")
    void shouldCalculateForCorrectExpression(String expr, int expected) {
        assertEquals(expected, calculate(expr), () -> "Expression: '" + expr + "' should evaluate to " + expected);
    }

    static Stream<Arguments> validExpressionsProvider() {
        return Stream.of(
                Arguments.of("5", 5),
                Arguments.of("-5", -5),
                Arguments.of("2 + 3", 5),
                Arguments.of("3 * 2 + 1", 7),
                Arguments.of("3 * -2 + 6", 0),
                Arguments.of("0 / 6", 0),
                Arguments.of("1 + 2 * 3", 7),
                Arguments.of("10 - 2 * 5", 0),
                Arguments.of("10 / 2 + 3", 8),
                Arguments.of("6 / 3 * 2 + 1", 5),
                Arguments.of("-5 + 2", -3),
                Arguments.of("-2 + 3 * 4 - 10 / 2", 5),
                Arguments.of("1000000 * 0 + 5", 5),
                Arguments.of("2 + 3 * 4 - 6 / 2", 11),
                Arguments.of("+1 + 2", 3),
                Arguments.of("1 + +2", 3)
        );
    }

    @ParameterizedTest
    @MethodSource("overflowExpressionsProvider")
    void shouldThrowExceptionOnOverflow(String expr) {
        assertThrows(ArithmeticException.class, () -> calculate(expr), "Expression: '" + expr + "'");
    }

    static Stream<String> overflowExpressionsProvider() {
        return Stream.of(
                Integer.MAX_VALUE + " + 1",
                Integer.MIN_VALUE + " - 1",
                Integer.MAX_VALUE + " * 2"
        );
    }

    @ParameterizedTest
    @MethodSource("divisionsByZeroProvider")
    void shouldThrowExceptionOnDivisionByZero(String expr) {
        assertThrows(ArithmeticException.class, () -> calculate(expr), "Expression: '" + expr + "'");
    }

    static Stream<String> divisionsByZeroProvider() {
        return Stream.of(
                "0 / 0",
                "-2 / 0",
                "7 / 0"
        );
    }

    @ParameterizedTest
    @MethodSource("invalidExpressionsProvider")
    @NullSource
    void shouldThrowExceptionOnInvalidExpression(String expr) {
        assertThrows(IllegalArgumentException.class, () -> calculate(expr), "Expression: '" + expr + "'");
    }

    static Stream<String> invalidExpressionsProvider() {
        return Stream.of(
                "",
                "   ",
                "1 5",
                "3 * / 2",
                "4 + 5 -",
                "abc + 5",
                "2+3*4-6/2"
        );
    }
}