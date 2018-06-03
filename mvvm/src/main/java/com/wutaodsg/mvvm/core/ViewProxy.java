package com.wutaodsg.mvvm.core;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModel;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * View 对象的代理类，实现了 {@link BaseView} 和 {@link ContainerView} 接口。
 * 它是框架底层代码的核心类之一。
 * </p>
 * <p>
 * 它在内部提供了对 View 所需要的 ViewModel 和 DataBinding 的创建和绑定操作。
 * 并且它将 V 层对像和 VM 层对象中声明的 DataBinding Variable 以合适的方式
 * 与 DataBinding Layout XML 文件中对应的变量绑定在一起。
 * </p>
 * <p>
 * 它将会在 View 中搜索所有 {@link UIAwareComponent}，将它们绑定到 View 中，
 * 从而让这些界面感知组件可以感知 View 的界面变化情况。
 * </p>
 * <p>
 * 如果在 View 上使用 {@link BindChildView} 或 {@link BindChildViews} 注解
 * 声明了子 View，ViewProxy 将会绑定这些子 View 并照管它们。
 * </p>
 */

public class ViewProxy<VM extends BaseViewModel, DB extends ViewDataBinding>
        implements BaseView<VM, DB>, ContainerView {

    private static final String TAG = "WuT.ViewProxy";


    private CoreView<VM, DB> mCoreView;
    private BaseMVVMActivity<VM, DB> mActivity;
    private BaseMVVMFragment<VM, DB> mFragment;

    private VM mViewModel;
    private DB mDataBinding;

    private Lifecycle.State mCurrentState;
    private Lifecycle.Event mCurrentEvent;

    private Bundle mSavedInstanceState;

    private ContainerViewImpl mContainerView;


    /**
     * 将 CoreView 和它的 ViewModel、DataBinding 对象绑定在一起 ，并初始化
     * 它们的状态。如果有的话，也会绑定它的 {@link ChildView}。<br/>
     * 这个 CoreView 必须是 {@link BaseMVVMActivity} 或者
     * {@link BaseMVVMFragment}。
     *
     * @param coreView CoreView 对象
     */
    @SuppressWarnings("unchecked")
    public ViewProxy(@NonNull CoreView<VM, DB> coreView) {
        mCurrentState = Lifecycle.State.INITIALIZED;

        mCoreView = coreView;
        mActivity = coreView instanceof BaseMVVMActivity ? (BaseMVVMActivity<VM, DB>) coreView : null;
        mFragment = coreView instanceof BaseMVVMFragment ? (BaseMVVMFragment<VM, DB>) coreView : null;
        assertCoreView(coreView);

        mContainerView = new ContainerViewImpl(mCoreView);

        if (mActivity != null) {
            mDataBinding = DataBindingUtil.setContentView(mActivity, mCoreView.getLayoutResId());
        } else {
            mDataBinding = DataBindingUtil.bind(mFragment.getView());
        }
        mViewModel = (VM) createViewModel(mCoreView);

        mViewModel.onAttach(mActivity != null ? mActivity : mFragment.getActivity());
        bindAllDataBindingVariables(mCoreView, mViewModel, mDataBinding);
        bindUIAwareComponent(mCoreView);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCurrentState = Lifecycle.State.CREATED;
        mCurrentEvent = Lifecycle.Event.ON_CREATE;

        mSavedInstanceState = savedInstanceState;

        bindChildViewsByAnnotation(mCoreView, null, mContainerView);
    }

    @Override
    public void onStart() {
        mCurrentState = Lifecycle.State.STARTED;
        mCurrentEvent = Lifecycle.Event.ON_START;

        mContainerView.childViewsOnStart();
    }

    @Override
    public void onResume() {
        mCurrentState = Lifecycle.State.RESUMED;
        mCurrentEvent = Lifecycle.Event.ON_RESUME;

        mContainerView.childViewsOnResume();
    }

    @Override
    public void onPause() {
        mCurrentEvent = Lifecycle.Event.ON_PAUSE;

        mContainerView.childViewsOnPause();
    }

    @Override
    public void onStop() {
        mCurrentEvent = Lifecycle.Event.ON_STOP;

        mContainerView.childViewsOnStop();
    }

    @Override
    public void onDestroy() {
        mCurrentState = Lifecycle.State.DESTROYED;
        mCurrentEvent = Lifecycle.Event.ON_DESTROY;

        mContainerView.unbindAllChildViews();
        mContainerView = null;

        unbindUIAwareComponent(mCoreView);
        mViewModel.onDetach();

        mViewModel = null;
        mDataBinding = null;
        mCoreView = null;
        mActivity = null;
        mFragment = null;
        mSavedInstanceState = null;
    }

    @Override
    public Lifecycle.State getCurrentState() {
        return mCurrentState;
    }

    @Override
    public Lifecycle.Event getCurrentEvent() {
        return mCurrentEvent;
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
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    boolean containsChildView(Class<CV> childViewClass, @IdRes int containerId) {
        return mContainerView.containsChildView(childViewClass, containerId);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    boolean containsChildView(Class<CV> childViewClass) {
        return mContainerView.containsChildView(childViewClass);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV bindChildView(Class<CV> childViewClass,
                     @IdRes int containerId,
                     boolean attachToParent,
                     boolean removeViews) {
        return mContainerView.bindChildView(childViewClass, containerId, attachToParent, removeViews);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV bindChildView(Class<CV> childViewClass, @IdRes int containerId,
                     boolean attachToParent) {
        return mContainerView.bindChildView(childViewClass, containerId, attachToParent);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV bindChildView(Class<CV> childViewClass, @IdRes int containerId) {
        return mContainerView.bindChildView(childViewClass, containerId);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    boolean unbindChildView(Class<CV> childViewClass, @IdRes int containerId) {
        return mContainerView.unbindChildView(childViewClass, containerId);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    boolean unbindChildView(Class<CV> childViewClass) {
        return mContainerView.unbindChildView(childViewClass);
    }

    @Override
    public boolean unbindAllChildViews() {
        return mContainerView.unbindAllChildViews();
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV getChildView(Class<CV> childViewClass, @IdRes int containerId) {
        return mContainerView.getChildView(childViewClass, containerId);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV[] getChildViews(Class<CV> childViewClass) {
        return mContainerView.getChildViews(childViewClass);
    }

    @Override
    public final ChildView[] getChildViews() {
        return mContainerView.getChildViews();
    }


    class ContainerViewImpl implements ContainerView {

        private CoreView mCoreView;
        private ChildView mChildView;
        private Map<Class<? extends ChildView>, Map<Integer, ChildView>> mChildViewMap;


        public ContainerViewImpl(@NonNull CoreView coreView, @Nullable ChildView childView) {
            mCoreView = coreView;
            mChildView = childView;
            mChildViewMap = new HashMap<>();
        }

        public ContainerViewImpl(@NonNull CoreView coreView) {
            this(coreView, null);
        }


        @Override
        public <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
        boolean containsChildView(Class<CV> childViewClass, @IdRes int containerId) {
            Map<Integer, ChildView> childViews = mChildViewMap.get(childViewClass);
            return childViews != null && childViews.get(containerId) != null;
        }

        @Override
        public <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
        boolean containsChildView(Class<CV> childViewClass) {
            return mChildViewMap.get(childViewClass) != null;
        }

        @SuppressLint("UseSparseArrays")
        @SuppressWarnings("unchecked")
        @Override
        public <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
        CV bindChildView(Class<CV> childViewClass,
                         @IdRes int containerId,
                         boolean attachToParent,
                         boolean removeViews) {
            // 判断当前生命周期事件是否可以执行 bindChildView 方法
            String name = mChildView != null ? mChildView.getClass().getName() : mCoreView.getClass().getName();
            if (mCurrentEvent != null &&
                    mCurrentEvent.compareTo(Lifecycle.Event.ON_PAUSE) >= 0) {
                throw new IllegalStateException(name + ": " +
                        "The bindChildView method can not be invoked in onPause and subsequent lifecycle events.");
            }

            if (!containsChildView(childViewClass, containerId)) {
                // 将 CoreView 转型成 Activity 或者 Fragment
                assertCoreView(mCoreView);
                BaseMVVMActivity activity = mCoreView instanceof BaseMVVMActivity ? (BaseMVVMActivity) mCoreView :
                        null;
                BaseMVVMFragment fragment = mCoreView instanceof BaseMVVMFragment ? (BaseMVVMFragment) mCoreView :
                        null;

                // 查找 ChildView 的容器 container 对象
                ViewGroup container;
                if (mChildView != null) {
                    container = (ViewGroup) mChildView.findViewById(containerId);
                } else {
                    container = (ViewGroup) mCoreView.findViewById(containerId);
                }
                if (container == null) {
                    throw new IllegalStateException("The specified Container does not exist, id: " + containerId);
                }
                if (removeViews) {
                    container.removeAllViews();
                }

                CV childView;
                try {
                    // 根据默认构造器创建 ChildView 对象
                    childView = childViewClass.newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException(childViewClass.getName() + ": must have a anonymous " +
                            "constructor");
                }
                // 在为 ChildView 设置必要参数之前，回调它的 beforeBindView() 方法
                childView.beforeBindView();

                // 创建 ChildView 的 DataBinding 对象
                CDB childViewDataBinding = DataBindingUtil.inflate(mCoreView.getLayoutInflater(),
                        childView.getLayoutResId(), container, attachToParent);
                if (childViewDataBinding == null) {
                    throw new IllegalStateException(childViewClass.getName() + ": The DataBinding of ChildView " +
                            "can't be built");
                }

                // 创建 ChildView 的 ViewModel 对象
                CVM childViewModel = (CVM) createViewModel(childView);

                // 为 ChildView 设置它的 VM、DB、Parent、Context 和 Container
                childView.setDataBinding(childViewDataBinding);
                childView.setViewModel(childViewModel);
                if (mChildView != null) {
                    childView.setParentChildView(mChildView);
                }
                if (activity != null) {
                    childView.setParentActivity(activity);
                    childView.setContext(activity);
                } else {
                    childView.setParentFragment(fragment);
                    childView.setContext(fragment.getContext());
                }
                childView.setContainer(container);

                // 为 ChildView 实施绑定
                if (activity != null) {
                    childViewModel.onAttach(activity);
                } else {
                    childViewModel.onAttach(fragment.getContext());
                }
                bindAllDataBindingVariables(childView, childViewModel, childViewDataBinding);
                bindUIAwareComponent(childView);

                // 将 ChildView 保存到 Map 中，以便后续获取
                Map<Integer, ChildView> childViews = mChildViewMap.get(childViewClass);
                if (childViews != null) {
                    childViews.put(containerId, childView);
                } else {
                    childViews = new HashMap<>(2);
                    childViews.put(containerId, childView);
                    mChildViewMap.put(childViewClass, childViews);
                }

                // 根据父 View 的生命周期事件调用相应的方法
                switch (mCurrentEvent) {
                    case ON_CREATE:
                        childView.onCreate(mSavedInstanceState);
                        break;

                    case ON_START:
                        childView.onCreate(mSavedInstanceState);
                        childView.onStart();
                        break;

                    case ON_RESUME:
                        childView.onCreate(mSavedInstanceState);
                        childView.onStart();
                        childView.onResume();
                        break;
                }

                return childView;

            }

            return (CV) mChildViewMap.get(childViewClass).get(containerId);
        }

        @Override
        public <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
        CV bindChildView(Class<CV> childViewClass, @IdRes int containerId, boolean attachToParent) {
            return bindChildView(childViewClass, containerId, attachToParent, false);
        }

        @Override
        public <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
        CV bindChildView(Class<CV> childViewClass, @IdRes int containerId) {
            return bindChildView(childViewClass, containerId, true, false);
        }

        @Override
        public <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
        boolean unbindChildView(Class<CV> childViewClass, @IdRes int containerId) {
            if (mCurrentEvent == null) {
                throw new IllegalStateException(mCoreView.getClass().getName() + ": " +
                        "The parent View is also initialized or destroyed.");
            }

            Map<Integer, ChildView> childViews = mChildViewMap.get(childViewClass);
            if (childViews != null) {
                ChildView childView = childViews.get(containerId);
                if (childView != null) {
                    switch (mCurrentEvent) {
                        case ON_RESUME:
                            childView.onPause();

                        case ON_START:
                        case ON_PAUSE:
                            childView.onStop();

                        case ON_CREATE:
                        case ON_STOP:
                        case ON_DESTROY:
                            destroyChildView(childView);
                            childView.onDestroy();
                            break;
                    }
                    childViews.remove(containerId);

                    return true;
                }
            }

            return false;
        }

        @Override
        public <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
        boolean unbindChildView(Class<CV> childViewClass) {
            if (mCurrentEvent == null) {
                throw new IllegalStateException(mCoreView.getClass().getName() + ": " +
                        "The parent View is also initialized or destroyed.");
            }

            Map<Integer, ChildView> childViews = mChildViewMap.get(childViewClass);
            if (childViews != null) {
                for (ChildView childView : childViews.values()) {
                    switch (mCurrentEvent) {
                        case ON_RESUME:
                            childView.onPause();

                        case ON_START:
                        case ON_PAUSE:
                            childView.onStop();

                        case ON_CREATE:
                        case ON_STOP:
                        case ON_DESTROY:
                            destroyChildView(childView);
                            childView.onDestroy();
                            break;
                    }
                }
                childViews.clear();
                mChildViewMap.remove(childViewClass);

                return true;
            }

            return false;
        }

        @Override
        public boolean unbindAllChildViews() {
            if (mChildViewMap.size() > 0) {
                for (Class<? extends ChildView> key : mChildViewMap.keySet()) {
                    unbindChildView(key);
                }
                mChildViewMap.clear();

                return true;
            }

            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
        CV getChildView(Class<CV> childViewClass, @IdRes int containerId) {
            Map<Integer, ChildView> childViews = mChildViewMap.get(childViewClass);
            return childViews != null ? (CV) childViews.get(containerId) : null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
        CV[] getChildViews(Class<CV> childViewClass) {
            Map<Integer, ChildView> childViews = mChildViewMap.get(childViewClass);
            if (childViews != null) {
                CV[] cvs = (CV[]) Array.newInstance(childViewClass, childViews.size());
                return childViews.values().toArray(cvs);
            }

            return null;
        }

        @Override
        public ChildView[] getChildViews() {
            int size = 0;
            for (Map<Integer, ChildView> childViews : mChildViewMap.values()) {
                size += childViews.size();
            }

            if (size > 0) {
                ChildView[] childViews = new ChildView[size];
                int i = 0;
                for (Map<Integer, ChildView> childViewMap : mChildViewMap.values()) {
                    for (ChildView childView : childViewMap.values()) {
                        childViews[i++] = childView;
                    }
                }

                return childViews;
            }

            return null;
        }


        void childViewsOnStart() {
            for (Map<Integer, ChildView> childViews : mChildViewMap.values()) {
                for (ChildView childView : childViews.values()) {
                    childView.onStart();
                }
            }
        }

        void childViewsOnResume() {
            for (Map<Integer, ChildView> childViews : mChildViewMap.values()) {
                for (ChildView childView : childViews.values()) {
                    childView.onResume();
                }
            }
        }

        void childViewsOnPause() {
            for (Map<Integer, ChildView> childViews : mChildViewMap.values()) {
                for (ChildView childView : childViews.values()) {
                    childView.onPause();
                }
            }
        }

        void childViewsOnStop() {
            for (Map<Integer, ChildView> childViews : mChildViewMap.values()) {
                for (ChildView childView : childViews.values()) {
                    childView.onStop();
                }
            }
        }
    }


    void bindChildViewsByAnnotation(@NonNull CoreView coreView,
                                    @Nullable ChildView childView,
                                    @NonNull ContainerView containerView) {
        assertCoreView(coreView);

        BindChildView bindChildView;
        BindChildViews bindChildViews;

        // 获取 BindChildView 和 BindChildViews 注解
        if (childView != null) {
            bindChildView = childView.getClass().getAnnotation(BindChildView.class);
            bindChildViews = childView.getClass().getAnnotation(BindChildViews.class);
        } else {
            bindChildView = coreView.getClass().getAnnotation(BindChildView.class);
            bindChildViews = coreView.getClass().getAnnotation(BindChildViews.class);
        }

        // 当注解存在时，通过这些注解绑定 ChildView
        if (bindChildView != null) {
            bindChildViewByAnnotation(bindChildView, containerView);
        }
        if (bindChildViews != null) {
            for (BindChildView b : bindChildViews.value()) {
                bindChildViewByAnnotation(b, containerView);
            }
        }
    }

    private void bindChildViewByAnnotation(@NonNull BindChildView bindChildView,
                                           @NonNull ContainerView containerView) {
        // 从 @BindChildView 注解中获取需要的信息
        Class<? extends ChildView> childViewClass = bindChildView.type();
        int containerId = bindChildView.container();
        boolean attachToParent = bindChildView.attachToParent();
        boolean removeViews = bindChildView.removeViews();

        containerView.bindChildView(childViewClass, containerId, attachToParent, removeViews);
    }

    private void bindAllDataBindingVariables(@NonNull CoreView coreView,
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

    private void bindUIAwareComponent(@NonNull CoreView coreView) {
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

    private void unbindUIAwareComponent(@NonNull CoreView coreView) {
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

    @SuppressWarnings("unchecked")
    @NonNull
    private BaseViewModel createViewModel(@NonNull CoreView coreView) {
        BaseViewModel viewModel = coreView.onCreateViewModel();
        if (viewModel == null) {
            ViewModelType viewModelType = coreView.getClass().getAnnotation(ViewModelType.class);
            if (viewModelType != null) {
                try {
                    Class<? extends BaseViewModel> viewModelClass = viewModelType.value();
                    viewModel = mCoreView.newViewModel(viewModelClass);
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
                    "You didn't build ViewModel! You can choose to use ViewModelType annotation or newViewModel " +
                    "() method to build ViewModel for View.");
        }

        return viewModel;
    }

    /**
     * 销毁 ChildView 的视图，解除 UIAwareComponent 的绑定
     */
    private void destroyChildView(@NonNull ChildView childView) {
        childView.getContainer().removeView(childView.getDataBinding().getRoot());
        unbindUIAwareComponent(childView);
    }

    /**
     * 如果 coreView 为 null 或 coreView 不属于 BaseMVVMActivity/BaseMVVMFragment 的子类，
     * 抛出异常。
     */
    private void assertCoreView(CoreView coreView) {
        if (!(coreView instanceof BaseMVVMActivity) && !(coreView instanceof BaseMVVMFragment)) {
            throw new IllegalStateException(coreView.getClass().getName() +
                    ": coreView must be one of the BaseMVVMActivity and BaseMVVMFragment");
        }
    }
}
