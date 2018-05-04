package com.wutaodsg.mvvm.core;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *     View 对象的代理类，实现了 {@link BaseView} 和 {@link ParentView} 接口。
 * </p>
 * <p>
 *     它在内部提供了对 View 所需要的 ViewModel 和 DataBinding 的创建和绑定操作。
 *     并且它将 V 层对像和 VM 层对象中声明的 DataBinding Variable 以合适的方式
 *     与 DataBinding Layout XML 文件中对应的变量绑定在一起。
 * </p>
 * <p>
 *     它将会在 View 中搜索所有 {@link UIAwareComponent}，将它们绑定到 View 中，
 *     从而让这些界面感知组件可以感知 View 的界面变化情况。
 * </p>
 * <p>
 *     如果在 View 上使用 {@link BindChildView} 或 {@link BindChildViews} 注解
 *     声明了子 View，ViewProxy 将会绑定这些子 View 并照管它们，
 * </p>
 */

public class ViewProxy<VM extends BaseViewModel, DB extends ViewDataBinding>
        implements BaseView<VM, DB>, ParentView {

    private static final String TAG = "WuT.ViewProxy";


    private CoreView<VM, DB> mCoreView;
    private VM mViewModel;
    private DB mDataBinding;

    private Map<Class, ChildView> mChildViewMap = new HashMap<>();


    /**
     * 将 activity 和它的 ViewModel、DataBinding 对象绑定在一起，并初始化
     * 它们的状态。同时也会绑定声明的子 View。
     *
     * @param activity BaseMVVMActivity 对象
     */
    @SuppressWarnings("unchecked")
    public ViewProxy(@NonNull BaseMVVMActivity<VM, DB> activity,
                     @Nullable Bundle savedInstanceState) {
        mCoreView = activity;
        mDataBinding = DataBindingUtil.setContentView(activity, mCoreView.getLayoutResId());
        mViewModel = (VM) createViewModel(activity, null, mCoreView);

        mViewModel.onAttach(activity);
        bindAllDataBindingVariables(mCoreView, mViewModel, mDataBinding);
        bindUIAwareComponent(mCoreView);

        bindChildViews(activity, null);
        childViewsOnCreate(savedInstanceState);
    }

    /**
     * 将 fragment 和它的 ViewModel、DataBinding 对象绑定在一起，并初始化
     * 它们的状态。同时也会绑定声明的子 View。
     *
     * @param fragment BaseMVVMFragment 对象
     */
    @SuppressWarnings("unchecked")
    public ViewProxy(@NonNull BaseMVVMFragment<VM, DB> fragment,
                     @Nullable Bundle savedInstanceState) {
        mCoreView = fragment;
        mDataBinding = DataBindingUtil.bind(fragment.getView());
        mViewModel = (VM) createViewModel(null, fragment, mCoreView);

        mViewModel.onAttach(fragment.getContext());
        bindAllDataBindingVariables(mCoreView, mViewModel, mDataBinding);
        bindUIAwareComponent(mCoreView);

        bindChildViews(null, fragment);
        childViewsOnCreate(savedInstanceState);
    }


    @NonNull
    public final VM getViewModel() {
        return mViewModel;
    }

    @NonNull
    public final DB getDataBinding() {
        return mDataBinding;
    }

    @Override
    public <VM extends BaseViewModel, DB extends ViewDataBinding, CV extends ChildView<VM, DB>>
    boolean containsChildView(Class<CV> childViewClass) {
        return mChildViewMap.containsKey(childViewClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <VM extends BaseViewModel, DB extends ViewDataBinding, CV extends ChildView<VM, DB>>
    CV getChildView(Class<CV> childViewClass) {
        return (CV) mChildViewMap.get(childViewClass);
    }

    @Override
    public List<ChildView> getChildViews() {
        return new ArrayList<>(mChildViewMap.values());
    }


    /**
     * 销毁子 View，解除 View 与 ViewModel、DataBinding 之间的绑定，
     * 最后清理 ViewProxy 所持有的资源。
     */
    void destroy() {
        destroyChildViews();
        unbindUIAwareComponent(mCoreView);
        mViewModel.onDetach();
        mViewModel = null;
        mDataBinding = null;
        mCoreView = null;
    }


    private void childViewsOnCreate(Bundle savedInstanceState) {
        for (ChildView childView : mChildViewMap.values()) {
            childView.onCreate(savedInstanceState);
        }
    }

    void childViewsOnStart() {
        for (ChildView childView : mChildViewMap.values()) {
            childView.onStart();
        }
    }

    void childViewsOnResume() {
        for (ChildView childView : mChildViewMap.values()) {
            childView.onResume();
        }
    }

    void childViewsOnPause() {
        for (ChildView childView : mChildViewMap.values()) {
            childView.onPause();
        }
    }

    void childViewsOnStop() {
        for (ChildView childView : mChildViewMap.values()) {
            childView.onStop();
        }
    }


    private void bindChildViews(@Nullable BaseMVVMActivity<VM, DB> activity,
                                @Nullable BaseMVVMFragment<VM, DB> fragment) {
        BindChildView bindChildView = null;
        BindChildViews bindChildViews = null;

        // 获取 BindChildView 和 BindChildViews 注解
        if (activity != null) {
            bindChildView = activity.getClass().getAnnotation(BindChildView.class);
            bindChildViews = activity.getClass().getAnnotation(BindChildViews.class);
        } else if (fragment != null) {
            bindChildView = fragment.getClass().getAnnotation(BindChildView.class);
            bindChildViews = fragment.getClass().getAnnotation(BindChildViews.class);
        }

        // 当注解存在时，通过这些注解绑定 ChildView
        if (bindChildView != null) {
            bindChildView(activity, fragment, bindChildView);
        }
        if (bindChildViews != null) {
            for (BindChildView childView : bindChildViews.value()) {
                bindChildView(activity, fragment, childView);
            }
        }
    }

    private void bindChildView(@Nullable BaseMVVMActivity<VM, DB> activity,
                               @Nullable BaseMVVMFragment<VM, DB> fragment,
                               @NonNull BindChildView bindChildView) {
        // 从 @BindChildView 注解中获取需要的信息
        Class<? extends ChildView> childViewClass = bindChildView.type();
        int containerId = bindChildView.container();
        boolean attachToParent = bindChildView.attachToParent();

        try {
            // 根据默认构造器创建 ChildView 对象
            ChildView childView = childViewClass.newInstance();

            // 在为 ChildView 设置必要参数之前，回调它的 beforeBindView() 方法
            childView.beforeBindView();

            // 查找 ChildView 的容器 container 对象
            ViewGroup container = null;
            if (activity != null) {
                container = activity.findViewById(containerId);
            } else if (fragment != null) {
                container = fragment.getView().findViewById(containerId);
            }

            // 创建 ChildView 的 DataBinding 对象
            ViewDataBinding childViewDataBinding = null;
            if (activity != null) {
                childViewDataBinding = DataBindingUtil.inflate(activity.getLayoutInflater(),
                        childView.getLayoutResId(), container, attachToParent);
            } else if (fragment != null) {
                childViewDataBinding = DataBindingUtil.inflate(fragment.getLayoutInflater(),
                        childView.getLayoutResId(), container, attachToParent);
            }

            // 创建 ChildView 的 ViewModel 对象
            BaseViewModel childViewModel = createViewModel(activity, fragment, childView);

            // 为 ChildView 设置它的 VM、DB、Parent、Context 和 Container
            childView.setDataBinding(childViewDataBinding);
            childView.setViewModel(childViewModel);
            if (activity != null) {
                childView.setParentActivity(activity);
                childView.setContext(activity);
            } else if (fragment != null) {
                childView.setParentFragment(fragment);
                childView.setContext(fragment.getContext());
            }
            childView.setContainer(container);

            // 为 ChildView 实施绑定
            if (activity != null) {
                childViewModel.onAttach(activity);
            } else if (fragment != null) {
                childViewModel.onAttach(fragment.getContext());
            }
            bindAllDataBindingVariables(childView, childViewModel, childViewDataBinding);
            bindUIAwareComponent(childView);

            // 将 ChildView 保存到 Proxy 中，以便后续获取
            mChildViewMap.put(childViewClass, childView);
        } catch (Exception e) {
            throw new IllegalStateException("\"" + childViewClass.getName() + "\" must have a anonymous " +
                    "constructor");
        }
    }

    private void destroyChildViews() {
        for (ChildView childView : mChildViewMap.values()) {
            unbindUIAwareComponent(childView);
            childView.onDestroy();
        }
        mChildViewMap.clear();
    }

    private void bindAllDataBindingVariables(@NonNull CoreView<VM, DB> coreView,
                                             @NonNull ViewModel viewModel,
                                             @NonNull ViewDataBinding dataBinding) {
        bindDataBindingVariables(coreView, dataBinding);
        bindDataBindingVariables(viewModel, dataBinding);
    }

    private void bindDataBindingVariables(@NonNull Object dataBindingVariable,
                                          @NonNull ViewDataBinding dataBinding) {
        Field[] commandVariableFields = dataBindingVariable.getClass().getDeclaredFields();
        if (commandVariableFields != null) {
            for (Field field : commandVariableFields) {
                BindVariable bindVariable = field.getAnnotation(BindVariable.class);
                if (bindVariable != null) {
                    try {
                        field.setAccessible(true);
                        dataBinding.setVariable(bindVariable.value(), field.get(dataBindingVariable));
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(dataBindingVariable.getClass().getName() + ": " +
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
                    mCoreView.getLifecycle().addObserver((UIAwareComponent) field.get(coreView));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(coreView.getClass().getName() + ": " +
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
                    mCoreView.getLifecycle().removeObserver((UIAwareComponent) field.get(coreView));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(coreView.getClass().getName() + ": " +
                            "Can not remove this UIAwareComponent: " + field.getName());
                }
            }
        }
    }

    private BaseViewModel createViewModel(@Nullable BaseMVVMActivity<VM, DB> activity,
                                          @Nullable BaseMVVMFragment<VM, DB> fragment,
                                          @NonNull CoreView coreView) {
        BaseViewModel viewModel = coreView.newViewModel();
        if (viewModel == null) {
            ViewModelType viewModelType = coreView.getClass().getAnnotation(ViewModelType.class);
            if (viewModelType != null) {
                try {
                    Class<? extends BaseViewModel> viewModelClass = viewModelType.value();
                    if (activity != null) {
                        viewModel = ViewModelProviders.of(activity).get(viewModelClass);
                    } else if (fragment != null) {
                        viewModel = ViewModelProviders.of(fragment).get(viewModelClass);
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(coreView.getClass().getName() + ": " +
                            "ViewModel creation failure! \nCheck whether the ViewModel" +
                            " referred to by the annotation  has a non-reference or default constructor.\n" +
                            " Whether or not the ViewModel type is consistent with the generics");
                }
            }
        }
        if (viewModel == null) {
            throw new IllegalStateException(coreView.getClass().getName() + ": " +
                    "You didn't create ViewModel! You can choose to use ViewModelType annotation or newViewModel " +
                    "() method to create ViewModel for View.");
        }

        return viewModel;
    }
}
