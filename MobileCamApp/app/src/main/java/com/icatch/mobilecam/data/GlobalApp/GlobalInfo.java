package com.icatch.mobilecam.data.GlobalApp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.icatch.mobilecam.Function.SDKEvent;
import com.icatch.mobilecam.Listener.ScreenListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.entity.LocalPbItemInfo;
import com.icatch.mobilecam.utils.WifiCheck;
import com.icatchtek.control.customer.type.ICatchCamEventID;

import java.util.List;

/**
 * Created by zhang yanhu C001012 on 2015/11/13 10:55.
 */
public class GlobalInfo {
    private final static String TAG = "GlobalInfo";
    private static GlobalInfo instance;
    private Activity activity;
    private SDKEvent sdkEvent;
    private List<LocalPbItemInfo> localPhotoList;
    private List<LocalPbItemInfo> localVideoList;
    private WifiCheck wifiCheck;
    ScreenListener listener;
    private OnEventListener onEventListener;

    public static GlobalInfo getInstance() {
        if (instance == null) {
            instance = new GlobalInfo();
        }
        return instance;
    }

    public void setOnEventListener(OnEventListener onEventListener) {
        this.onEventListener = onEventListener;
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

    public List<LocalPbItemInfo> getLocalPhotoList() {
        return localPhotoList;
    }

    public void setLocalPhotoList(List<LocalPbItemInfo> localPhotoList) {
        this.localPhotoList = localPhotoList;
    }

    public void setLocalVideoList(List<LocalPbItemInfo> localVideoList) {
        this.localVideoList = localVideoList;
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
                    //AppDialog.showDialogWarn( activity, R.string.dialog_card_removed );
                    if(onEventListener!=null){
                        onEventListener.eventListener(SDKEvent.EVENT_SDCARD_REMOVED);
                    }
                    break;
                case SDKEvent.EVENT_SDCARD_INSERT:
                    AppLog.i( TAG, "receive EVENT_SDCARD_INSERT" );
                    //AppDialog.showDialogWarn( activity, R.string.dialog_card_removed );
                    if(onEventListener!=null){
                        onEventListener.eventListener(SDKEvent.EVENT_SDCARD_INSERT);
                    }
                    break;

                default:
                    break;
            }
        }
    };

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
    public void delete(){
        sdkEvent = null;
    }

    public interface OnEventListener{
        void eventListener(int sdkEventId);
    }
}