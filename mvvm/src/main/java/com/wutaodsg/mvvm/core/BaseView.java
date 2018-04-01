package com.wutaodsg.mvvm.core;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;

/**
 * MVVM 中 V 层 View 的基础接口。定义了 View 的基本功能。
 * <p>
 * 我们可以通过 View 对象获取它所绑定的 ViewModel 和 DataBinding 对象。
 * <p>
 * View 只做两件事，一件是根据 ViewModel 中存储的状态渲染界面，另外一件是将用户的操作转发给 ViewModel。
 */

public interface BaseView<VM extends BaseViewModel, DB extends ViewDataBinding> {

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
