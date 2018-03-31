package com.wutaodsg.mvvm.core;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

/**
 * UI 感知组件，用来感知界面的可见情况。
 * <p>
 * 当界面由不可见变为可见时，onVisible 方法被回调；
 * 当界面由可见变为不可见时，onInvisible 方法被回调。
 */

public interface UIAwareComponent extends LifecycleObserver {

    /**
     * 当界面由不可见变为可见时，回调此方法
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onVisible();

    /**
     * 当界面由可见变为不可见时，回调此方法
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onInvisible();
}
