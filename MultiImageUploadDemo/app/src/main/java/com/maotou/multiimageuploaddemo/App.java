package com.maotou.multiimageuploaddemo;

import android.app.Application;

/**
 * Created by wuchundu on 2018/7/2.
 */

public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static App getApp(){
        return app;
    }
}
