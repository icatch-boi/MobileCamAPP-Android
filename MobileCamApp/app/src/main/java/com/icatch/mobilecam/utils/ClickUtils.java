package com.icatch.mobilecam.utils;

import android.view.View;

import com.icatch.mobilecam.Log.AppLog;


/**
 * @author b.jiang
 * @date 2018/12/14
 * @description
 */
public class ClickUtils {
    // 两次点击按钮之间的点击间隔不能少于500毫秒
    private static final long MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime = 0;
    private static int mLastClickViewId;

    public static synchronized boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        long timeInterval = Math.abs(curClickTime - lastClickTime);
        if (timeInterval < MIN_CLICK_DELAY_TIME) {
            AppLog.d("ClickUtils","isFastClick");
            return true;
        }else {
            lastClickTime = curClickTime;
            return false;
        }
    }


    public static synchronized boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeInterval = Math.abs(time - lastClickTime);
        if (timeInterval < MIN_CLICK_DELAY_TIME) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }

    /**
     * 是否是快速点击
     *
     * @param v 点击的控件
     * @return true:是，false:不是
     */
    public static boolean isFastDoubleClick(View v) {
        int viewId = v.getId();
        long time = System.currentTimeMillis();
        long timeInterval = time - lastClickTime;
        if (timeInterval < MIN_CLICK_DELAY_TIME && viewId == mLastClickViewId) {
            AppLog.d("ClickUtils","isFastDoubleClick timeInterval:" + timeInterval+ " viewId:" + viewId + " mLastClickViewId:" +mLastClickViewId);
            return true;
        } else {
            lastClickTime = time;
            mLastClickViewId = viewId;
            return false;
        }
    }

    public static boolean isFastDoubleClick(int id) {
        int viewId = id;
        long time = System.currentTimeMillis();
        long timeInterval = time - lastClickTime;
        if (timeInterval < MIN_CLICK_DELAY_TIME && viewId == mLastClickViewId) {
            AppLog.d("ClickUtils","isFastDoubleClick timeInterval:" + timeInterval+ " viewId:" + viewId + " mLastClickViewId:" +mLastClickViewId);
            return true;
        } else {
            lastClickTime = time;
            mLastClickViewId = viewId;
            return false;
        }
    }
}
