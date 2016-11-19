package com.houxy.wificar;

import android.app.Application;

/**
 * Created by Houxy on 2016/11/17.
 */

public class WifiCarApplication extends Application{

    private static WifiCarApplication INSTANCE;
    public static String cacheDir = "";

    public static WifiCarApplication getContext() {
        return INSTANCE;
    }

    private void setInstance(WifiCarApplication app) {
        setDaysApplication(app);
    }

    private static void setDaysApplication(WifiCarApplication a) {
        WifiCarApplication.INSTANCE = a;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        /**
         * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
         */
        if (getApplicationContext().getExternalCacheDir() != null) {
            cacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            cacheDir = getApplicationContext().getCacheDir().toString();
        }
    }

}
