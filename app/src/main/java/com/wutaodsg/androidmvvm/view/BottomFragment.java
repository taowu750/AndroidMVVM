package com.wutaodsg.androidmvvm.view;


import com.wutaodsg.androidmvvm.R;
import com.wutaodsg.androidmvvm.databinding.FragmentBottomBinding;
import com.wutaodsg.androidmvvm.viewmodel.BottomFragmentViewModel;
import com.wutaodsg.mvvm.core.BaseMVVMFragment;
import com.wutaodsg.mvvm.core.ViewModelType;

/**
 * Created by wutao on 2018/4/5.
 */

@ViewModelType(BottomFragmentViewModel.class)
public class BottomFragment extends BaseMVVMFragment<BottomFragmentViewModel, FragmentBottomBinding> {
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_bottom;
    }
}
