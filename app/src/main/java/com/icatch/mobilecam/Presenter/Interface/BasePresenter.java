package com.icatch.mobilecam.Presenter.Interface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.GlobalApp.ExitApp;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;

import java.lang.reflect.Method;

/**
 * Created by zhang yanhu C001012 on 2015/11/19 15:43.
 */
public abstract  class BasePresenter {
    private final String tag = "BasePresenter";
    protected Activity activity;
    public BasePresenter(Activity activity) {
        this.activity = activity;
    }
    public void initCfg(){
        GlobalInfo.getInstance().setCurrentApp(activity);
        // never sleep when run this activity
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // do not display menu bar
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        ICatchCameraConfig.getInstance().setPreviewCacheParam(1000, 1000);
    }

    public void redirectToAnotherActivity(Context context,Class<?> cls){
        Intent intent = new Intent();
        AppLog.i(tag, "intent:start redirectToAnotherActivity class =" + cls.getName());
        intent.setClass(context, cls);
        context.startActivity(intent);
    }
    
    public void finishActivity() {
        activity.finish();
    }

    public void isAppBackground() {
        if(AppInfo.isAppSentToBackground(activity)) {
            ExitApp.getInstance().exit();
        }
    }

    public void submitAppInfo() {
        GlobalInfo.getInstance().setCurrentApp(activity);
        ExitApp.getInstance().addActivity(activity);
    }

    public void removeActivity(){
        if(activity != null){
            ExitApp.getInstance().removeActivity(activity);
        }
    }

    public void showOptionIcon(Menu menu) {
        //display icon
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }
    }
}
