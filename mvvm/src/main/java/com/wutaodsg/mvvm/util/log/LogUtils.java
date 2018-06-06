package com.wutaodsg.mvvm.util.log;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用来控制日志打印行为的类，可以控制某个级别以下的日志不会被打印出来，
 * 甚至可以关闭所有的日志打印。
 * </p>
 * <p>
 * 它还可以为你的日志 tag 前面加上一个前缀，通过在日志过滤器中设置这个前缀，
 * 你可以看到所有具有这个前缀的日志。
 * </p>
 * <p>
 * 这个日志工具还可以不打印某些日志以及只打印某些日志。
 * </p>
 * <p>
 * 需要注意的是，{@link #addExcludedTag(String)}、{@link #removeExcludedTag(String)}、
 * {@link #addSpecificTag(String)} 和 {@link #removeSpecificTag(String)} 这些方法
 * 不是线程安全的，最好在 MainActivity 或自定义 Application 类中配置这些数据。
 * </p>
 */

public class LogUtils {

    /**
     * 分别对应着不同的日志级别，可以赋给 level 变量来控制打印何种日志，
     * 当 level 为 NOTHING 将什么都不打印。
     */
    public static final int VERBOSE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;
    public static final int NOTHING = 5;


    /**
     * 代表日志的打印级别，低于当前级别的日志将不会被打印。
     */
    @LogLevelType
    public static int level = VERBOSE;

    /**
     * 日志 tag 的前缀，设置它的值后，所有日志的 tag 前面都会加上这个前缀。
     */
    public static String tagPrefix;


    private static List<String> sExcludedTags = new ArrayList<>();

    private static volatile boolean sIsSpecific = false;
    private static List<String> sSpecificTags = new ArrayList<>();


    private LogUtils() {
        throw new IllegalStateException("The object of LogUtils cannot be created");
    }


    /**
     * tag 是否不会被打印。
     *
     * @param tag 日志标签
     * @return 不会被打印返回 true，否则返回 false
     */
    public static boolean containsExcludedTag(@NonNull String tag) {
        return sExcludedTags.contains(tag);
    }

    /**
     * 将 tag 添加到不被打印的 tag 集合中，这个集合中的 tag 不会被打印出来。
     *
     * @param tag 日志标签
     * @return 添加成功返回 true，已经存在返回 false
     */
    public static boolean addExcludedTag(@NonNull String tag) {
        if (!sExcludedTags.contains(tag)) {
            sExcludedTags.add(tag);
            return true;
        }

        return false;
    }

    /**
     * 将 tag 从不会被打印的 tag 集合中移除，这个集合中的 tag 不会被打印出来。
     *
     * @param tag 日志标签
     * @return 移除成功返回 true，不存在返回 false
     */
    public static boolean removeExcludedTag(@NonNull String tag) {
        return sExcludedTags.remove(tag);
    }

    /**
     * 当前 LogUtils 是否处于只打印某些日志的状态。
     * 在这种状态下，LogUtils 只会打印指定 tag 的日志。
     * 这个值默认为 false。
     *
     * @return true 表示 LogUtils 只会打印某些日志，false 则没有这种限制
     */
    public static boolean isSpecific() {
        return sIsSpecific;
    }

    /**
     * 设置 LogUtils 的状态，参见 {@link #isSpecific()}。
     *
     * @param specific LogUtils 的状态
     */
    public static void setSpecific(boolean specific) {
        sIsSpecific = specific;
    }

    /**
     * 判断 tag 是否在被打印的日志集合中，参见 {@link #isSpecific()}。
     *
     * @param tag 日志标签
     * @return true 表示在只会被打印的日志集合中，false 相反
     */
    public static boolean containsSpecificTag(@NonNull String tag) {
        return sSpecificTags.contains(tag);
    }

    /**
     * 添加被打印的 tag，参见 {@link #isSpecific()}。
     *
     * @param tag 日志标签
     * @return 添加成功返回 true，已经存在返回 false
     */
    public static boolean addSpecificTag(@NonNull String tag) {
        if (!sSpecificTags.contains(tag)) {
            sSpecificTags.add(tag);

            return true;
        }

        return false;
    }

    /**
     * 删除被打印的 tag，参见 {@link #isSpecific()}。
     *
     * @param tag 日志标签
     * @return 删除成功返回 true，不存在返回 false
     */
    public static boolean removeSpecificTag(@NonNull String tag) {
        return sSpecificTags.remove(tag);
    }


    public static void v(String tag, String msg) {
        log(VERBOSE, tag, msg, null);
    }

    public static void v(String tag, String msg, Throwable throwable) {
        log(VERBOSE, tag, msg, throwable);
    }

    public static void d(String tag, String msg) {
        log(DEBUG, tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable throwable) {
        log(DEBUG, tag, msg, throwable);
    }

    public static void i(String tag, String msg) {
        log(INFO, tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable throwable) {
        log(INFO, tag, msg, throwable);
    }

    public static void w(String tag, String msg) {
        log(WARN, tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable throwable) {
        log(WARN, tag, msg, throwable);
    }

    public static void e(String tag, String msg) {
        log(ERROR, tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable throwable) {
        log(ERROR, tag, msg, throwable);
    }


    private static void log(@LogLevelType int logLevel, String tag, String msg, Throwable throwable) {
        if (level <= logLevel) {
            String tagWithPrefix = tag;
            if (!TextUtils.isEmpty(tagPrefix)) {
                tagWithPrefix = tagPrefix + tag;
            }

            boolean isLog = false;
            if (sIsSpecific) {
                if (sSpecificTags.contains(tag)) {
                    isLog = true;
                }
            } else if (!sExcludedTags.contains(tag)) {
                isLog = true;
            }

            if (isLog) {
                switch (logLevel) {
                    case VERBOSE:
                        if (throwable != null) {
                            Log.v(tagWithPrefix, msg, throwable);
                        } else {
                            Log.v(tagWithPrefix, msg);
                        }
                        break;

                    case DEBUG:
                        if (throwable != null) {
                            Log.d(tagWithPrefix, msg, throwable);
                        } else {
                            Log.d(tagWithPrefix, msg);
                        }
                        break;

                    case INFO:
                        if (throwable != null) {
                            Log.i(tagWithPrefix, msg, throwable);
                        } else {
                            Log.i(tagWithPrefix, msg);
                        }
                        break;

                    case WARN:
                        if (throwable != null) {
                            Log.w(tagWithPrefix, msg, throwable);
                        } else {
                            Log.w(tagWithPrefix, msg);
                        }
                        break;

                    case ERROR:
                        if (throwable != null) {
                            Log.e(tagWithPrefix, msg, throwable);
                        } else {
                            Log.e(tagWithPrefix, msg);
                        }
                        break;

                    case NOTHING:
                        break;
                }
            }
        }
    }


    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    @IntDef({VERBOSE, DEBUG, INFO, WARN, ERROR, NOTHING})
    @interface LogLevelType {
    }
}
