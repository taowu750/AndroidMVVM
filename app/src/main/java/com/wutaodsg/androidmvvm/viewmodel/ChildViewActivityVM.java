package com.wutaodsg.androidmvvm.viewmodel;

import android.databinding.ObservableInt;

import com.android.databinding.library.baseAdapters.BR;
import com.wutaodsg.androidmvvm.R;
import com.wutaodsg.mvvm.core.BaseViewModel;
import com.wutaodsg.mvvm.core.annotation.BindVariable;

/**
 * Created by wutao on 2018/5/4.
 */

public class ChildViewActivityVM extends BaseViewModel {

    @BindVariable(BR.checkedItem)
    private final ObservableInt mCheckedItem = new ObservableInt(R.id.nav_call);
}
