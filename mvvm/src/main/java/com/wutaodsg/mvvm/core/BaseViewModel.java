package com.wutaodsg.mvvm.core;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

/**
 * MVVM 模式中 VM 层的基类，它继承了 Android 框架中的 ViewModel。
 * <p>
 * ViewModel 只做和业务逻辑和业务数据相关的事，不做任何和 UI 相关的事情，ViewModel
 * 层不会持有任何控件的引用，更不会在 ViewModel 中通过 UI 控件的引用去做更新 UI 的事情。
 * <p>
 * ViewModel 也只做两件事。一方面提供 observable properties 给 View 观察，
 * 一方面提供 functions 给 View 调用，通常会导致 observable properties 的改变，以及带来一些额外状态。
 * <p>
 * 我们需要把界面上的数据绑定（Data Binding）、依赖属性（Dependency）放在 ViewModel 中。并向
 * View 提供关于这些数据和属性状态改变时所发生事件的 functions。
 */

public class BaseViewModel extends ViewModel {

    private Context mContext;


    /**
     * 绑定一个 Context 对象，这个 Context 由 V 层对象提供。<br/>
     * 它会在 V 层对象被创建的时候调用，并绑定 Context。
     *
     * @param context Context 对象
     */
    @CallSuper
    public void onAttach(@NonNull Context context) {
        mContext = context;
    }

    /**
     * 解绑 Context。<br/>
     * 它会在 V 层对象被销毁时被调用，并解绑 Context。
     */
    @CallSuper
    public void onDetach() {
        mContext = null;
    }

    /**
     * 返回绑定在这个 ViewModel 上的 Context 对象。
     *
     * @return Context 对象
     */
    public final Context getContext() {
        return mContext;
    }
}
