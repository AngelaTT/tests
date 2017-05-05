package com.software.videoplayer;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class MyApplication extends Application {

    private static MyApplication sInstance;

    private ExecutorService es = Executors.newFixedThreadPool(3);

    public void execRunnable(Runnable r) {
        if (!es.isShutdown()) {
            es.execute(r);
        }
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }


}
