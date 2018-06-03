package com.wutaodsg.mvvm.core;

import android.support.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 为父 View（即 {@link BaseMVVMActivity} 或 {@link BaseMVVMFragment}）指定
 * 一个子 View。这些子 View 被父 View 所包含。
 * 有关子 View 的详细概念参见 {@link ChildView}。
 * </p>
 * <p>
 * 例子：<br/>
 * {@code @BindChildView(type = MyChildView.class, containerId=R.id.my_child_container)
 * public class MainActivity extends BaseMVVMActivity<MainActivityVM, ActivityMainBinding>}
 * </p>
 */

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindChildView {

    /**
     * 表示 ChildView 的类型，参数是 ChildView 子类的 Class 对象
     */
    Class<? extends ChildView> type();

    /**
     * 表示在父 View 中，用来放置包含子 View 的容器的 id。
     */
    @IdRes
    int container();

    /**
     * <p>
     * 表示子 View 的布局参数是否应该附加到父 View 的布局参数中。如果为 false，
     * 则父 View 仅用于为 XML 中的子 View 创建 LayoutParams 的正确子类。
     * </p>
     * <p>
     * 默认值为 true。
     * </p>
     */
    boolean attachToParent() default true;

    /**
     * <p>
     * 是否在绑定之前清除 container 中的其他 View，默认为 false。
     * </p>
     */
    boolean removeViews() default false;
}
