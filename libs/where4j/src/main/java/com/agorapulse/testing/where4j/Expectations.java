package com.agorapulse.testing.where4j;

import com.agorapulse.testing.where4j.dsl.*;
import org.junit.jupiter.api.DynamicTest;

import java.util.Arrays;

public interface Expectations extends Iterable<DynamicTest> {

    static Headers3 header(String nameA, String nameB, String nameC) {
        return new Headers3(nameA, nameB, nameC);
    }

    static <A, B, C>Row3<A, B, C> row(A a, B b, C c) {
        return new Row3<>(a, b, c);
    }

    @SafeVarargs
    static <A, B, C> Where3<A, B, C> forEach(Headers3 headers, Row3<A, B, C> first, Row3<A, B, C> ... rest) {
        return new Where3<>(headers, first, Arrays.asList(rest));
    }

}
