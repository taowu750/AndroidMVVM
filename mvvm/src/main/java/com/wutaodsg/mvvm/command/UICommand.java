package com.wutaodsg.mvvm.command;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.wutaodsg.mvvm.core.UIAwareComponent;

/**
 * 这个命令与 UI 息息相关。UICommand 用来为 ViewModel 的 Functions 提供有关 UI 界面的操作。
 * 在 ViewModel 中一个 Function 的执行过程中，View 通过这个 Command 可以将自己想要执行的
 * 界面操作插入到 Function 的执行过程中。
 * <p>
 * UICommand 具有界面感知功能，当界面由可见变为不可见时，它会将所持有的一切命令保存，然后
 * 置为 null，这样就意味着在界面不可见的情况下 UICommand 的一切行为将失效；
 * 而当界面由不可见变为可见时，它会将保存的命令（如果有的话）恢复。
 * <p>
 * UICommand 包含有几个动作：<br/>
 * 1. enabled：ViewModel 通过调用 enabled 传递 Function 是否可执行的信息。<br/>
 * 2. onStart: 这个动作在 Function 执行之前被调用，可以在这里做一些界面的初始化工作。<br/>
 * 3. onProgress: 这个动作在 Function 执行期间被调用，在这里可以传递关于人物进度的信息。<br/>
 * 4. executionStatus：ViewModel 通过调用 executionSuccess 传递 Function 是否执行成功的信息。<br/>
 * 5. executionResult: ViewModel 通过 executionResult 传递 Function 的执行结果。
 * <p>
 * 需要注意的是，UICommand 中的所有动作都必须在 Android 主线程中进行。
 * 当 Function 中的有些操作需要在非 UI 线程中执行时，你可以通过为这个 UICommand 设置 Handler，
 * 从而让 Function 可以将所有的 UI 操作放到主线程中执行。
 * <p>
 * UICommand 通过 Builder 创建，所以你可以选择想要传递的动作。
 * <p>
 * 泛型 RESULT 表示执行结果，也就是 executionResult 的参数；<br/>
 * 泛型 PROGRESS 表示执行进度。也就是 onProgress 的参数。
 */

public class UICommand<RESULT, PROGRESS> implements UIAwareComponent {

    private Handler mHandler;
    private Action1<Boolean> mEnabled;
    private Action0 mOnStart;
    private Action1<PROGRESS> mOnProgress;
    private Action1<Boolean> mExecutionStatus;
    private Action1<RESULT> mExecutionResult;

    private SparseArray<Object> mStore;


    private UICommand(Builder<RESULT, PROGRESS> builder) {
        mHandler = builder.mHandler;
        mEnabled = builder.mEnabled;
        mOnStart = builder.mOnStart;
        mOnProgress = builder.mOnProgress;
        mExecutionStatus = builder.mExecutionStatus;
        mExecutionResult = builder.mExecutionResult;

        mStore = new SparseArray<>(6);
    }


    public boolean hasHandler() {
        return mHandler != null;
    }

    public boolean hasEnabled() {
        return mEnabled != null;
    }

    public boolean hasOnStart() {
        return mOnStart != null;
    }

    public boolean hasOnProgress() {
        return mOnProgress != null;
    }

    public boolean hasExecutingSuccess() {
        return mExecutionStatus != null;
    }

    public boolean hasExecutionResult() {
        return mExecutionResult != null;
    }


    @Nullable
    public Handler getHandler() {
        return mHandler;
    }

    @Nullable
    public Action1<Boolean> getEnabled() {
        return mEnabled;
    }

    @Nullable
    public Action0 getOnStart() {
        return mOnStart;
    }

    @Nullable
    public Action1<PROGRESS> getOnProgress() {
        return mOnProgress;
    }

    @Nullable
    public Action1<Boolean> getExecutionStatus() {
        return mExecutionStatus;
    }

    @Nullable
    public Action1<RESULT> getExecutionResult() {
        return mExecutionResult;
    }


    public void callHandler(@NonNull Runnable action) {
        if (mHandler != null) {
            Message.obtain(mHandler, action).sendToTarget();
        }
    }

    public void callEnabled(boolean enabled) {
        if (mEnabled != null) {
            mEnabled.execute(enabled);
        }
    }

    public void callOnStart() {
        if (mOnStart != null) {
            mOnStart.execute();
        }
    }

    public void callOnProgress(@NonNull PROGRESS progress) {
        if (mOnProgress != null) {
            mOnProgress.execute(progress);
        }
    }

    public void callExecutionStatus(boolean executionStatus) {
        if (mExecutionStatus != null) {
            mExecutionStatus.execute(executionStatus);
        }
    }

    public void callExecutionResult(@NonNull RESULT result) {
        if (mExecutionResult != null) {
            mExecutionResult.execute(result);
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onVisible() {
        if (mStore != null) {
            mHandler = (Handler) mStore.get(0, mHandler);
            mEnabled = (Action1<Boolean>) mStore.get(1, mEnabled);
            mOnStart = (Action0) mStore.get(2, mOnStart);
            mOnProgress = (Action1<PROGRESS>) mStore.get(3, mOnProgress);
            mExecutionStatus = (Action1<Boolean>) mStore.get(4, mExecutionStatus);
            mExecutionResult = (Action1<RESULT>) mStore.get(5, mExecutionResult);
        }
    }

    @Override
    public void onInvisible() {
        if (mStore != null) {
            mStore.put(0, mHandler);
            mStore.put(1, mEnabled);
            mStore.put(2, mOnStart);
            mStore.put(3, mOnProgress);
            mStore.put(4, mExecutionStatus);
            mStore.put(5, mExecutionResult);
        }
        mHandler = null;
        mEnabled = null;
        mOnStart = null;
        mOnProgress = null;
        mExecutionStatus = null;
        mExecutionResult = null;
    }


    public static class Builder<RESULT, PROGRESS> {

        private Handler mHandler;
        private Action1<Boolean> mEnabled;
        private Action0 mOnStart;
        private Action1<PROGRESS> mOnProgress;
        private Action1<Boolean> mExecutionStatus;
        private Action1<RESULT> mExecutionResult;


        public Builder setHandler(@NonNull final Handler handler) {
            mHandler = handler;

            return this;
        }

        public Builder setEnabled(@NonNull final Action1<Boolean> enabled) {
            mEnabled = enabled;

            return this;
        }

        public Builder setOnStart(@NonNull final Action0 onStart) {
            mOnStart = onStart;

            return this;
        }

        public Builder setOnProgress(@NonNull final Action1<PROGRESS> onProgress) {
            mOnProgress = onProgress;

            return this;
        }

        public Builder setExecutionStatus(@NonNull final Action1<Boolean> executionStatus) {
            mExecutionStatus = executionStatus;

            return this;
        }

        public Builder setExecutionResult(@NonNull final Action1<RESULT> executionResult) {
            mExecutionResult = executionResult;

            return this;
        }

        public UICommand<RESULT, PROGRESS> create() {
            return new UICommand<>(this);
        }
    }
}
