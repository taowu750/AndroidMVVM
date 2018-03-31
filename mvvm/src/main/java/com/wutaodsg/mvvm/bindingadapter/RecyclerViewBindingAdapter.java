package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import com.wutaodsg.mvvm.command.ReplyCommand;


/**
 * Created by wutao on 2018/3/8.
 */

public class RecyclerViewBindingAdapter {

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
