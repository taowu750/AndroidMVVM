package com.wutaodsg.mvvm.core.iview;

import com.wutaodsg.mvvm.core.BaseViewModel;
import com.wutaodsg.mvvm.core.annotation.ExtraViewModel;
import com.wutaodsg.mvvm.core.annotation.ExtraViewModels;

/**
 * <p>
 * 在 MVVM 模式中，每个 View 必须与一个 ViewModel 相关联。而这个接口表示的
 * View 不仅会有一个主 ViewModel，还可能会有其他额外的 ViewModel。
 * 该接口提供了动态绑定解绑额外 ViewModel 的方法，同时也可以由
 * {@link ExtraViewModel} 和 {@link ExtraViewModels} 注解在编译时
 * 静态绑定 ViewModel。
 * </p>
 * <p>
 * 需要注意的是，它不能对主 ViewModel 进行操作，此接口提供的一切
 * 方法对主 ViewModel 无效。<br/>
 * 此外，额外的 ViewModel 必须具有默认的构造器。
 * </p>
 */

public interface ExtraViewModelView {

    /**
     * 判断是否已经类型为 viewModelClass 的 ViewModel。
     *
     * @param viewModelClass ViewModel 的类型
     * @param <EVM>          ViewModel 的类型
     * @return 已经绑定返回 true，否则返回 false
     */
    <EVM extends BaseViewModel>
    boolean containsExtraViewModel(Class<EVM> viewModelClass);

    /**
     * <p>
     * 绑定一个额外的 ViewModel，如果绑定时 View 还未初始化或已经销毁则抛出异常<br/>
     * 绑定成功返回该 ViewModel，这个 ViewModel 在绑定时自动执行相应的生命周期方法；
     * 如果绑定的 ViewModel 与 View 的主 ViewModel 类型相同返回 null。
     * </p>
     * <p>
     * 你不需要在调用这个方法之后调用 {@link #unbindExtraViewModel(Class)} 解除绑定
     * 来防止内存泄漏，被绑定的 ViewModel 会在 View 被销毁后一起销毁。
     * </p>
     *
     * @param viewModelClass ViewModel 的类型
     * @param <EVM>          ViewModel 的类型
     * @return 绑定成功返回 EVM 类型的 ViewModel，如果绑定的 ViewModel
     * 与 View 的主 ViewModel 类型相同返回 null
     */
    <EVM extends BaseViewModel>
    EVM bindExtraViewModel(Class<EVM> viewModelClass);

    /**
     * 解除绑定一个 ViewModel，被解除绑定的 ViewModel 会自动执行销毁方法。
     * 如果在 View 未初始化时执行此方法会导致抛出异常。
     *
     * @param viewModelClass ViewModel 的类型
     * @param <EVM>          ViewModel 的类型
     * @return 解除绑定成功返回 true，如果不存在或类型是主 ViewModel，返回 false
     */
    <EVM extends BaseViewModel>
    boolean unbindExtraViewModel(Class<EVM> viewModelClass);

    /**
     * 解除所有额外绑定的 ViewModel。如果在 View 未初始化时执行此方法会导致抛出异常。
     *
     * @return 解除绑定成功返回 true，否则返回 false
     */
    boolean unbindAllExtraViewModels();

    /**
     * 根据类型获取对应的 ViewModel。
     *
     * @param viewModelClass ViewModel 的类型
     * @param <EVM>          ViewModel 的类型
     * @return 存在返回对应的 ViewModel 对象；如果不存在或类型是主 ViewModel，返回 null
     */
    <EVM extends BaseViewModel>
    EVM getExtraViewModel(Class<EVM> viewModelClass);

    /**
     * 返回所有额外绑定的 ViewModel 对象组成的数组。
     *
     * @return 额外 ViewModel 的数组，不存在返回 null
     */
    BaseViewModel[] getAllExtraViewModels();
}
