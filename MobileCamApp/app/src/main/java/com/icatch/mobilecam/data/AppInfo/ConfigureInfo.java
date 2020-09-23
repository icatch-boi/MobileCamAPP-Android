package com.icatch.mobilecam.data.AppInfo;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.Log.SdkLog;
import com.icatch.mobilecam.utils.StorageUtil;
import com.icatch.mobilecam.utils.fileutils.FileOper;
import com.icatchtek.pancam.customer.ICatchPancamConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zhang yanhu C001012 on 2015/11/17 14:01.
 */
public class ConfigureInfo {
    private static ConfigureInfo configureInfo;
    private static final String TAG = "ConfigureInfo";
    private final String[] cfgTopic = {
            "AppVersion=" + AppInfo.APP_VERSION,
            "SupportAutoReconnection=false",
            "SaveStreamVideo=false",
            "SaveStreamAudio=false",
            "broadcast=false",
            "SupportSetting=false",
            "SaveAppLog=true",
            "SaveSDKLog=true",
            "disconnectRetry=3",
            "enableSoftwareDecoder=false",
            "disableAudio=false",
            "enableLive=false",
            "enableSdkRender=true",
            "enableInterExtHeadCheck=true"
    };

    private ConfigureInfo() {

    }

    public static ConfigureInfo getInstance() {
        if (configureInfo == null) {
            configureInfo = new ConfigureInfo();
        }
        return configureInfo;
    }

    public void initCfgInfo(Context context) {
        AppLog.d(TAG, "readCfgInfo..........");
        FileOper.createDirectory( StorageUtil.getRootPath(context) + AppInfo.DOWNLOAD_PATH_PHOTO );
        FileOper.createDirectory( StorageUtil.getRootPath(context) + AppInfo.DOWNLOAD_PATH_VIDEO );
        String directoryPath = context.getExternalCacheDir() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
        AppLog.d(TAG, "readCfgInfo..........directoryPath=" + directoryPath);
        String fileName = AppInfo.PROPERTY_CFG_FILE_NAME;
        String info = "";
        for (int ii = 0; ii < cfgTopic.length; ii++) {
            info = info + cfgTopic[ii] + "\n";
        }
        File file = new File(directoryPath + fileName);
        if (file.exists() == false) {
            FileOper.createFile(directoryPath, fileName);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(directoryPath + fileName);
                out.write(info.getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //如果配置文件存在，判断是否需要替换旧版的配置文件;
        else {
            CfgProperty cfgInfo = new CfgProperty(directoryPath + fileName);
            String cfgVersion = null;
            try {
                cfgVersion = cfgInfo.getProperty("AppVersion");
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (cfgVersion != null) {
                AppLog.d(TAG, "cfgVersion..........=" + cfgVersion);
                if (!cfgVersion.equals(AppInfo.APP_VERSION)) {
                    writeCfgInfo(directoryPath + fileName, info);
                }
                AppLog.d(TAG, "cfgVersion=" + cfgVersion + " appVersion=" + AppInfo.APP_VERSION);
            } else {
                writeCfgInfo(directoryPath + fileName, info);
                AppLog.d(TAG, "cfgVersion=" + cfgVersion + " appVersion=" + AppInfo.APP_VERSION);
            }
        }

        CfgProperty cfgInfo = new CfgProperty(directoryPath + fileName);
        String AutoReconnection = null;
        String saveStreamVideo = null;
        String saveStreamAudio = null;
        String broadcast = null;
        String supportSetting = null;
        String saveAppLog = null;
        String saveSDKLog = null;
        String disconnectRetry = null;
        String enableSoftwareDecoder = null;
        String disableAudio = null;
        String enableLive = null;
        String enableRender = null;
        String enableInterExtHeadCheck = null;
        try {
            AutoReconnection = cfgInfo.getProperty("SupportAutoReconnection");
            saveStreamVideo = cfgInfo.getProperty("SaveStreamVideo");
            saveStreamAudio = cfgInfo.getProperty("SaveStreamAudio");
            broadcast = cfgInfo.getProperty("broadcast");
            supportSetting = cfgInfo.getProperty("SupportSetting");
            saveAppLog = cfgInfo.getProperty("SaveAppLog");
            saveSDKLog = cfgInfo.getProperty("SaveSDKLog");
            disconnectRetry = cfgInfo.getProperty("disconnectRetry");
            enableSoftwareDecoder = cfgInfo.getProperty("enableSoftwareDecoder");
            disableAudio = cfgInfo.getProperty("disableAudio");
            enableLive = cfgInfo.getProperty("enableLive");
            enableRender = cfgInfo.getProperty("enableSdkRender");
            enableInterExtHeadCheck = cfgInfo.getProperty("enableInterExtHeadCheck");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String streamOutputPath = Environment.getExternalStorageDirectory().toString() + AppInfo.STREAM_OUTPUT_DIRECTORY_PATH;
        FileOper.createDirectory(streamOutputPath);
       // ICatchCameraConfig.getInstance().enableDumpMediaStream(false, streamOutputPath);
//        try {
//            ICatchPancamConfig.getInstance().enableDumpTransportStream(false, streamOutputPath);
//        } catch (IchNoSuchPathException e) {
//            e.printStackTrace();
//        } catch (IchPermissionDeniedException e) {
//            e.printStackTrace();
//        }
        if (saveAppLog != null) {
            if (saveAppLog.equals("true")) {
                AppLog.enableAppLog();
            }
            AppLog.i(TAG, "GlobalInfo.saveAppLog..........=" + saveAppLog);
        }


        if (saveSDKLog != null) {
            if (saveSDKLog.equals("true")) {
                AppInfo.saveSDKLog = true;
                SdkLog.getInstance().enableSDKLog();
            }
            AppLog.i(TAG, "GlobalInfo.saveSDKLog..........=" + AppInfo.saveSDKLog);
        }

        if (enableInterExtHeadCheck != null) {
            if (enableInterExtHeadCheck.equals("true")) {
                ICatchPancamConfig.getInstance().setExtHeadCheck(true);
            } else {
                ICatchPancamConfig.getInstance().setExtHeadCheck(false);
            }
            AppLog.i(TAG, "enableInterExtHeadCheck=" + enableInterExtHeadCheck);
        }

        if (enableRender != null) {
            //sdk 渲染，
            if (enableRender.equals("true") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AppInfo.enableRender = true;
            } else {
                AppInfo.enableRender = false;
            }
            AppLog.i(TAG, "AppInfo.enableRender..........=" + AppInfo.enableRender);
        }

        if (AutoReconnection != null) {
            if (AutoReconnection.equals("true")) {
                AppInfo.isSupportAutoReconnection = true;
            } else {
                AppInfo.isSupportAutoReconnection = false;
            }
            AppLog.i(TAG, " end isSupportAutoReconnection = " + AppInfo.isSupportAutoReconnection);
        }

        if (saveStreamVideo != null) {
//            nullif (saveStreamVideo.equals("true")) {
//                AppLog.d(TAG, "saveStreamVideo..........=" + true);
//                ICatchCameraConfig.getInstance().enableDumpMediaStream(true, streamOutputPath);
//                // save video
//            }
            if (saveStreamVideo.equals("true")) {
//                ICatchCameraConfig.getInstance().enableDumpMediaStream(true, streamOutputPath);
                AppInfo.enableDumpVideo = true;
            } else {
                AppInfo.enableDumpVideo = false;
            }
        }
        if (saveStreamAudio != null) {
            if (saveStreamAudio.equals("true")) {
                AppLog.d(TAG, "enableDumpMediaStream..........=" + false);
                //ICatchCameraConfig.getInstance().enableDumpMediaStream(false, streamOutputPath);
                // save audio
            }
        }
        if (broadcast != null) {
            AppLog.d(TAG, "broadcast..........=" + broadcast);
            if (broadcast.equals("true")) {
                AppLog.d(TAG, "broadcast..........=" + broadcast);
                AppInfo.isSupportBroadcast = true;
            }
            AppLog.d(TAG, "GlobalInfo.isSupportBroadcast..........=" + AppInfo.isSupportBroadcast);
        }
        if (supportSetting != null) {
            AppLog.i(TAG, "supportSetting..........=" + supportSetting);
            if (supportSetting.equals("true")) {
                AppLog.i(TAG, "supportSetting..........=" + supportSetting);
                AppInfo.isSupportSetting = true;
            }
            AppLog.i(TAG, "GlobalInfo.isSupportSetting..........=" + AppInfo.isSupportSetting);
        }

        AppLog.d(TAG, "disconnectRetry=" + disconnectRetry);
        if (disconnectRetry != null) {
            int retryCount = Integer.parseInt(disconnectRetry);
            //ICatchWificamConfig.getInstance().setConnectionCheckParam(retryCount, 15);
            AppLog.d(TAG, "retryCount=" + retryCount);
        }

        if (enableSoftwareDecoder != null) {
            if (enableSoftwareDecoder.equals("true")) {
                ICatchPancamConfig.getInstance().setSoftwareDecoder(true);
                AppInfo.enableSoftwareDecoder = true;
            } else {
                ICatchPancamConfig.getInstance().setSoftwareDecoder(false);
                AppInfo.enableSoftwareDecoder = false;
            }
        }
        AppLog.d(TAG, "open enableSoftwareDecoder:" + enableSoftwareDecoder);
        AppLog.i(TAG, "disableAudio=" + disableAudio);
        if (disableAudio != null) {
            if (disableAudio.equals("true")) {
                AppInfo.disableAudio = true;
            } else {
                AppInfo.disableAudio = false;
            }
            AppLog.i(TAG, "AppInfo.disableAudio..........=" + AppInfo.disableAudio);
        }

        AppLog.i(TAG, "enableLive=" + enableLive);
        if (enableLive != null) {
            if (enableLive.equals("true")) {
                AppInfo.enableLive = true;
            } else {
                AppInfo.enableLive = false;
            }
            AppLog.i(TAG, "AppInfo.enableLive..........=" + AppInfo.enableLive);
        }
    }

    private void writeCfgInfo(String path, String cfgInfo) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(cfgInfo.getBytes(), 0, cfgInfo.getBytes().length);
            out.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            AppLog.i(TAG, "end writeCfgInfo :IOException ");
            e1.printStackTrace();
        }
    }
}
