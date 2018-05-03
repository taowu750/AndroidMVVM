package com.wutaodsg.mvvm.util.vmeventbus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wutaodsg.mvvm.command.Action0;
import com.wutaodsg.mvvm.command.Action1;
import com.wutaodsg.mvvm.core.BaseViewModel;


/**
 * ViewModel 的命令。一个 ViewModel 对应于一个命令，当调用 {@link #execute()}
 * 或 {@link #execute(Object)} 方法时，就会执行这个命令。
 * <p>
 * 可以在构造阶段提供 {@link ViewModelScheduler} 提供命令运行的线程环境。
 * 如果不提供，那么这个命令将会运行在发布事件者的线程环境中。
 * <p>
 * 更多详细信息参见 {@link ViewModelEventBus}。
 */

public class ViewModelCommand<T> {

    private BaseViewModel mViewModel;
    private Action0 mCommandWithoutData;
    private Action1<T> mCommandWithData;
    private ViewModelScheduler mViewModelScheduler;


    public ViewModelCommand(@NonNull BaseViewModel viewModel,
                            @NonNull Action0 commandWithoutData,
                            @Nullable ViewModelScheduler viewModelScheduler) {
        mViewModel = viewModel;
        mCommandWithoutData = commandWithoutData;
        mViewModelScheduler = viewModelScheduler;
    }

    public ViewModelCommand(@NonNull BaseViewModel viewModel,
                            @NonNull Action1<T> commandWithData,
                            @Nullable ViewModelScheduler viewModelScheduler) {
        mViewModel = viewModel;
        mCommandWithData = commandWithData;
        mViewModelScheduler = viewModelScheduler;
    }

    public ViewModelCommand(@NonNull BaseViewModel viewModel,
                            @NonNull Action0 commandWithoutData) {
        this(viewModel, commandWithoutData, null);
    }

    public ViewModelCommand(@NonNull BaseViewModel viewModel,
                            @NonNull Action1<T> commandWithData) {
        this(viewModel, commandWithData, null);
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof ViewModelCommand && mViewModel != null && mViewModel.equals(obj);

    }

    @NonNull
    public BaseViewModel getViewModel() {
        return mViewModel;
    }

    @Nullable
    public Action0 getCommandWithoutData() {
        return mCommandWithoutData;
    }

    @Nullable
    public Action1<T> getCommandWithData() {
        return mCommandWithData;
    }

    public void execute() {
        if (mCommandWithoutData != null) {
            if (mViewModelScheduler != null) {
                mViewModelScheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        mCommandWithoutData.execute();
                    }
                });
            } else {
                mCommandWithoutData.execute();
            }
        }
    }

    public void execute(@NonNull final T t) {
        if (mCommandWithData != null) {
            if (mViewModelScheduler != null) {
                mViewModelScheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        mCommandWithData.execute(t);
                    }
                });
            } else {
                mCommandWithData.execute(t);
            }
        }
    }

    public void clear() {
        mViewModel = null;
        mCommandWithoutData = null;
        mCommandWithData = null;
        mViewModelScheduler = null;
    }
}
