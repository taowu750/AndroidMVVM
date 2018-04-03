package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.view.MotionEvent;
import android.view.View;

import com.wutaodsg.mvvm.command.ReplyCommand;
import com.wutaodsg.mvvm.command.ResponseCommand;


/**
 * {@link View} 的 BindingAdapter。
 */

public class ViewBindingAdapter {

    /**
     * 设置 View 的点击事件监听器，命令为 clickCommand，按钮被点击时调用。
     *
     * @param view         {@link View} 对象
     * @param clickCommand {@link ReplyCommand} 对象，点击事件监听器
     */
    @BindingAdapter({"clickCommand"})
    public static void setClickCommand(final View view,
                                       final ReplyCommand clickCommand) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCommand != null) {
                    clickCommand.execute();
                }
            }
        });
    }

    /**
     * 设置 View 的长按事件监听命令，命令为 longClickCommand，返回值表示是否成功。
     *
     * @param view             {@link View} 对象
     * @param longClickCommand {@link ReplyCommand} 对象，长按事件监听命令
     */
    @BindingAdapter({"longClickCommand"})
    public static void setLongClickCommand(final View view,
                                           final ResponseCommand<?, Boolean> longClickCommand) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return longClickCommand != null ? longClickCommand.execute() : false;
            }
        });
    }

    /**
     * 设置 View 的触摸事件监听命令，命令是 onTouchCommand，参数 {@link MotionEvent}
     * 表示触摸数据，返回值表示是否成功。
     *
     * @param view           {@link View} 对象
     * @param onTouchCommand {@link ResponseCommand} 对象，触摸事件监听命令
     */
    @BindingAdapter({"onTouchCommand"})
    public static void setOnTouchCommand(final View view,
                                         final ResponseCommand<MotionEvent, Boolean> onTouchCommand) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchCommand != null ? onTouchCommand.execute(event) : false;
            }
        });
    }

    /**
     * 设置 View 是否获取到焦点。
     *
     * @param view         {@link View} 对象
     * @param requestFocus 是否获取到焦点
     */
    @BindingAdapter({"requestFocus"})
    public static void setRequestFocusCommand(final View view,
                                              final boolean requestFocus) {
        if (requestFocus) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        } else {
            view.clearFocus();
        }
    }

    /**
     * 设置 View 的焦点改变事件监听命令，命令是 onFocusChangeCommand，参数表示
     * 当前是否获取到焦点。
     *
     * @param view                 {@link View} 对象
     * @param onFocusChangeCommand {@link ReplyCommand} 对象，焦点改变事件监听命令
     */
    @BindingAdapter({"onFocusChangeCommand"})
    public static void setOnFocusCommand(final View view,
                                         final ReplyCommand<Boolean> onFocusChangeCommand) {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (onFocusChangeCommand != null) {
                    onFocusChangeCommand.execute(hasFocus);
                }
            }
        });
    }
}
