package com.wutaodsg.mvvm.bindingadapter;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.wutaodsg.mvvm.command.ReplyCommand;

/**
 * {@link EditText} 的 BindingAdapter。
 */

public class EditTextBindingAdapter {

    /*
    setFocusable 这个是用键盘能否获取焦点。
    setFocusableInTouchMode 这个是在触摸模式下能否获取焦点。

    focusable这种属性,更多的是为了解决非触摸输入的,因为你用遥控器或键盘点击控件,就必然要涉及到焦点的问题,
    只有可以获得焦点的控件才能响应键盘或者遥控器或者轨迹球的确定事件.
	focusableInTouchMode.这个属性在进入触摸输入模式后,该控件是否还有获得焦点的能力.

	可以简单的理解为,用户一旦开始通过点击屏幕的方式输入,手机就进入了"touch mode".
	focusableInTouchMode这种属性,多半是设给EditText这种即使在TouchMode下,依然需要获取焦点的控件.
	比如Button之类的控件,在touch mode下,就已经没有获取焦点的必要了.Android里面EditText是用来接受用户
	输入的,那问题是在touch mode下我们没键盘,怎么办呢,android会主动给我们弹出一个软键盘出来
	(或者是手写输入法,这些无所谓...).那现在想象,我们的界面上有五个这种EditText,那弹出的软键盘的输入,
	到底要写在哪个EditText上呢?所以这里就需要焦点来介入了.只有获得了焦点的那个EditText,
	才能获取软键盘的输入.
     */

    /**
     * 设置 EditText 的 requestFocus 属性，这个属性决定它是否可以获取到软键盘的焦点。
     *
     * @param editText     {@link EditText} 对象
     * @param requestFocus 是否获取到软键盘的焦点
     */
    @BindingAdapter({"requestFocus"})
    public static void requestFocusCommand(final EditText editText, final boolean requestFocus) {
        if (requestFocus) {
            editText.setFocusableInTouchMode(true);
            editText.setSelection(editText.getText().length());
            editText.requestFocus();
            InputMethodManager im = (InputMethodManager) editText.getContext().getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            im.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            editText.setEnabled(false);
            editText.setEnabled(true);
        }
    }

    /**
     * 设置 EditText 的文本改变事件监听命令，有以下三个命令：
     * <p>
     * 1. <code>beforeTextChangedCommand</code>：文本改变前事件监听命令，参数为 {@link TextChangeDataWrapper}，表示文本改变前的数据；<br/>
     * 2. <code>onTextChangedCommand</code>：    文本改变时事件监听命令，参数为 {@link TextChangeDataWrapper}，表示文本改变时的数据；<br/>
     * 3. <code>afterTextChangedCommand</code>： 文本改变后事件监听命令，参数为 {@link String}，表示文本改变结束后的字符串对象。<br/>
     *
     * @param editText                 {@link EditText} 对象
     * @param beforeTextChangedCommand {@link ReplyCommand} 对象，文本改变前事件监听命令
     * @param onTextChangedCommand     {@link ReplyCommand} 对象，文本改变时事件监听命令
     * @param afterTextChangedCommand  {@link ReplyCommand} 对象，文本改变后事件监听命令
     */
    @BindingAdapter(value = {"beforeTextChangedCommand", "onTextChangedCommand", "afterTextChangedCommand"},
            requireAll = false)
    public static void setEditTextChangedCommand(final EditText editText,
                                                 final ReplyCommand<TextChangeDataWrapper> beforeTextChangedCommand,
                                                 final ReplyCommand<TextChangeDataWrapper> onTextChangedCommand,
                                                 final ReplyCommand<String> afterTextChangedCommand) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (beforeTextChangedCommand != null) {
                    beforeTextChangedCommand.execute(new TextChangeDataWrapper(s, start, count, count));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (onTextChangedCommand != null) {
                    onTextChangedCommand.execute(new TextChangeDataWrapper(s, start, before, count));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (afterTextChangedCommand != null) {
                    afterTextChangedCommand.execute(s.toString());
                }
            }
        });
    }


    /**
     * 用来封装 EditText 文本改变事件处理方法中的参数。
     * <p>
     * 1. 属性 {@link #s} 表示文本数据；<br/>
     * 2. 属性 {@link #start} 表示文本开始的下标；<br/>
     * 3. 属性 {@link #before} 表示之前文本的字符数；<br/>
     * 4. 属性 {@link #count} 表示文本字符数。
     */
    public static class TextChangeDataWrapper {

        public final CharSequence s;
        public final int start;
        public final int before;
        public final int count;


        public TextChangeDataWrapper(CharSequence s, int start, int before, int count) {
            this.s = s;
            this.start = start;
            this.before = before;
            this.count = count;
        }
    }
}
