package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.support.v4.widget.SwipeRefreshLayout;

import com.wutaodsg.mvvm.command.ReplyCommand;

/**
 * {@link SwipeRefreshLayout} 的 BindingAdapter。
 */

public class SwipeRefreshLayoutBindingAdapter {

    /**
     * 用来获取 refreshing 属性值，这个值反映了 SwipeRefreshLayout 当前是否处于刷新状态。
     *
     * @param swipeRefreshLayout {@link SwipeRefreshLayout} 对象
     * @return 返回 true 表示 SwipeRefreshLayout 当前处于下拉刷新状态，false 则相反
     */
    @InverseBindingAdapter(attribute = "refreshing", event = "refreshingAttrChanged")
    public static boolean isRefreshing(SwipeRefreshLayout swipeRefreshLayout) {
        return swipeRefreshLayout.isRefreshing();
    }

    /**
     * 用来设置 refreshing 属性值，参见 {@link #isRefreshing(SwipeRefreshLayout)}。
     *
     * @param swipeRefreshLayout {@link SwipeRefreshLayout} 对象
     * @param refreshing         当前 SwipeRefreshLayout 是否处于刷新状态
     */
    @BindingAdapter("refreshing")
    public static void setRefreshing(SwipeRefreshLayout swipeRefreshLayout, boolean refreshing) {
        if (swipeRefreshLayout.isRefreshing() != refreshing) {
            swipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    /**
     * 设置 SwipeRefreshLayout 的下拉刷新事件监听命令和反向绑定命令。
     *
     * @param swipeRefreshLayout    {@link SwipeRefreshLayout}
     * @param onRefreshCommand      {@link ReplyCommand}对象，下拉刷新事件监听命令
     * @param refreshingAttrChanged {@link InverseBindingListener} 对象，下拉刷新事件的反向绑定命令
     */
    @BindingAdapter(value = {"onRefreshCommand", "refreshingAttrChanged"}, requireAll = false)
    public static void setRefreshingListener(final SwipeRefreshLayout swipeRefreshLayout,
                                             final ReplyCommand onRefreshCommand,
                                             final InverseBindingListener refreshingAttrChanged) {
        SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (refreshingAttrChanged != null) {
                    refreshingAttrChanged.onChange();
                }
                if (onRefreshCommand != null) {
                    onRefreshCommand.execute();
                }
                setRefreshing(swipeRefreshLayout, true);
            }
        };
        swipeRefreshLayout.setOnRefreshListener(listener);
    }
}
