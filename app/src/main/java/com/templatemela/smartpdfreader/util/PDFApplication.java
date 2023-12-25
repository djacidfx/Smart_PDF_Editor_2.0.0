package com.templatemela.smartpdfreader.util;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

public class PDFApplication extends Application {
    private static PDFApplication myApplicationInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        myApplicationInstance = this;
    }

    @Override
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
