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

import com.wutaodsg.mvvm.core.annotation.BindChildView;
import com.wutaodsg.mvvm.core.annotation.BindChildViews;
import com.wutaodsg.mvvm.core.annotation.BindVariable;
import com.wutaodsg.mvvm.core.annotation.ExtraViewModel;
import com.wutaodsg.mvvm.core.annotation.ExtraViewModels;
import com.wutaodsg.mvvm.core.annotation.MainViewModel;
import com.wutaodsg.mvvm.core.iview.BaseView;
import com.wutaodsg.mvvm.core.iview.ContainerView;
import com.wutaodsg.mvvm.core.iview.CoreView;
import com.wutaodsg.mvvm.core.iview.ExtraViewModelView;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * View 对象的代理类，实现了 {@link BaseView} 、{@link ContainerView}
 * 以及 {@link ExtraViewModelView} 接口。它是框架底层代码的核心类之一。
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
        implements BaseView<VM, DB>, ContainerView, ExtraViewModelView {

    private static final String TAG = "WuT.ViewProxy";


    private CoreView<VM, DB> mCoreView;

    private VM mViewModel;
    private DB mDataBinding;

    private Lifecycle.State mCurrentState;
    private Lifecycle.Event mCurrentEvent;

    private Bundle mSavedInstanceState;

    private ContainerViewImpl mContainerView;
    private ExtraViewModelViewImpl mExtraViewModelView;


    /**
     * 将 CoreView 和它的 ViewModel、DataBinding 对象绑定在一起 ，并初始化
     * 它们的状态。如果有的话，也会绑定它的 {@link ChildView}。<br/>
     * 这个 CoreView 必须是 {@link BaseMVVMActivity} 或者
     * {@link BaseMVVMFragment}。<br/>
     * 如果 coreView 同时实现了 {@link ExtraViewModelView} 接口，
     * ViewProxy 还会绑定它的额外的 ViewModel。
     *
     * @param coreView CoreView 对象
     */
    @SuppressWarnings("unchecked")
    public ViewProxy(@NonNull CoreView<VM, DB> coreView) {
        mCurrentState = Lifecycle.State.INITIALIZED;

        mCoreView = coreView;
        BaseMVVMActivity<VM, DB> activity = coreView instanceof BaseMVVMActivity ? (BaseMVVMActivity<VM, DB>)
                coreView : null;
        BaseMVVMFragment<VM, DB> fragment = coreView instanceof BaseMVVMFragment ? (BaseMVVMFragment<VM, DB>)
                coreView : null;
        assertCoreView(coreView);

        mContainerView = new ContainerViewImpl(mCoreView);

        if (activity != null) {
            mDataBinding = DataBindingUtil.setContentView(activity, mCoreView.getLayoutResId());
        } else {
            mDataBinding = DataBindingUtil.bind(fragment.getView());
        }
        mViewModel = (VM) createMainViewModel(mCoreView);

        mViewModel.onAttach(mCoreView.getContext());
        bindAllDataBindingVariables(mCoreView, mViewModel, mDataBinding);
        bindUIAwareComponent(mCoreView);

        if (mCoreView instanceof ExtraViewModelView) {
            mExtraViewModelView = new ExtraViewModelViewImpl((ExtraViewModelView) mCoreView);
            bindExtraViewModelsByAnnotation((ExtraViewModelView) mCoreView);
        }
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
        mContainerView.clear();
        mContainerView = null;

        unbindUIAwareComponent(mCoreView);
        mViewModel.onDetach();

        mViewModel = null;
        mDataBinding = null;
        mCoreView = null;
        mSavedInstanceState = null;

        if (mExtraViewModelView != null) {
            mExtraViewModelView.unbindAllExtraViewModels();
            mExtraViewModelView.clear();
            mExtraViewModelView = null;
        }
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


    @Override
    public <EVM extends BaseViewModel>
    boolean containsExtraViewModel(Class<EVM> viewModelClass) {
        return mExtraViewModelView != null && mExtraViewModelView.containsExtraViewModel(viewModelClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <EVM extends BaseViewModel> EVM bindExtraViewModel(Class<EVM> viewModelClass) {
        return mExtraViewModelView != null ? (EVM) mExtraViewModelView.bindExtraViewModel(viewModelClass) :
                null;
    }

    @Override
    public <EVM extends BaseViewModel> boolean unbindExtraViewModel(Class<EVM> viewModelClass) {
        return mExtraViewModelView != null && mExtraViewModelView.unbindExtraViewModel(viewModelClass);
    }

    @Override
    public boolean unbindAllExtraViewModels() {
        return mExtraViewModelView != null && mExtraViewModelView.unbindAllExtraViewModels();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <EVM extends BaseViewModel> EVM getExtraViewModel(Class<EVM> viewModelClass) {
        return mExtraViewModelView != null ? (EVM) mExtraViewModelView.getExtraViewModel(viewModelClass) :
                null;
    }

    @Override
    public BaseViewModel[] getAllExtraViewModels() {
        return mExtraViewModelView != null ? mExtraViewModelView.getAllExtraViewModels() : null;
    }


    class ContainerViewImpl implements ContainerView {

        private CoreView mAncestorView;
        private ChildView mParentChildView;
        private Map<Class<? extends ChildView>, Map<Integer, ChildView>> mChildViewMap;


        /**
         * 父 View 是 ChildView 使用这个方法。
         */
        public ContainerViewImpl(@NonNull CoreView ancestorView, @Nullable ChildView parentChildView) {
            mAncestorView = ancestorView;
            mParentChildView = parentChildView;
            mChildViewMap = new HashMap<>(2);
        }

        /**
         * 父 View 是 BaseMVVMActivity 或 BaseMVVMFragment 用这个方法。
         */
        public ContainerViewImpl(@NonNull CoreView ancestorView) {
            this(ancestorView, null);
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
            String name = mParentChildView != null ? mParentChildView.getClass().getName() : mAncestorView.getClass()
                    .getName();
            if (mCurrentEvent == null ||
                    mCurrentEvent.compareTo(Lifecycle.Event.ON_PAUSE) >= 0) {
                throw new IllegalStateException(name + ": " +
                        "The bindChildView method can not be invoked in onPause and subsequent lifecycle events.");
            }

            if (childViewClass == null) {
                return null;
            }

            if (!containsChildView(childViewClass, containerId)) {
                // 如果父 View 是 ChildView 并且与将要绑定的 ChildView 类型相同，
                // 则停止绑定并返回 null
                if (mParentChildView != null && mParentChildView.getClass().equals(childViewClass)) {
                    return null;
                }

                // 将 CoreView 转型成 Activity 或者 Fragment
                assertCoreView(mAncestorView);
                BaseMVVMActivity activity = mAncestorView instanceof BaseMVVMActivity ? (BaseMVVMActivity)
                        mAncestorView :
                        null;
                BaseMVVMFragment fragment = mAncestorView instanceof BaseMVVMFragment ? (BaseMVVMFragment)
                        mAncestorView :
                        null;

                // 查找 ChildView 的容器 container 对象
                ViewGroup container;
                if (mParentChildView != null) {
                    container = (ViewGroup) mParentChildView.findViewById(containerId);
                } else {
                    container = (ViewGroup) mAncestorView.findViewById(containerId);
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
                CDB childViewDataBinding = DataBindingUtil.inflate(mAncestorView.getLayoutInflater(),
                        childView.getLayoutResId(), container, attachToParent);
                if (childViewDataBinding == null) {
                    throw new IllegalStateException(childViewClass.getName() + ": The DataBinding of ChildView " +
                            "can't be built");
                }

                // 创建 ChildView 的 ViewModel 对象
                CVM childViewModel = (CVM) createMainViewModel(childView);

                // 为 ChildView 设置它的 VM、DB、Parent、Context 和 Container
                childView.setDataBinding(childViewDataBinding);
                childView.setViewModel(childViewModel);
                if (mParentChildView != null) {
                    childView.setParentChildView(mParentChildView);
                }
                if (activity != null) {
                    childView.setParentActivity(activity);
                } else {
                    childView.setParentFragment(fragment);
                }
                childView.setContext(mAncestorView.getContext());
                childView.setContainer(container);

                // 为 ChildView 实施绑定：ViewModel、Variables 和 UIAwareComponent
                childViewModel.onAttach(mAncestorView.getContext());
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

            } else {
                return (CV) mChildViewMap.get(childViewClass).get(containerId);
            }
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
                throw new IllegalStateException(mAncestorView.getClass().getName() + ": " +
                        "The parent View is also initialized.");
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
                throw new IllegalStateException(mAncestorView.getClass().getName() + ": " +
                        "The parent View is also initialized.");
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

        void clear() {
            mChildViewMap.clear();
            mAncestorView = null;
            mParentChildView = null;
            mChildViewMap = null;
        }
    }


    class ExtraViewModelViewImpl<EV extends ExtraViewModelView & CoreView>
            implements ExtraViewModelView {

        private Map<Class<? extends BaseViewModel>, BaseViewModel> mViewModelMap;
        private EV mExtraView;


        public ExtraViewModelViewImpl(@NonNull EV extraView) {
            mExtraView = extraView;
            mViewModelMap = new HashMap<>(2);
        }

        @Override
        public <EVM extends BaseViewModel>
        boolean containsExtraViewModel(Class<EVM> viewModelClass) {
            return mViewModelMap.containsKey(viewModelClass);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <EVM extends BaseViewModel>
        EVM bindExtraViewModel(Class<EVM> viewModelClass) {
            if (mCurrentEvent == null ||
                    mCurrentEvent.compareTo(Lifecycle.Event.ON_DESTROY) >= 0) {
                throw new IllegalStateException(mExtraView.getClass().getName() + ": " +
                        "The parent View is also initialized or destroyed.");
            }

            if (viewModelClass == null) {
                return null;
            }

            if (!mViewModelMap.containsKey(viewModelClass)) {
                if (mExtraView.getViewModel().getClass().equals(viewModelClass)) {
                    return null;
                }

                EVM extraViewModel = (EVM) mExtraView.newViewModel(viewModelClass);
                bindDataBindingVariables(extraViewModel, mExtraView.getDataBinding());
                extraViewModel.onAttach(mExtraView.getContext());
                mViewModelMap.put(viewModelClass, extraViewModel);

                return extraViewModel;
            } else {
                return (EVM) mViewModelMap.get(viewModelClass);
            }
        }

        @Override
        public <EVM extends BaseViewModel>
        boolean unbindExtraViewModel(Class<EVM> viewModelClass) {
            if (mCurrentEvent == null) {
                throw new IllegalStateException(mExtraView.getClass().getName() + ": " +
                        "The parent View is also initialized or destroyed.");
            }

            BaseViewModel extraViewModel = mViewModelMap.get(viewModelClass);
            if (extraViewModel != null) {
                extraViewModel.onDetach();
                mViewModelMap.remove(viewModelClass);

                return true;
            }

            return false;
        }

        @Override
        public boolean unbindAllExtraViewModels() {
            if (mCurrentEvent == null) {
                throw new IllegalStateException(mExtraView.getClass().getName() + ": " +
                        "The parent View is also initialized or destroyed.");
            }

            if (mViewModelMap.size() > 0) {
                for (BaseViewModel extraViewModel : mViewModelMap.values()) {
                    extraViewModel.onDetach();
                }
                mViewModelMap.clear();

                return true;
            }

            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <EVM extends BaseViewModel>
        EVM getExtraViewModel(Class<EVM> viewModelClass) {
            return (EVM) mViewModelMap.get(viewModelClass);
        }

        @Override
        public BaseViewModel[] getAllExtraViewModels() {
            if (mViewModelMap.size() > 0) {
                return mViewModelMap.values().toArray(new BaseViewModel[mViewModelMap.size()]);
            } else {
                return null;
            }
        }


        void clear() {
            mViewModelMap.clear();
            mExtraView = null;
            mViewModelMap = null;
        }
    }


    void bindChildViewsByAnnotation(@NonNull CoreView ancestorView,
                                    @Nullable ChildView parentChildView,
                                    @NonNull ContainerView containerView) {
        assertCoreView(ancestorView);

        BindChildView bindChildView;
        BindChildViews bindChildViews;

        // 获取 BindChildView 和 BindChildViews 注解
        if (parentChildView != null) {
            bindChildView = parentChildView.getClass().getAnnotation(BindChildView.class);
            bindChildViews = parentChildView.getClass().getAnnotation(BindChildViews.class);
        } else {
            bindChildView = ancestorView.getClass().getAnnotation(BindChildView.class);
            bindChildViews = ancestorView.getClass().getAnnotation(BindChildViews.class);
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

    void bindExtraViewModelsByAnnotation(@NonNull ExtraViewModelView extraViewModelView) {
        ExtraViewModel extraViewModel = extraViewModelView.getClass().getAnnotation(ExtraViewModel.class);
        ExtraViewModels extraViewModels = extraViewModelView.getClass().getAnnotation(ExtraViewModels.class);

        if (extraViewModel != null) {
            extraViewModelView.bindExtraViewModel(extraViewModel.value());
        }
        if (extraViewModels != null) {
            for (ExtraViewModel e : extraViewModels.value()) {
                extraViewModelView.bindExtraViewModel(e.value());
            }
        }
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
    private BaseViewModel createMainViewModel(@NonNull CoreView coreView) {
        BaseViewModel viewModel = coreView.onCreateViewModel();
        if (viewModel == null) {
            MainViewModel mainViewModel = coreView.getClass().getAnnotation(MainViewModel.class);
            if (mainViewModel != null) {
                try {
                    Class<? extends BaseViewModel> viewModelClass = mainViewModel.value();
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
                    "You didn't build ViewModel! You can choose to use MainViewModel annotation or newViewModel " +
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
