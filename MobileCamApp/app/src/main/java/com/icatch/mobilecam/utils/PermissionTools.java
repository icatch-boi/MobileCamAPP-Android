package com.icatch.mobilecam.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.icatch.mobilecam.Log.AppLog;

import java.util.LinkedList;
import java.util.List;

public class PermissionTools {
    private static String TAG = PermissionTools.class.getSimpleName();
    public static final int WRITE_OR_READ_EXTERNAL_STORAGE_REQUEST_CODE = 102;
//    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    public static final int ALL_REQUEST_CODE = 102;

    public static final int CAMERA_REQUEST_CODE = 103;

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
                == PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestAllPermissions(final Activity activity) {
        AppLog.d(TAG, "Start request all necessary permissions");
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
        List<String> requestList = new LinkedList<>();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            requestList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
        }

        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            requestList.add(Manifest.permission.RECORD_AUDIO);
        }
        if(requestList.size() > 0){
            String[] systemRequestArray = requestList.toArray(new String[requestList.size()]);
            ActivityCompat.requestPermissions(activity, systemRequestArray,ALL_REQUEST_CODE);
        } else {
            AppLog.d(TAG, "permission has granted!");
        }
        AppLog.d(TAG, "End requestPermissions");
    }

    public static boolean checkAllSelfPermission(final Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED&&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                            == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;
        }else {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED&&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                            == PackageManager.PERMISSION_GRANTED;
        }

    }

    public static boolean checkCameraSelfPermission(final Activity activity){
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestCameraPermissions(final Activity activity) {
        AppLog.d(TAG, "Start request camera necessary permissions");
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
        List<String> requestList = new LinkedList<>();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            requestList.add(Manifest.permission.CAMERA);
        }
        if(requestList.size() > 0){
            String[] systemRequestArray = requestList.toArray(new String[requestList.size()]);
            ActivityCompat.requestPermissions(activity, systemRequestArray,CAMERA_REQUEST_CODE);
        } else {
            AppLog.d(TAG, "permission has granted!");
        }
        AppLog.d(TAG, "End requestPermissions");
    }


}
