package com.icatch.mobilecam.Application;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import android.util.Log;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.utils.CrashHandler;
import com.icatch.mobilecam.utils.imageloader.ImageLoaderConfig;
import com.tencent.bugly.crashreport.CrashReport;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;


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
        //CrashHandler.getInstance().init(this);
        initBuglyCrash(this);
        ImageLoaderConfig.initImageLoader(getApplicationContext(), null);
    }

//    private void initBuglyCrash() {
//        Log.d(TAG,"initBuglyCrash");
//        CrashReport.initCrashReport(getApplicationContext(), "e4a2c0f65a", true);
//    }

    private void initBuglyCrash(Context context) {
        Log.d(TAG,"initBuglyCrash");
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            @Override
            public Map<String, String> onCrashHandleStart(int crashType, String errorType,
                                                          String errorMessage, String errorStack) {
                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                map.put("Key", "Value");
                AppLog.d(TAG,"onCrashHandleStart crashType:" + crashType);
                AppLog.d(TAG,"onCrashHandleStart errorType:" + errorType);
                AppLog.d(TAG,"onCrashHandleStart errorMessage:" + errorMessage);
                AppLog.d(TAG,"onCrashHandleStart errorStack:\n" + errorStack);
                return map;
            }

            @Override
            public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType,
                                                           String errorMessage, String errorStack) {
                AppLog.d(TAG,"onCrashHandleStart2GetExtraDatas crashType:" + crashType);
                AppLog.d(TAG,"onCrashHandleStart2GetExtraDatas errorType:" + errorType);
                AppLog.d(TAG,"onCrashHandleStart2GetExtraDatas errorMessage:" + errorMessage);
                AppLog.d(TAG,"onCrashHandleStart2GetExtraDatas errorStack:\n" + errorStack);
                try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }

        });
        CrashReport.initCrashReport(getApplicationContext(), "e4a2c0f65a", true,strategy);

    }

    public static Context getContext() {
        return instance;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
