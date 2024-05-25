package com.agorapulse.testing.where4j.dsl;

@FunctionalInterface
public interface Assertion3<A, B, C> {

    boolean verify(A a, B b, C c);

}
