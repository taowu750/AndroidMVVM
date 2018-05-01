package com.wutaodsg.mvvm.core;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作为 MVVM 模式 V 层中的基类 Fragment。
 * <p>
 * 它不仅仅作为 View 对象，也是一个弱化的控制器（Controller），所以我们需要把界面上
 * 控件的命令（Command）放在它里面。
 * <p>
 * 它会在 {@link #onActivityCreated(Bundle)} 阶段绑定 ViewModel 和 DataBinding，将在它内部声明的
 * {@link com.wutaodsg.mvvm.command.ReplyCommand} 或 {@link com.wutaodsg.mvvm.command.ResponseCommand}
 * 绑定到界面中（前提是这些 Command 上正确的使用 {@link BindVariable} 指定了 DataBinding Variable）。<br/>
 * 此外，它还会检查是否有 {@link UIAwareComponent} 域，如果有，就将它们绑定到自己的生命周期中。
 * <p>
 * 需要注意的是，由于 BaseMVVMFragment 在自己的 {@link #onActivityCreated(Bundle)} 方法结束后，
 * 绑定过程才会结束，所以在这个回调方法结束之前，不可以使用 {@link #getViewModel()} 或
 * {@link #getDataBinding()} 方法，否则会抛出异常。
 * <p>
 * 典型的，当一个 Activity 界面上有两个 Fragment，其中一个 Fragment 想要获取到另一个 Fragment
 * 的 ViewModel 对象，那么它就不可以在自己的 {@link #onActivityCreated(Bundle)} 方法
 * 中进行这样的操作，因为这个时候，不能够保证另一个 Fragment 已经绑定成功。
 */

public abstract class BaseMVVMFragment<VM extends BaseViewModel, DB extends ViewDataBinding>
        extends Fragment implements CoreView<VM, DB> {

    private BaseViewProxy<VM, DB> mBaseViewProxy;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        beforeBindView();
        return inflater.inflate(getLayoutResId(), container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBaseViewProxy = new BaseViewProxy<>(this, this, newViewModel());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beforeDetach();
        mBaseViewProxy.clear();
        mBaseViewProxy = null;
    }


    /**
     * 获取与这个 Fragment 绑定的 ViewModel 对象。
     * <p>
     * 如果 {@link BaseMVVMFragment} 还未创建成功，也就是它的 {@link #onActivityCreated(Bundle)} 方法还未
     * 完全执行成，则会抛出 {@link IllegalStateException} 异常。
     *
     * @return 与这个 Fragment 绑定的 ViewModel 对象
     * @throws IllegalStateException 如果 {@link BaseMVVMFragment} 还未创建成功，抛出此异常
     */
    @Override
    @NonNull
    public final VM getViewModel() {
        assertBaseViewProxy();
        return mBaseViewProxy.getViewModel();
    }

    /**
     * 获取与这个 Fragment 绑定的 DataBinding 对象。
     * <p>
     * 如果 {@link BaseMVVMFragment} 还未创建成功，也就是它的 {@link #onActivityCreated(Bundle)} 方法还未
     * 完全执行成，则会抛出 {@link IllegalStateException} 异常。
     *
     * @return 与这个 Fragment 绑定的 DataBinding 对象
     * @throws IllegalStateException 如果 {@link BaseMVVMFragment} 还未创建成功，抛出此异常
     */
    @Override
    @NonNull
    public final DB getDataBinding() {
        assertBaseViewProxy();
        return mBaseViewProxy.getDataBinding();
    }

    @Nullable
    @Override
    public VM newViewModel() {
        return null;
    }

    @Override
    @CallSuper
    public void beforeBindView() {

    }

    @Override
    @CallSuper
    public void beforeDetach() {

    }


    private void assertBaseViewProxy() {
        if (mBaseViewProxy == null) {
            throw new IllegalStateException(getClass().getName() + ": " +
                    "The corresponding ViewModel and DataBinding are not bound. " +
                    "Because the \"" + BaseMVVMFragment.class.getName() + ".onActivityCreated(Bundle)\" has not been " +
                    "fully executed");
        }
    }
}
