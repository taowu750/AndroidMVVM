package com.wutaodsg.mvvm.util.vmeventbus;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * {@link ViewModelScheduler} 的实现类，面向 Executor，提供线程环境。
 */

public class ExecutorViewModelScheduler implements ViewModelScheduler {

    private final Executor mExecutor;


    public ExecutorViewModelScheduler(@NonNull Executor executor) {
        mExecutor = executor;
    }


    @Override
    public void schedule(@NonNull Runnable task) {
        mExecutor.execute(task);
    }
}
