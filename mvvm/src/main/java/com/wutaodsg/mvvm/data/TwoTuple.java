package com.wutaodsg.mvvm.data;

/**
 * 两元组，用来存放两种不同类型的数据。
 */

public class TwoTuple<A, B> {

    public final A a;
    public final B b;


    public TwoTuple(A a, B b) {
        this.a = a;
        this.b = b;
    }
}
