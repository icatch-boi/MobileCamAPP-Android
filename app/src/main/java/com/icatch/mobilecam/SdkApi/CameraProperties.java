/**
 * Added by zhangyanhu C01012,2014-6-23
 */
package com.icatch.mobilecam.SdkApi;

import android.util.Log;

import com.icatch.mobilecam.DataConvert.BurstConvert;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.PropertyId.PropertyId;
import com.icatchtek.control.customer.ICatchCameraControl;
import com.icatchtek.control.customer.ICatchCameraProperty;
import com.icatchtek.control.customer.exception.IchCameraModeException;
import com.icatchtek.control.customer.exception.IchDevicePropException;
import com.icatchtek.control.customer.exception.IchNoSDCardException;
import com.icatchtek.control.customer.type.ICatchCamLightFrequency;
import com.icatchtek.reliant.customer.exception.IchDeviceException;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchSocketException;
import com.icatchtek.reliant.customer.type.ICatchCodec;
import com.icatchtek.reliant.customer.type.ICatchVideoFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CameraProperties {
    private final String tag = "CameraProperties";
    private List<Integer> fuction;
    // private PreviewStream previewStream = new PreviewStream();
    private List<Integer> modeList;
    private ICatchCameraProperty cameraProperty;
    private ICatchCameraControl cameraAction;
    private List<ICatchVideoFormat> resolutionList;

    public CameraProperties(ICatchCameraProperty cameraProperty, ICatchCameraControl cameraAction) {
        this.cameraAction = cameraAction;
        this.cameraProperty = cameraProperty;
    }

    public List<String> getSupportedImageSizes() {
        List<String> list = null;
        try {
            list = cameraProperty.getSupportedImageSizes();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getSupportedImageSizes list.size =" + list.size());
        if (list != null) {
            for (int ii = 0; ii < list.size(); ii++) {
                AppLog.i(tag, "image size ii=" + ii + " size=" + list.get(ii));
            }
        }
        return list;
    }

    public List<String> getSupportedVideoSizes() {
        AppLog.i(tag, "begin getSupportedVideoSizes");
        List<String> list = null;
        try {
            list = cameraProperty.getSupportedVideoSizes();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "begin getSupportedVideoSizes size =" + list.size());
        return list;
    }

    public List<Integer> getSupportedWhiteBalances() {
        AppLog.i(tag, "begin getSupportedWhiteBalances");
        List<Integer> list = null;
        try {
            list = cameraProperty.getSupportedWhiteBalances();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getSupportedWhiteBalances list.size() =" + list.size());
        return list;
    }

    public List<Integer> getSupportedCaptureDelays() {
        AppLog.i(tag, "begin getSupportedCaptureDelays");
        List<Integer> list = null;
        try {
            list = cameraProperty.getSupportedCaptureDelays();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getSupportedCaptureDelays list.size() =" + list.size());
        for (Integer temp : list
                ) {
            AppLog.i(tag, "end getSupportedCaptureDelays list value=" + temp);
        }
        return list;
    }

    public List<Integer> getSupportedLightFrequencys() {
        AppLog.i(tag, "begin getSupportedLightFrequencys");
        List<Integer> list = null;

        try {
            list = cameraProperty.getSupportedLightFrequencies();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // delete LIGHT_FREQUENCY_AUTO because UI don't need this option
        for (int ii = 0; ii < list.size(); ii++) {
            if (list.get(ii) == ICatchCamLightFrequency.ICH_CAM_LIGHT_FREQUENCY_AUTO) {
                list.remove(ii);
            }
        }
        AppLog.i(tag, "end getSupportedLightFrequencys list.size() =" + list.size());
        return list;
    }

    public boolean setImageSize(String value) {
        AppLog.i(tag, "begin setImageSize set value =" + value);
        boolean retVal = false;

        try {
            retVal = cameraProperty.setImageSize(value);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setImageSize retVal=" + retVal);
        return retVal;
    }

    public boolean setVideoSize(String value) {
        AppLog.i(tag, "begin setVideoSize set value =" + value);
        boolean retVal = false;

        try {
            retVal = cameraProperty.setVideoSize(value);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setVideoSize retVal=" + retVal);
        return retVal;
    }

    public boolean setWhiteBalance(int value) {
        AppLog.i(tag, "begin setWhiteBalanceset value =" + value);
        boolean retVal = false;
        if (value < 0 || value == 0xff) {
            return false;
        }
        try {
            retVal = cameraProperty.setWhiteBalance(value);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setWhiteBalance retVal=" + retVal);
        return retVal;
    }

    public boolean setLightFrequency(int value) {
        AppLog.i(tag, "begin setLightFrequency set value =" + value);
        boolean retVal = false;
        if (value < 0 || value == 0xff) {
            return false;
        }
        try {
//			retVal = cameraProperty.setLightFrequency(value);
            retVal = cameraProperty.setLightFrequency(value);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setLightFrequency retVal=" + retVal);
        return retVal;
    }

    public String getCurrentImageSize() {
        AppLog.i(tag, "begin getCurrentImageSize");
        String value = "unknown";

        try {
            value = cameraProperty.getCurrentImageSize();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentImageSize value =" + value);
        return value;
    }

    public String getCurrentVideoSize() {
        AppLog.i(tag, "begin getCurrentVideoSize");
        String value = "unknown";

        try {
            value = cameraProperty.getCurrentVideoSize();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentVideoSize value =" + value);
        return value;
    }

    public int getCurrentWhiteBalance() {
        AppLog.i(tag, "begin getCurrentWhiteBalance");
        int value = 0xff;
        try {
            value = cameraProperty.getCurrentWhiteBalance();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentWhiteBalance retvalue =" + value);
        return value;
    }

    public int getCurrentLightFrequency() {
        AppLog.i(tag, "begin getCurrentLightFrequency");
        int value = 0xff;
        try {
            value = cameraProperty.getCurrentLightFrequency();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentLightFrequency value =" + value);
        return value;
    }

    public boolean setCaptureDelay(int value) {
        AppLog.i(tag, "begin setCaptureDelay set value =" + value);
        boolean retVal = false;

        try {
            AppLog.i(tag, "start setCaptureDelay ");
            retVal = cameraProperty.setCaptureDelay(value);
            AppLog.i(tag, "end setCaptureDelay ");
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setCaptureDelay retVal =" + retVal);
        return retVal;
    }

    public int getCurrentCaptureDelay() {
        AppLog.i(tag, "begin getCurrentCaptureDelay");
        int retVal = 0;

        try {
            retVal = cameraProperty.getCurrentCaptureDelay();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentCaptureDelay retVal =" + retVal);
        return retVal;
    }

    public int getCurrentDateStamp() {
        AppLog.i(tag, "begin getCurrentDateStampType");
        int retValue = 0;
        try {
            retValue = cameraProperty.getCurrentDateStamp();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "getCurrentDateStampType retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-4-1
     */
    public boolean setDateStamp(int dateStamp) {
        AppLog.i(tag, "begin setDateStampType set value = " + dateStamp);
        Boolean retValue = false;
        try {
            retValue = cameraProperty.setDateStamp(dateStamp);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentVideoSize retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-4-1
     */
    public List<Integer> getDateStampList() {
        AppLog.i(tag, "begin getDateStampList");
        List<Integer> list = null;
        try {
            list = cameraProperty.getSupportedDateStamps();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getDateStampList list.size ==" + list.size());
        return list;
    }

    public List<Integer> getSupportFuction() {
        AppLog.i(tag, "begin getSupportFuction");
        List<Integer> fuction = null;
        // List<Integer> temp = null;
        try {
            fuction = cameraProperty.getSupportedProperties();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getSupportFuction fuction=" + fuction);
        return fuction;
    }

    /**
     * to prase the burst number Added by zhangyanhu C01012,2014-2-10
     */
    public int getCurrentBurstNum() {
        AppLog.i(tag, "begin getCurrentBurstNum");
        int number = 0xff;
        try {
            number = cameraProperty.getCurrentBurstNumber();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "getCurrentBurstNum num =" + number);
        return number;
    }

    public int getCurrentAppBurstNum() {
        AppLog.i(tag, "begin getCurrentAppBurstNum");
        int number = 0xff;
        try {
            number = cameraProperty.getCurrentBurstNumber();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        number = BurstConvert.getInstance().getBurstConverFromFw(number);
        AppLog.i(tag, "getCurrentAppBurstNum num =" + number);
        return number;
    }

    public boolean setCurrentBurst(int burstNum) {
        AppLog.i(tag, "begin setCurrentBurst set value = " + burstNum);
        if (burstNum < 0 || burstNum == 0xff) {
            return false;
        }
        boolean retValue = false;
        try {
            retValue = cameraProperty.setBurstNumber(burstNum);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setCurrentBurst retValue =" + retValue);
        return retValue;
    }

    public int getRemainImageNum() {
        AppLog.i(tag, "begin getRemainImageNum");
        int num = 0;
        try {
            num = cameraAction.getFreeSpaceInImages();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchNoSDCardException e) {
            AppLog.e(tag, "IchNoSDCardException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getRemainImageNum num =" + num);
        return num;
    }

    public int getRecordingRemainTime() {
        AppLog.i(tag, "begin getRecordingRemainTimeInt");
        int recordingTime = 0;

        try {
            recordingTime = cameraAction.getRemainRecordingTime();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchNoSDCardException e) {
            e.printStackTrace();
        }
        AppLog.i(tag, "end getRecordingRemainTimeInt recordingTime =" + recordingTime);
        return recordingTime;
    }

    public boolean isSDCardExist() {
        AppLog.i(tag, "begin isSDCardExist");
        Boolean isReady = false;
        try {
            isReady = cameraAction.isSDCardExist();
        } catch (IchSocketException e) {
            AppLog.e(tag,
                    "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag,
                    "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag,
                    "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDeviceException e) {
            AppLog.e(tag, "IchDeviceException");
            e.printStackTrace();
        }
        AppLog.i(tag, "end isSDCardExist isReady =" + isReady);
        return isReady;

        //return GlobalInfo.getInstance().getCurrentCamera().isSdCardReady();
        // JIRA ICOM-1577 End:Modify by b.jiang C01063 2015-07-17
    }

    public int getBatteryElectric() {
        AppLog.i(tag, "start getBatteryElectric");
        int electric = 0;
        try {
            electric = cameraAction.getCurrentBatteryLevel();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getBatteryElectric electric =" + electric);
        return electric;
    }

    public boolean supportVideoPlayback() {
        AppLog.i(tag, "begin hasVideoPlaybackFuction");
        boolean retValue = false;
        try {
            retValue = cameraAction.supportVideoPlayback();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchNoSDCardException e) {
            AppLog.e(tag, "IchNoSDCardException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "hasVideoPlaybackFuction retValue =" + retValue);
        return retValue;
        // return false;
    }

    public boolean cameraModeSupport(int mode) {
        AppLog.i(tag, "begin cameraModeSupport  mode=" + mode);
        Boolean retValue = false;
        if (modeList == null) {
            modeList = getSupportedModes();
        }
//        modeList = getSupportedModes();
        if (modeList.contains(mode)) {
            retValue = true;
        }
        AppLog.i(tag, "end cameraModeSupport retValue =" + retValue);
        return retValue;
    }

    public String getCameraMacAddress() {
        AppLog.i(tag, "begin getCameraMacAddress macAddress macAddress ");
        String macAddress = cameraAction.getMacAddress();
        AppLog.i(tag, "end getCameraMacAddress macAddress =" + macAddress);
        return macAddress;
    }

    public boolean hasFuction(int fuc) {
        AppLog.i(tag, "begin hasFuction query fuction = " + fuc);
        if (fuction == null) {
            fuction = getSupportFuction();
        }
        Boolean retValue = false;
        if (fuction.contains(fuc) == true) {
            retValue = true;
        }
        AppLog.i(tag, "end hasFuction retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-4
     */
    public List<Integer> getsupportedDateStamps() {
        AppLog.i(tag, "begin getsupportedDateStamps");
        List<Integer> list = null;

        try {
            list = cameraProperty.getSupportedDateStamps();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getsupportedDateStamps list.size() =" + list.size());
        return list;
    }

    public List<Integer> getsupportedBurstNums() {
        // TODO Auto-generated method stub
        AppLog.i(tag, "begin getsupportedBurstNums");
        List<Integer> list = null;

        try {
            list = cameraProperty.getSupportedBurstNumbers();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getsupportedBurstNums list.size() =" + list.size());
        if(list != null && !list.isEmpty()){
            for (int size:list
                 ) {
                AppLog.i(tag, "end getsupportedBurstNums size=" + size);
            }
        }
        return list;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-4
     */
    public List<Integer> getSupportedFrequencies() {
        // TODO Auto-generated method stub
        AppLog.i(tag, "begin getSupportedFrequencies");
        List<Integer> list = null;
        try {
            list = cameraProperty.getSupportedLightFrequencies();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getSupportedFrequencies list.size() =" + list.size());
        return list;
    }

    /**
     * Added by zhangyanhu C01012,2014-8-21
     *
     * @return
     */
    public List<Integer> getSupportedModes() {
        AppLog.i(tag, "begin getSupportedModes");

        List<Integer> list = null;
        try {
            list = cameraAction.getSupportedModes();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getSupportedModes list =" + list);
        return list;
    }

    public List<Integer> getSupportedTimeLapseDurations() {
        AppLog.i(tag, "begin getSupportedTimeLapseDurations");
        List<Integer> list = null;
        // boolean retValue = false;
        try {
            list = cameraProperty.getSupportedTimeLapseDurations();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int ii = 0; ii < list.size(); ii++) {
            AppLog.i(tag, "list.get(ii) =" + list.get(ii));
        }
        AppLog.i(tag, "end getSupportedTimeLapseDurations list =" + list.size());
        return list;
    }

    public List<Integer> getSupportedTimeLapseIntervals() {
        AppLog.i(tag, "begin getSupportedTimeLapseIntervals");
        List<Integer> list = null;
        // boolean retValue = false;
        try {
            list = cameraProperty.getSupportedTimeLapseIntervals();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int ii = 0; ii < list.size(); ii++) {
            AppLog.i(tag, "list.get(ii) =" + list.get(ii));
        }
        AppLog.i(tag, "end getSupportedTimeLapseIntervals list =" + list.size());
        return list;
    }

    public boolean setTimeLapseDuration(int timeDuration) {
        AppLog.i(tag, "begin setTimeLapseDuration videoDuration =" + timeDuration);
        boolean retVal = false;
        if (timeDuration < 0 || timeDuration == 0xff) {
            return false;
        }
        try {
            retVal = cameraProperty.setTimeLapseDuration(timeDuration);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setTimeLapseDuration retVal=" + retVal);
        return retVal;
    }

    public int getCurrentTimeLapseDuration() {
        AppLog.i(tag, "begin getCurrentTimeLapseDuration");
        int retVal = 0xff;
        try {
            retVal = cameraProperty.getCurrentTimeLapseDuration();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentTimeLapseDuration retVal=" + retVal);
        return retVal;
    }

    public boolean setTimeLapseInterval(int timeInterval) {
        AppLog.i(tag, "begin setTimeLapseInterval videoDuration =" + timeInterval);
        boolean retVal = false;
//		if (timeInterval < 0 || timeInterval == 0xff) {
//			return false;
//		}
        try {
            retVal = cameraProperty.setTimeLapseInterval(timeInterval);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setTimeLapseInterval retVal=" + retVal);
        return retVal;
    }

    public int getCurrentTimeLapseInterval() {
        AppLog.i(tag, "begin getCurrentTimeLapseInterval");
        int retVal = 0xff;
        try {
            retVal = cameraProperty.getCurrentTimeLapseInterval();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentTimeLapseInterval retVal=" + retVal);
        return retVal;
    }

    public float getMaxZoomRatio() {
        AppLog.i(tag, "start getMaxZoomRatio");
        float retValue = 0;
        try {
            retValue = cameraProperty.getMaxZoomRatio() / 10.0f;
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getMaxZoomRatio retValue =" + retValue);
        return retValue;
    }

    public float getCurrentZoomRatio() {
        AppLog.i(tag, "start getCurrentZoomRatio");
        float retValue = 0;
        try {
            retValue = cameraProperty.getCurrentZoomRatio() / 10.0f;
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentZoomRatio retValue =" + retValue);
        return retValue;
    }

    public int getCurrentUpsideDown() {
        AppLog.i(tag, "start getCurrentUpsideDown");
        int retValue = 0;
        try {
            retValue = cameraProperty.getCurrentUpsideDown();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentUpsideDown retValue =" + retValue);
        return retValue;
    }

    public boolean setUpsideDown(int upside) {
        AppLog.i(tag, "start setUpsideDown upside = " + upside);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setUpsideDown(upside);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setUpsideDown retValue =" + retValue);
        return retValue;
    }

    public int getCurrentSlowMotion() {
        AppLog.i(tag, "start getCurrentSlowMotion");
        int retValue = 0;
        try {
            retValue = cameraProperty.getCurrentSlowMotion();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentSlowMotion retValue =" + retValue);
        return retValue;
    }

    public boolean setSlowMotion(int slowMotion) {
        AppLog.i(tag, "start setSlowMotion slowMotion = " + slowMotion);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setSlowMotion(slowMotion);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setSlowMotion retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraDate() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd HHmmss");
        String tempDate = myFmt.format(date);
        tempDate = tempDate.replaceAll(" ", "T");
        tempDate = tempDate + ".0";
        AppLog.i(tag, "start setCameraDate date = " + tempDate);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.CAMERA_DATE, tempDate);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setCameraDate retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraEssidName(String ssidName) {
        AppLog.i(tag, "start setCameraEssidName date = " + ssidName);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.ESSID_NAME, ssidName);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setCameraEssidName retValue =" + retValue);
        return retValue;
    }

    public String getCameraEssidName() {
        AppLog.i(tag, "start getCameraEssidName");
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(PropertyId.ESSID_NAME);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCameraEssidName retValue =" + retValue);
        return retValue;
    }

    public String getCameraEssidPassword() {
        AppLog.i(tag, "start getCameraEssidPassword");
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(PropertyId.ESSID_PASSWORD);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCameraEssidPassword retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraEssidPassword(String ssidPassword) {
        AppLog.i(tag, "start setStringPropertyValue date = " + ssidPassword);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.ESSID_PASSWORD, ssidPassword);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setCameraSsid retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraSsid(String ssid) {
        AppLog.i(tag, "start setCameraSsid date = " + ssid);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.CAMERA_ESSID, ssid);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setCameraSsid retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraName(String cameraName) {
        AppLog.i(tag, "start setStringPropertyValue cameraName = " + cameraName);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.CAMERA_NAME, cameraName);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setStringPropertyValue retValue =" + retValue);
        return retValue;
    }

    public String getCameraName() {
        AppLog.i(tag, "start getCameraName");
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(PropertyId.CAMERA_NAME);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCameraName retValue =" + retValue);
        return retValue;
    }

    public String getCameraName(ICatchCameraProperty cameraConfiguration1) {
        AppLog.i(tag, "start getCameraName");
        String retValue = null;
        try {
            retValue = cameraConfiguration1.getCurrentStringPropertyValue(PropertyId.CAMERA_NAME);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCameraName retValue =" + retValue);
        return retValue;
    }

    public String getCameraPasswordNew() {
        AppLog.i(tag, "start getCameraPassword");
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(PropertyId.CAMERA_PASSWORD_NEW);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCameraPassword retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraPasswordNew(String cameraNamePassword) {
        AppLog.i(tag, "start setCameraPasswordNew cameraName = " + cameraNamePassword);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.CAMERA_PASSWORD_NEW, cameraNamePassword);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setCameraPasswordNew retValue =" + retValue);
        return retValue;
    }

    public String getCameraSsid() {
        AppLog.i(tag, "start getCameraSsid date = ");
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(PropertyId.CAMERA_ESSID);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCameraSsid retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraPassword(String password) {
        AppLog.i(tag, "start setCameraSsid date = " + password);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.CAMERA_PASSWORD, password);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setCameraSsid retValue =" + retValue);
        return retValue;
    }

    public String getCameraPassword() {
        AppLog.i(tag, "start getCameraPassword date = ");
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(PropertyId.CAMERA_PASSWORD);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCameraPassword retValue =" + retValue);
        return retValue;
    }

    public boolean setCaptureDelayMode(int value) {
        AppLog.i(tag, "start setCaptureDelayMode value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setPropertyValue(PropertyId.CAPTURE_DELAY_MODE, value);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setCaptureDelayMode retValue =" + retValue);
        return retValue;
    }

    public int getVideoRecordingTime() {
        AppLog.i(tag, "start getRecordingTime");
        int retValue = 0;
        try {
            // JIRA ICOM-1608 Begin:Modify by b.jiang C01063 2015-07-20
            // 0xD7F0 -> PropertyId.VIDEO_RECORDING_TIME
            // retValue = cameraProperty.getCurrentPropertyValue(0xD7F0);
            retValue = cameraProperty.getCurrentPropertyValue(PropertyId.VIDEO_RECORDING_TIME);
            // JIRA ICOM-1608 End:Modify by b.jiang C01063 2015-07-20
        } catch (Exception e) {
            AppLog.e(tag, "Exception e:" + e.getClass().getSimpleName());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getRecordingTime retValue =" + retValue);
        return retValue;
    }

    public boolean setServiceEssid(String value) {
        AppLog.i(tag, "start setServiceEssid value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.SERVICE_ESSID, value);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setServiceEssid retValue =" + retValue);
        return retValue;
    }

    public boolean setServicePassword(String value) {
        AppLog.i(tag, "start setServicePassword value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(PropertyId.SERVICE_PASSWORD, value);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setServicePassword retValue =" + retValue);
        return retValue;
    }

    public boolean notifyFwToShareMode(int value) {
        AppLog.i(tag, "start notifyFwToShareMode value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setPropertyValue(PropertyId.NOTIFY_FW_TO_SHARE_MODE, value);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end notifyFwToShareMode retValue =" + retValue);
        return retValue;
    }

    public List<Integer> getSupportedPropertyValues(int propertyId) {
        AppLog.i(tag, "begin getSupportedPropertyValues propertyId =" + propertyId);
        List<Integer> list = null;
        try {
            list = cameraProperty.getSupportedPropertyValues(propertyId);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getSupportedPropertyValues list.size() =" + list.size());
        return list;
    }

    public int getCurrentPropertyValue(int propertyId) {
        AppLog.i(tag, "start getCurrentPropertyValue propertyId = " + propertyId);
        int retValue = 0;
        try {
            retValue = cameraProperty.getCurrentPropertyValue(propertyId);
        } catch (Exception e) {
            AppLog.e(tag, "Exception e:" + e.getClass().getSimpleName());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentPropertyValue retValue =" + retValue);
        return retValue;
    }

    public String getCurrentStringPropertyValue(int propertyId) {
        AppLog.i(tag, "start getCurrentStringPropertyValue propertyId = " + propertyId);
        String retValue = null;
        try {
            retValue = cameraProperty.getCurrentStringPropertyValue(propertyId);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentStringPropertyValue retValue =" + retValue);
        return retValue;
    }

    public boolean setPropertyValue(int propertyId, int value) {
        AppLog.i(tag, "start setPropertyValue propertyId=" + propertyId + " value=" + value);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setPropertyValue(propertyId, value);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setPropertyValue retValue =" + retValue);
        return retValue;
    }

    public boolean setStringPropertyValue(int propertyId, String value) {
        AppLog.i(tag, "start setStringPropertyValue propertyId=" + propertyId + " value=[" + value + "]");
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStringPropertyValue(propertyId, value);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setStringPropertyValue retValue =" + retValue);
        return retValue;
    }

    public int getVideoSizeFlow() {
        AppLog.i(tag, "start getVideoSizeFlow");
        int retValue = 0;
        try {
            retValue = cameraProperty.getCurrentPropertyValue(PropertyId.VIDEO_SIZE_FLOW);
        } catch (Exception e) {
            AppLog.e(tag, "Exception e:" + e.getClass().getSimpleName());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getVideoSizeFlow retValue =" + retValue);
        return retValue;
    }

    public boolean notifyCameraConnectChnage(int value) {
        AppLog.i(tag, "start notifyCameraConnectChnage value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraProperty.setPropertyValue(PropertyId.CAMERA_CONNECT_CHANGE, value);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end notifyCameraConnectChnage retValue =" + retValue);
        return retValue;
    }

    public List<ICatchVideoFormat> getResolutionList() {
        AppLog.i(tag, "start getResolution");
        if (resolutionList == null) {
            try {
                resolutionList = cameraProperty.getSupportedStreamingInfos();
            } catch (Exception e) {
                AppLog.e(tag, "Exception e:" + e.getMessage());
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        AppLog.i(tag, "end getResolution retList=" + resolutionList);
        for (ICatchVideoFormat temp : resolutionList
                ) {
            AppLog.i(tag, "end getResolution format=" + temp);
        }
        return resolutionList;
    }

    public List<ICatchVideoFormat> getResolutionList(int codecType) {
        AppLog.i(tag, "start getResolution");
        if (resolutionList == null) {
            try {
                resolutionList = cameraProperty.getSupportedStreamingInfos();
            } catch (Exception e) {
                AppLog.e(tag, "Exception e:" + e.getMessage());
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        AppLog.i(tag, "end getResolution retList=" + resolutionList);
        List<ICatchVideoFormat> tempList = new LinkedList<>();
        for (ICatchVideoFormat temp : resolutionList) {
            if (temp.getCodec() == codecType) {
                tempList.add(temp);
            }
            AppLog.i(tag, "end getResolution format=" + temp);
        }
        return tempList;
    }

    public String getBestResolution() {
        AppLog.i(tag, "start getBestResolution");
        String bestResolution = null;

        List<ICatchVideoFormat> tempList = getResolutionList();
        if (tempList == null || tempList.size() == 0) {
            return null;
        }
        Log.d("1111", "getResolutionList() tempList.size() = " + tempList.size());
        int tempWidth = 0;
        int tempHeigth = 0;

        ICatchVideoFormat temp;

        for (int ii = 0; ii < tempList.size(); ii++) {
            temp = tempList.get(ii);
            if (temp.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                if (bestResolution == null) {
                    bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                }

                if (temp.getVideoW() == 640 && temp.getVideoH() == 360) {
                    bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                    return bestResolution;
                } else if (temp.getVideoW() == 640 && temp.getVideoH() == 480) {
                    if (tempWidth != 640) {
                        bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = 640;
                        tempHeigth = 480;
                    }
                } else if (temp.getVideoW() == 720) {
                    if (tempWidth != 640) {
                        if (temp.getVideoW() * 9 == temp.getVideoH() * 16)// 16:9优先
                        {
                            bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                            tempWidth = 720;
                            tempHeigth = temp.getVideoH();
                        } else if (temp.getVideoW() * 3 == temp.getVideoH() * 4)// 4:3
                        {
                            if (tempWidth != 720)
                                bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                            tempWidth = 720;
                            tempHeigth = temp.getVideoH();
                        }
                    }
                } else if (temp.getVideoW() < tempWidth) {
                    if (temp.getVideoW() * 9 == temp.getVideoH() * 16)// 16:9优先
                    {
                        bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = temp.getVideoW();
                        tempHeigth = temp.getVideoH();
                    } else if (temp.getVideoW() * 3 == temp.getVideoH() * 4)// 4:3
                    {
                        if (tempWidth != temp.getVideoW())
                            bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = temp.getVideoW();
                        tempHeigth = temp.getVideoH();
                    }
                }
            }
        }
        if (bestResolution != null) {
            return bestResolution;
        }
        for (int ii = 0; ii < tempList.size(); ii++) {
            temp = tempList.get(ii);
            if (temp.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
                if (bestResolution == null) {
                    bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                }

                if (temp.getVideoW() == 640 && temp.getVideoH() == 360) {
                    bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                    return bestResolution;
                } else if (temp.getVideoW() == 640 && temp.getVideoH() == 480) {
                    if (tempWidth != 640) {
                        bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = 640;
                        tempHeigth = 480;
                    }
                } else if (temp.getVideoW() == 720) {
                    if (tempWidth != 640) {
                        if (temp.getVideoW() * 9 == temp.getVideoH() * 16)// 16:9优先
                        {
                            bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                            tempWidth = 720;
                            tempHeigth = temp.getVideoH();
                        } else if (temp.getVideoW() * 3 == temp.getVideoH() * 4)// 4:3
                        {
                            if (tempWidth != 720)
                                bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                            tempWidth = 720;
                            tempHeigth = temp.getVideoH();
                        }
                    }
                } else if (temp.getVideoW() < tempWidth) {
                    if (temp.getVideoW() * 9 == temp.getVideoH() * 16)// 16:9优先
                    {
                        bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = temp.getVideoW();
                        tempHeigth = temp.getVideoH();
                    } else if (temp.getVideoW() * 3 == temp.getVideoH() * 4)// 4:3
                    {
                        if (tempWidth != temp.getVideoW())
                            bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = temp.getVideoW();
                        tempHeigth = temp.getVideoH();
                    }
                }
            }
        }

        AppLog.i(tag, "end getBestResolution");
        return bestResolution;

    }

    public String getFWDefaultResolution() {
        AppLog.i(tag, "start getFWDefaultResolution");
        String resolution = null;
        ICatchVideoFormat retValue = null;
        try {
            retValue = cameraProperty.getCurrentStreamingInfo();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (retValue != null) {
            if (retValue.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                resolution = "H264?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate() + "&";
            } else if (retValue.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
                resolution = "MJPG?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate() + "&";
            }
        }
        AppLog.i(tag, "end getFWDefaultResolution");
        return resolution;

    }

    public boolean setStreamingInfo(ICatchVideoFormat iCatchVideoFormat) {
        AppLog.i(tag, "start setStreamingInfo");
        boolean retValue = false;
        try {
            retValue = cameraProperty.setStreamingInfo(iCatchVideoFormat);
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end setStreamingInfo");
        return retValue;

    }

    public String getCurrentStreamInfo() {
        AppLog.i(tag, "start getCurrentStreamInfo cameraProperty=" + cameraProperty);

        ICatchVideoFormat retValue = null;
        String bestResolution = null;
        try {
            retValue = cameraProperty.getCurrentStreamingInfo();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getCurrentStreamInfo retValue = " + retValue);
        if (retValue == null) {
            return null;
        }
        if (hasFuction(0xd7ae)) {
            if (retValue.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                bestResolution = "H264?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate() + "&FPS="
                        + retValue.getFrameRate() + "&";
            } else if (retValue.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
                bestResolution = "MJPG?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate() + "&FPS="
                        + retValue.getFrameRate() + "&";
            }
        } else {
            if (retValue.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                bestResolution = "H264?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate();
            } else if (retValue.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
                bestResolution = "MJPG?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate();
            }
        }

        AppLog.i(tag, "end getCurrentStreamInfo bestResolution =" + bestResolution);
        return bestResolution;
    }

    public int getPreviewCacheTime() {
        AppLog.i(tag, "start getPreviewCacheTime");
        int retValue = 0;
        try {
            retValue = cameraProperty.getPreviewCacheTime();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(tag, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getPreviewCacheTime retValue =" + retValue);
        return retValue;
    }

    public boolean isSupportPreview() {
        AppLog.i(tag, "start getRecordingTime");
        int retValue = 0;
        boolean isSupport = false;
        try {
            retValue = cameraProperty.getCurrentPropertyValue(PropertyId.SUPPORT_PREVIEW);
        } catch (Exception e) {
            AppLog.e(tag, "Exception:" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.i(tag, "end getRecordingTime retValue =" + retValue);
        if (retValue == 0) {
            isSupport = false;
        } else {
            isSupport = true;
        }
        return isSupport;
    }
}
