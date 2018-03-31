package com.wutaodsg.mvvm.data;

/**
 * 三元组对象
 */

public class ThreeTuple<A, B, C> extends TwoTuple<A, B> {

    public final C c;


    public ThreeTuple(A a, B b, C c) {
        super(a, b);
        this.c = c;
    }
}
