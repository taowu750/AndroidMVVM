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
 */

public abstract class BaseMVVMFragment<VM extends BaseViewModel, DB extends ViewDataBinding>
        extends Fragment implements CoreView<VM, DB> {

    private BaseViewProxy<VM, DB> mBaseViewProxy;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResId(), container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBaseViewProxy = new BaseViewProxy<>(this, this);

        beforeCreateWithArgs();
        Bundle args = getArguments();
        if (args != null) {
            onCreateWithArgs(args);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBaseViewProxy.clear();
        mBaseViewProxy = null;
    }


    /**
     * 在 {@link #onCreateWithArgs(Bundle)} 方法之前调用。
     */
    @CallSuper
    protected void beforeCreateWithArgs() {

    }

    /**
     * 当这个 Fragment 被创建时使用了 {@link #setArguments(Bundle)} 方法设置了
     * Bundle 参数，Bundle 将会传递到这个方法中。
     * <p>
     * 这个方法在与这个 Fragment 相关联的 Activity 被创建之后调用。
     *
     * @param args {@link #getArguments()} 的返回值
     */
    protected void onCreateWithArgs(@NonNull Bundle args) {

    }

    @Override
    public void beforeBindViewModel() {

    }

    @Override
    @NonNull
    public final VM getViewModel() {
        return mBaseViewProxy.getViewModel();
    }

    @Override
    public final void setViewModel(@NonNull VM viewModel) {
        mBaseViewProxy.setViewModel(viewModel);
    }

    @Override
    @NonNull
    public final DB getDataBinding() {
        return mBaseViewProxy.getDataBinding();
    }
}
