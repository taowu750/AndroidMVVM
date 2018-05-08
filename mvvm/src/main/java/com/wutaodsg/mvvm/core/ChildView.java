package com.wutaodsg.mvvm.core;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 此类代表一个子 View。子 View 被一般被父 View（即 {@link BaseMVVMActivity} 或 {@link BaseMVVMFragment}）
 * 包含。也就是说，子 View 一般存在于父 View 中，比如父 Layout XML 文件中的
 * &lt;include&gt; 所引用的子 View；或者 Layout XML 上控件所引用的视图，比如
 * NavigationView 的 app:headerLayout 属性所表示的视图。<br/>
 * 在父 View 的 Layout XML 中，需要为包含子 View 的容器设置一个 id，用来指定
 * 子 View 的位置。
 * </p>
 * <p>
 * ChildView 实现了 {@link CoreView}。此外，它也具有生命周期函数：{@link #onCreate(Bundle)}、
 * {@link #onStart()}、{@link #onResume()}、{@link #onPause()}、{@link #onStop()}、
 * {@link #onDestroy()}，这些生命周期函数会在父 View 的对于方法中被调用。
 * </p>
 * <p>
 * ChildView 保存了它的父 View 的引用，它的容器的引用，以及一个 Context。
 * 如果 ChildView 的父 View 是 {@link BaseMVVMActivity}，可以使用
 * {@link #getParentActivity()} 获取这个引用；如果 ChildView 的父 View
 * 是 {@link BaseMVVMFragment}，可以使用 {@link #getParentFragment()}
 * 获取这个引用。<br/>
 * 还可以使用 {@link #getParentActivity(Class)} 或 {@link #getParentFragment(Class)}
 * 获取更加精确的类型。
 * </p>
 * <p>
 * 在父 View 上使用 {@link BindChildView} 或 {@link BindChildViews} 注解
 * 来声明 ChildView。
 * </p>
 * <p>
 * 需要注意的是，ChildView 必须有一个无参构造器，否则框架将无法正确的构造它。
 * </p>
 */

public abstract class ChildView<VM extends BaseViewModel, DB extends ViewDataBinding>
        implements CoreView<VM, DB> {

    /**
     * 父 View 为 {@link BaseMVVMActivity} 的子类
     */
    public static final int PARENT_TYPE_ACTIVITY = 0;
    /**
     * 父 View 为 {@link BaseMVVMFragment} 的子类
     */
    public static final int PARENT_TYPE_FRAGMENT = 1;


    private VM mViewModel;
    private DB mDataBinding;

    private BaseMVVMActivity mParentActivity;
    private BaseMVVMFragment mParentFragment;
    private Context mContext;
    private ViewGroup mContainer;

    @ParentType
    private int mParentType;


    @NonNull
    @Override
    public DB getDataBinding() {
        return mDataBinding;
    }

    @NonNull
    @Override
    public VM getViewModel() {
        return mViewModel;
    }

    @Nullable
    @Override
    public VM newViewModel() {
        return null;
    }

    @Override
    @CallSuper
    public void beforeBindView() {

    }

    /**
     * ChildView 不支持这个方法，如果有需要，请使用它的父 View 的 getLifecycle() 方法。
     */
    @NonNull
    @Override
    public final Lifecycle getLifecycle() {
        throw new UnsupportedOperationException("ChildView does not support this method!");
    }

    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        if (mViewModel == null ||
                mDataBinding == null ||
                mContext == null ||
                mContainer == null ||
                (mParentActivity == null && mParentFragment == null)) {
            throw new IllegalStateException("\"" + getClass().getName() + "\" is wrongly constructed!");
        }
    }

    @CallSuper
    protected void onStart() {

    }

    @CallSuper
    protected void onResume() {

    }

    @CallSuper
    protected void onPause() {

    }

    @CallSuper
    protected void onStop() {

    }

    @Override
    @CallSuper
    public void beforeDetach() {

    }

    @CallSuper
    protected void onDestroy() {
        beforeDetach();
        mViewModel.onDetach();
        mViewModel = null;
        mDataBinding = null;
        mParentActivity = null;
        mParentFragment = null;
        mContext = null;
        mContainer = null;
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
     * 返回 ChildView 父 View 的类型，有两种值：{@link #PARENT_TYPE_ACTIVITY} 表示
     * 父 View 为 {@link BaseMVVMActivity} 的子类；{@link #PARENT_TYPE_FRAGMENT} 表示父
     * View 为 {@link BaseMVVMFragment} 的子类。
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


    @IntDef({PARENT_TYPE_ACTIVITY, PARENT_TYPE_FRAGMENT})
    @Retention(RetentionPolicy.SOURCE)
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
        if (mParentFragment != null) {
            throw new IllegalStateException("Parent Fragment already exists, you can't set parentActivity");
        }
        assertReset(mParentActivity, "Parent Activity");
        mParentActivity = parentActivity;
        mParentType = PARENT_TYPE_ACTIVITY;
    }

    void setParentFragment(@NonNull BaseMVVMFragment parentFragment) {
        if (mParentActivity != null) {
            throw new IllegalStateException("Parent Activity already exists, you can't set parentFragment");
        }
        assertReset(mParentFragment, "Parent Fragment");
        mParentFragment = parentFragment;
        mParentType = PARENT_TYPE_FRAGMENT;
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
