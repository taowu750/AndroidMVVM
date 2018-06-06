package com.wutaodsg.mvvm.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     当父 View 包含多个子 View 时，使用这个注解可以为父 View 同时指定多个子 View。
 * </p>
 * <p>
 *     详细信息参见 {@link BindChildView}。
 * </p>
 */

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindChildViews {

    /**
     * 需要绑定的多个子 View。
     */
    BindChildView[] value();
}
