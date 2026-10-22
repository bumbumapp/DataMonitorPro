package com.drnoob.datamonitor;

import android.app.Application;

public class DataMonitorApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EdgeToEdgeCallbacks.register(this);
    }
}
