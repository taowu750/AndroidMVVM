package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.support.v4.widget.NestedScrollView;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import com.wutaodsg.mvvm.command.ReplyCommand;

/**
 * {@link ScrollView} 和 {@link NestedScrollView} 的 BindingAdapter。
 */

public class ScrollViewBindingAdapter {

    /**
     * 设置 NestedScrollView 的滚动事件监听命令，命令为 onScrollChangeCommand，
     * 参数为 {@link NestedScrollChangeDataWrapper}，表示滚动数据。
     *
     * @param nestedScrollView      {@link NestedScrollView} 对象
     * @param onScrollChangeCommand {@link ReplyCommand} 对象，滚动事件监听命令
     */
    @BindingAdapter({"onScrollChangeCommand"})
    public static void setOnScrollChangeCommand(final NestedScrollView nestedScrollView,
                                                final ReplyCommand<NestedScrollChangeDataWrapper>
                                                        onScrollChangeCommand) {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (onScrollChangeCommand != null) {
                    onScrollChangeCommand.execute(new NestedScrollChangeDataWrapper(scrollX, scrollY, oldScrollX,
                            oldScrollY));
                }
            }
        });
    }

    /**
     * 设置 scrollView 的滚动事件监听命令，命令为 onScrollChangeCommand，
     * 参数为 {@link ScrollChangeDataWrapper}，表示滚动数据。
     *
     * @param scrollView            {@link ScrollView} 对象
     * @param onScrollChangeCommand {@link ReplyCommand} 对象，滚动事件监听命令
     */
    @BindingAdapter({"onScrollChangeCommand"})
    public static void setOnScrollChangeCommand(final ScrollView scrollView,
                                                final ReplyCommand<ScrollChangeDataWrapper> onScrollChangeCommand) {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (onScrollChangeCommand != null) {
                    onScrollChangeCommand.execute(new ScrollChangeDataWrapper(scrollView.getScrollX(), scrollView
                            .getScrollY()));
                }
            }
        });
    }


    /**
     * ScrollView 的滚动事件数据包装器对象，用来封装 ScrollView 滚动事件处理方法中的参数。
     * <p>
     * 1. 属性 {@link #scrollX} 表示当前滚动位置的横坐标；
     * 2. 属性 {@link #scrollY} 表示当前滚动位置的纵坐标；
     */
    public static class ScrollChangeDataWrapper {

        public final float scrollX;
        public final float scrollY;


        public ScrollChangeDataWrapper(float scrollX, float scrollY) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
        }
    }

    /**
     * NestedScrollView 的滚动事件数据包装器对象，用来封装 NestedScrollView 滚动事件处理方法中的参数。
     * <p>
     * 1. 属性 {@link #scrollX} 表示当前滚动位置的横坐标；
     * 2. 属性 {@link #scrollY} 表示当前滚动位置的纵坐标；
     * 3. 属性 {@link #oldScrollX} 表示原来滚动位置的横坐标；
     * 4. 属性 {@link #oldScrollY} 表示原来滚动位置的纵坐标；
     */
    public static class NestedScrollChangeDataWrapper {

        public final int scrollX;
        public final int scrollY;
        public final int oldScrollX;
        public final int oldScrollY;


        public NestedScrollChangeDataWrapper(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.oldScrollX = oldScrollX;
            this.oldScrollY = oldScrollY;
        }
    }
}
