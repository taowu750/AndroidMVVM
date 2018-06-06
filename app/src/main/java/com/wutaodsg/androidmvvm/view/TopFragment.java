package com.wutaodsg.androidmvvm.view;

import android.content.Intent;

import com.android.databinding.library.baseAdapters.BR;
import com.wutaodsg.androidmvvm.R;
import com.wutaodsg.androidmvvm.constant.ViewModelEventTags;
import com.wutaodsg.androidmvvm.databinding.FragmentTopBinding;
import com.wutaodsg.androidmvvm.viewmodel.BottomFragmentViewModel;
import com.wutaodsg.androidmvvm.viewmodel.TopFragmentViewModel;
import com.wutaodsg.mvvm.bindingadapter.EditTextBindingAdapter.TextChangeDataWrapper;
import com.wutaodsg.mvvm.command.Action0;
import com.wutaodsg.mvvm.command.Action1;
import com.wutaodsg.mvvm.command.ReplyCommand;
import com.wutaodsg.mvvm.core.BaseMVVMFragment;
import com.wutaodsg.mvvm.core.annotation.BindVariable;
import com.wutaodsg.mvvm.core.annotation.MainViewModel;
import com.wutaodsg.mvvm.util.log.LogUtils;
import com.wutaodsg.mvvm.util.vmeventbus.ViewModelEventBus;


/**
 * Created by wutao on 2018/4/5.
 */

@MainViewModel(TopFragmentViewModel.class)
public class TopFragment extends BaseMVVMFragment<TopFragmentViewModel, FragmentTopBinding> {

    private static final String TAG = "TopFragment";
    
    
    @BindVariable(BR.onTextChangeCommand)
    private final ReplyCommand<TextChangeDataWrapper> mOnTextChangeCommand = new ReplyCommand<>(new Action1<TextChangeDataWrapper>() {

        @Override
        public void execute(TextChangeDataWrapper textChangeDataWrapper) {
            boolean result = ViewModelEventBus.getInstance().post(ViewModelEventTags.TEXT,
                    textChangeDataWrapper.s.toString(), BottomFragmentViewModel.class);
            LogUtils.d(TAG, "onTextChangeCommand: result: " + result);
        }
    });

    @BindVariable(BR.openChildViewClickCommand)
    private final ReplyCommand mOpenChildViewClickCommand = new ReplyCommand(new Action0() {
        @Override
        public void execute() {
            startActivity(new Intent(getContext(), ChildViewActivity.class));
        }
    });


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_top;
    }
}
