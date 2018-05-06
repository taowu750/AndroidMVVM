package com.wutaodsg.mvvm.util.log;

import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     用来控制日志打印行为的类，可以控制某个级别以下的日志不会被打印出来，
 *     甚至可以关闭所有的日志打印。
 * </p>
 * <p>
 *     它还可以为你的日志 tag 前面加上一个前缀，通过在日志过滤器中设置这个前缀，
 *     你可以看到所有具有这个前缀的日志。
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
    @LevelType
    public static int level = VERBOSE;

    /**
     * 日志 tag 的前缀，设置它的值后，所有日志的 tag 前面都会加上这个前缀。
     */
    public static String tag_prefix;


    private LogUtils() {
        throw new IllegalStateException("The object of LogUtils cannot be created");
    }


    public static void v(String tag, String msg) {
        if (level <= VERBOSE) {
            if (!TextUtils.isEmpty(tag_prefix)) {
                tag = tag_prefix + tag;
            }
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable throwable) {
        if (level <= VERBOSE) {
            if (!TextUtils.isEmpty(tag_prefix)) {
                tag = tag_prefix + tag;
            }
            Log.v(tag, msg, throwable);
        }
    }

    public static void d(String tag, String msg) {
        if (level <= DEBUG) {
            if (!TextUtils.isEmpty(tag_prefix)) {
                tag = tag_prefix + tag;
            }
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable throwable) {
        if (level <= DEBUG) {
            if (!TextUtils.isEmpty(tag_prefix)) {
                tag = tag_prefix + tag;
            }
            Log.d(tag, msg, throwable);
        }
    }

    public static void i(String tag, String msg) {
        if (level <= INFO) {
            if (!TextUtils.isEmpty(tag_prefix)) {
                tag = tag_prefix + tag;
            }
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable throwable) {
        if (level <= INFO) {
            if (!TextUtils.isEmpty(tag_prefix)) {
                tag = tag_prefix + tag;
            }
            Log.i(tag, msg, throwable);
        }
    }

    public static void w(String tag, String msg) {
        if (level <= WARN) {
            if (!TextUtils.isEmpty(tag_prefix)) {
                tag = tag_prefix + tag;
            }
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable throwable) {
        if (level <= WARN) {
            if (!TextUtils.isEmpty(tag_prefix)) {
                tag = tag_prefix + tag;
            }
            Log.w(tag, msg, throwable);
        }
    }

    public static void e(String tag, String msg) {
        if (level <= ERROR) {
            if (!TextUtils.isEmpty(tag_prefix)) {
                tag = tag_prefix + tag;
            }
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable throwable) {
        if (level <= ERROR) {
            if (!TextUtils.isEmpty(tag_prefix)) {
                tag = tag_prefix + tag;
            }
            Log.e(tag, msg, throwable);
        }
    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    @IntDef({VERBOSE, DEBUG, INFO, WARN, ERROR, NOTHING})
    @interface LevelType {}
}
