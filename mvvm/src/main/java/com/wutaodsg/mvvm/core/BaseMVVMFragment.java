package com.wutaodsg.mvvm.core;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
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

        mBaseViewProxy = new BaseViewProxy<>(this, this, newViewModel());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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


    private void assertBaseViewProxy() {
        if (mBaseViewProxy == null) {
            throw new IllegalStateException(getClass().getName() + ": " +
                    "The corresponding ViewModel and DataBinding are not bound." +
                    "Because the \"" + BaseMVVMFragment.class.getName() + ".onCreate\" has not been fully executed");
        }
    }
}
