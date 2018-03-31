package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wutaodsg.mvvm.command.ReplyCommand;
import com.wutaodsg.mvvm.command.ResponseCommand;

/**
 * Created by wutao on 2018/3/8.
 */

public class ListViewBindingAdapter {

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
     * 加载多少项的信息。
     *
     * @param listView
     * @param onLoadMoreCommand
     */
    @BindingAdapter({"onLoadMoreCommand"})
    public static void setOnLoadMoreCommand(final ListView listView,
                                            final ReplyCommand<Integer> onLoadMoreCommand) {
        listView.setOnScrollListener(new OnLoadMoreListener(listView, onLoadMoreCommand));
    }


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
     * 当 ListView 加载到最后一项，上拉刷新时使用这个类。
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
