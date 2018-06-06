package com.wutaodsg.mvvm.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当一个 View 需要绑定多个额外的 ViewModel 时，使用这个注解同时为
 * View 绑定多个 ViewModel。
 * <p>
 *     详细情况参见 {@link ExtraViewModel}。
 * </p>
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtraViewModels {

    /**
     * 声明需要绑定的多个额外的 ViewModel。
     */
    ExtraViewModel[] value();
}
