package com.agorapulse.testing.where4j;

import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

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
    Expectations basicTestFluent() {
        Calculator calculator = new Calculator();

        return given("a", "b", "c")
            .are(2, 3, 5)
            .and(3, 5, 8)
            .and(4, 7, 11)
            .expect("#a + #b = #c", (a, b, c) -> calculator.add(a, b) == c);
    }

    @TestFactory
    Expectations basicTestWithStreams() {
        Calculator calculator = new Calculator();

        return forEach(
            "a", Stream.of(2, 3, 4),
            "b", Stream.of(3, 5, 7),
            "c", Stream.of(5, 8, 11)
        ).expect("#a + #b = #c", (a, b, c) ->
            calculator.add(a, b) == c
        );
    }

    @TestFactory
    Expectations basicTestWithIterables() {
        Calculator calculator = new Calculator();

        return forEach(
            "a", Arrays.asList(2, 3, 4),
            "b", Arrays.asList(3, 5, 7),
            "c", Arrays.asList(5, 8, 11)
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

    @TestFactory
    Expectations basicTestWithList() {
        return forEach(
            header("list", "a", "b"),
            row(Arrays.asList(1, 2, 3), 1, 5),
            row(Arrays.asList(1, 2, 3), 1, 1L)
        ).expect("#list contains #a but not #b", (list, a, b) ->
            list.contains(a) && !list.contains(b)
        );
    }

    @TestFactory
    Expectations basicTestWithMap() {
        return forEach(
            header("map", "key", "value"),
            row(Collections.singletonMap("foo", "bar"), "foo", "bar")
        ).expect("#map contains #key with value #value", (map, key, value) ->
            map.containsKey(key) && Objects.equals(map.get(key), value)
        );
    }

}
