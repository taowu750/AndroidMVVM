package com.wutaodsg.androidmvvm.viewmodel;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.android.databinding.library.baseAdapters.BR;
import com.wutaodsg.androidmvvm.R;
import com.wutaodsg.mvvm.core.BaseViewModel;
import com.wutaodsg.mvvm.core.annotation.BindVariable;

/**
 * Created by wutao on 2018/5/4.
 */

public class NavHeaderViewVM extends BaseViewModel {

    @BindVariable(BR.iconId)
    private final ObservableInt mIconId = new ObservableInt(R.drawable.nav_icon);

    @BindVariable(BR.name)
    private final ObservableField<String> mName = new ObservableField<>("taowu750");

    @BindVariable(BR.mail)
    private final ObservableField<String> mMail = new ObservableField<>("2691320794@qq.com");
}
