package com.wutaodsg.mvvm.core;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wutaodsg.mvvm.core.annotation.BindChildView;
import com.wutaodsg.mvvm.core.annotation.BindChildViews;
import com.wutaodsg.mvvm.core.iview.ContainerView;
import com.wutaodsg.mvvm.core.iview.CoreView;
import com.wutaodsg.mvvm.core.iview.ExtraViewModelView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 此类代表一个子 View。子 View 被一般被父 View（即 {@link BaseMVVMActivity}、
 * {@link BaseMVVMFragment} 或其他 ChildView）
 * 包含。也就是说，子 View 一般存在于父 View 中，比如父 Layout XML 文件中的
 * &lt;include&gt; 所引用的子 View；或者 Layout XML 上控件所引用的视图，比如
 * NavigationView 的 app:headerLayout 属性所表示的视图。<br/>
 * 在父 View 的 Layout XML 中，需要为包含子 View 的容器设置一个 id，用来指定
 * 子 View 的位置。
 * </p>
 * <p>
 * 同一种类型 ChildView 可以被绑定在父视图上的不同容器中，也就是说可以进行<em>多次绑定</em>，
 * 需要注意的是，同一个容器不可以多次绑定一种 ChildView。
 * </p>
 * <p>
 * ChildView 实现了 {@link CoreView}。所以它具有生命周期方法：{@link #onCreate(Bundle)}、
 * {@link #onStart()}、{@link #onResume()}、{@link #onPause()}、{@link #onStop()}、
 * {@link #onDestroy()}，这些生命周期函数会在父 View 的对应方法中被调用。
 * </p>
 * <p>
 * ChildView 还实现了 {@link ContainerView}，使得它也可以包含其他 ChildView。
 * 所以可以通过这点实现<em>递归绑定</em>。
 * </p>
 * <p>
 * ChildView 保存了它的父 View 的引用，它的容器的引用，以及一个 Context 的引用。
 * 如果 ChildView 的父 View 是 {@link BaseMVVMActivity}，可以使用
 * {@link #getParentActivity()} 获取这个引用；
 * 如果 ChildView 的父 View 是 {@link BaseMVVMFragment}，可以使用
 * {@link #getParentFragment()} 获取这个引用。<br/>
 * 如果 ChildView 的父 View 是其他 ChildView，可以使用
 * {@link #getParentChildView()} 获取这个引用<br/>
 * 还可以使用 {@link #getParentActivity(Class)} 或 {@link #getParentFragment(Class)}
 * 获取更加精确的类型。<br/>
 * 当 ChildView 的父 View 是 ChildView 时，ChildView 也会保存最原始祖先（也就是不断
 * 向上回溯最终得到的父 View）的引用，这个最原始祖先要么是 {@link BaseMVVMActivity}
 * 要么是 {@link BaseMVVMFragment}。
 * </p>
 * <p>
 * 在父 View 上使用 {@link BindChildView} 或 {@link BindChildViews} 注解
 * 来声明 ChildView。<br/>
 * 也可以使用 {@link ContainerView} 的方法进行<em>动态绑定</em>。
 * </p>
 * <p>
 * ChildView 可以绑定额外的 ViewModel，通过使用 {@link com.wutaodsg.mvvm.core.annotation.ExtraViewModel}
 * 、{@link com.wutaodsg.mvvm.core.annotation.ExtraViewModels} 注解声明
 * 或使用 {@link #bindExtraViewModel(Class)} 方法动态绑定。
 * </p>
 * <p>
 * 需要注意的是，ChildView 必须有一个无参构造器，否则框架将无法正确的构造它。
 * </p>
 */

public abstract class ChildView<VM extends BaseViewModel, DB extends ViewDataBinding>
        implements CoreView<VM, DB>, ContainerView, ExtraViewModelView {

    /**
     * 父 View 为 {@link BaseMVVMActivity} 的子类
     */
    public static final int PARENT_TYPE_ACTIVITY = 0;
    /**
     * 父 View 为 {@link BaseMVVMFragment} 的子类
     */
    public static final int PARENT_TYPE_FRAGMENT = 1;
    /**
     * 父 View 为 ChildView 的子类
     */
    public static final int PARENT_TYPE_CHILD_VIEW = 2;


    private VM mViewModel;
    private DB mDataBinding;

    private BaseMVVMActivity mParentActivity;
    private BaseMVVMFragment mParentFragment;
    private ChildView mParentChildView;
    private Context mContext;
    private ViewGroup mContainer;

    private ViewProxy mViewProxy;
    private ViewProxy.ContainerViewImpl mContainerView;
    private ViewProxy.ExtraViewModelViewImpl mExtraViewModelView;

    @ParentType
    private int mParentType;


    /**
     * ChildView 不支持这个方法，如果有需要，请使用它的父 View 的 getLifecycle() 方法。
     */
    @NonNull
    @Override
    public final Lifecycle getLifecycle() {
        throw new UnsupportedOperationException("ChildView does not support this method!");
    }


    @SuppressWarnings("unchecked")
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        if (mViewModel == null ||
                mDataBinding == null ||
                mContext == null ||
                mContainer == null ||
                (mParentActivity == null && mParentFragment == null)) {
            throw new IllegalStateException(getClass().getName() + ": is wrongly constructed!");
        }

        CoreView coreView;
        if (mParentActivity != null) {
            mViewProxy = mParentActivity.mViewProxy;
            coreView = mParentActivity;
        } else {
            mViewProxy = mParentFragment.mViewProxy;
            coreView = mParentFragment;
        }

        mContainerView = mViewProxy.new ContainerViewImpl(coreView, this);
        mViewProxy.bindChildViewsByAnnotation(coreView, this, mContainerView);

        mExtraViewModelView = mViewProxy.new ExtraViewModelViewImpl(this);
        mViewProxy.bindExtraViewModelsByAnnotation(this);
    }

    @CallSuper
    public void onStart() {
        mContainerView.childViewsOnStart();
    }

    @CallSuper
    public void onResume() {
        mContainerView.childViewsOnResume();
    }

    @CallSuper
    public void onPause() {
        mContainerView.childViewsOnPause();
    }

    @CallSuper
    public void onStop() {
        mContainerView.childViewsOnStop();
    }

    @CallSuper
    public void onDestroy() {
        beforeDetach();

        mContainerView.unbindAllChildViews();
        mContainerView.clear();
        mContainerView = null;

        mViewModel.onDetach();
        mViewModel = null;
        mDataBinding = null;
        mParentActivity = null;
        mParentFragment = null;
        mViewProxy = null;
        mContext = null;
        mContainer = null;

        mExtraViewModelView.unbindAllExtraViewModels();
        mExtraViewModelView.clear();
        mExtraViewModelView = null;
    }

    @NonNull
    @Override
    public final DB getDataBinding() {
        return mDataBinding;
    }

    @NonNull
    @Override
    public final VM getViewModel() {
        return mViewModel;
    }

    @Override
    public final Lifecycle.State getCurrentState() {
        return mViewProxy.getCurrentState();
    }

    @Override
    public final Lifecycle.Event getCurrentEvent() {
        return mViewProxy.getCurrentEvent();
    }


    @Nullable
    @Override
    public VM onCreateViewModel() {
        return null;
    }

    @Override
    @CallSuper
    public void beforeBindView() {

    }

    @Override
    @CallSuper
    public void beforeDetach() {

    }

    @Override
    public final <T extends View> T findViewById(int id) {
        return mDataBinding.getRoot().findViewById(id);
    }

    @Override
    public final LayoutInflater getLayoutInflater() {
        return mParentActivity != null ? mParentActivity.getLayoutInflater() :
                mParentFragment.getLayoutInflater();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <SVM extends BaseViewModel> SVM newViewModel(Class<SVM> viewModelClass) {
        return (SVM) (mParentActivity != null ? mParentActivity.newViewModel(viewModelClass) :
                mParentFragment.newViewModel(viewModelClass));
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

    @SuppressWarnings("unchecked")
    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV bindChildView(Class<CV> childViewClass,
                     @IdRes int containerId,
                     boolean attachToParent,
                     boolean removeViews) {
        return (CV) mContainerView.bindChildView(childViewClass, containerId, attachToParent, removeViews);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV bindChildView(Class<CV> childViewClass, @IdRes int containerId,
                     boolean attachToParent) {
        return (CV) mContainerView.bindChildView(childViewClass, containerId, attachToParent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV bindChildView(Class<CV> childViewClass, @IdRes int containerId) {
        return (CV) mContainerView.bindChildView(childViewClass, containerId);
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

    @SuppressWarnings("unchecked")
    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV getChildView(Class<CV> childViewClass, @IdRes int containerId) {
        return (CV) mContainerView.getChildView(childViewClass, containerId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV[] getChildViews(Class<CV> childViewClass) {
        return (CV[]) mContainerView.getChildViews(childViewClass);
    }

    @Override
    public final ChildView[] getChildViews() {
        return mContainerView.getChildViews();
    }


    @Override
    public <EVM extends BaseViewModel> boolean containsExtraViewModel(Class<EVM> viewModelClass) {
        return mExtraViewModelView.containsExtraViewModel(viewModelClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <EVM extends BaseViewModel> EVM bindExtraViewModel(Class<EVM> viewModelClass) {
        return (EVM) mExtraViewModelView.bindExtraViewModel(viewModelClass);
    }

    @Override
    public <EVM extends BaseViewModel> boolean unbindExtraViewModel(Class<EVM> viewModelClass) {
        return mExtraViewModelView.unbindExtraViewModel(viewModelClass);
    }

    @Override
    public boolean unbindAllExtraViewModels() {
        return mExtraViewModelView.unbindAllExtraViewModels();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <EVM extends BaseViewModel> EVM getExtraViewModel(Class<EVM> viewModelClass) {
        return (EVM) mExtraViewModelView.getExtraViewModel(viewModelClass);
    }

    @Override
    public BaseViewModel[] getAllExtraViewModels() {
        return mExtraViewModelView.getAllExtraViewModels();
    }


    /**
     * 判断 ChildView 的父 View 是否为 {@link BaseMVVMActivity} 的子类。
     *
     * @return ChildView 的父 View 是 {@link BaseMVVMActivity} 的子类返回 true，否则返回 false
     */
    public final boolean parentIsActivity() {
        return mParentActivity != null;
    }

    /**
     * 判断 ChildView 的父 View 是否为 {@link BaseMVVMFragment} 的子类。
     *
     * @return ChildView 的父 View 是 {@link BaseMVVMFragment} 的子类返回 true，否则返回 false
     */
    public final boolean parentIsFragment() {
        return mParentFragment != null;
    }

    /**
     * 判断 ChildView 的父 View 是否为 ChildView 的子类。
     *
     * @return ChildView 的父 View 是 ChildView 的子类返回 true，否则返回 false
     */
    public final boolean parentIsChildView() {
        return mParentChildView != null;
    }

    /**
     * 返回 ChildView 父 View 的类型，有三种值：{@link #PARENT_TYPE_ACTIVITY} 表示
     * 父 View 为 {@link BaseMVVMActivity} 的子类；{@link #PARENT_TYPE_FRAGMENT} 表示父
     * View 为 {@link BaseMVVMFragment} 的子类；{@link #PARENT_TYPE_CHILD_VIEW} 表示
     * 父 View 为 ChildView 的子类。
     *
     * @return 返回 ChildView 父 View 的类型
     */
    @ParentType
    public final int getParentType() {
        return mParentType;
    }

    /**
     * 如果此 ChildView 的父 View 是 {@link #PARENT_TYPE_ACTIVITY} 类型，返回
     * 它的父 View，否则返回 null。
     *
     * @return 类型为 {@link BaseMVVMActivity} 的父 View
     */
    @Nullable
    public final BaseMVVMActivity getParentActivity() {
        return mParentActivity;
    }

    /**
     * 如果此 ChildView 的父 View 是 {@link #PARENT_TYPE_FRAGMENT} 类型，返回
     * 它的父 View，否则返回 null。
     *
     * @return 类型为 {@link BaseMVVMFragment} 的父 View
     */
    @Nullable
    public final BaseMVVMFragment getParentFragment() {
        return mParentFragment;
    }

    /**
     * 如果此 ChildView 的父 View 是 {@link #PARENT_TYPE_CHILD_VIEW} 类型，返回
     * 它的父 View，否则返回 null。
     *
     * @return 类型为 ChildView 的父 View
     */
    @Nullable
    public final ChildView getParentChildView() {
        return mParentChildView;
    }

    /**
     * 如果此 ChildView 父 Activity 的类型与参数 parentActivityClass 表示的类型
     * 相同，返回这个 ChildView 的父 Activity；如果不相同或者此 ChildView 的父
     * Activity 为 null，返回 null。
     *
     * @param parentActivityClass 表示此 ChildView 父 Activity 的类型
     * @param <PVM>               父 Activity 的 ViewModel 类型
     * @param <PDB>               父 Activity 的 DataBinding 类型
     * @param <PA>                父 Activity 的类型
     * @return 父 Activity 引用或 null
     */
    @SuppressWarnings("unchecked")
    public final <PVM extends BaseViewModel, PDB extends ViewDataBinding, PA extends BaseMVVMActivity<PVM, PDB>>
    PA getParentActivity(@NonNull Class<PA> parentActivityClass) {
        if (mParentActivity != null) {
            if (mParentActivity.getClass().equals(parentActivityClass)) {
                return (PA) mParentActivity;
            }
        }

        return null;
    }

    /**
     * 如果此 ChildView 父 Fragment 的类型与参数 parentFragmentClass 表示的类型
     * 相同，返回这个 ChildView 的父 Fragment；如果不相同或者此 ChildView 的父
     * Fragment 为 null，返回 null。
     *
     * @param parentFragmentClass 表示此 ChildView 父 Fragment 的类型
     * @param <PVM>               父 Fragment 的 ViewModel 类型
     * @param <PDB>               父 Fragment 的 DataBinding 类型
     * @param <PF>                父 Fragment 的类型
     * @return 父 Fragment 引用或 null
     */
    @SuppressWarnings("unchecked")
    public final <PVM extends BaseViewModel, PDB extends ViewDataBinding, PF extends BaseMVVMFragment<PVM, PDB>>
    PF getParentFragment(@NonNull Class<PF> parentFragmentClass) {
        if (mParentFragment != null) {
            if (mParentFragment.getClass().equals(parentFragmentClass)) {
                return (PF) mParentFragment;
            }
        }

        return null;
    }

    /**
     * 如果此 ChildView 父 ChildView 的类型与参数 parentChildViewClass 表示的类型
     * 相同，返回这个 ChildView 的父 ChildView；如果不相同或者此 ChildView 的父
     * ChildView 为 null，返回 null。
     *
     * @param parentChildViewClass 表示此 ChildView 父 ChildView 的类型
     * @param <PVM>                父 ChildView 的 ViewModel 类型
     * @param <PDB>                父 ChildView 的 DataBinding 类型
     * @param <PC>                 父 ChildView 的类型
     * @return 父 ChildView 引用或 null
     */
    @SuppressWarnings("unchecked")
    public final <PVM extends BaseViewModel, PDB extends ViewDataBinding, PC extends ChildView<PVM, PDB>>
    PC getParentChildView(@NonNull Class<PC> parentChildViewClass) {
        if (mParentChildView != null) {
            if (mParentChildView.getClass().equals(parentChildViewClass)) {
                return (PC) mParentChildView;
            }
        }

        return null;
    }

    /**
     * 返回与此 ChildView 绑定的 Context 对象。
     *
     * @return {@link Context} 对象
     */
    @NonNull
    public final Context getContext() {
        return mContext;
    }

    /**
     * 返回 ChildView 的容器对象。
     *
     * @return 容器 {@link ViewGroup} 对象
     */
    @NonNull
    public final ViewGroup getContainer() {
        return mContainer;
    }


    @IntDef({PARENT_TYPE_ACTIVITY, PARENT_TYPE_FRAGMENT, PARENT_TYPE_CHILD_VIEW})
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.FIELD, ElementType.METHOD})
    public @interface ParentType {
    }


    void setViewModel(@NonNull VM viewModel) {
        assertReset(mViewModel, "View Model");
        mViewModel = viewModel;
    }

    void setDataBinding(@NonNull DB dataBinding) {
        assertReset(mDataBinding, "Data Binding");
        mDataBinding = dataBinding;
    }

    void setParentActivity(@NonNull BaseMVVMActivity parentActivity) {
        assertReset(mParentActivity, "Parent Activity");
        mParentActivity = parentActivity;
        mParentType = PARENT_TYPE_ACTIVITY;
    }

    void setParentFragment(@NonNull BaseMVVMFragment parentFragment) {
        assertReset(mParentFragment, "Parent Fragment");
        mParentFragment = parentFragment;
        mParentType = PARENT_TYPE_FRAGMENT;
    }

    void setParentChildView(@NonNull ChildView parentChildView) {
        assertReset(mParentChildView, "Parent ChildView");
        mParentChildView = parentChildView;
        mParentType = PARENT_TYPE_CHILD_VIEW;
    }

    void setContext(@NonNull Context context) {
        assertReset(mContext, "Context");
        mContext = context;
    }

    void setContainer(@NonNull ViewGroup container) {
        assertReset(mContainer, "Container");
        mContainer = container;
    }


    private void assertReset(Object object, String name) {
        if (object != null) {
            throw new IllegalStateException(name + " already exists, you can't reset it.");
        }
    }
}
