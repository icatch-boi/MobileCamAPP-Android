/**
 * Added by zhangyanhu C01012,2014-7-2
 */
package com.icatch.mobilecam.SdkApi;


import com.icatch.mobilecam.Log.AppLog;
import com.icatchtek.control.customer.ICatchCameraState;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;

/**
 * Added by zhangyanhu C01012,2014-7-2
 */
public class CameraState {
    private final String tag = "CameraState";
    private ICatchCameraState cameraState;

    public CameraState(ICatchCameraState cameraState) {
        this.cameraState = cameraState;
    }

    public boolean isMovieRecording() {
        AppLog.i(tag, "begin isMovieRecording");
        boolean retValue = false;
        try {
            retValue = cameraState.isMovieRecording();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchInvalidSessionException");
            e.printStackTrace();
        }
        AppLog.i(tag, "end isMovieRecording retValue=" + retValue);
        return retValue;
    }

    public boolean isTimeLapseVideoOn() {
        AppLog.i(tag, "begin isTimeLapseVideoOn");
        boolean retValue = false;
        try {
            retValue = cameraState.isTimeLapseVideoOn();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchInvalidSessionException");
            e.printStackTrace();
        }
        AppLog.i(tag, "end isTimeLapseVideoOn retValue=" + retValue);
        return retValue;
    }

    public boolean isTimeLapseStillOn() {
        AppLog.i(tag, "begin isTimeLapseStillOn");
        boolean retValue = false;
        try {
            retValue = cameraState.isTimeLapseStillOn();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchInvalidSessionException");
            e.printStackTrace();
        }
        AppLog.i(tag, "end isTimeLapseStillOn retValue=" + retValue);
        return retValue;
    }

    public boolean isSupportImageAutoDownload() {
        AppLog.i(tag, "begin isSupportImageAutoDownload");
        boolean retValue = false;
        try {
            retValue = cameraState.supportImageAutoDownload();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end isSupportImageAutoDownload = " + retValue);
        return retValue;
    }
}
