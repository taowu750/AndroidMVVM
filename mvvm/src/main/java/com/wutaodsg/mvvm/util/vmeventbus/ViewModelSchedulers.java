package com.wutaodsg.mvvm.util.vmeventbus;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 封装多种 {@link ViewModelScheduler} 的实现，提供不同种类的线程环境：
 * <p>
 * 1. {@link #computation()}：专门为计算提供的 ViewModelScheduler，不要把 I/O 操作放在 computation() 中，否则 I/O 等待的时间会浪费 CPU。<br/>
 * 2. {@link #io()}：专门为 IO 提供的 ViewModelScheduler。不要把计算工作放在 io() 中，可以避免创建不必要的线程。<br/>
 * 3. {@link #single()}：只有一个线程的 ViewModelScheduler，它会把所有任务放在一个线程中调度。<br/>
 * <p>
 * 此外，ViewModelSchedulers 还提供了 {@link #from(Executor)} 和 {@link #from(Handler)} 来使得
 * 你能够定制自己的 ViewModelScheduler。
 */

public class ViewModelSchedulers {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int COMPUTATION_CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int COMPUTATION_MAXIMUM_POOL_SIZE = COMPUTATION_CORE_POOL_SIZE * 2 + 1;
    private static final long COMPUTATION_KEEP_ALIVE = 10;


    private static final ViewModelScheduler mComputationSchedulers = new ExecutorViewModelScheduler(new
            ThreadPoolExecutor(
            COMPUTATION_CORE_POOL_SIZE, COMPUTATION_MAXIMUM_POOL_SIZE, COMPUTATION_KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(), new NameThreadFactory("computation")));
    private static final ViewModelScheduler mIOSchedulers = new ExecutorViewModelScheduler(Executors.newCachedThreadPool
            (new NameThreadFactory("io")));
    private static final ViewModelScheduler mSingleSchedulers = new ExecutorViewModelScheduler(Executors
            .newSingleThreadExecutor
            (new NameThreadFactory("single")));

    private static final ViewModelScheduler mAndroidMainSchedulers = new AndroidViewModelScheduler(Looper
            .getMainLooper(),
            "main thread");


    public static ViewModelScheduler computation() {
        return mComputationSchedulers;
    }

    public static ViewModelScheduler io() {
        return mIOSchedulers;
    }

    public static ViewModelScheduler single() {
        return mSingleSchedulers;
    }

    public static ViewModelScheduler mainThread() {
        return mAndroidMainSchedulers;
    }

    public static ViewModelScheduler from(@NonNull Executor executor) {
        return new ExecutorViewModelScheduler(executor);
    }

    public static ViewModelScheduler from(@NonNull Handler handler) {
        return new AndroidViewModelScheduler(handler, handler.getLooper().getThread().getName());
    }


    private static class NameThreadFactory implements ThreadFactory {

        private String mName;

        private AtomicInteger mCount = new AtomicInteger(0);


        public NameThreadFactory(@NonNull String name) {
            mName = name;
        }


        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, mName + "#" + mCount.getAndIncrement());
        }
    }
}
