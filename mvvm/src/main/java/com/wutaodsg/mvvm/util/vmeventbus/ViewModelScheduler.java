package com.wutaodsg.mvvm.util.vmeventbus;

import android.support.annotation.NonNull;

/**
 * 在 {@link ViewModelEventBus} 中，通过这个类为注册的 {@link com.wutaodsg.mvvm.core.BaseViewModel}
 * 提供运行时线程环境，也就是说，注册的 ViewModel 想要让自己的回调命令运行在
 * 什么线程中，可以参见 {@link ViewModelSchedulers} 查看更多信息。
 */

public interface ViewModelScheduler {

    /**
     * 调度任务。
     *
     * @param task 想要运行的任务
     */
    void schedule(@NonNull Runnable task);
}
