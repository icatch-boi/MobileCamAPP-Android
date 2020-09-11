/**
 * Added by zhangyanhu C01012,2014-6-27
 */
package com.icatch.mobilecam.SdkApi;

import android.util.Log;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.MyCamera.CommandSession;
import com.icatchtek.control.customer.ICatchCameraAssist;
import com.icatchtek.control.customer.ICatchCameraControl;
import com.icatchtek.control.customer.ICatchCameraListener;
import com.icatchtek.control.customer.exception.IchCameraModeException;
import com.icatchtek.control.customer.exception.IchCaptureImageException;
import com.icatchtek.control.customer.exception.IchDevicePropException;
import com.icatchtek.control.customer.exception.IchStorageFormatException;
import com.icatchtek.reliant.customer.exception.IchDeviceException;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchListenerExistsException;
import com.icatchtek.reliant.customer.exception.IchListenerNotExistsException;
import com.icatchtek.reliant.customer.exception.IchNotSupportedException;
import com.icatchtek.reliant.customer.exception.IchSocketException;
import com.icatchtek.reliant.customer.exception.IchTimeOutException;

public class CameraAction {
    private static final String TAG = "CameraAction";
    private ICatchCameraControl cameraControl;
    public ICatchCameraAssist cameraAssist;

    public CameraAction(ICatchCameraControl control,ICatchCameraAssist assist) {
        this.cameraControl = control;
        this.cameraAssist = assist;
    }

    public boolean capturePhoto() {
        AppLog.i(TAG, "begin doStillCapture");
        boolean ret = false;
        try {
            ret = cameraControl.capturePhoto();
        } catch (Exception e) {
            AppLog.e(TAG, "Exception e:" +e.getClass().getSimpleName());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end doStillCapture ret = " + ret);
        return ret;
    }

    public boolean triggerCapturePhoto() {
        AppLog.i(TAG, "begin triggerCapturePhoto");
        boolean ret = false;
        try {
            ret = cameraControl.triggerCapturePhoto();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCaptureImageException e) {
            AppLog.e(TAG, "IchCaptureImageException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end triggerCapturePhoto ret = " + ret);
        return ret;
    }

    public boolean startMovieRecord() {
        AppLog.i(TAG, "begin startVideoCapture");
        boolean retVal = false;

        try {
            int ret = cameraControl.startMovieRecord();
            if(ret >= 0){
                retVal = true;
            }
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end startVideoCapture ret =" + retVal);
        return retVal;
    }

    public boolean startTimeLapse() {
        AppLog.i(TAG, "begin startTimeLapse");
        boolean ret = false;

        try {
            ret = cameraControl.startTimeLapse();
        } catch (Exception e) {
            AppLog.d(TAG, "Exception e:" + e.getClass().getSimpleName());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end startTimeLapse ret =" + ret);
        return ret;
    }

    public boolean stopTimeLapse() {
        AppLog.i(TAG, "begin stopMovieRecordTimeLapse");
        boolean ret = false;

        try {
            ret = cameraControl.stopTimeLapse();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end stopMovieRecordTimeLapse ret =" + ret);
        return ret;
    }

    public boolean stopVideoCapture() {
        AppLog.i(TAG, "begin stopVideoCapture");
        boolean retVal = false;

        try {
            int ret = cameraControl.stopMovieRecord();
            if(ret == 0){
                retVal = true;
            }
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //暂时规避,调用两次
        AppLog.i(TAG, "end stopVideoCapture ret =" + retVal);
        return retVal;
    }

    public boolean formatStorage() {
        AppLog.i(TAG, "begin formatSD");
        boolean retVal = false;

        try {
            int ret = cameraControl.formatStorage();
            if(ret == 0){
                retVal = true;
            }
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStorageFormatException e) {
            AppLog.e(TAG, "IchStorageFormatException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "begin formatSD retVal =" + retVal);
        return retVal;
    }

    public boolean sleepCamera() {
        AppLog.i(TAG, "begin sleepCamera");
        boolean retValue = false;
        try {
            try {
                retValue = cameraControl.toStandbyMode();
            } catch (IchDeviceException e) {
                // TODO Auto-generated catch block
                AppLog.e(TAG, "IchDeviceException");
                e.printStackTrace();
            } catch (IchInvalidSessionException e) {
                AppLog.e(TAG, "IchInvalidSessionException");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end sleepCamera retValue =" + retValue);
        return retValue;
    }

    public boolean addCustomEventListener(int eventID, ICatchCameraListener listener) {
        AppLog.i(TAG, "begin addEventListener eventID=" + eventID);
        boolean retValue = false;
        try {
            retValue = cameraControl.addCustomEventListener(eventID, listener);
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchListenerExistsException e) {
            e.printStackTrace();
        }

        AppLog.i(TAG, "end addEventListener retValue = " + retValue);
        return retValue;
    }

    public boolean delCustomEventListener(int eventID, ICatchCameraListener listener) {
        AppLog.i(TAG, "begin delEventListener eventID=" + eventID);
        if(cameraControl == null){
            return false;
        }
        boolean retValue = false;
        try {
            retValue = cameraControl.delCustomEventListener(eventID, listener);
        } catch (IchListenerNotExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end delEventListener retValue = " + retValue);
        return retValue;
    }

    public boolean addEventListener(int eventID, ICatchCameraListener listener) {
        AppLog.i(TAG, "begin addEventListener eventID=" + eventID);

        boolean retValue = false;
        try {

            retValue = cameraControl.addEventListener(eventID, listener);

        } catch (IchListenerExistsException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchListenerExistsException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end addEventListener retValue = " + retValue);
        return retValue;
    }

    public boolean delEventListener(int eventID, ICatchCameraListener listener) {
        AppLog.i(TAG, "begin delEventListener eventID=" + eventID);
        if(cameraControl == null){
            return false;
        }
        boolean retValue = false;
        try {
            retValue = cameraControl.delEventListener(eventID, listener);
        } catch (IchListenerNotExistsException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchListenerExistsException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end delEventListener retValue = " + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-2
     */
    public String getCameraMacAddress() {
        // TODO Auto-generated method stub
        String macAddress = "";
        macAddress = cameraControl.getMacAddress();
        return macAddress;
    }

    public boolean zoomIn() {
        AppLog.i(TAG, "begin zoomIn");
        boolean retValue = false;
        try {
            retValue = cameraControl.zoomIn();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStorageFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end zoomIn retValue = " + retValue);
        return retValue;
    }

    public boolean zoomOut() {
        AppLog.i(TAG, "begin zoomOut");
        boolean retValue = false;
        try {
            retValue = cameraControl.zoomOut();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStorageFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end zoomOut retValue = " + retValue);
        return retValue;
    }

    public boolean updateFW(String fileName) {
        boolean ret = false;
        AppLog.i(TAG, "begin update FW");
        CommandSession mSDKSession = CameraManager.getInstance().getCurCamera().getSDKsession();
        try {
            ret = cameraAssist.updateFw(mSDKSession.getSDKSession(), fileName);
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
            e.printStackTrace();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchDevicePropException");
            e.printStackTrace();
        } catch (IchTimeOutException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchTimeOutException");
            e.printStackTrace();
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchDeviceException");
            e.printStackTrace();
        } catch (IchNotSupportedException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchNotSupportedException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end updateFW ret=" + ret);
        return ret;
    }

    public static boolean addGlobalEventListener(int iCatchEventID, ICatchCameraListener listener, Boolean forAllSession) {
        boolean retValue = false;
        try {
            retValue = ICatchCameraAssist.addEventListener(iCatchEventID, listener, forAllSession);
        } catch (IchListenerExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG,"addGlobalEventListener id=" + iCatchEventID + " retValue=" + retValue);
        return retValue;
    }

    public static boolean delGlobalEventListener(int iCatchEventID, ICatchCameraListener listener, Boolean forAllSession) {
        boolean retValue = false;
        try {
            retValue = ICatchCameraAssist.delEventListener(iCatchEventID, listener, forAllSession);
        } catch (IchListenerNotExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retValue;
    }

    public boolean previewMove(int xshift, int yshfit) {
        AppLog.i(TAG, "begin previewMove");
        boolean ret = false;
        ret = cameraControl.pan(xshift, yshfit);
        AppLog.i(TAG, "end previewMove ret = " + ret);
        return ret;
        //return true;
    }

    public boolean resetPreviewMove() {
        AppLog.i(TAG, "begin resetPreviewMove");
        boolean ret = false;
        ret = cameraControl.panReset();
        AppLog.i(TAG, "end resetPreviewMove ret = " + ret);
        return ret;
        //return true;
    }

    public boolean changePreviewMode(int mode){
        AppLog.i(TAG, "begin changePreviewMode mode:" + mode);
        boolean ret = false;
        try {
            ret = cameraControl.changePreviewMode(mode);
        } catch (IchCameraModeException e) {
            e.printStackTrace();
        }
        AppLog.i(TAG, "end changePreviewMode ret = " + ret);
        return ret;
    }
}
