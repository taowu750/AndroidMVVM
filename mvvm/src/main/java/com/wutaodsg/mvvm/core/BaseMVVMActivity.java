package com.wutaodsg.mvvm.core;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.wutaodsg.mvvm.core.annotation.BindChildView;
import com.wutaodsg.mvvm.core.annotation.BindChildViews;
import com.wutaodsg.mvvm.core.annotation.BindVariable;
import com.wutaodsg.mvvm.core.iview.ContainerView;
import com.wutaodsg.mvvm.core.iview.CoreView;
import com.wutaodsg.mvvm.core.iview.ExtraViewModelView;

/**
 * <p>
 * 作为 MVVM 模式 V 层中的基类 Activity。
 * </p>
 * <p>
 * 它不仅仅作为 View 对象，也是一个弱化的控制器（Controller），所以我们需要把界面上
 * 控件的命令（Command）放在它里面。
 * </p>
 * <p>
 * 它会在 {@link #onCreate(Bundle)} 阶段绑定 ViewModel 和 DataBinding，将在它内部声明的
 * {@link com.wutaodsg.mvvm.command.ReplyCommand} 或 {@link com.wutaodsg.mvvm.command.ResponseCommand}
 * 绑定到界面中（前提是这些 Command 上正确的使用 {@link BindVariable} 指定了 DataBinding Variable）。<br/>
 * 此外，它还会检查是否有 {@link UIAwareComponent} 域，如果有，就将它们绑定到自己的生命周期中。
 * </p>
 * <p>
 * 可以使用 {@link BindChildView} 或 {@link BindChildViews} 注解声明子 View，
 * 详细信息参见 {@link ChildView}。
 * </p>
 * <p>
 * 需要注意的是，由于 BaseMVVMActivity 在自己的 {@link #onCreate(Bundle)} 方法结束后，
 * 绑定过程才会结束，所以在这个回调方法结束之前，不可以使用 {@link #getViewModel()} 或
 * {@link #getDataBinding()} 等方法，否则会抛出异常。
 * </p>
 * <p>
 * BaseMVVMActivity 可以绑定额外的 ViewModel，通过使用
 * {@link com.wutaodsg.mvvm.core.annotation.ExtraViewModel}、
 * {@link com.wutaodsg.mvvm.core.annotation.ExtraViewModels} 注解声明
 * 或使用 {@link #bindExtraViewModel(Class)} 方法动态绑定。
 * </p>
 */

public abstract class BaseMVVMActivity<VM extends BaseViewModel, DB extends ViewDataBinding>
        extends AppCompatActivity implements CoreView<VM, DB>, ContainerView, ExtraViewModelView {

    ViewProxy<VM, DB> mViewProxy;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeBindView();
        mViewProxy = new ViewProxy<>(this);
        mViewProxy.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewProxy.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewProxy.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mViewProxy.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewProxy.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beforeDetach();
        mViewProxy.onDestroy();
        mViewProxy = null;
    }


    /**
     * 获取与这个 Activity 绑定的 ViewModel 对象。
     * <p>
     * 如果 {@link BaseMVVMActivity} 还未创建成功，也就是它的 {@link #onCreate(Bundle)} 方法还未
     * 完全执行成，则会抛出 {@link IllegalStateException} 异常。<br>
     * 除此之外，在 onDestroy 方法中调用 getViewModel() 也会抛出异常，如果您有在此 Activity
     * 销毁时调用 getViewModel() 的必要，可以考虑在 beforeDetach() 回调方法中使用。
     *
     * @return 与这个 Activity 绑定的 ViewModel 对象
     * @throws IllegalStateException 如果 {@link BaseMVVMActivity} 还未创建成功，抛出此异常
     */
    @Override
    @NonNull
    public final VM getViewModel() {
        assertViewProxy();
        return mViewProxy.getViewModel();
    }

    /**
     * 获取与这个 Activity 绑定的 DataBinding 对象。
     * <p>
     * 如果 {@link BaseMVVMActivity} 还未创建成功，也就是它的 {@link #onCreate(Bundle)} 方法还未
     * 完全执行成，则会抛出 {@link IllegalStateException} 异常。<br>
     * 除此之外，在 onDestroy 方法中调用 getDataBinding() 也会抛出异常，如果您有在此 Activity
     * 销毁时调用 getDataBinding() 的必要，可以考虑在 beforeDetach() 回调方法中使用。
     *
     * @return 与这个 Activity 绑定的 DataBinding 对象
     * @throws IllegalStateException 如果 {@link BaseMVVMActivity} 还未创建成功，抛出此异常
     */
    @Override
    @NonNull
    public final DB getDataBinding() {
        assertViewProxy();
        return mViewProxy.getDataBinding();
    }

    @Override
    public final Lifecycle.State getCurrentState() {
        assertViewProxy();
        return mViewProxy.getCurrentState();
    }

    @Override
    public final Lifecycle.Event getCurrentEvent() {
        assertViewProxy();
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
    public Context getContext() {
        return this;
    }

    @Override
    public final <SVM extends BaseViewModel> SVM newViewModel(Class<SVM> viewModelClass) {
        return ViewModelProviders.of(this).get(viewModelClass);
    }


    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    boolean containsChildView(Class<CV> childViewClass, @IdRes int containerId) {
        assertViewProxy();
        return mViewProxy.containsChildView(childViewClass, containerId);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    boolean containsChildView(Class<CV> childViewClass) {
        assertViewProxy();
        return mViewProxy.containsChildView(childViewClass);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV bindChildView(Class<CV> childViewClass,
                     @IdRes int containerId,
                     boolean attachToParent,
                     boolean removeViews) {
        assertViewProxy();
        return mViewProxy.bindChildView(childViewClass, containerId, attachToParent, removeViews);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV bindChildView(Class<CV> childViewClass, @IdRes int containerId,
                     boolean attachToParent) {
        assertViewProxy();
        return mViewProxy.bindChildView(childViewClass, containerId, attachToParent);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV bindChildView(Class<CV> childViewClass, @IdRes int containerId) {
        assertViewProxy();
        return mViewProxy.bindChildView(childViewClass, containerId);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    boolean unbindChildView(Class<CV> childViewClass, @IdRes int containerId) {
        assertViewProxy();
        return mViewProxy.unbindChildView(childViewClass, containerId);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    boolean unbindChildView(Class<CV> childViewClass) {
        assertViewProxy();
        return mViewProxy.unbindChildView(childViewClass);
    }

    @Override
    public boolean unbindAllChildViews() {
        assertViewProxy();
        return mViewProxy.unbindAllChildViews();
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV getChildView(Class<CV> childViewClass, @IdRes int containerId) {
        assertViewProxy();
        return mViewProxy.getChildView(childViewClass, containerId);
    }

    @Override
    public final <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV[] getChildViews(Class<CV> childViewClass) {
        assertViewProxy();
        return mViewProxy.getChildViews(childViewClass);
    }

    @Override
    public final ChildView[] getChildViews() {
        assertViewProxy();
        return mViewProxy.getChildViews();
    }


    @Override
    public <EVM extends BaseViewModel>
    boolean containsExtraViewModel(Class<EVM> viewModelClass) {
        assertViewProxy();
        return mViewProxy.containsExtraViewModel(viewModelClass);
    }

    @Override
    public <EVM extends BaseViewModel>
    EVM bindExtraViewModel(Class<EVM> viewModelClass) {
        assertViewProxy();
        return mViewProxy.bindExtraViewModel(viewModelClass);
    }

    @Override
    public <EVM extends BaseViewModel>
    boolean unbindExtraViewModel(Class<EVM> viewModelClass) {
        assertViewProxy();
        return mViewProxy.unbindExtraViewModel(viewModelClass);
    }

    @Override
    public boolean unbindAllExtraViewModels() {
        assertViewProxy();
        return mViewProxy.unbindAllChildViews();
    }

    @Override
    public <EVM extends BaseViewModel>
    EVM getExtraViewModel(Class<EVM> viewModelClass) {
        assertViewProxy();
        return mViewProxy.getExtraViewModel(viewModelClass);
    }

    @Override
    public BaseViewModel[] getAllExtraViewModels() {
        assertViewProxy();
        return mViewProxy.getAllExtraViewModels();
    }


    private void assertViewProxy() {
        if (mViewProxy == null) {
            throw new IllegalStateException(getClass().getName() + ": " +
                    "The corresponding ViewModel and DataBinding are not bound or has been relieved of binding.\n" +
                    "Because the \"" + BaseMVVMActivity.class.getName() + ".onCreate(Bundle)\" has not been " +
                    "fully executed.\n" +
                    "It is also possible that you invoked the getViewModel () or getDataBinding () method in " +
                    "onDestroy ().");
        }
    }
}
