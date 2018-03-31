package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.support.v4.view.ViewPager;

import com.wutaodsg.mvvm.command.ReplyCommand;

/**
 * Created by wutao on 2018/3/8.
 */

public class ViewPagerBindingAdapter {

    @BindingAdapter(value = {"onPageScrolledCommand", "onPageSelectedCommand", "onPageScrollStateChangedCommand"},
            requireAll = false)
    public static void onScrollChangeCommand(final ViewPager viewPager,
                                             final ReplyCommand<PageScrolledDataWrapper> onPageScrolledCommand,
                                             final ReplyCommand<Integer> onPageSelectedCommand,
                                             final ReplyCommand<Integer> onPageScrollStateChangedCommand) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int state;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (onPageScrolledCommand != null) {
                    onPageScrolledCommand.execute(new PageScrolledDataWrapper(position, positionOffset,
                            positionOffsetPixels, state));
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (onPageSelectedCommand != null) {
                    onPageSelectedCommand.execute(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                this.state = state;
                if (onPageScrollStateChangedCommand != null) {
                    onPageScrollStateChangedCommand.execute(state);
                }
            }
        });
    }


    public static class PageScrolledDataWrapper {

        public final float position;
        public final float positionOffset;
        public final int positionOffsetPixels;
        public final int state;


        public PageScrolledDataWrapper(float position, float positionOffset, int positionOffsetPixels, int state) {
            this.position = position;
            this.positionOffset = positionOffset;
            this.positionOffsetPixels = positionOffsetPixels;
            this.state = state;
        }
    }
}
