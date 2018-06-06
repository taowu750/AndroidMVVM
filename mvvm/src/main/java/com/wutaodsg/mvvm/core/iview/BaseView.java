package com.wutaodsg.mvvm.core.iview;

import android.arch.lifecycle.Lifecycle;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.wutaodsg.mvvm.core.BaseViewModel;

/**
 * <p>
 * MVVM 中 V 层 View 的基础接口。定义了 View 的基本功能。
 * </p>
 * <p>
 * 我们可以通过 BaseView 对象获取它所绑定的 ViewModel 和 DataBinding 对象。
 * </p>
 * <p>
 * BaseView 还具有生命周期回调方法，会在适当的时候被回调。
 * </p>
 * <p>
 * BaseView 只做两件事，一件是根据 ViewModel 中存储的状态渲染界面，另外一件是将用户的操作转发给 ViewModel。
 * </p>
 */

public interface BaseView<VM extends BaseViewModel, DB extends ViewDataBinding> {

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    /**
     * 返回当前的生命周期状态。
     *
     * @return 参见 {@link Lifecycle.State}
     */
    Lifecycle.State getCurrentState();

    /**
     * 返回当前的生命周期事件。
     *
     * @return 参见 {@link Lifecycle.Event}
     */
    Lifecycle.Event getCurrentEvent();

    /**
     * 获取 View 对象所绑定的 ViewModel 对象。它的返回值不能为空。
     *
     * @return ViewModel 对象
     */
    @NonNull
    VM getViewModel();

    /**
     * 返回 View 对象所绑定的 DataBinding 对象。
     *
     * @return DataBinding 对象
     */
    @NonNull
    DB getDataBinding();
}
