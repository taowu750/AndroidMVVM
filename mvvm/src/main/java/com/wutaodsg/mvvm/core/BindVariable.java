package com.wutaodsg.mvvm.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用在 ViewModel 的数据绑定和 ViewController 的命令绑定上，通过提供所绑定项的
 * BR.id，绑定在 BR.id 所对应的项上。
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindVariable {

    int value();
}
