package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wutaodsg.mvvm.command.ReplyCommand;
import com.wutaodsg.mvvm.command.ResponseCommand;

/**
 * {@link ListView} 的 BindingAdapter。
 */

public class ListViewBindingAdapter {

    /**
     * 设置 ListView 的滚动监听命令，有以下两个命令：
     * <p>
     * 1. onScrollChangeCommand：滚动事件监听命令，参数 {@link ScrollChangeDataWrapper}，表示滚动数据；<br/>
     * 2. onScrollStateChangedCommand：滚动时状态事件监听命令，参数 {@link Integer}，表示滚动状态。
     *
     * @param listView                    {@link ListView} 对象
     * @param onScrollChangeCommand       {@link ReplyCommand} 对象，滚动时事件监听命令
     * @param onScrollStateChangedCommand {@link ReplyCommand} 对象，滚动时状态事件监听命令
     */
    @BindingAdapter(value = {"onScrollChangeCommand", "onScrollStateChangedCommand"}, requireAll = false)
    public static void setOnScrollChangeCommand(final ListView listView,
                                                final ReplyCommand<ScrollChangeDataWrapper> onScrollChangeCommand,
                                                final ReplyCommand<Integer> onScrollStateChangedCommand) {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int scrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.scrollState = scrollState;
                if (onScrollStateChangedCommand != null) {
                    onScrollStateChangedCommand.execute(scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (onScrollChangeCommand != null) {
                    onScrollChangeCommand.execute(new ScrollChangeDataWrapper(scrollState, firstVisibleItem,
                            visibleItemCount, totalItemCount));
                }
            }
        });
    }

    /**
     * 设置 ListView 的子项点击事件监听命令，命令为 onItemClickCommand，
     * 参数为 {@link Integer}，表示点击时位置。
     *
     * @param listView           {@link ListView} 对象
     * @param onItemClickCommand {@link ReplyCommand} 对象，子项点击事件监听命令
     */
    @BindingAdapter({"onItemClickCommand"})
    public static void setOnItemClickListener(final ListView listView,
                                              final ReplyCommand<Integer> onItemClickCommand) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemClickCommand != null) {
                    onItemClickCommand.execute(position);
                }
            }
        });
    }

    /**
     * 设置 ListView 的子项长按事件监听命令，命令为 onItemLongClickCommand，参数为 {@link Integer},
     * 表示点击位置，返回值为 {@link Boolean}，表示事件是否成功。
     *
     * @param listView               {@link ListView} 对象
     * @param onItemLongClickCommand {@link ResponseCommand} 对象，子项长按事件监听命令
     */
    @BindingAdapter({"onItemLongClickCommand"})
    public static void setOnItemLongClickCommand(final ListView listView,
                                                 final ResponseCommand<Integer, Boolean> onItemLongClickCommand) {
        // 如果已经加载到 ListView 中的最后一项
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return onItemLongClickCommand != null ? onItemLongClickCommand.execute(position) : false;
            }
        });
    }

    /**
     * onLoadMoreCommand 用在在 ListView 下拉刷新时，通过 onLoadMoreCommand 传递 ListView 已经
     * 加载多少项的信息。参数 {@link Integer}，表示刷新之前加载了多少项。
     *
     * @param listView          {@link ListView} 对象
     * @param onLoadMoreCommand {@link ReplyCommand} 对象，加载更多事件监听命令
     */
    @BindingAdapter({"onLoadMoreCommand"})
    public static void setOnLoadMoreCommand(final ListView listView,
                                            final ReplyCommand<Integer> onLoadMoreCommand) {
        listView.setOnScrollListener(new OnLoadMoreListener(listView, onLoadMoreCommand));
    }


    /**
     * ListView 的滚动事件数据包装器对象，用来封装 ListView 滚动事件处理方法中的参数。
     * <p>
     * 1. 属性 {@link #firstVisibleItem} 表示第一个可见的子项在 ListView 数据集中的位置；<br/>
     * 2. 属性 {@link #visibleItemCount} 表示 ListView 中可以看得见的子项的数目；<br/>
     * 3. 属性 {@link #totalItemCount} 表示 ListView 中子项的总数；<br/>
     * 4. 属性 {@link #scrollState} 表示 ListView 当前的滚动状态。
     */
    public static class ScrollChangeDataWrapper {

        public final int firstVisibleItem;
        public final int visibleItemCount;
        public final int totalItemCount;
        public final int scrollState;


        public ScrollChangeDataWrapper(int firstVisibleItem, int visibleItemCount, int totalItemCount, int
                scrollState) {
            this.firstVisibleItem = firstVisibleItem;
            this.visibleItemCount = visibleItemCount;
            this.totalItemCount = totalItemCount;
            this.scrollState = scrollState;
        }
    }

    /**
     * 当 ListView 加载到最后一项，上拉刷新时使用这个类。它是一个滚动事件监听器。
     */
    public static class OnLoadMoreListener implements AbsListView.OnScrollListener {

        private ListView mListView;
        private final ReplyCommand<Integer> mOnLoadMoreCommand;


        public OnLoadMoreListener(ListView listView, ReplyCommand<Integer> onLoadMoreCommand) {
            mListView = listView;
            mOnLoadMoreCommand = onLoadMoreCommand;
        }


        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem + visibleItemCount >= totalItemCount
                    && totalItemCount != 0
                    && totalItemCount != mListView.getHeaderViewsCount() + mListView.getFooterViewsCount()) {
                if (mOnLoadMoreCommand != null) {
                    mOnLoadMoreCommand.execute(totalItemCount);
                }
            }
        }
    }
}
