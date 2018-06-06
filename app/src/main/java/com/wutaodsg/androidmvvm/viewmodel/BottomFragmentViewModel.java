package com.wutaodsg.androidmvvm.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.android.databinding.library.baseAdapters.BR;
import com.wutaodsg.androidmvvm.constant.ViewModelEventTags;
import com.wutaodsg.mvvm.command.Action1;
import com.wutaodsg.mvvm.core.BaseViewModel;
import com.wutaodsg.mvvm.core.annotation.BindVariable;
import com.wutaodsg.mvvm.util.log.LogUtils;
import com.wutaodsg.mvvm.util.vmeventbus.ViewModelCommand;
import com.wutaodsg.mvvm.util.vmeventbus.ViewModelEventBus;
import com.wutaodsg.mvvm.util.vmeventbus.ViewModelSchedulers;


/**
 * Created by wutao on 2018/4/5.
 */

public class BottomFragmentViewModel extends BaseViewModel {

    private static final String TAG = "BottomFragmentVM";
    

    @BindVariable(BR.text)
    private final ObservableField<String> mText = new ObservableField<>("");


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        boolean result = ViewModelEventBus.getInstance().register(ViewModelEventTags.TEXT, String.class,
                new ViewModelCommand<>(this, new Action1<String>() {
                    @Override
                    public void execute(String s) {
                        setText(s);
                    }
                }, ViewModelSchedulers.mainThread()));
        LogUtils.d(TAG, "onAttach: register: " + result);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        boolean result = ViewModelEventBus.getInstance().unregister(ViewModelEventTags.TEXT, String.class,
                this);
        LogUtils.d(TAG, "onCleared: unregister: " + result);
    }


    public void setText(String text) {
        mText.set(text);
    }
}
