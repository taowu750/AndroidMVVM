package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import com.wutaodsg.mvvm.command.ReplyCommand;

/**
 * {@link SwipeRefreshLayout} 的 BindingAdapter。
 */

public class SwipeRefreshLayoutBindingAdapter {

    /**
     * 设置 SwipeRefreshLayout 的刷新事件监听命令，命令为 onRefreshCommand，
     * 在下拉刷新时调用。
     *
     * @param swipeRefreshLayout {@link SwipeRefreshLayout} 对象
     * @param onRefreshCommand   {@link ReplyCommand} 对象，刷新事件监听命令
     */
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
