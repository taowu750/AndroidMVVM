package com.wutaodsg.mvvm.core;

import android.arch.lifecycle.LifecycleOwner;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;

/**
 * 扩展了基础 View 接口的功能，从而和 ViewModel、DataBinding Layout XML 具有了
 * 更多的联系。
 * <p>
 * 此外它还扩展了 LifecycleOwner 接口，从而可以与 Android 生命周期组件关联。
 */

public interface CoreView<VM extends BaseViewModel, DB extends ViewDataBinding>
        extends BaseView<VM, DB>, LifecycleOwner {

    /**
     * 在 View 绑定 ViewModel 之前做一些操作<br/>
     * 你可以在这个方法中通过 {@link android.arch.lifecycle.ViewModelProviders} 的
     * {@code of(FragmentActivity/Activity).get(Class)} 方法重新创建你想要的 ViewModel
     * 对象（需要注意的是，此对象必须是 BaseViewModel 的子类），然后调用 {@link #setViewModel(BaseViewModel)}
     * 方法设置你所创建的 ViewModel 对象。
     */
    void beforeBindViewModel();

    /**
     * 获取 View 对象的 DataBinding Layout XML 文件 id。
     *
     * @return DataBinding Layout XML 文件 id
     */
    @LayoutRes
    int getLayoutResId();
}
