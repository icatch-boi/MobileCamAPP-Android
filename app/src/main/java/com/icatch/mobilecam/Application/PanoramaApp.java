package com.icatch.mobilecam.Application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.icatch.mobilecam.utils.CrashHandler;
import com.icatch.mobilecam.utils.imageloader.ImageLoaderConfig;

import java.lang.reflect.Method;


public class PanoramaApp extends Application {
    private static final String TAG = PanoramaApp.class.getSimpleName();
    private static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();
        String arch = "";//cpu类型
        try {
            Class<?> clazz = Class.forName( "Android.os.SystemProperties" );
            Method get = clazz.getDeclaredMethod( "get", new Class[]{String.class} );
            arch = (String) get.invoke( clazz, new Object[]{"ro.product.cpu.abi"} );
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d( TAG, "arch =" + arch);
        instance = getApplicationContext();
        CrashHandler.getInstance().init(this);
//        initBuglyCrash();
        ImageLoaderConfig.initImageLoader(getApplicationContext(), null);
    }

//    private void initBuglyCrash() {
//        Log.d(TAG,"initBuglyCrash");
//        CrashReport.initCrashReport(getApplicationContext(), "e4a2c0f65a", true);
//    }

    public static Context getContext() {
        return instance;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
