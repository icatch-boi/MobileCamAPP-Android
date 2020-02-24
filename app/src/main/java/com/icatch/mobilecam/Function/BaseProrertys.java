package com.icatch.mobilecam.Function;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.data.Hash.PropertyHashMapStatic;
import com.icatch.mobilecam.data.PropertyId.PropertyId;
import com.icatch.mobilecam.data.entity.PropertyTypeInteger;
import com.icatch.mobilecam.data.entity.PropertyTypeString;
import com.icatch.mobilecam.data.type.TimeLapseDuration;
import com.icatch.mobilecam.data.type.TimeLapseInterval;
import com.icatchtek.control.customer.type.ICatchCamProperty;

/**
 * Created by b.jiang on 2017/9/15.
 */

public class BaseProrertys {
    private String TAG = BaseProrertys.class.getSimpleName();
    private CameraProperties cameraProperty;
    private PropertyTypeInteger whiteBalance;
    private PropertyTypeInteger burst;
    private PropertyTypeInteger electricityFrequency;
    private PropertyTypeInteger dateStamp;
    private PropertyTypeInteger slowMotion;
    private PropertyTypeInteger upside;
    private PropertyTypeInteger captureDelay;
    private PropertyTypeString videoSize;
    private PropertyTypeString imageSize;
    private TimeLapseInterval timeLapseInterval;
    private TimeLapseDuration timeLapseDuration;
    private PropertyTypeInteger timeLapseMode;

    public BaseProrertys(CameraProperties cameraProperty) {
        this.cameraProperty = cameraProperty;
        PropertyHashMapStatic.getInstance().initPropertyHashMap();
        initProperty();
    }

    private void initProperty() {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "Start initProperty");
        whiteBalance = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.whiteBalanceMap, PropertyId.WHITE_BALANCE, GlobalInfo
                .getInstance().getAppContext());
        burst = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.burstMap, ICatchCamProperty.ICH_CAM_CAP_BURST_NUMBER, GlobalInfo
                .getInstance().getAppContext());
        dateStamp = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.dateStampMap, PropertyId.DATE_STAMP, GlobalInfo.getInstance()
                .getAppContext());
        slowMotion = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.slowMotionMap, PropertyId.SLOW_MOTION, GlobalInfo.getInstance()
                .getAppContext());
        upside = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.upsideMap, PropertyId.UP_SIDE, GlobalInfo.getInstance()
                .getAppContext());

        electricityFrequency = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.electricityFrequencyMap, PropertyId.LIGHT_FREQUENCY,
                GlobalInfo.getInstance().getAppContext());

        captureDelay = new PropertyTypeInteger(cameraProperty, PropertyId.CAPTURE_DELAY, GlobalInfo.getInstance().getAppContext());
        videoSize = new PropertyTypeString(cameraProperty, PropertyId.VIDEO_SIZE, GlobalInfo.getInstance().getAppContext());
        imageSize = new PropertyTypeString(cameraProperty, PropertyId.IMAGE_SIZE, GlobalInfo.getInstance().getAppContext());
        timeLapseInterval = new TimeLapseInterval(cameraProperty);
        timeLapseDuration = new TimeLapseDuration(cameraProperty);
        timeLapseMode = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.timeLapseMode, PropertyId.TIMELAPSE_MODE, GlobalInfo.getInstance()
                .getAppContext());
        AppLog.i(TAG, "End initProperty");
    }

    public PropertyTypeInteger getWhiteBalance() {
        return whiteBalance;
    }

    public PropertyTypeInteger getBurst() {
        return burst;
    }

    public PropertyTypeInteger getDateStamp() {
        return dateStamp;
    }

    public PropertyTypeInteger getCaptureDelay() {
        return captureDelay;
    }

    public PropertyTypeInteger getSlowMotion() {
        return slowMotion;
    }

    public PropertyTypeInteger getUpside() {
        return upside;
    }

    public PropertyTypeString getVideoSize() {
        return videoSize;
    }

    public PropertyTypeString getImageSize() {
        return imageSize;
    }

    public PropertyTypeInteger getElectricityFrequency() {
        return electricityFrequency;
    }

    public TimeLapseInterval getTimeLapseInterval() {
        return timeLapseInterval;
    }

    public TimeLapseDuration gettimeLapseDuration() {
        return timeLapseDuration;
    }

    public PropertyTypeInteger getTimeLapseMode() {
        return timeLapseMode;
    }
}
