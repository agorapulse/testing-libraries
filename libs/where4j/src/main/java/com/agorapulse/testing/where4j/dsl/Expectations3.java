package com.agorapulse.testing.where4j.dsl;

import com.agorapulse.testing.where4j.Expectations;
import org.junit.jupiter.api.DynamicTest;

import java.util.Iterator;

class Expectations3<A, B, C> implements Expectations {

    private final Where3<A, B, C> where;
    private final String template;
    private final Assertion3<A, B, C> verification;

    Expectations3(Where3<A, B, C> where, String template, Assertion3<A, B, C> verification) {
        this.where = where;
        this.template = template;
        this.verification = verification;
    }

    @Override
    public Iterator<DynamicTest> iterator() {
        return where.generateTests(template, verification);
    }

}
