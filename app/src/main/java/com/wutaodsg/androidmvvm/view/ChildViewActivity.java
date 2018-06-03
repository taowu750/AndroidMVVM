package com.wutaodsg.androidmvvm.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.android.databinding.library.baseAdapters.BR;
import com.wutaodsg.androidmvvm.R;
import com.wutaodsg.androidmvvm.databinding.ActivityChildViewBinding;
import com.wutaodsg.androidmvvm.viewmodel.ChildViewActivityVM;
import com.wutaodsg.mvvm.command.Function1;
import com.wutaodsg.mvvm.command.ResponseCommand;
import com.wutaodsg.mvvm.core.BaseMVVMActivity;
import com.wutaodsg.mvvm.core.BindVariable;
import com.wutaodsg.mvvm.core.ViewModelType;
import com.wutaodsg.mvvm.util.log.LogUtils;


@ViewModelType(ChildViewActivityVM.class)
//@BindChildView(type = NavHeaderView.class, container = R.id.nav_view)
public class ChildViewActivity extends BaseMVVMActivity<ChildViewActivityVM, ActivityChildViewBinding> {

    private static final String TAG = "ChildViewActivity";
    
    
    @BindVariable(BR.onItemSelectedCommand)
    private final ResponseCommand<MenuItem, Boolean> mOnItemSelectedCommand = new ResponseCommand<>(new Function1<MenuItem, Boolean>() {

        @Override
        public Boolean call(MenuItem menuItem) {
            getDataBinding().drawerLayout.closeDrawers();
            return true;
        }
    });


    @Override
    public int getLayoutResId() {
        return R.layout.activity_child_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.d(TAG, "onCreate: created");
    }

    @Override
    public void onStart() {
        super.onStart();

        bindChildView(NavHeaderView.class, R.id.nav_view);
        LogUtils.d(TAG, "onStart: containsChildView child view: " +
                containsChildView(NavHeaderView.class, R.id.nav_view));
        LogUtils.d(TAG, "onStart: Child View: " + getChildViews()[0]);
    }
}
