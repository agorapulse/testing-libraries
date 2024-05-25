package com.agorapulse.testing.where4j;

import org.junit.jupiter.api.TestFactory;

import static com.agorapulse.testing.where4j.Expectations.*;
import static org.junit.jupiter.api.Assertions.*;

class ExpectationsTest {

    @TestFactory
    Expectations basicTestWithThreeVariablesAndExplicitEvaluation() {
        Calculator calculator = new Calculator();

        return forEach(
            header("a", "b", "c"),
            row(2, 3, 5),
            row(3, 5, 8),
            row(4, 7, 11)
        ).expect("#a + #b = #c", (a, b, c) ->
            calculator.add(a, b) == c
        );
    }

    @TestFactory
    Expectations basicTestWithThreeVariablesAndExplicitEvaluationWithMultipleLines() {
        Calculator calculator = new Calculator();

        return forEach(
            header("a", "b", "c"),
            row(2, 3, 5),
            row(3, 5, 8),
            row(4, 7, 11)
        ).expect("#a + #b = #c", (a, b, c) ->
            calculator.add(a, b) == c
            && calculator.subtract(c, a) == b
            && calculator.subtract(c, b) == a
        );
    }

    @TestFactory
    Expectations basicTestWithThreeVariablesAndAssertion() {
        Calculator calculator = new Calculator();

        return forEach(
            header("a", "b", "c"),
            row(2, 3, 5),
            row(3, 5, 8),
            row(4, 7, 11)
        ).verify("#a + #b = #c", (a, b, c) ->
            assertEquals(c, calculator.add(a, b))
        );
    }

}
