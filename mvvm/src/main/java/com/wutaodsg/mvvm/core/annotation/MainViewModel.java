package com.wutaodsg.mvvm.core.annotation;

import com.wutaodsg.mvvm.core.BaseViewModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明一个 View 主 ViewModel 的类型。
 * <p>
 * 这个注解用在 V 层类上面,用来为 View 提供创建指定类型的 ViewModel 对象所需要的信息。
 * <p>
 * 例子：<br/>
 * {@code @MainViewModel(MainActivityViewModel.class)
 *        public class MainActivity extends BaseMVVMActivity<MainActivityViewModel, ActivityMainBinding>}
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MainViewModel {

    Class<? extends BaseViewModel> value();
}
