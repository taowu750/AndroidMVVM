package com.wutaodsg.androidmvvm.view;

import android.os.Bundle;

import com.wutaodsg.androidmvvm.R;
import com.wutaodsg.androidmvvm.databinding.NavHeaderBinding;
import com.wutaodsg.androidmvvm.viewmodel.NavHeaderViewVM;
import com.wutaodsg.mvvm.core.ChildView;
import com.wutaodsg.mvvm.core.annotation.MainViewModel;
import com.wutaodsg.mvvm.util.log.LogUtils;

/**
 * Created by wutao on 2018/5/4.
 */
@MainViewModel(NavHeaderViewVM.class)
public class NavHeaderView extends ChildView<NavHeaderViewVM, NavHeaderBinding> {

    private static final String TAG = "NavHeaderView";

    
    @Override
    public int getLayoutResId() {
        return R.layout.nav_header;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChildViewActivity parent = getParentActivity(ChildViewActivity.class);
        LogUtils.d(TAG, "onCreate: parent: " + parent);
        LogUtils.d(TAG, "onCreate: context: " + getContext());
        LogUtils.d(TAG, "onCreate: container: " + getContainer());
    }

    @Override
    public void onStart() {
        super.onStart();

        LogUtils.d(TAG, "onStart: NavHeaderView onStart");
    }
}
