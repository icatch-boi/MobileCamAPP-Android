package com.icatch.mobilecam.data.GlobalApp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

import com.icatch.mobilecam.ui.appdialog.AppDialog;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.entity.LocalPbItemInfo;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.Listener.ScreenListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.Function.SDKEvent;
import com.icatch.mobilecam.MyCamera.MyCamera;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.utils.WifiCheck;
import com.icatchtek.control.customer.type.ICatchCamEventID;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhang yanhu C001012 on 2015/11/13 10:55.
 */
public class GlobalInfo {
    private final static String TAG = "GlobalInfo";
    private static GlobalInfo instance;
    private Activity activity;
    private List<MyCamera> cameraList;
    private SDKEvent sdkEvent;
    private List<MultiPbItemInfo> remotePhotoList;
    public List<MultiPbItemInfo> remoteVideoList;
    private List<MultiPbItemInfo> remoteCommonPhotoList;
    private List<MultiPbItemInfo> remotePanoramaPhotoList;
    public LruCache<Integer, Bitmap> mLruCache;
    private List<LocalPbItemInfo> localPhotoList;
    private List<LocalPbItemInfo> localVideoList;
    private List<LocalPbItemInfo> localCommonPhotoList;
    private List<LocalPbItemInfo> localPanoramaPhotoList;
    private String ssid;
    private WifiCheck wifiCheck;
    private HashMap<Integer, Integer> panoramaPhotoPositionMap;
    private HashMap<Integer, Integer> commonPhotoPositionMap;
    ScreenListener listener;
    private HashMap<Integer, Integer> videoPositionMap;


    public static GlobalInfo getInstance() {
        if (instance == null) {
            instance = new GlobalInfo();
        }
        return instance;
    }

    public Context getAppContext() {
        return (Context) activity;
    }

    public void setCurrentApp(Activity activity) {
        this.activity = activity;
    }

    public Activity getCurrentApp() {
        return activity;
    }

    public List<MyCamera> getCameraList() {
        return cameraList;
    }

    public List<LocalPbItemInfo> getLocalPhotoList() {
        return localPhotoList;
    }

    public void setLocalPhotoList(List<LocalPbItemInfo> localPhotoList) {
        this.localPhotoList = localPhotoList;
//        initLocalPhotoListInfo();
    }

    public List<LocalPbItemInfo> getLocalVideoList() {
        return localVideoList;
    }

    public void setLocalVideoList(List<LocalPbItemInfo> localVideoList) {
        this.localVideoList = localVideoList;
//        initLocalPhotoListInfo();
    }


    public List<MultiPbItemInfo> getRemotePhotoList() {
        return remotePhotoList;
    }

    public void setRemotePhotoList(List<MultiPbItemInfo> remotePhotoList) {
        this.remotePhotoList = remotePhotoList;
    }

    public void initLocalPhotoListInfo() {
        if (localPhotoList == null || localPhotoList.isEmpty()) {
            return;
        }
        localCommonPhotoList = new LinkedList<LocalPbItemInfo>();
        localPanoramaPhotoList = new LinkedList<LocalPbItemInfo>();
        panoramaPhotoPositionMap = new HashMap<Integer, Integer>();
        commonPhotoPositionMap = new HashMap<Integer, Integer>();
        int jj = 0, kk = 0;
        for (int ii = 0; ii < localPhotoList.size(); ii++) {
            if (localPhotoList.get( ii ).isPanorama()) {
                localPanoramaPhotoList.add( localPhotoList.get( ii ) );
                panoramaPhotoPositionMap.put( ii, jj++ );
            } else {
                localCommonPhotoList.add( localPhotoList.get( ii ) );
                commonPhotoPositionMap.put( ii, kk++ );
            }
        }
    }

    public HashMap<Integer, Integer> getPanoramaPhotoPositionMap() {
        return panoramaPhotoPositionMap;
    }

    public HashMap<Integer, Integer> getCommonPhotoPositionMap() {
        return commonPhotoPositionMap;
    }

    public void initRemotePhotoListInfo() {
        if (remotePhotoList == null || remotePhotoList.isEmpty()) {
            return;
        }
        remoteCommonPhotoList = new LinkedList<MultiPbItemInfo>();
        remotePanoramaPhotoList = new LinkedList<MultiPbItemInfo>();
        panoramaPhotoPositionMap = new HashMap<Integer, Integer>();
        commonPhotoPositionMap = new HashMap<Integer, Integer>();
        int jj = 0, kk = 0;
        for (int ii = 0; ii < remotePhotoList.size(); ii++) {
            if (remotePhotoList.get( ii ).isPanorama()) {
                remotePanoramaPhotoList.add( remotePhotoList.get( ii ) );
                panoramaPhotoPositionMap.put( ii, jj++ );
            } else {
                remoteCommonPhotoList.add( remotePhotoList.get( ii ) );
                commonPhotoPositionMap.put( ii, kk++ );
            }
        }
    }

    public List<MultiPbItemInfo> getRemoteCommonPhotoList() {
        return remoteCommonPhotoList;
    }

    public List<MultiPbItemInfo> getRemotePanoramaPhotoList() {
        return remotePanoramaPhotoList;
    }

    public void startScreenListener() {
        listener = new ScreenListener( getCurrentApp() );
        listener.begin( new ScreenListener.ScreenStateListener() {

            @Override
            public void onUserPresent() {
                AppLog.i( TAG, "onUserPresent" );
            }

            @Override
            public void onScreenOn() {
                AppLog.i( TAG, "onScreenOn" );

            }

            @Override
            public void onScreenOff() {
                AppLog.i( TAG, "onScreenOff,need to close app!" );
                //ExitApp.getInstance().exitWhenScreenOff();
                ExitApp.getInstance().finishAllActivity();
            }
        } );
    }

    public void endSceenListener() {
        if (listener != null) {
            listener.unregisterListener();
        }
    }

    private Handler globalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDKEvent.EVENT_CONNECTION_FAILURE:
                    //need to show dialog
                    AppLog.i( TAG, "receive EVENT_CONNECTION_FAILURE" );
                    //JIAR IC-534 begin ADD by b.jiang 2016-06-20
                    wifiCheck = new WifiCheck( activity );
                    if(!AppInfo.isNeedReconnect){
                        return;
                    }
                    if (AppInfo.isSupportAutoReconnection) {
                        wifiCheck.showAutoReconnectDialog();
                    } else {
                        wifiCheck.showConectFailureWarningDlg( activity );
                    }
                    //JIAR IC-534 end ADD by b.jiang 2016-06-20
//                    AppDialog.showConectFailureWarning(activity);
                    break;
                case SDKEvent.EVENT_SDCARD_REMOVED:
                    AppLog.i( TAG, "receive EVENT_SDCARD_REMOVED" );
                    AppDialog.showDialogWarn( activity, R.string.dialog_card_removed );
            }
        }
    };

    public void enableConnectCheck(boolean enable) {
        if (sdkEvent == null) {
            sdkEvent = new SDKEvent( globalHandler );
        }
        sdkEvent.addEventListener( ICatchCamEventID.ICH_CAM_EVENT_CONNECTION_DISCONNECTED );
    }

    public void addEventListener(int eventId){
        if (sdkEvent == null) {
            sdkEvent = new SDKEvent(globalHandler);
        }
        sdkEvent.addEventListener(eventId);
    }

    public void delEventListener(int eventId){
        if (sdkEvent != null) {
            sdkEvent.delEventListener(eventId);
        }
    }

}
