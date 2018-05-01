package com.wutaodsg.mvvm.core;

import android.arch.lifecycle.LifecycleOwner;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

/**
 * 扩展了基础 View 接口的功能，从而和 ViewModel、DataBinding Layout XML 具有了
 * 更多的联系。
 * <p>
 * 此外它还扩展了 LifecycleOwner 接口，从而可以与 Android 生命周期组件关联。
 */

public interface CoreView<VM extends BaseViewModel, DB extends ViewDataBinding>
        extends BaseView<VM, DB>, LifecycleOwner {

    /**
     * 用来自定义想要创建的 ViewModel。
     * <p>
     * 你可以在这个方法中通过 {@link android.arch.lifecycle.ViewModelProviders} 的
     * {@code of(FragmentActivity/Activity).get(Class)} 方法重新创建你想要的 ViewModel
     * 对象（需要注意的是，此对象必须是 VM 泛型指定的类型）。
     * <p>
     * 这个方法创建的 ViewModel 在不为 null 的情况下，将会被优先绑定到 View 对象中.
     * 也就是说使用 {@link ViewModelType} 注解指定的 ViewModel 会被此方法创建的
     * ViewModel 覆盖掉。
     * <p>
     * 此方法返回值可以为 null。
     */
    @Nullable
    VM newViewModel();

    /**
     * 这个方法将在 View 绑定 UI 视图（也就是 Layout XML 文件）之前调用。
     * 此时，ViewModel 和 DataBinding 也都还没有绑定。
     */
    void beforeBindView();

    /**
     * 这个方法将在 View 被销毁时（一般是 onDestroy() 方法被调用的时候），
     * 解绑 ViewModel 和 DataBinding 之前被回调。因此这允许你在解绑之前做一些数据保存等的工作。
     */
    void beforeDetach();

    /**
     * 获取 View 对象的 DataBinding Layout XML 文件 id。
     *
     * @return DataBinding Layout XML 文件 id
     */
    @LayoutRes
    int getLayoutResId();
}
