package com.icatch.mobilecam.MyCamera;

import android.util.Log;

import com.icatch.mobilecam.Log.AppLog;
import com.icatchtek.control.customer.ICatchCameraSession;
import com.icatchtek.reliant.customer.exception.IchInvalidArgumentException;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchTransportException;
import com.icatchtek.reliant.customer.transport.ICatchITransport;

public class CommandSession {
    private static final String TAG = CommandSession.class.getSimpleName();
    private static int scanflag;
    private final static String tag = "CommandSession";
    private ICatchCameraSession session;
    private String ipAddress;
    private String uid;
    private String username;
    private String password;
    private boolean sessionPrepared = false;

    public CommandSession(String ipAddress, String uid, String username, String password) {
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        this.uid = uid;
    }

    public CommandSession() {
    }

    public boolean prepareSession(ICatchITransport itrans) {
        // TODO Auto-generated constructor stub
        try {
            ICatchCameraSession.getCameraConfig(itrans).enablePTPIP();
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        }
        sessionPrepared = true;
        session = ICatchCameraSession.createSession();
        boolean retValue = false;
        try {
            retValue = session.prepareSession(itrans);
        } catch (IchTransportException e) {
            AppLog.d(tag, "IchTransportException");
            e.printStackTrace();
        }
        sessionPrepared = retValue;
        AppLog.e(tag, "preparePanoramaSession =" + sessionPrepared);
        return retValue;
    }

    public boolean prepareSession(ICatchITransport itrans,boolean enablePTPIP) {
        // TODO Auto-generated constructor stub
        AppLog.d(TAG, "start prepareSession itrans="+ itrans + " enablePTPIP=" +enablePTPIP);
        if (enablePTPIP) {
            try {
                ICatchCameraSession.getCameraConfig(itrans).enablePTPIP();
            } catch (IchInvalidArgumentException e) {
                AppLog.e(tag, "enablePTPIP IchInvalidArgumentException");
                e.printStackTrace();
            }
        } else {
            try {
                ICatchCameraSession.getCameraConfig(itrans).disablePTPIP();
            } catch (IchInvalidArgumentException e) {
                AppLog.e(tag, "disablePTPIP IchInvalidArgumentException");
                e.printStackTrace();
            }
        }

        sessionPrepared = true;
        AppLog.d(tag, "start createSession");
        session = ICatchCameraSession.createSession();
        boolean retValue = false;
        try {
            retValue = session.prepareSession(itrans);
        } catch (IchTransportException e) {
            e.printStackTrace();
        }
        if (retValue == false) {
            AppLog.e(tag, "failed to preparePanoramaSession");
            sessionPrepared = false;
            Log.v("1111", "CommandSession,preparePanoramaSession fail!");
        }
        AppLog.d(tag, "end preparePanoramaSession ret=" + sessionPrepared);
        return sessionPrepared;
    }

    public ICatchCameraSession getSDKSession() {
        AppLog.d(TAG, "getSDKSession =" + session);
        return session;
    }

    public boolean checkWifiConnection() {
        AppLog.i(tag, "Start checkWifiConnection");
        boolean retValue = false;

        try {
            retValue = session.checkConnection();
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        }

        AppLog.i(tag, "End checkWifiConnection,retValue=" + retValue);
        return retValue;
    }

    public boolean destroySession() {
        AppLog.i(tag, "Start destroyPanoramaSession");
        Boolean retValue = false;
        try {
            retValue = session.destroySession();
            AppLog.i(tag, "End  destroyPanoramaSession,retValue=" + retValue);
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retValue;
    }

    public static boolean startDeviceScan() {
        AppLog.i(tag, "Start startDeviceScan");
        boolean tempStartDeviceScanValue = false;
//        boolean tempStartDeviceScanValue = ICatchCameraSession.startDeviceScan();

        AppLog.i(tag, "End startDeviceScan,tempStartDeviceScanValue=" + tempStartDeviceScanValue);
        if (tempStartDeviceScanValue) {
            scanflag = 1;
        }
        return tempStartDeviceScanValue;
    }

    public static void stopDeviceScan() {
        AppLog.i(tag, "Start stopDeviceScan");
        boolean tempStopDeviceScanValue = false;
        if (scanflag == 1) {
//            tempStopDeviceScanValue = ICatchCameraSession.stopDeviceScan();
        } else {
            tempStopDeviceScanValue = true;
        }
        scanflag = 0;
        AppLog.i(tag, "End stopDeviceScan,tempStopDeviceScanValue=" + tempStopDeviceScanValue);
    }
}
