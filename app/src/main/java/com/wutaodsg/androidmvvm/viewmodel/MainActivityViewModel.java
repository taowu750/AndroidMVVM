package com.wutaodsg.androidmvvm.viewmodel;

import android.databinding.ObservableField;
import android.util.Log;

import com.android.databinding.library.baseAdapters.BR;
import com.wutaodsg.androidmvvm.model.NetworkUtil;
import com.wutaodsg.androidmvvm.model.UserInfoConfirmUtil;
import com.wutaodsg.androidmvvm.model.UserLoginUtil;
import com.wutaodsg.androidmvvm.view.MainActivity;
import com.wutaodsg.mvvm.command.UICommand;
import com.wutaodsg.mvvm.core.BaseViewModel;
import com.wutaodsg.mvvm.core.BindVariable;


/**
 * ViewModel 只关心自己的成员变量（也就是 Data Binding），只会获取/设置这些数据，有关于 UI 的操作将
 * 由 View 处理，而其他逻辑（比如验证数据正确性、网络操作等）将会交由 Model 层处理
 */

public class MainActivityViewModel extends BaseViewModel {

    private static final String TAG = MainActivity.TAG_PREFIX + "MainActivityVM";
    
    
    private static final String ERROR_MESSAGE_NO_NETWORK = "请连接网络";
    private static final String ERROR_MESSAGE_INVALID_INPUT = "用户名或密码不正确";
    private static final String ERROR_MESSAGE_USER_LOGIN_FAILED = "用户登录失败";


    /*
    数据绑定（Data Binding）最好有一个初始值
     */

    @BindVariable(BR.userName)
    private final ObservableField<String> mUserName = new ObservableField<>("");

    @BindVariable(BR.password)
    private final ObservableField<String> mPassword = new ObservableField<>("");

    @BindVariable(BR.errorMessage)
    private final ObservableField<String> mErrorMessage = new ObservableField<>("");


    /*
    为了将 View 的事件和 ViewModel 的事件处理结合起来，
    我们需要用到回调接口的方式。
     */

    public boolean isInputInvalid() {
        Log.d(TAG, "isInputInvalid: userName=" + mUserName.get() + ", password=" + mPassword.get());
        return UserInfoConfirmUtil.isValidUserNameAndPassword(mUserName.get(), mPassword.get());
    }

    public void login(final UICommand loginUICommand) {
        if (NetworkUtil.hasNetwork(getContext())) {
            if (UserInfoConfirmUtil.isRegisteredUserNameAndPassword(mUserName.get(), mPassword.get())) {
                loginUICommand.callOnStart();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UserLoginUtil.login(mUserName.get(), mPassword.get());
                        } catch (InterruptedException e) {
                            loginUICommand.callHandler(new Runnable() {
                                @Override
                                public void run() {
                                    loginUICommand.callExecutionStatus(false);
                                    mErrorMessage.set(ERROR_MESSAGE_USER_LOGIN_FAILED);
                                }
                            });
                        }

                        loginUICommand.callHandler(new Runnable() {
                            @Override
                            public void run() {
                                loginUICommand.callExecutionStatus(true);
                                mErrorMessage.set("");
                            }
                        });
                    }
                }).start();
            } else {
                loginUICommand.callEnabled(false);
                mErrorMessage.set(ERROR_MESSAGE_INVALID_INPUT);
            }
        } else {
            loginUICommand.callEnabled(false);
            mErrorMessage.set(ERROR_MESSAGE_NO_NETWORK);
        }
    }
}
