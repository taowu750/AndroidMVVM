package com.wutaodsg.mvvm.command;

/**
 * 用来封装有返回值的控件命令，这个命令在执行时接受 0 个或 1 个参数。
 * <p>
 * ResponseCommand 的构造器接受 {@link Function0} 或 {@link Function1} 对象，
 * 并使用 {@link #execute()} 或 {@link #execute(Object)} 回调它们。
 * <p>
 * 此外，它的构造器接受一个 canExecute 参数，用来表示命令是否可以执行，
 * 如果这个参数为 null，就表示命令一定可以执行。
 * <p>
 * ResponseCommand 一般放在 View 层中，使用 {@link com.wutaodsg.mvvm.core.BindVariable}
 * 标识界面上对应的 DataBinding Command，并在绑定阶段将它们绑定到一起。
 * <p>
 * 泛型 T 表示接受的参数类型，不接受参数时可以为空；
 * 泛型 R 表示返回值类型。
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
