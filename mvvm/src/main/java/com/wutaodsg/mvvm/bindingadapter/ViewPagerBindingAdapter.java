package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.support.v4.view.ViewPager;

import com.wutaodsg.mvvm.command.ReplyCommand;

/**
 * {@link ViewPager} 的 BindingAdapter。
 */

public class ViewPagerBindingAdapter {

    /**
     * 设置 ViewPager 的滚动事件监听命令，命令有以下三个：
     * <p>
     * 1. onPageScrolledCommand：滚动中事件监听命令，参数 {@link PageScrolledDataWrapper} 表示滚动数据；
     * 2. onPageSelectedCommand：滚动中界面被选择事件监听命令，参数 {@link Integer} 表示选中页面的位置；
     * 3. onPageScrollStateChangedCommand：滚动状态改变事件监听命令，参数 {@link Integer} 表示滚动状态。
     *
     * @param viewPager                       {@link ViewPager} 对象
     * @param onPageScrolledCommand           {@link ReplyCommand} 对象，滚动中事件监听命令
     * @param onPageSelectedCommand           {@link ReplyCommand} 对象，滚动中界面被选择事件监听命令
     * @param onPageScrollStateChangedCommand {@link ReplyCommand} 对象，滚动状态改变事件监听命令
     */
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


    /**
     * ViewPager 的滚动事件数据包装器对象，用来封装 ViewPager 滚动事件处理方法中的参数。
     * <p>
     * 1. 属性 {@link #position} 表示当前显示的首个页面的位置；<br/>
     * 2. 属性 {@link #positionOffset} 表示从页面位置到页面的偏移量，值在 [0, 1)；<br/>
     * 3. 属性 {@link #positionOffsetPixels} 表示从页面位置到页面的偏移像素数。
     */
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
