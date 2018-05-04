package com.wutaodsg.mvvm.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 线性分割线，用在 RecyclerView 中，可以是水平或垂直的。<br/>
 * 原理参见 http://www.jianshu.com/p/4eff036360da
 */

public class LineDivider extends RecyclerView.ItemDecoration {

    private Drawable divider;
    private int orientation;


    // dividerDrawableResId 是分割线的样式资源 id，存放在 drawable 目录下
    public LineDivider(Context context, @OrientationType int orientation, @DrawableRes int dividerDrawableResId) {
        this.orientation = orientation;
        this.divider = context.getResources().getDrawable(dividerDrawableResId);
    }


    // 绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            drawHorizontalLine(c, parent);
        } else if (orientation == LinearLayoutManager.VERTICAL) {
            drawVerticalLine(c, parent);
        }
    }

    // 由于分割线也有宽高，所有每个 Item 需要向下或向右偏移
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            // 画横线，就是向下偏移一个分割线的高度
            outRect.set(0, 0, 0, divider.getIntrinsicHeight());
        } else if (orientation == LinearLayoutManager.VERTICAL) {
            // 画竖线，就是向右偏移一个分割线的宽度
            outRect.set(0, 0, 0, divider.getIntrinsicWidth());
        }
    }


    // 画横线, 这里的 parent 其实是在屏幕正在显示的部分
    private void drawHorizontalLine(Canvas c, RecyclerView parent) {
        // left、right、top 和 bottom 是分割线的坐标位置
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            // 获取 child 的布局信息
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    // 画竖线
    private void drawVerticalLine(Canvas c, RecyclerView parent) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            // 获取 child 的布局信息
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + params.rightMargin;
            int right = left + divider.getIntrinsicWidth();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }


    @IntDef({LinearLayoutManager.VERTICAL, LinearLayoutManager.HORIZONTAL})
    @interface OrientationType {
    }
}
