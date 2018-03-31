package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.support.v4.widget.NestedScrollView;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import com.wutaodsg.mvvm.command.ReplyCommand;

/**
 * Created by wutao on 2018/3/8.
 */

public class ScrollViewBindingAdapter {

    @BindingAdapter({"onScrollChangeCommand"})
    public static void setOnScrollChangeCommand(final NestedScrollView nestedScrollView,
                                                final ReplyCommand<NestedScrollDataWrapper> onScrollChangeCommand) {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (onScrollChangeCommand != null) {
                    onScrollChangeCommand.execute(new NestedScrollDataWrapper(scrollX, scrollY, oldScrollX,
                            oldScrollY));
                }
            }
        });
    }

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


    public static class ScrollChangeDataWrapper {

        public final float scrollX;
        public final float scrollY;


        public ScrollChangeDataWrapper(float scrollX, float scrollY) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
        }
    }

    public static class NestedScrollDataWrapper {

        public final int scrollX;
        public final int scrollY;
        public final int oldScrollX;
        public final int oldScrollY;


        public NestedScrollDataWrapper(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.oldScrollX = oldScrollX;
            this.oldScrollY = oldScrollY;
        }
    }
}
