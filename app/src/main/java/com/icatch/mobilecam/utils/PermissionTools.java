package com.icatch.mobilecam.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.icatch.mobilecam.Log.AppLog;

public class PermissionTools {
    private static String TAG = PermissionTools.class.getSimpleName();
    public static final int WRITE_OR_READ_EXTERNAL_STORAGE_REQUEST_CODE = 102;
    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;

    public static void RequestPermissions(final Activity activity) {
        AppLog.d(TAG, "Start RequestPermissions");
        if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) ) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    WRITE_OR_READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {

        }
        AppLog.d(TAG, "End RequestPermissions");
    }

    public static boolean CheckSelfPermission(final Activity activity){
        return (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)&
                (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED);
    }

}
