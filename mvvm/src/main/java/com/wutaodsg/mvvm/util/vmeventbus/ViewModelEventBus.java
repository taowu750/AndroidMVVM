package com.wutaodsg.mvvm.util.vmeventbus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wutaodsg.mvvm.core.BaseViewModel;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * ViewModelEvent 主要用来在 ViewModel 之间传递事件，为 ViewModel 之间提供交流的通道。
 * <p>
 * 在 ViewModelEvent 中，我们首先要确定一个事件标志（event tag），这个事件
 * 标志代表着一条事件总线（也就是一类事件）。我们可以在这个标志下注册事件的接受者，也就是 ViewModel，
 * 并为 ViewModel 设置接受数据的类型、事件来临时发生的动作，以及事件运行时的
 * 线程环境。
 * <p>
 * 通过调用 {@link #register(String, Class, ViewModelCommand)} 或
 * {@link #register(String, ViewModelCommand)} 方法，我们可以将
 * ViewModel 与事件标志、事件类型以及命令绑定起来，从而在事件来临时执行
 * 相应的操作。
 * <p>
 * ViewModelEventBus 提供了一系列的 <code>unregister</code> 方法来帮助你
 * 细粒度的进行取消注册操作。
 * <p>
 * 在注册之后，通过调用 {@link #post(String, Object)} 方法，你可以向
 * 这个 event tag 下接受该数据类型的注册者发送数据；也可以使用 {@link #post(String)}
 * 方法向这个 event tag 下不需要数据的注册者发送信号，要求他们进行操作。<br/>
 * 此外，你也可以使用其他 <code>post</code> 方法细粒度的发送事件。
 * <p>
 * 需要注意的是，ViewModel 的注册和取消注册必须是成对操作，也就是说在
 * 注册一个 ViewModel 之后，必须在将来某个时间取消注册这个 ViewModel，
 * 避免出现内存泄漏的问题。推荐在 {@link BaseViewModel#onAttach(Context)}
 * 方法中注册，然后在 {@link BaseViewModel#onCleared()} 方法中取消注册。
 * <p>
 * <strong>为了更好的管理 event tag，我推荐你使用一个专门的接口（比如叫 ViewModelEventTags），
 * 在这个接口中声明所有的 event tag，这样就好管理它们。</strong>
 */

public class ViewModelEventBus {

    private final ConcurrentHashMap<String, ConcurrentHashMap<Class, CopyOnWriteArrayList<ViewModelCommand>>>
            mEventBus = new ConcurrentHashMap<>();

    private final ViewModelCommandCache mCacheWithData = new ViewModelCommandCache();
    private final ViewModelCommandCache mCacheWithoutData = new ViewModelCommandCache();


    private ViewModelEventBus() {
    }


    /**
     * 获取 ViewModelEventBus 的单例。
     *
     * @return ViewModelEventBus 的单例
     */
    public static ViewModelEventBus getInstance() {
        return Holder.INSTANCE;
    }


    /**
     * 注册一个 ViewModel。
     * <p>
     * 这个 ViewModel 将会在 event tag 下，接受类型为 dataClass 的
     * 数据，当事件来临时，调用 command 里面的命令。
     * <p>
     * 需要注意的是，command 必须使用 {@link com.wutaodsg.mvvm.command.Action1}
     * 作为命令函数，因为这个 ViewModel 需要接受类型为 dataClass 的
     * 数据。否则，事件来临时，这个 ViewModel 将不会做任何操作，从而
     * 错过事件。
     * <p>
     * 关于事件，参见 <code>post</code> 方法。
     *
     * @param eventTag  事件标志
     * @param command   事件来临时进行的操作
     * @param dataClass ViewModel 将会接受的数据的类型
     * @param <T>       数据类型
     * @return 注册成功返回 true，如果已经注册了返回 false
     */
    public <T> boolean register(@NonNull String eventTag,
                                @NonNull Class<T> dataClass,
                                @NonNull ViewModelCommand<T> command) {
        ConcurrentHashMap<Class, CopyOnWriteArrayList<ViewModelCommand>>
                eventMap = mEventBus.get(eventTag);
        if (eventMap != null) {
            CopyOnWriteArrayList<ViewModelCommand> viewModelCommands = eventMap.get(dataClass);
            if (viewModelCommands != null) {
                if (!viewModelCommands.contains(command)) {
                    viewModelCommands.add(command);
                } else {
                    // 已经存在返回 false
                    return false;
                }
            } else {
                viewModelCommands = new CopyOnWriteArrayList<>();
                viewModelCommands.add(command);
                eventMap.put(dataClass, viewModelCommands);
            }
        } else {
            eventMap = new ConcurrentHashMap<>();
            CopyOnWriteArrayList<ViewModelCommand> viewModelCommands = new CopyOnWriteArrayList<>();
            viewModelCommands.add(command);
            eventMap.put(dataClass, viewModelCommands);
            mEventBus.put(eventTag, eventMap);
        }

        return true;
    }

    /**
     * 注册一个 ViewModel。
     * <p>
     * 这个 ViewModel 将会在 event tag 下，不接受任何数据，
     * 当事件来临时，调用 command 里面的命令。
     * <p>
     * 需要注意的是，command 必须使用 {@link com.wutaodsg.mvvm.command.Action0}
     * 作为命令函数，因为这个 ViewModel 不需要接受任何数据。
     * 否则，事件来临时，这个 ViewModel 将不会做任何操作，从而错过事件。
     * <p>
     * 关于事件，参见 <code>post</code> 方法。
     *
     * @param eventTag 事件标志
     * @param command  事件来临时进行的操作
     * @return 注册成功返回 true，如果已经注册了返回 false
     */
    public boolean register(@NonNull String eventTag,
                            @NonNull ViewModelCommand command) {
        ConcurrentHashMap<Class, CopyOnWriteArrayList<ViewModelCommand>>
                eventMap = mEventBus.get(eventTag);
        if (eventMap != null) {
            CopyOnWriteArrayList<ViewModelCommand> viewModelCommands = eventMap.get(NoDataEventType.class);
            if (viewModelCommands != null) {
                if (!viewModelCommands.contains(command)) {
                    viewModelCommands.add(command);
                } else {
                    return false;
                }
            } else {
                viewModelCommands = new CopyOnWriteArrayList<>();
                viewModelCommands.add(command);
                eventMap.put(NoDataEventType.class, viewModelCommands);
            }
        } else {
            eventMap = new ConcurrentHashMap<>();
            CopyOnWriteArrayList<ViewModelCommand> viewModelCommands = new CopyOnWriteArrayList<>();
            viewModelCommands.add(command);
            eventMap.put(NoDataEventType.class, viewModelCommands);
            mEventBus.put(eventTag, eventMap);
        }

        return true;
    }

    /**
     * 取消在 eventTag 下注册的所有 ViewModel 对象。
     *
     * @param eventTag 事件标志
     * @return 取消注册成功返回 true，原来没有注册过返回 false
     */
    public boolean unregister(@NonNull String eventTag) {
        ConcurrentHashMap<Class, CopyOnWriteArrayList<ViewModelCommand>>
                eventMap = mEventBus.get(eventTag);
        if (eventMap != null) {
            for (CopyOnWriteArrayList<ViewModelCommand> viewModelCommands : eventMap.values()) {
                for (ViewModelCommand viewModelCommand : viewModelCommands) {
                    viewModelCommand.clear();
                }
                viewModelCommands.clear();
            }
            eventMap.clear();
            mEventBus.remove(eventTag);

            return true;
        }

        return false;
    }

    /**
     * 取消在这个 eventTag 下注册的，所有接受数据类型为 dataClass 的 ViewModel 对象。
     * 如果 dataClass 为 null，就注销所有不接受数据的 ViewModel 对象。
     *
     * @param eventTag  事件标志
     * @param dataClass ViewModel 接受的数据类型；值为 null 表示不接受数据
     * @return 取消注册成功返回 true，原来没有注册过返回 false
     */
    public <T> boolean unregister(@NonNull String eventTag,
                                  @Nullable Class<T> dataClass) {
        Class dc = dataClass;
        if (dc == null) {
            dc = NoDataEventType.class;
        }

        ConcurrentHashMap<Class, CopyOnWriteArrayList<ViewModelCommand>>
                eventMap = mEventBus.get(eventTag);
        if (eventMap != null) {
            CopyOnWriteArrayList<ViewModelCommand> viewModelCommands = eventMap.get(dc);
            if (viewModelCommands != null) {
                for (ViewModelCommand viewModelCommand : viewModelCommands) {
                    viewModelCommand.clear();
                }
                viewModelCommands.clear();
                eventMap.remove(dc);

                return true;
            }
        }

        return false;
    }

    /**
     * 取消在这个 eventTag 下注册的 viewModel，这个 ViewModel 所有的注册记录都会被注销。
     *
     * @param eventTag  事件标志
     * @param viewModel 在这个 eventTag 下注册的 ViewModel 对象
     * @return 取消注册成功返回 true，原来没有注册过返回 false
     */
    public boolean unregister(@NonNull String eventTag,
                              @NonNull BaseViewModel viewModel) {
        boolean result = false;
        ConcurrentHashMap<Class, CopyOnWriteArrayList<ViewModelCommand>>
                eventMap = mEventBus.get(eventTag);
        if (eventMap != null) {
            for (CopyOnWriteArrayList<ViewModelCommand> viewModelCommands : eventMap.values()) {
                for (ViewModelCommand viewModelCommand : viewModelCommands) {
                    if (viewModelCommand.getViewModel().equals(viewModel)) {
                        viewModelCommands.remove(viewModelCommand);
                        viewModelCommand.clear();
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 取消在这个 eventTag 下注册的，接受数据类型为 dataClass 的 viewModel。
     * 如果 dataClass 为 null，就注销不接受数据的 viewModel。
     *
     * @param eventTag  事件标志
     * @param dataClass viewModel 接受的数据类型；值为 null 表示不接受数据
     * @param viewModel 在这个 eventTag 下注册的 ViewModel 对象
     * @return 取消注册成功返回 true，原来没有注册过返回 false
     */
    public <T> boolean unregister(@NonNull String eventTag,
                                  @Nullable Class<T> dataClass,
                                  @NonNull BaseViewModel viewModel) {
        Class dc = dataClass;
        if (dc == null) {
            dc = NoDataEventType.class;
        }

        ConcurrentHashMap<Class, CopyOnWriteArrayList<ViewModelCommand>>
                eventMap = mEventBus.get(eventTag);
        if (eventMap != null) {
            CopyOnWriteArrayList<ViewModelCommand> viewModelCommands = eventMap.get(dc);
            if (viewModelCommands != null) {
                for (ViewModelCommand viewModelCommand : viewModelCommands) {
                    if (viewModelCommand.getViewModel().equals(viewModel)) {
                        viewModelCommands.remove(viewModelCommand);
                        viewModelCommand.clear();

                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * ViewModelEventBus 中是否含有事件标志为 eventTag 的事件总线。
     *
     * @param eventTag 事件标志
     * @return 如果存在返回 true，否则返回 false
     */
    public boolean contains(@NonNull String eventTag) {
        return mEventBus.containsKey(eventTag);
    }

    /**
     * ViewModelEventBus 中是否含有事件标志为 eventTag 且接受类型为 dataClass
     * 的事件总线。如果参数 dataClass 为 null 表示不接受数据的事件总线。
     *
     * @param eventTag  事件标志
     * @param dataClass 接受数据的类型，如果为 null 表示不接受数据的事件总线。
     * @param <T>       数据类型
     * @return 如果存在返回 true，否则返回 false
     */
    public <T> boolean contains(@NonNull String eventTag,
                                @Nullable Class<T> dataClass) {
        Class dc = dataClass;
        if (dc == null) {
            dc = NoDataEventType.class;
        }

        return mEventBus.containsKey(eventTag) && mEventBus.get(eventTag).containsKey(dc);
    }

    /**
     * ViewModelEventBus 中是否含有事件标志为 eventTag 、接受类型为 dataClass，
     * 且类型为 viewModelClass 的 ViewModel 对象。如果参数 dataClass 为 null 表示不接受数据
     * 的 ViewModel 对象。
     *
     * @param eventTag       事件标志
     * @param dataClass      接受数据的类型，如果为 null 表示不接受数据的 ViewModel 对象。
     * @param viewModelClass ViewModel 的类型
     * @param <T>            数据类型
     * @return 如果存在返回 true，否则返回 false
     */
    public <T> boolean contains(@NonNull String eventTag,
                                @Nullable Class<T> dataClass,
                                @NonNull Class<? extends BaseViewModel> viewModelClass) {
        Class dc = dataClass;
        if (dc == null) {
            dc = NoDataEventType.class;
        }

        ConcurrentHashMap<Class, CopyOnWriteArrayList<ViewModelCommand>>
                eventMap = mEventBus.get(eventTag);
        if (eventMap != null) {
            CopyOnWriteArrayList<ViewModelCommand> viewModelCommands = eventMap.get(dc);
            if (viewModelCommands != null) {
                for (ViewModelCommand viewModelCommand : viewModelCommands) {
                    if (viewModelCommand.getViewModel().getClass().equals(viewModelClass)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 发送事件。
     * <p>
     * 这个方法所发送的事件面向这个 eventTag 下注册的所有 ViewModel 对象。
     * 而只有那些不接受数据的 ViewModel 对象会接受事件，并回调命令。
     * <p>
     * 这种发送事件的方式相当于广播。
     *
     * @param eventTag 事件标志
     * @return 当有 ViewModel 响应这个事件时返回 true，否则返回 false
     */
    public boolean post(@NonNull String eventTag) {
        ConcurrentHashMap<Class, CopyOnWriteArrayList<ViewModelCommand>>
                eventMap = mEventBus.get(eventTag);
        if (eventMap != null) {
            CopyOnWriteArrayList<ViewModelCommand> viewModelCommands = eventMap.get(NoDataEventType.class);
            if (viewModelCommands != null) {
                for (ViewModelCommand viewModelCommand : viewModelCommands) {
                    viewModelCommand.execute();
                }

                return true;
            }
        }

        return false;
    }

    /**
     * 发送事件。
     * <p>
     * 这个方法所发送的事件面向这个 eventTag 下注册的所有 ViewModel 对象。
     * 而只有那些接收数据且数据类型与 data 相同的 ViewModel 对象会接受事件，并回调命令。
     * <p>
     * 这种发送事件的方式相当于广播。
     *
     * @param eventTag 事件标志
     * @param data     数据
     * @return 当有 ViewModel 响应这个事件时返回 true，否则返回 false
     */
    @SuppressWarnings("unchecked")
    public <T> boolean post(@NonNull String eventTag, @NonNull T data) {
        Class dataClass = data.getClass();
        ConcurrentHashMap<Class, CopyOnWriteArrayList<ViewModelCommand>>
                eventMap = mEventBus.get(eventTag);
        if (eventMap != null) {
            CopyOnWriteArrayList<ViewModelCommand> viewModelCommands = eventMap.get(dataClass);
            if (viewModelCommands != null) {
                for (ViewModelCommand viewModelCommand : viewModelCommands) {
                    viewModelCommand.execute(data);
                }

                return true;
            }
        }

        return false;
    }

    /**
     * 发送事件。
     * <p>
     * 这个方法所发送的事件面向这个 eventTag 下注册类型为 viewModelClass，
     * 并且接受数据类型与 data 相同的 viewModel。只有这个 viewModel 会接受事件并回调命令。
     * <p>
     * 这种发送命令的方式相当于点对点传播。
     * <p>
     * 这个方法还会缓存上一次的事件，如果这一次所发送的事件与上一次相同，
     * 就无需再次进行查找操作，而是从缓存中读取数据并进行操作。
     *
     * @param eventTag       事件标志
     * @param data           数据
     * @param viewModelClass 指定 ViewModel 的类型
     * @return 当有 ViewModel 响应这个事件时返回 true，否则返回 false
     */
    @SuppressWarnings("unchecked")
    public <T> boolean post(@NonNull String eventTag,
                            @NonNull T data,
                            @NonNull Class<? extends BaseViewModel> viewModelClass) {
        Class dataClass = data.getClass();
        if (mCacheWithData.cached(eventTag, dataClass, viewModelClass)) {
            mCacheWithData.getCache().execute(data);

            return true;
        } else {
            ConcurrentHashMap<Class, CopyOnWriteArrayList<ViewModelCommand>>
                    eventMap = mEventBus.get(eventTag);
            if (eventMap != null) {
                CopyOnWriteArrayList<ViewModelCommand> viewModelCommands = eventMap.get(dataClass);
                if (viewModelCommands != null) {
                    for (ViewModelCommand viewModelCommand : viewModelCommands) {
                        if (viewModelCommand.getViewModel().getClass().equals(viewModelClass)) {
                            viewModelCommand.execute(data);
                            mCacheWithData.setCache(viewModelCommand);

                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }

    /**
     * 发送事件。
     * <p>
     * 这个方法所发送的事件面向这个 eventTag 下注册类型为 viewModelClass，
     * 并且不接受任何数据的 viewModel。只有这个 viewModel 会接受事件并回调命令。
     * <p>
     * 这种发送命令的方式相当于点对点传播。
     * <p>
     * 这个方法还会缓存上一次的事件，如果这一次所发送的事件与上一次相同，
     * 就无需再次进行查找操作，而是从缓存中读取数据并进行操作。
     *
     * @param eventTag       事件标志
     * @param viewModelClass 指定 ViewModel 的类型
     * @return 当有 ViewModel 响应这个事件时返回 true，否则返回 false
     */
    public <T> boolean post(@NonNull String eventTag,
                            @NonNull Class<? extends BaseViewModel> viewModelClass) {
        if (mCacheWithoutData.cached(eventTag, NoDataEventType.class, viewModelClass)) {
            mCacheWithoutData.getCache().execute();

            return true;
        } else {
            ConcurrentHashMap<Class, CopyOnWriteArrayList<ViewModelCommand>>
                    eventMap = mEventBus.get(eventTag);
            if (eventMap != null) {
                CopyOnWriteArrayList<ViewModelCommand> viewModelCommands = eventMap.get(NoDataEventType.class);
                if (viewModelCommands != null) {
                    for (ViewModelCommand viewModelCommand : viewModelCommands) {
                        if (viewModelCommand.getViewModel().getClass().equals(viewModelClass)) {
                            viewModelCommand.execute();
                            mCacheWithoutData.setCache(viewModelCommand);

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


    private static final class Holder {
        private static final ViewModelEventBus INSTANCE = new ViewModelEventBus();
    }

    /**
     * 用来标志不接受数据的事件类型
     */
    private static final class NoDataEventType {
    }


    private static final class ViewModelCommandCache {

        private String mEventTag;
        private Class mDataClass;
        private Class<? extends BaseViewModel> mViewModelClass;

        private WeakReference<ViewModelCommand> mViewModelCommandRef;


        public void setKey(String eventTag, Class dataClass, Class<? extends BaseViewModel> viewModelClass) {
            mEventTag = eventTag;
            mDataClass = dataClass;
            mViewModelClass = viewModelClass;
        }

        public boolean cached(String eventTag, Class dataClass, Class<? extends BaseViewModel> viewModelClass) {
            if (equals(mEventTag, eventTag) &&
                    equals(mDataClass, dataClass) &&
                    equals(mViewModelClass, mViewModelClass) &&
                    mViewModelCommandRef != null &&
                    mViewModelCommandRef.get() != null) {
                return true;
            } else {
                setKey(eventTag, dataClass, viewModelClass);

                return false;
            }
        }

        public ViewModelCommand getCache() {
            return mViewModelCommandRef != null ? mViewModelCommandRef.get() : null;
        }

        public void setCache(ViewModelCommand viewModelCommand) {
            if (mViewModelCommandRef == null) {
                mViewModelCommandRef = new WeakReference<>(viewModelCommand);
            } else {
                mViewModelCommandRef.clear();
                mViewModelCommandRef = new WeakReference<>(viewModelCommand);
            }
        }


        private <T> boolean equals(@Nullable T t1, @Nullable T t2) {
            return t1 == null && t2 == null || t1 != null && t2 != null && t1.equals(t2);
        }
    }
}
