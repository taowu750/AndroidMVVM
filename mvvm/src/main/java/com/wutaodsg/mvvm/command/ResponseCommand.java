package com.wutaodsg.mvvm.command;

/**
 * 用来封装有返回值的控件命令，接受 0 或 1 个命令。
 */

public class ResponseCommand<T, R> {

    private Function0<R> mCommand0;
    private Function1<T, R> mCommand1;
    private Function0<Boolean> mCanExecute;


    public ResponseCommand(Function0<R> command0) {
        mCommand0 = command0;
    }

    public ResponseCommand(Function1<T, R> command1) {
        mCommand1 = command1;
    }

    public ResponseCommand(Function0<R> command0, Function0<Boolean> canExecute) {
        mCommand0 = command0;
        mCanExecute = canExecute;
    }

    public ResponseCommand(Function1<T, R> command1, Function0<Boolean> canExecute) {
        mCommand1 = command1;
        mCanExecute = canExecute;
    }


    public boolean canExecute() {
        return mCanExecute != null ? mCanExecute.call() : true;
    }

    public R execute() {
        if (mCommand0 != null && canExecute()) {
            return mCommand0.call();
        }
        return null;
    }

    public R execute(T t) {
        if (mCommand1 != null && canExecute()) {
            return mCommand1.call(t);
        }
        return null;
    }
}
