package com.wutaodsg.mvvm.command;

/**
 * 用来封装没有返回值的控件命令，这个命令在执行时接受 0 个或 1 个参数。
 * <p>
 * ReplyCommand 的构造器接受 {@link Action0} 或 {@link Action1} 对象，
 * 并使用 {@link #execute()} 或 {@link #execute(Object)} 回调它们。
 * <p>
 * 此外，它的构造器接受一个 canExecute 参数，用来表示命令是否可以执行，
 * 如果这个参数为 null，就表示命令一定可以执行。
 * <p>
 * ReplyCommand 一般放在 View 层中，使用 {@link com.wutaodsg.mvvm.core.BindVariable}
 * 标识界面上对应的 DataBinding Command，并在绑定阶段将它们绑定到一起。
 * <p>
 * 泛型 T 表示接受的参数类型，不接受参数时可以为空。
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
