package com.icatch.mobilecam.Function;

import android.os.Handler;
import android.util.Log;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.SdkApi.CameraAction;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.entity.SearchedCameraInfo;
import com.icatchtek.control.customer.ICatchCameraListener;
import com.icatchtek.control.customer.type.ICatchCamEvent;
import com.icatchtek.control.customer.type.ICatchCamEventID;

/**
 * Created by b.jiang on 2017/9/15.
 */

public class GlobalEvent {
    private String TAG = "GlobalEvent";
    public static final int EVENT_SEARCHED_NEW_CAMERA = 15;
    public static final int EVENT_SDCARD_REMOVED = 16;
    public static final int EVENT_SDCARD_INSERT = 17;
    private NoSdcardListener noSdcardListener;
    private ScanCameraListener scanCameraListener;

    private Handler handler;

    public GlobalEvent(Handler handler) {
        this.handler = handler;
    }

    public class NoSdcardListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive NoSdcardListener");
            AppInfo.isSdCardExist = false;
            handler.obtainMessage(EVENT_SDCARD_REMOVED).sendToTarget();
            AppLog.i(TAG, "receive NoSdcardListener GlobalInfo.isSdCard = " + AppInfo.isSdCardExist);
        }
    }

    public class ScanCameraListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "Send..........EVENT_SEARCHED_NEW_CAMERA");
            Log.d("1111", "get a uid arg0.getgetStringValue3() ==" + arg0.getStringValue3());
            handler.obtainMessage(EVENT_SEARCHED_NEW_CAMERA, new SearchedCameraInfo(arg0.getStringValue2(), arg0.getStringValue1(), arg0.getIntValue1(),
                    arg0.getStringValue3())).sendToTarget();
        }
    }


    public void addGlobalEventListener(int iCatchEventID, Boolean forAllSession) {
        AppLog.i(TAG, "Start addGlobalEventListener iCatchEventID=" + iCatchEventID);
        switch (iCatchEventID) {
            case ICatchCamEventID.ICATCH_EVENT_DEVICE_SCAN_ADD:
                scanCameraListener = new ScanCameraListener();
                CameraAction.addGlobalEventListener(ICatchCamEventID.ICATCH_EVENT_DEVICE_SCAN_ADD, scanCameraListener, forAllSession);
                break;
            case ICatchCamEventID.ICH_CAM_EVENT_SDCARD_REMOVED:
                noSdcardListener = new NoSdcardListener();
                CameraAction.addGlobalEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_REMOVED, noSdcardListener, forAllSession);
                break;
        }
        AppLog.i(TAG, "End addGlobalEventListener");
    }

    public void delGlobalEventListener(int iCatchEventID, Boolean forAllSession) {
        AppLog.i(TAG, "Start delGlobalEventListener iCatchEventID=" + iCatchEventID);
        switch (iCatchEventID) {
            case ICatchCamEventID.ICATCH_EVENT_DEVICE_SCAN_ADD:
                if (scanCameraListener != null) {
                    CameraAction.delGlobalEventListener(ICatchCamEventID.ICATCH_EVENT_DEVICE_SCAN_ADD, scanCameraListener, forAllSession);
                }
                break;
            case ICatchCamEventID.ICH_CAM_EVENT_SDCARD_REMOVED:
                if (noSdcardListener != null) {
                    CameraAction.delGlobalEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_REMOVED, noSdcardListener, forAllSession);
                }
                break;
        }
        AppLog.i(TAG, "End delGlobalEventListener");
    }
}
