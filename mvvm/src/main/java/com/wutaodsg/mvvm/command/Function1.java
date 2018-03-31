package com.wutaodsg.mvvm.command;

/**
 * 有一个参数的函数。
 */
public interface Function1<T, R> {

    R call(T t);
}
