package com.icatch.mobilecam.data.SystemInfo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.icatch.mobilecam.Log.AppLog;

import java.io.File;

/**
 * Created by zhang yanhu C001012 on 2015/11/13 10:40.
 */
public class SystemInfo {
    private static final String TAG = "SystemInfo";
    private static SystemInfo instance;

    public static SystemInfo getInstance() {
        if (instance == null) {
            instance = new SystemInfo();
        }
        return instance;
    }

    public static DisplayMetrics getMetrics(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics;
    }

    public String getLocalMacAddress(Activity activity) {
        WifiManager wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        AppLog.i(TAG, "current Mac=" + info.getMacAddress().toLowerCase());
        return info.getMacAddress().toLowerCase();
    }

    public static long getSDFreeSize() {
        // 得到文件系统的信息：存储块大小，总的存储块数量，可用存储块数量
        // 获取sd卡空间
        // 存储设备会被分为若干个区块
        // 每个区块的大小 * 区块总数 = 存储设备的总大小
        // 每个区块的大小 * 可用区块的数量 = 存储设备可用大小
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long totalBlocks;
        long availableBlocks;
        // 由于API18（Android4.3）以后getBlockSize过时并且改为了getBlockSizeLong
        // 因此这里需要根据版本号来使用那一套API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
        }
        // 利用formatSize函数把字节转换为用户等看懂的大小数值单位
        //byte
        AppLog.i(TAG, "getSDFreeSize=" + blockSize * availableBlocks);
        return (blockSize * availableBlocks);
    }

    //封装Formatter.formatFileSize方法，具体可以参考安卓的API
    private String formatSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

    public static long getFreeMemory(Context mContext) {
        long freeMemorySize;
        // 得到ActivityManager
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 创建ActivityManager.MemoryInfo对象
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // 取得剩余的内存空间
        freeMemorySize = mi.availMem / 1024;
        AppLog.i(TAG, "current FreeMemory=" + freeMemorySize);
        return freeMemorySize;
    }

    public static int getWindowVisibleCountMax(Context context, int row) {
        int visibleCountMax = 0;
        int height = getMetrics(context).heightPixels;
        int width = getMetrics(context).widthPixels;
        int itemWidth = width / row;
        visibleCountMax = (height / itemWidth) * row + row;
        AppLog.i(TAG, "end getWindowVisibleCountMax visibleCountMax=" + visibleCountMax);
        return visibleCountMax;
    }

    public static void hideInputMethod(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            View view = activity.getCurrentFocus();
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
