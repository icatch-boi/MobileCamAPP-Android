package com.icatch.mobilecam.Listener;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.OrientationEventListener;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.AppInfo.AppInfo;


/**
 * Created by pub on 2017/5/9.
 */

public class MyOrientoinListener extends OrientationEventListener {
    private static final String TAG = "MyOrientoinListener";
    private Context context;
    private Activity activity;



    public MyOrientoinListener(Activity activity, Context context) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    public MyOrientoinListener(Activity activity, Context context, int rate) {
        super(context, rate);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onOrientationChanged(int orientation) {
//        AppLog.d(TAG, "orention" + orientation);
        int screenOrientation = context.getResources().getConfiguration().orientation;
//        AppLog.d(TAG, "screenOrientation" + screenOrientation);
//        if (((orientation >= 0) && (orientation < 45)) || (orientation > 315)) {//设置竖屏
////            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && orientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
//            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//
//                AppLog.d(TAG, "设置竖屏");
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
////                oriBtn.setText("竖屏");
//            }
//        }
//        else if (orientation > 225 && orientation < 315) { //设置横屏
//            AppLog.d(TAG, "设置横屏");
//            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
////                oriBtn.setText("横屏");
//            }
//        } else if (orientation > 45 && orientation < 135) {// 设置反向横屏
//            AppLog.d(TAG, "反向横屏");
//            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
////                oriBtn.setText("反向横屏");
//            }
//        }
//
//        else if (orientation > 135 && orientation < 225) {
//            AppLog.d(TAG, "反向竖屏");
//            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
////                oriBtn.setText("反向竖屏");
//            }
//        }

        if (((orientation >= 0) && (orientation < 45)) || (orientation > 315)) {//设置竖屏
//            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && orientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            if (AppInfo.curScreenOrientation != ScreenOrientation.SCREEN_ORIENTATION_PORTRAIT) {

                AppLog.d(TAG, "设置竖屏");
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                AppInfo.curScreenOrientation = ScreenOrientation.SCREEN_ORIENTATION_PORTRAIT;
            }
        }
        else if (orientation > 135 && orientation < 225) {
            if (AppInfo.curScreenOrientation != ScreenOrientation.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                AppLog.d(TAG, "设置反向竖屏");
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                AppInfo.curScreenOrientation = ScreenOrientation.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            }
        }
    }

    public enum ScreenOrientation{
        SCREEN_ORIENTATION_PORTRAIT,
        SCREEN_ORIENTATION_REVERSE_PORTRAIT,
        SCREEN_ORIENTATION_LANDSCAPE,
        SCREEN_ORIENTATION_REVERSE_LANDSCAPE
    }
}
