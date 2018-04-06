package com.wutaodsg.androidmvvm.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.android.databinding.library.baseAdapters.BR;
import com.wutaodsg.androidmvvm.R;
import com.wutaodsg.androidmvvm.databinding.ActivityMainBinding;
import com.wutaodsg.androidmvvm.viewmodel.MainActivityViewModel;
import com.wutaodsg.mvvm.command.Action0;
import com.wutaodsg.mvvm.command.Action1;
import com.wutaodsg.mvvm.command.Function0;
import com.wutaodsg.mvvm.command.ReplyCommand;
import com.wutaodsg.mvvm.command.UICommand;
import com.wutaodsg.mvvm.core.BaseMVVMActivity;
import com.wutaodsg.mvvm.core.BindVariable;
import com.wutaodsg.mvvm.core.ViewModelType;


/**
 * View 层只关心 UI，以及命令绑定（Command Binding）。View 将其他操作交由 ViewModel 处理，
 * 自己只需调用这些操作即可。
 */
@ViewModelType(MainActivityViewModel.class)
public class MainActivity extends BaseMVVMActivity<MainActivityViewModel, ActivityMainBinding> {

    public static final String TAG_PREFIX = "WuT.";

    private static final String TAG = TAG_PREFIX + "MainActivity";


    private AlertDialog mLoginWaitingDialog;

    private UICommand mLoginCommand = new UICommand.Builder()
            .setHandler(new Handler())
            .setEnabled(new Action1<Boolean>() {
                @Override
                public void execute(Boolean enabled) {
                    if (!enabled) {
                        getDataBinding().errorMessageTextView.setVisibility(View.VISIBLE);
                    }
                }
            })
            .setOnStart(new Action0() {
                @Override
                public void execute() {
                    mLoginWaitingDialog.show();
                    getDataBinding().errorMessageTextView.setVisibility(View.GONE);
                }
            })
            .setExecutionStatus(new Action1<Boolean>() {
                @Override
                public void execute(Boolean status) {
                    mLoginWaitingDialog.dismiss();
                    if (status) {
                        getDataBinding().errorMessageTextView.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "登陆成功！", Toast.LENGTH_SHORT)
                                .show();
                        startActivity(new Intent(MainActivity.this, TestViewModelEventBusActivity.class));
                    } else {
                        getDataBinding().errorMessageTextView.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "登陆失败！", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            })
            .create();

    @BindVariable(BR.loginButtonClickCommand)
    private final ReplyCommand mLoginButtonClickCommand = new ReplyCommand(new Action0() {
        @Override
        public void execute() {
            getViewModel().login(mLoginCommand);
        }
    }, new Function0<Boolean>() {
        @Override
        public Boolean call() {
            return getViewModel().isInputInvalid();
        }
    });


    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginWaitingDialog = new ProgressDialog.Builder(this)
                .setTitle("正在登陆")
                .setMessage("请等待，正在登陆...")
                .setCancelable(false)
                .create();
    }
}
