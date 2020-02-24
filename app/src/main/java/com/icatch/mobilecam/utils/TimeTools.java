package com.icatch.mobilecam.utils;

/**
 * Created by zhang yanhu C001012 on 2015/12/15 15:25.
 */

public class TimeTools {
    private static long lastClickTime;
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}