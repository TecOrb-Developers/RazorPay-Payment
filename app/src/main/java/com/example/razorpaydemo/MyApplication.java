package com.example.razorpaydemo;

import android.app.Application;

import com.google.gson.Gson;

public class MyApplication extends Application {
    private Gson gson;
    private static MyApplication applicationInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;
        gson = new Gson();
    }

    public Gson getGson() {
        return gson;
    }

    public static MyApplication getInstance() {
        return applicationInstance;
    }
}
