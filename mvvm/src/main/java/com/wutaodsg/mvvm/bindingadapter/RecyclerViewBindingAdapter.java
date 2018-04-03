package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import com.wutaodsg.mvvm.command.ReplyCommand;


/**
 * {@link RecyclerView} 的 BindingAdapter。
 */

public class RecyclerViewBindingAdapter {

    /**
     * 设置 RecyclerView 的滚动监听事件命令。有两个命令：
     * <p>
     * 1. onScrollChangeCommand：滚动事件监听命令，参数 {@link ScrollChangeDataWrapper}，表示滚动数据；<br/>
     * 2. onScrollStateChangedCommand：滚动时状态事件监听命令，参数 {@link Integer}，表示滚动状态。
     *
     * @param recyclerView                {@link RecyclerView} 对象
     * @param onScrollChangeCommand       {@link ReplyCommand} 对象，滚动时事件监听命令
     * @param onScrollStateChangedCommand {@link ReplyCommand} 对象，滚动时状态事件监听命令
     */
    @BindingAdapter(value = {"onScrollChangeCommand", "onScrollStateChangedCommand"}, requireAll = false)
    public static void setOnScrollChangeCommand(final RecyclerView recyclerView,
                                                final ReplyCommand<ScrollChangeDataWrapper> onScrollChangeCommand,
                                                final ReplyCommand<Integer> onScrollStateChangedCommand) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int state;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                state = newState;
                if (onScrollStateChangedCommand != null) {
                    onScrollStateChangedCommand.execute(newState);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (onScrollChangeCommand != null) {
                    onScrollChangeCommand.execute(new ScrollChangeDataWrapper(dx, dy, state));
                }
            }
        });
    }


    /**
     * RecyclerView 的滚动事件数据包装器对象，用来封装 RecyclerView 滚动事件处理方法中的参数。
     * <p>
     * 1. 属性 {@link #scrollX} 表示当前滚动位置的横坐标；
     * 2. 属性 {@link #scrollY} 表示当前滚动位置的纵坐标；
     * 3. 属性 {@link #state} 表示当前的滚动状态。
     */
    public static class ScrollChangeDataWrapper {

        public final float scrollX;
        public final float scrollY;
        public final int state;


        public ScrollChangeDataWrapper(float scrollX, float scrollY, int state) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.state = state;
        }
    }
}
