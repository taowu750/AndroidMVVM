package com.wutaodsg.mvvm.core;

import android.databinding.ViewDataBinding;

import java.util.List;

/**
 * <p>
 *     ParentView 包含了一组用来操作子 View 的方法。
 * </p>
 */

public interface ParentView {

    /**
     * 判断父 View 是否包含指定类型的子 View。
     *
     * @param childViewClass
     * @param <VM>
     * @param <DB>
     * @param <CV>
     * @return
     */
    <VM extends BaseViewModel, DB extends ViewDataBinding, CV extends ChildView<VM, DB>>
    boolean containsChildView(Class<CV> childViewClass);

    /**
     * 根据子 View 的 Class 对象获取子 View 对象。没有包含这个子 View 返回 null。
     *
     * @param childViewClass
     * @param <VM>
     * @param <DB>
     * @param <CV>
     * @return
     */
    <VM extends BaseViewModel, DB extends ViewDataBinding, CV extends ChildView<VM, DB>>
    CV getChildView(Class<CV> childViewClass);

    /**
     * 获取在这个父 View 中所有子 View 对象组成的 List。
     *
     * @return
     */
    List<ChildView> getChildViews();
}
