package com.wutaodsg.androidmvvm.viewmodel;

import android.databinding.ObservableField;

import com.android.databinding.library.baseAdapters.BR;
import com.wutaodsg.mvvm.core.BaseViewModel;
import com.wutaodsg.mvvm.core.annotation.BindVariable;


/**
 * Created by wutao on 2018/4/5.
 */

public class TopFragmentViewModel extends BaseViewModel {

    @BindVariable(BR.text)
    private final ObservableField<String> mText = new ObservableField<>("");


    public String getText() {
        return mText.get();
    }
}
