package com.wutaodsg.mvvm.core;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * 作为 MVVM 模式 V 层中的基类 Activity。
 * <p>
 * 它不仅仅作为 View 对象，也是一个弱化的控制器（Controller），所以我们需要把界面上
 * 控件的命令（Command）放在它里面。
 */

public abstract class BaseMVVMActivity<VM extends BaseViewModel, DB extends ViewDataBinding>
        extends AppCompatActivity implements CoreView<VM, DB> {

    private BaseViewProxy<VM, DB> mBaseViewProxy;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseViewProxy = new BaseViewProxy<>(this, this, newViewModel());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaseViewProxy.clear();
        mBaseViewProxy = null;
    }



    /**
     * 获取与这个 Activity 绑定的 ViewModel 对象。
     * <p>
     * 如果 {@link BaseMVVMActivity} 还未创建成功，也就是它的 {@link #onCreate(Bundle)} 方法还未
     * 完全执行成，则会抛出 {@link IllegalStateException} 异常。
     *
     * @return 与这个 Activity 绑定的 ViewModel 对象
     * @throws IllegalStateException 如果 {@link BaseMVVMActivity} 还未创建成功，抛出此异常
     */
    @Override
    @NonNull
    public final VM getViewModel() {
        assertBaseViewProxy();
        return mBaseViewProxy.getViewModel();
    }

    /**
     * 获取与这个 Activity 绑定的 DataBinding 对象。
     * <p>
     * 如果 {@link BaseMVVMActivity} 还未创建成功，也就是它的 {@link #onCreate(Bundle)} 方法还未
     * 完全执行成，则会抛出 {@link IllegalStateException} 异常。
     *
     * @return 与这个 Activity 绑定的 DataBinding 对象
     * @throws IllegalStateException 如果 {@link BaseMVVMActivity} 还未创建成功，抛出此异常
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
                    "Because the \"" + BaseMVVMActivity.class.getName() + ".onCreate\" has not been fully executed");
        }
    }
}
