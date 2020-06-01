package com.icatch.mobilecam.data.AppInfo;

import android.app.ActivityManager;
import android.content.Context;

import com.icatch.mobilecam.Listener.MyOrientoinListener;
import com.icatch.mobilecam.data.entity.BluetoothAppDevice;
import com.icatch.mobilecam.data.type.PhotoWallLayoutType;
import com.icatch.mobilecam.Log.AppLog;
import com.icatchtek.bluetooth.customer.client.ICatchBluetoothClient;

import java.util.List;

/**
 * Created by yh.zhang C001012 on 2015/10/15:13:27.
 * Fucntion:
 */
public class AppInfo {
    public static final String APP_PATH = "/MobileCam/";
    public static final String APP_VERSION = "V1.2.0_beta11";
    public static final String SDK_VERSION = "V3.6.0.23";
    public static final String SDK_LOG_DIRECTORY_PATH = APP_PATH  + "MobileCam_SDK_Log/";
    public static final String APP_LOG_DIRECTORY_PATH = APP_PATH  + "MobileCam_APP_Log/";
    public static final String PROPERTY_CFG_FILE_NAME = "netconfig.properties";
    public static final String STREAM_OUTPUT_DIRECTORY_PATH = APP_PATH + "Resoure/Raw/";
    public static final String PROPERTY_CFG_DIRECTORY_PATH = APP_PATH + "Resoure/";
    public static final String DOWNLOAD_PATH_PHOTO = "/DCIM/MobileCam/photo/";
    public static final String DOWNLOAD_PATH_VIDEO = "/DCIM/MobileCam/video/";
    public static final String AUTO_DOWNLOAD_PATH = "/DCIM/MobileCam/photo/";
    public static final String FW_UPGRADE_FILENAME = "sphost.BRN";
    private static final String TAG = AppInfo.class.getSimpleName();
    public static final String FILE_GOOGLE_TOKEN = "file_googleToken.dat";

    public static boolean isSupportAutoReconnection = false;
    public static boolean isSupportBroadcast = false;
    public static boolean isSupportSetting = false;
    public static boolean saveSDKLog = false;
    public static boolean isSdCardExist = true;
    public static boolean disableAudio = false;
    public static boolean enableLive = false;
    public static boolean autoDownloadAllow = false;
    public static float autoDownloadSizeLimit = 1.0f;// GB
    public static boolean isDownloading = false;
//    public static String liveAddress = "";

    public static PhotoWallLayoutType photoWallLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_GRID;
    public static int currentViewpagerPosition = 0;
    public static int curVisibleItem = 0;
    public static boolean enableSoftwareDecoder = false;
    public static boolean isBLE = false;
    public static ICatchBluetoothClient iCatchBluetoothClient;
    public static BluetoothAppDevice curBtDevice;
    public static boolean isReleaseBTClient = true;
    public static int videoCacheNum = 0;
    public static int curFps = 30;
    public static double unsteadyTime = 0.1; //s
    //    public static String LIVE_URL = "rtmp://a.rtmp.youtube.com/live2/jfbb-jmsv-6gaf-52tq";
//    public static boolean disableLive = false;
    public static String inputIp = "192.168.1.1";
    public static boolean isNeedReconnect = true;
    public static boolean enableDumpVideo = false;
    public static MyOrientoinListener.ScreenOrientation curScreenOrientation = MyOrientoinListener.ScreenOrientation.SCREEN_ORIENTATION_PORTRAIT;
    public static boolean enableRender = false;

    public static boolean isAppSentToBackground(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if(appProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(context.getPackageName())) {
                    /*
                     * BACKGROUND=400 EMPTY=500 FOREGROUND=100 GONE=1000
                     * PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                     */
                    if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        AppLog.d(TAG, "isAppSentToBackground: true");
                        return true;
                    } else {
                        AppLog.d(TAG, "isAppSentToBackground: false");
                        return false;
                    }
                }
            }
        }
        AppLog.d(TAG, "isAppSentToBackground: false");
        return false;
    }
}
