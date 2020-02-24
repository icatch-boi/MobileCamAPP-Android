package com.icatch.mobilecam.Function.ThumbnailGetting;

import android.graphics.Bitmap;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.LocalSession;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.utils.BitmapTools;
import com.icatchtek.control.customer.ICatchCameraPlayback;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchFileType;
import com.icatchtek.reliant.customer.type.ICatchFrameBuffer;

public class ThumbnailOperation {
    //ThumbnailGetting
    private static String TAG = "ThumbnailOperation";

    public static Bitmap getVideoThumbnailFromSdk(String videoPath) {
        if (videoPath == null) {
            return null;
        }
        ICatchCameraPlayback cameraPlayback = null;
        AppLog.d(TAG, "start getVideoThumbnailFromSdk");
        Bitmap bitmap = null;
        ICatchFrameBuffer frameBuffer = null;
        int datalength = 0;
        byte[] buffer = null;
        LocalSession.getInstance().prepareCommandSession();
        cameraPlayback = LocalSession.getInstance().getICatchCameraPlayback();
        if (cameraPlayback == null) {
            return null;
        }
        ICatchFile icathfile = new ICatchFile(33, ICatchFileType.ICH_FILE_TYPE_VIDEO, videoPath, "", 0);
        AppLog.d(TAG, "start getThumbnail videoPath=" + videoPath);
        try {
            frameBuffer = cameraPlayback.getThumbnail(icathfile);
        } catch (Exception e) {
            AppLog.d(TAG, "getThumbnail Exception");
            e.printStackTrace();
        }
        AppLog.d(TAG, "frameBuffer=" + frameBuffer);
        if (frameBuffer == null) {
            return null;
        }
        buffer = frameBuffer.getBuffer();
        datalength = frameBuffer.getFrameSize();
        AppLog.d(TAG, "frameBuffer buffer=" + buffer + " datalength=" + datalength);
        if (datalength > 0) {
            bitmap = BitmapTools.decodeByteArray(buffer, 300, 300);
//            bitmap = BitmapFactory.decodeByteArray(buffer, 0, datalength);
        }
        LocalSession.getInstance().destroyCommandSession();
        AppLog.d(TAG, "end getVideoThumbnailFromSdk bitmap=" + bitmap);
        return bitmap;
//        return null;
    }


    public static Bitmap getVideoThumbnail(String videoPath) {
        AppLog.d(TAG, "start getVideoThumbnail");
//        Bitmap bitmap = BitmapTools.getVideoThumbnail(videoPath, BitmapTools.THUMBNAIL_WIDTH, BitmapTools.THUMBNAIL_HEIGHT);
//        if (bitmap == null) {
//            bitmap = getVideoThumbnailFromSdk(videoPath);
//        }
        Bitmap bitmap = getVideoThumbnailFromSdk(videoPath);
        AppLog.d(TAG, "end getVideoThumbnail bitmap=" + bitmap);
        return bitmap;
    }

    public static Bitmap getlocalVideoWallThumbnail(ICatchCameraPlayback iCatchCameraPlayback, String videoPath) {
        AppLog.d(TAG, "start getVideoThumbnail");
        Bitmap bitmap = BitmapTools.getVideoThumbnail(videoPath, BitmapTools.THUMBNAIL_WIDTH, BitmapTools.THUMBNAIL_HEIGHT);
//        Bitmap bitmap = null;
        if (bitmap == null) {
            bitmap = getLocalVideoThumbnail(iCatchCameraPlayback, videoPath);
        }
        AppLog.d(TAG, "end getVideoThumbnail bitmap=" + bitmap);
        return bitmap;
    }

    public static Bitmap getLocalVideoThumbnail(ICatchCameraPlayback iCatchCameraPlayback, String videoPath) {
        AppLog.d(TAG, "start getLocalVideoThumbnail");
        if (iCatchCameraPlayback == null) {
            return null;
        }
        Bitmap bitmap = null;
        ICatchFrameBuffer frameBuffer = null;
        int datalength = 0;
        byte[] buffer = null;
        ICatchFile icathfile = new ICatchFile(33, ICatchFileType.ICH_FILE_TYPE_VIDEO, videoPath, "", 0);
        try {
            frameBuffer = iCatchCameraPlayback.getThumbnail(icathfile);
        } catch (Exception e) {
            AppLog.d(TAG, "start getLocalVideoThumbnail " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        if (frameBuffer != null) {
            buffer = frameBuffer.getBuffer();
            datalength = frameBuffer.getFrameSize();
            AppLog.d(TAG, "start getLocalVideoThumbnail buffer=" + buffer + " datalength=" + datalength);
            if (datalength > 0) {
                bitmap = BitmapTools.decodeByteArray(buffer, 160, 160);
            }
        }
        AppLog.d(TAG, "end getLocalVideoThumbnail bitmap=" + bitmap);
        return bitmap;
    }

    public static int getBatteryLevelIcon(int batteryLevel) {
        AppLog.d(TAG, "current setBatteryLevelIcon= " + batteryLevel);
        int resId = -1;
        if (batteryLevel < 20 && batteryLevel >= 0) {
            resId = R.drawable.ic_battery_alert_green_24dp;
        } else if (batteryLevel == 33) {
            resId = R.drawable.ic_battery_30_green_24dp;
        } else if (batteryLevel == 66) {
            resId = R.drawable.ic_battery_60_green_24dp;
        } else if (batteryLevel == 100) {
            resId = R.drawable.ic_battery_full_green_24dp;
        } else if (batteryLevel > 100) {
            resId = R.drawable.ic_battery_charging_full_green_24dp;
        }
        return resId;
    }
}
