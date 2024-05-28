package com.agorapulse.testing.where4j;

import com.agorapulse.testing.where4j.dsl.Headers3;
import com.agorapulse.testing.where4j.dsl.Row3;
import com.agorapulse.testing.where4j.dsl.Where3;
import com.agorapulse.testing.where4j.dsl.Zip3;
import org.junit.jupiter.api.DynamicContainer;

import java.util.Arrays;
import java.util.stream.Stream;

public interface Expectations extends Iterable<DynamicContainer> {

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

    static <A, B, C> Where3<A, B, C> forEach(
        String headerA,
        Iterable<A> valuesA,
        String headerB,
        Iterable<B> valuesB,
        String headerC,
        Iterable<C> valuesC
    ) {
        return new Where3<>(header(headerA, headerB, headerC), new Zip3<>(valuesA.iterator(), valuesB.iterator(), valuesC.iterator()));
    }

    static <A, B, C> Where3<A, B, C> forEach(
        String headerA,
        Stream<A> valuesA,
        String headerB,
        Stream<B> valuesB,
        String headerC,
        Stream<C> valuesC
    ) {
        return new Where3<>(header(headerA, headerB, headerC), new Zip3<>(valuesA.iterator(), valuesB.iterator(), valuesC.iterator()));
    }

}
