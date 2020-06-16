package com.icatch.mobilecam.data.GlobalApp;

import android.app.Activity;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.MyCamera.MyCamera;
import com.icatch.mobilecam.SdkApi.FileOperation;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhang yanhu C001012 on 2015/11/18 11:30.
 */
public class ExitApp {
    private final static String TAG = "ExitApp";
    private LinkedList<Activity> activityList = new LinkedList<Activity>();
    private static ExitApp instance;

    public static ExitApp getInstance() {
        if (instance == null) {
            instance = new ExitApp();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        if (activityList.contains(activity) == false) {
            activityList.addFirst(activity);
            AppLog.d(TAG, "addActivity activity=" + activity.getClass().getSimpleName());
            AppLog.d(TAG, "addActivity activityList size=" + activityList.size());
        }
    }

    public void removeActivity(Activity activity) {
        activityList.remove(activity);
        AppLog.d(TAG, "removeActivity activity=" + activity.getClass().getSimpleName());
        AppLog.d(TAG, "removeActivity activityList size=" + activityList.size());
    }

    public void exit() {
//        List<MyCamera> cameraList = GlobalInfo.getInstance().getCameraList();
//        if(cameraList != null && cameraList.isEmpty() == false){
//            PreviewStream previewStream = PreviewStream.getInstance();
//            FileOperation fileOperation = FileOperation.getInstance();
//            VideoPlayback videoPlayback = VideoPlayback.getInstance();
//            for (MyCamera camera : cameraList) {
//                if (camera.getSDKsession().isSessionOK() == true) {
//                    fileOperation.cancelDownload(camera.getplaybackClient());
//                    previewStream.stopMediaStream(camera.getpreviewStreamClient());
//                    videoPlayback.stopPlaybackStream(camera.getVideoPlaybackClint());
//                    camera.disconnectCamera();
//                }
//            }
//        }
//        Collections.reverse(activityList);
        AppLog.i(TAG, "start exit activity activityList size=" + activityList.size());
        if (activityList != null && activityList.isEmpty() == false) {
            for (Activity activity : activityList) {
                activity.finish();
            }
            activityList.clear();
        }
        MyCamera curCamera = CameraManager.getInstance().getCurCamera();
        if(curCamera != null && curCamera.isConnected()){
            curCamera.disconnect();
        }
        AppLog.i(TAG, "end exit System.exit");
        AppLog.refreshAppLog();
        System.exit(0);
    }

    public void finishAllActivity() {
        List<MyCamera> cameraList = GlobalInfo.getInstance().getCameraList();
        if (cameraList != null && cameraList.isEmpty() == false) {
            for (MyCamera camera : cameraList) {
                if (camera.isConnected() == true) {
                    FileOperation fileOperation = camera.getFileOperation();
                    fileOperation.cancelDownload();
                    camera.disconnect();
                }
            }
        }

        AppLog.i(TAG, "start finsh activity");
        if (activityList != null && activityList.isEmpty() == false) {
            for (Activity activity : activityList) {
                activity.finish();
            }
            activityList.clear();
        }
        AppLog.refreshAppLog();
    }
}
