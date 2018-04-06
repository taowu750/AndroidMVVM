package com.wutaodsg.mvvm.util.vmeventbus;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

/**
 * {@link ViewModelScheduler} 的实现类，面向 Android 的 Handler、Looper，
 * 提供线程环境。
 */

public class AndroidViewModelScheduler implements ViewModelScheduler {

    private Handler mHandler;


    public AndroidViewModelScheduler(@NonNull Handler handler, @NonNull String name) {
        mHandler = handler;
        mHandler.getLooper().getThread().setName(name);
    }

    public AndroidViewModelScheduler(@NonNull Looper looper, @NonNull String name) {
        mHandler = new Handler(looper);
        looper.getThread().setName(name);
    }


    @Override
    public void schedule(@NonNull Runnable task) {
        Message.obtain(mHandler, task)
                .sendToTarget();
    }
}
