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
        mBaseViewProxy = new BaseViewProxy<>(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaseViewProxy.clear();
        mBaseViewProxy = null;
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
