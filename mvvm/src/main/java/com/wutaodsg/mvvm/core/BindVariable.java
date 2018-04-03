package com.wutaodsg.mvvm.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用在 ViewModel 的数据绑定域和 View 的命令绑定域上，通过提供所绑定项的
 * BR.id，将这些数据绑定域和命令绑定域绑定在 BR.id 所对应的 DataBinding Variable 上。
 * <p>
 * 例如：<br/>
 * {@code @BindVariable(BR.user)
 *        private final ObservableField<User> user = new ObservableField<>()}
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindVariable {

    int value();
}
