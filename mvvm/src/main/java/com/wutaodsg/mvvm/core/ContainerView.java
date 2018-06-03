package com.wutaodsg.mvvm.core;

import android.databinding.ViewDataBinding;
import android.support.annotation.IdRes;

/**
 * <p>
 * ContainerView 代表了能容纳 {@link ChildView} 的 View，它具有一组动态操作 {@link ChildView} 的方法。
 * </p>
 */

public interface ContainerView {

    /**
     * 是否含有类型为 childViewClass 且父容器 id 为 containerId 的 ChildView。
     *
     * @param childViewClass ChildView 类型
     * @param containerId    ChildView 容器 id
     * @param <CVM>          ChildView ViewModel 类型
     * @param <CDB>          ChildView DataBinding 类型
     * @param <CV>           ChildView 类型
     * @return 存在返回 true，否则返回 false
     */
    <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    boolean containsChildView(Class<CV> childViewClass, @IdRes int containerId);

    /**
     * 是否含有类型为 childViewClass 的 ChildView。
     *
     * @param childViewClass ChildView 类型
     * @param <CVM>          ChildView ViewModel 类型
     * @param <CDB>          ChildView DataBinding 类型
     * @param <CV>           ChildView 类型
     * @return 存在返回 true，否则返回 false
     */
    <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    boolean containsChildView(Class<CV> childViewClass);

    /**
     * 绑定 ChildView 到这个 ContainerView 上。<br/>
     * 需要注意的是，这个方法只有在父 View 的 onCreate() 到 onResume() 方法之间的
     * 生命期中调用才有效，否则会抛出异常。<br/>
     * 被绑定的 ChildView 会执行相应的生命周期方法保持与父 View 相同。
     *
     * @param childViewClass ChildView 类型
     * @param containerId    ChildView 容器 id
     * @param attachToParent 表示子 View 的布局参数是否应该附加到父 View 的布局参数中。如果为 false，
     *                       则父 View 仅用于为 XML 中的子 View 创建 LayoutParams 的正确子类。
     * @param removeViews    是否在绑定之前清除 container 中的所有其他布局
     * @param <CVM>          ChildView ViewModel 类型
     * @param <CDB>          ChildView DataBinding 类型
     * @param <CV>           ChildView 类型
     * @return 绑定成功返回对应的 ChildView 对象，如果已经存在返回 null
     */
    <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV bindChildView(Class<CV> childViewClass,
                          @IdRes int containerId,
                          boolean attachToParent,
                          boolean removeViews);

    /**
     * 参见 {@link #bindChildView(Class, int, boolean, boolean)}，
     * 相当于调用 bindChildView(childViewClass, containerId, attachToParent, false)。
     *
     * @param childViewClass ChildView 类型
     * @param containerId    ChildView 容器 id
     * @param attachToParent 表示子 View 的布局参数是否应该附加到父 View 的布局参数中。如果为 false，
     *                       则父 View 仅用于为 XML 中的子 View 创建 LayoutParams 的正确子类。
     * @param <CVM>          ChildView ViewModel 类型
     * @param <CDB>          ChildView DataBinding 类型
     * @param <CV>           ChildView 类型
     * @return 绑定成功返回对应的 ChildView 对象，如果已经存在返回 null
     */
    <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV bindChildView(Class<CV> childViewClass, @IdRes int containerId, boolean attachToParent);

    /**
     * 参见 {@link #bindChildView(Class, int, boolean, boolean)}，
     * 相当于调用 bindChildView(childViewClass, containerId, true, false)。
     *
     * @param childViewClass ChildView 类型
     * @param containerId    ChildView 容器 id
     * @param <CVM>          ChildView ViewModel 类型
     * @param <CDB>          ChildView DataBinding 类型
     * @param <CV>           ChildView 类型
     * @return 绑定成功返回对应的 ChildView 对象，如果已经存在返回 null
     */
    <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV bindChildView(Class<CV> childViewClass, @IdRes int containerId);

    /**
     * 解绑类型为 childViewClass，容器 id 为 containerId 的 ChildView，没有绑定过仅仅返回 false。
     * 这个方法会根据父 View 的生命周期阶段回调 ChildView 相应的生命周期方法。
     *
     * @param childViewClass ChildView 类型
     * @param containerId    ChildView 容器 id
     * @param <CVM>          ChildView ViewModel 类型
     * @param <CDB>          ChildView DataBinding 类型
     * @param <CV>           ChildView 类型
     * @return 解除绑定成功返回 true，没有绑定过返回 false
     */
    <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    boolean unbindChildView(Class<CV> childViewClass, @IdRes int containerId);

    /**
     * 解绑类型为 childViewClass 的所有 ChildView，没有绑定过仅仅返回 false。
     * 需要注意的是，这个方法会回调 ChildView 的 {@link ChildView#onDestroy()} 方法。
     *
     * @param childViewClass ChildView 类型
     * @param <CVM>          ChildView ViewModel 类型
     * @param <CDB>          ChildView DataBinding 类型
     * @param <CV>           ChildView 类型
     * @return 解除绑定成功返回 true，没有绑定过返回 false
     */
    <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    boolean unbindChildView(Class<CV> childViewClass);

    /**
     * 解绑所有的 ChildView，没有绑定过仅仅返回 false。
     *
     * @return 解除绑定成功返回 true，没有绑定过返回 false
     */
    boolean unbindAllChildViews();

    /**
     * 获取类型为 childViewClass，容器 id 为 containerId 的 ChildView，不存在
     * 返回 null。
     *
     * @param childViewClass ChildView 类型
     * @param containerId    ChildView 容器 id
     * @param <CVM>          ChildView ViewModel 类型
     * @param <CDB>          ChildView DataBinding 类型
     * @param <CV>           ChildView 类型
     * @return 指定类型的 ChildView 对象，不存在返回 null
     */
    <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV getChildView(Class<CV> childViewClass, @IdRes int containerId);

    /**
     * 获取类型为 childViewClass 的所有 ChildView，不存在返回 null。
     *
     * @param childViewClass ChildView 类型
     * @param <CVM>          ChildView ViewModel 类型
     * @param <CDB>          ChildView DataBinding 类型
     * @param <CV>           ChildView 类型
     * @return 指定类型的 ChildView 对象数组，不存在返回 null
     */
    <CVM extends BaseViewModel, CDB extends ViewDataBinding, CV extends ChildView<CVM, CDB>>
    CV[] getChildViews(Class<CV> childViewClass);

    /**
     * 获取所有绑定在 ContainerView 上的 ChildView 数组，不存在返回 null。
     *
     * @return ChildView 数组，不存在返回 null
     */
    ChildView[] getChildViews();
}
