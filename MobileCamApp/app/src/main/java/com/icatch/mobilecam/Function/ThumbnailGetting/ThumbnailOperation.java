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

    public static int getBatteryLevelIcon02(int batteryLevel) {
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

    public static int getBatteryLevelIcon(int batteryPower) {
        AppLog.d(TAG, "current setBatteryLevelIcon= " + batteryPower);
        int drawableId = -1;
        if (batteryPower <= 0) {
            drawableId = R.drawable.video_buttery_0;
        } else if (batteryPower > 0 && batteryPower <= 10) {
            drawableId = R.drawable.video_buttery_10;
        } else if (batteryPower > 10 && batteryPower <= 20) {
            drawableId = R.drawable.video_buttery_20;
        } else if (batteryPower > 20 && batteryPower <= 30) {
            drawableId = R.drawable.video_buttery_30;
        } else if (batteryPower > 30 && batteryPower <= 40) {
            drawableId = R.drawable.video_buttery_40;
        } else if (batteryPower > 40 && batteryPower <= 50) {
            drawableId = R.drawable.video_buttery_50;
        } else if(batteryPower > 50 && batteryPower <= 60){
            drawableId = R.drawable.video_buttery_60;
        } else if(batteryPower > 60 && batteryPower <= 70){
            drawableId = R.drawable.video_buttery_70;
        } else if(batteryPower > 70 && batteryPower <= 80){
            drawableId = R.drawable.video_buttery_80;
        } else if(batteryPower > 80 && batteryPower <= 90){
            drawableId = R.drawable.video_buttery_90;
        } else if(batteryPower > 90 && batteryPower <= 100){
            drawableId = R.drawable.video_buttery_100;
        } else {
            drawableId = R.drawable.video_buttery_100;
        }
        return drawableId;
    }
}
