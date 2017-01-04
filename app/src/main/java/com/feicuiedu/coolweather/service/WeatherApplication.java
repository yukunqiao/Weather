package com.feicuiedu.coolweather.service;

import android.app.Application;

import org.litepal.LitePalApplication;
import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by Administrator on 2016/12/30.
 */

public class WeatherApplication extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
