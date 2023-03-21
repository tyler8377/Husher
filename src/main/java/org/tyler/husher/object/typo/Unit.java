package org.tyler.husher.object.typo;

public class Unit<A> {

    private final A a;

    public Unit(A a) {
        this.a = a;
    }

    public A getA() {
        return a;
    }
}
