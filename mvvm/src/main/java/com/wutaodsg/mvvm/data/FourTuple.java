package com.wutaodsg.mvvm.data;

/**
 * 四元组对象。
 */

public class FourTuple<A, B, C, D> extends ThreeTuple<A, B, C> {

    public final D d;


    public FourTuple(A a, B b, C c, D d) {
        super(a, b, c);
        this.d = d;
    }
}
