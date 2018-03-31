package com.wutaodsg.mvvm.command;

/**
 * 一个有一个参数的过程。
 */
public interface Action1<T> {

    void execute(T t);
}
