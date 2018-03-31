package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.view.MotionEvent;
import android.view.View;

import com.wutaodsg.mvvm.command.ReplyCommand;
import com.wutaodsg.mvvm.command.ResponseCommand;


/**
 * Created by wutao on 2018/3/8.
 */

public class ViewBindingAdapter {

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
