package com.wutaodsg.androidmvvm.view;

import android.os.Bundle;
import android.util.Log;

import com.wutaodsg.androidmvvm.R;
import com.wutaodsg.androidmvvm.databinding.NavHeaderBinding;
import com.wutaodsg.androidmvvm.viewmodel.NavHeaderViewVM;
import com.wutaodsg.mvvm.core.ChildView;
import com.wutaodsg.mvvm.core.ViewModelType;

/**
 * Created by wutao on 2018/5/4.
 */
@ViewModelType(NavHeaderViewVM.class)
public class NavHeaderView extends ChildView<NavHeaderViewVM, NavHeaderBinding> {

    private static final String TAG = MainActivity.TAG_PREFIX + "NavHeaderView";
    
    
    @Override
    public int getLayoutResId() {
        return R.layout.nav_header;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChildViewActivity parent = getParentActivity(ChildViewActivity.class);
        Log.d(TAG, "onCreate: parent: " + parent);
        Log.d(TAG, "onCreate: context: " + getContext());
        Log.d(TAG, "onCreate: container: " + getContainer());
    }
}
