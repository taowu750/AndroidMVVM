package com.wutaodsg.mvvm.command;

/**
 * 用来封装没有返回值的控件命令，接受 0 或 1 个参数。
 */

public class ReplyCommand<T> {

    private Action0 mCommand0;
    private Action1<T> mCommand1;
    private Function0<Boolean> mCanExecute;


    public ReplyCommand(Action0 command0) {
        mCommand0 = command0;
    }

    public ReplyCommand(Action1<T> command1) {
        mCommand1 = command1;
    }

    public ReplyCommand(Action0 command0, Function0<Boolean> canExecute) {
        mCommand0 = command0;
        mCanExecute = canExecute;
    }

    public ReplyCommand(Action1<T> command1, Function0<Boolean> canExecute) {
        mCommand1 = command1;
        mCanExecute = canExecute;
    }


    public boolean canExecute() {
        return mCanExecute != null ? mCanExecute.call() : true;
    }

    public void execute() {
        if (mCommand0 != null && canExecute()) {
            mCommand0.execute();
        }
    }

    public void execute(T t) {
        if (mCommand1 != null && canExecute()) {
            mCommand1.execute(t);
        }
    }
}
