package com.wutaodsg.mvvm.core;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.lang.reflect.Field;

/**
 * 针对 BaseView 接口的代理类，实现了 BaseView 接口。
 * <p>
 * 它在内部提供了对 View 所需要的 ViewModel 和 DataBinding 的创建和绑定操作。
 * 并且它将 V 层对像和 VM 层对象中声明的 DataBinding Variable 以合适的方式
 * 与 DataBinding Layout XML 文件中对应的变量绑定在一起。
 * <p>
 * 此外，它将会在 View 中搜索所有 {@link UIAwareComponent}，将它们绑定到 View 中，
 * 从而让这些界面感知组件可以感知 View 的界面变化情况。
 */

public class BaseViewProxy<VM extends BaseViewModel, DB extends ViewDataBinding>
        implements BaseView<VM, DB> {

    private CoreView<VM, DB> mCoreView;

    private VM mViewModel;
    private DB mDataBinding;


    /**
     * 将 FragmentActivity 和它的 ViewModel、DataBinding 对象绑定在一起，并初始化
     * 它们的状态。需要注意的是，参数 fragmentActivity 与 coreView 必须是同一个对象。
     *
     * @param fragmentActivity FragmentActivity 对象
     * @param coreView CoreView 对象，与参数 fragmentActivity 是同一个对象
     * @param viewModel ViewModel 对象，也就是自定义的 ViewModel，{@link CoreView#newViewModel()}
     */
    @SuppressWarnings("unchecked")
    public BaseViewProxy(@NonNull FragmentActivity fragmentActivity,
                         @NonNull CoreView<VM, DB> coreView,
                         @Nullable VM viewModel) {
        mCoreView = coreView;
        mViewModel = viewModel;
        mDataBinding = DataBindingUtil.setContentView(fragmentActivity, mCoreView.getLayoutResId());

        ViewModelType viewModelType = fragmentActivity.getClass().getAnnotation(ViewModelType.class);
        if (mViewModel == null && viewModelType != null) {
            try {
                Class<VM> viewModelClass = (Class<VM>) viewModelType.value();
                mViewModel = ViewModelProviders.of(fragmentActivity).get(viewModelClass);
            } catch (Exception e) {
                throw new IllegalStateException(mCoreView.getClass().getName() + ": " +
                        "ViewModel creation failure! Check whether the ViewModel" +
                        " referred to by the annotation  has a non-reference or default constructor." +
                        " Whether or not the ViewModel type is consistent with the generics");
            }
        }

        getViewModel().onAttach(fragmentActivity);
        bindAllDataBindingVariables(mCoreView);
        bindUIAwareComponent(mCoreView);
    }

    /**
     * 将 Fragment 和它的 ViewModel、DataBinding 对象绑定在一起，并初始化
     * 它们的状态。需要注意的是，参数 fragment 与 coreView 必须是同一个对象。
     *
     * @param fragment Fragment 对象
     * @param coreView CoreView 对象，与参数 fragment 是同一个对象
     * @param viewModel ViewModel 对象，也就是自定义的 ViewModel，{@link CoreView#newViewModel()}
     */
    @SuppressWarnings("unchecked")
    public BaseViewProxy(@NonNull Fragment fragment,
                         @NonNull CoreView<VM, DB> coreView,
                         @Nullable VM viewModel) {
        mCoreView = coreView;
        mViewModel = viewModel;
        mDataBinding = DataBindingUtil.bind(fragment.getView());

        ViewModelType viewModelType = fragment.getClass().getAnnotation(ViewModelType.class);
        if (mViewModel == null && viewModelType != null) {
            try {
                Class<VM> viewModelClass = (Class<VM>) viewModelType.value();
                mViewModel = ViewModelProviders.of(fragment).get(viewModelClass);
            } catch (Exception e) {
                throw new IllegalStateException(mCoreView.getClass().getName() + ": " +
                        "ViewModel creation failure! Check whether the ViewModel" +
                        " referred to by the annotation  has a non-reference or default constructor." +
                        " Whether or not the ViewModel type is consistent with the generics");
            }
        }

        getViewModel().onAttach(fragment.getContext());
        bindAllDataBindingVariables(mCoreView);
        bindUIAwareComponent(mCoreView);
    }


    @NonNull
    public final VM getViewModel() {
        if (mViewModel == null) {
            throw new IllegalStateException(mCoreView.getClass().getName() + ": No ViewModel were created!");
        }

        return mViewModel;
    }

    @NonNull
    public final DB getDataBinding() {
        return mDataBinding;
    }

    /**
     * 解除 View 与 ViewModel、DataBinding 之间的绑定，然后清理 BaseViewProxy 所持有的资源。
     */
    public final void clear() {
        unbindUIAwareComponent(mCoreView);
        mViewModel.onDetach();
        mViewModel = null;
        mDataBinding = null;
        mCoreView = null;
    }


    private void bindAllDataBindingVariables(CoreView<VM, DB> coreView) {
        if (mDataBinding != null && mViewModel != null) {
            bindDataBindingVariables(coreView);
            bindDataBindingVariables(getViewModel());
        }
    }

    private void bindDataBindingVariables(Object dataBindingVariable) {
        Field[] commandVariableFields = dataBindingVariable.getClass().getDeclaredFields();
        if (commandVariableFields != null) {
            for (Field field : commandVariableFields) {
                BindVariable bindVariable = field.getAnnotation(BindVariable.class);
                if (bindVariable != null) {
                    try {
                        field.setAccessible(true);
                        mDataBinding.setVariable(bindVariable.value(), field.get(dataBindingVariable));
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(mCoreView.getClass().getName() + ": " +
                                "Can't bind item according this field: " + field.getName());
                    }
                }
            }
        }
    }

    private void bindUIAwareComponent(CoreView<VM, DB> coreView) {
        Field[] fields = coreView.getClass().getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();
            if (UIAwareComponent.class.isAssignableFrom(type)) {
                try {
                    field.setAccessible(true);
                    coreView.getLifecycle().addObserver((UIAwareComponent) field.get(coreView));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(mCoreView.getClass().getName() + ": " +
                            "Can not add this UIAwareComponent: " + field.getName());
                }
            }
        }
    }

    private void unbindUIAwareComponent(CoreView<VM, DB> coreView) {
        Field[] fields = coreView.getClass().getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();
            if (UIAwareComponent.class.isAssignableFrom(type)) {
                try {
                    field.setAccessible(true);
                    coreView.getLifecycle().removeObserver((UIAwareComponent) field.get(coreView));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(mCoreView.getClass().getName() + ": " +
                            "Can not remove this UIAwareComponent: " + field.getName());
                }
            }
        }
    }
}
