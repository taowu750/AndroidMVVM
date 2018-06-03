package com.wutaodsg.androidmvvm.base;

import android.app.Application;
import android.content.Context;

import com.wutaodsg.mvvm.util.log.LogUtils;

/**
 * 项目基础的 Application。
 */

public class App extends Application {

    private static Context sContext;


    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        LogUtils.tagPrefix = "WuT.";
//        LogUtils.addExcludedTag(NavHeaderView.class.getSimpleName());
//        LogUtils.setSpecific(true);
//        LogUtils.addSpecificTag(ChildViewActivity.class.getSimpleName());
    }


    public static Context getContext() {
        return sContext;
    }
}
