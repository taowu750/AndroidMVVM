package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import com.wutaodsg.mvvm.command.ReplyCommand;

/**
 * Created by wutao on 2018/3/8.
 */

public class SwipeRefreshLayoutBindingAdapter {

    @BindingAdapter({"onRefreshCommand"})
    public static void setOnRefreshCommand(final SwipeRefreshLayout swipeRefreshLayout,
                                           final ReplyCommand onRefreshCommand) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (onRefreshCommand != null) {
                    onRefreshCommand.execute();
                }
            }
        });
    }
}
