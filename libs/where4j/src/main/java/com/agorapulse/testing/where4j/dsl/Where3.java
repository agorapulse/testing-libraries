package com.agorapulse.testing.where4j.dsl;

import com.agorapulse.testing.where4j.Expectations;
import org.junit.jupiter.api.DynamicTest;
import org.opentest4j.AssertionFailedError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Where3<A, B , C> {

    private final List<Row3<A, B, C>> data = new ArrayList<>();
    private final Headers3 headers;

    public Where3(Headers3 headers, Row3<A, B, C> first, Collection<Row3<A, B, C>> rest) {
        this.headers = headers;

        this.data.add(first);
        this.data.addAll(rest);
    }

    public Expectations expect(String template, Assertion3<A, B, C> verification) {
        return new Expectations3<>(this, template, verification);
    }

    public Expectations verify(String template, Verifcation3<A, B, C> verification) {
        return new Expectations3<>(this, template, (a, b, c) -> {
            verification.verify(a, b, c);
            return true;
        });
    }

    Stream<DynamicTest> generateTests(String template, Assertion3<A, B, C> verification) {
        return data.stream().map(row -> {
            String title = template.replace("#" + headers.getA(), String.valueOf(row.getA()));
            title = title.replace("#" + headers.getB(), String.valueOf(row.getB()));
            title = title.replace("#" + headers.getC(), String.valueOf(row.getC()));
            String finalTitle = title;
            return DynamicTest.dynamicTest(
                finalTitle,
                () -> {
                    if (!verification.verify(row.getA(), row.getB(), row.getC())) {
                        throw new AssertionFailedError("Verification failed for " + finalTitle + " with values " + headers.getA() + "=" + row.getA() + ", " + headers.getB() + "=" + row.getB() + ", " + headers.getC() + "=" + row.getC());
                    }
                }
            );
        });
    }
}
