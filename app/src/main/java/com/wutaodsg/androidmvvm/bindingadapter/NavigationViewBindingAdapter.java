package com.wutaodsg.androidmvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import com.wutaodsg.mvvm.command.ResponseCommand;

/**
 * Created by wutao on 2018/5/3.
 */

public class NavigationViewBindingAdapter {

    @BindingAdapter("checkedItem")
    public static void setCheckedItem(NavigationView view, @IdRes int itemId) {
        view.setCheckedItem(itemId);
    }

    @BindingAdapter("onItemSelectedCommand")
    public static void setOnItemSelectCommand(NavigationView view,
                                              final ResponseCommand<MenuItem, Boolean> onItemSelectedCommand) {
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (onItemSelectedCommand != null) {
                    return onItemSelectedCommand.execute(item);
                }
                return false;
            }
        });
    }
}
