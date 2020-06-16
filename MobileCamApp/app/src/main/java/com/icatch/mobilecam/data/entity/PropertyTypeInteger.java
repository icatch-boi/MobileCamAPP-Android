package com.icatch.mobilecam.data.entity;

import android.content.Context;
import android.content.res.Resources;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatch.mobilecam.data.CustomException.NullPointerException;
import com.icatch.mobilecam.data.Hash.PropertyHashMapDynamic;
import com.icatch.mobilecam.data.Mode.PreviewMode;
import com.icatch.mobilecam.data.PropertyId.PropertyId;
import com.icatch.mobilecam.data.type.SlowMotion;
import com.icatch.mobilecam.data.type.TimeLapseMode;
import com.icatch.mobilecam.data.type.Upside;
import com.icatchtek.control.customer.type.ICatchCamProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PropertyTypeInteger {
    private static final String TAG = "PropertyTypeInteger";
    private HashMap<Integer, ItemInfo> hashMap;
    private int propertyId;
    private String[] valueListString;
    private List<Integer> valueListInt;
    private Context context;
    private Resources res;
    private CameraProperties cameraProperties;

    public PropertyTypeInteger(CameraProperties cameraProperties,HashMap<Integer, ItemInfo> hashMap, int propertyId, Context context) {
        this.hashMap = hashMap;
        this.propertyId = propertyId;
        this.context = context;
        this.cameraProperties = cameraProperties;
        initItem();
    }

    public PropertyTypeInteger(CameraProperties cameraProperties,int propertyId, Context context) {
        this.propertyId = propertyId;
        this.context = context;
        this.cameraProperties = cameraProperties;
        initItem();
    }

    public void initItem() {
        // TODO Auto-generated method stub
        if (hashMap == null) {
            hashMap = PropertyHashMapDynamic.getInstance().getDynamicHashInt(cameraProperties,propertyId);
        }
        res = context.getResources();

        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                valueListInt = cameraProperties.getSupportedWhiteBalances();
                break;
            case PropertyId.CAPTURE_DELAY:
                valueListInt = cameraProperties.getSupportedCaptureDelays();
                break;
            case PropertyId.BURST_NUMBER:
                List<Integer> tempList = cameraProperties.getsupportedBurstNums();
                valueListInt = new LinkedList<>();
                for (int key:tempList
                ) {
                    if(hashMap != null && hashMap.containsKey(key)){
                        valueListInt.add(key);
                    }else {
                        AppLog.d(TAG,"Contains unsupported values BurstNums key:" + key);
                    }
                }
                break;
            case PropertyId.LIGHT_FREQUENCY:
                valueListInt = cameraProperties.getSupportedLightFrequencys();
                break;
            case PropertyId.DATE_STAMP:
                valueListInt = cameraProperties.getsupportedDateStamps();
                break;
            case PropertyId.UP_SIDE:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(Upside.UPSIDE_OFF);
                valueListInt.add(Upside.UPSIDE_ON);
                break;
            case PropertyId.SLOW_MOTION:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(SlowMotion.SLOW_MOTION_OFF);
                valueListInt.add(SlowMotion.SLOW_MOTION_ON);
                break;
            case PropertyId.TIMELAPSE_MODE:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(TimeLapseMode.TIME_LAPSE_MODE_STILL);
                valueListInt.add(TimeLapseMode.TIME_LAPSE_MODE_VIDEO);
                break;
            default:
                valueListInt = cameraProperties.getSupportedPropertyValues(propertyId);
                break;
        }
        valueListString = new String[valueListInt.size()];
        if (valueListInt != null) {
            for (int ii = 0; ii < valueListInt.size(); ii++) {
                if (hashMap.get(valueListInt.get(ii)) == null) {
                    continue;
                }
                String uiStringInSettingString = hashMap.get(valueListInt.get(ii)).uiStringInSettingString;
                if (uiStringInSettingString != null) {
                    valueListString[ii] = uiStringInSettingString;
                }else {
                    valueListString[ii] = res.getString(hashMap.get(valueListInt.get(ii)).uiStringInSetting);
                }
            }
        }
    }

    public int getCurrentValue() {
        // TODO Auto-generated method stub
        int retValue;
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                retValue = cameraProperties.getCurrentWhiteBalance();
                break;
            case PropertyId.CAPTURE_DELAY:
                retValue = cameraProperties.getCurrentCaptureDelay();
                break;
            case PropertyId.BURST_NUMBER:
                retValue = cameraProperties.getCurrentBurstNum();
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = cameraProperties.getCurrentLightFrequency();
                break;
            case PropertyId.DATE_STAMP:
                retValue = cameraProperties.getCurrentDateStamp();
                break;
            case PropertyId.UP_SIDE:
                retValue = cameraProperties.getCurrentUpsideDown();
                break;
            case PropertyId.SLOW_MOTION:
                retValue = cameraProperties.getCurrentSlowMotion();
                break;
            case PropertyId.TIMELAPSE_MODE:
                retValue = CameraManager.getInstance().getCurCamera().timeLapsePreviewMode;
                break;
            default:
                retValue = cameraProperties.getCurrentPropertyValue(propertyId);
                break;
        }
        return retValue;
    }

    public String getCurrentUiStringInSetting() {
        // TODO Auto-generated method stub
        if(hashMap == null){
            return "Unknown";
        }
        ItemInfo itemInfo = hashMap.get(getCurrentValue());
        String ret = null;
        if (itemInfo == null) {
            ret = "Unknown";
        } else {
            ret = res.getString(hashMap.get(getCurrentValue()).uiStringInSetting);
        }

        return ret;
    }

    public String getCurrentUiStringInPreview() {
        if(hashMap == null){
            return "Unknown";
        }
        ItemInfo itemInfo = hashMap.get(getCurrentValue());
        String ret = null;
        if (itemInfo == null) {
            ret = "Unknown";
        } else {
            ret = itemInfo.uiStringInPreview;
        }
        // TODO Auto-generated method stub
        return ret;
    }

    public String getCurrentUiStringInSetting(int position) {
        // TODO Auto-generated method stub
        return valueListString[position];
    }

    public int getCurrentIcon() throws NullPointerException {
        // TODO Auto-generated method stub
        ItemInfo itemInfo = hashMap.get(getCurrentValue());
        AppLog.d(TAG, "itemInfo=" + itemInfo);
        if (itemInfo == null) {
            throw new NullPointerException(TAG, "getCurrentIcon itemInfo is null", "");
        }
        return itemInfo.iconID;
    }

    public String[] getValueList() {
        // TODO Auto-generated method stub
        return valueListString;
    }

    public Boolean setValue(int value) {
        // TODO Auto-generated method stub
        boolean retValue;
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                retValue = cameraProperties.setWhiteBalance(value);
                break;
            case PropertyId.CAPTURE_DELAY:
                retValue = cameraProperties.setCaptureDelay(value);
                break;
            case PropertyId.BURST_NUMBER:
                retValue = cameraProperties.setCurrentBurst(value);
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = cameraProperties.setLightFrequency(value);
                break;
            case PropertyId.DATE_STAMP:
                retValue = cameraProperties.setDateStamp(value);
                break;
            default:
                retValue = cameraProperties.setPropertyValue(propertyId, value);
                break;
        }
        return retValue;
    }

    public Boolean setValueByPosition(int position) {
        // TODO Auto-generated method stub

        boolean retValue = false;
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                retValue = cameraProperties.setWhiteBalance(valueListInt.get(position));
                break;
            case PropertyId.CAPTURE_DELAY:
                retValue = cameraProperties.setCaptureDelay(valueListInt.get(position));
                break;
            case PropertyId.BURST_NUMBER:
                retValue = cameraProperties.setCurrentBurst(valueListInt.get(position));
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = cameraProperties.setLightFrequency(valueListInt.get(position));
                break;
            case PropertyId.DATE_STAMP:
                retValue = cameraProperties.setDateStamp(valueListInt.get(position));
                break;
            case PropertyId.UP_SIDE:
                retValue = cameraProperties.setUpsideDown(valueListInt.get(position));
                break;
            case PropertyId.SLOW_MOTION:
                retValue = cameraProperties.setSlowMotion(valueListInt.get(position));
                break;
            default:
                retValue = cameraProperties.setPropertyValue(propertyId, valueListInt.get(position));
                break;
        }
        return retValue;
    }

    public Boolean needDisplayByMode(int previewMode) {
        boolean retValue = false;
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                //retValue = cameraProperties.setWhiteBalance(valueListInt.get(position));
                if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_WHITE_BALANCE)) {
                    retValue = true;
                    break;
                }
                retValue = true;
                break;
            case PropertyId.CAPTURE_DELAY:
                if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_IMAGE_SIZE) &&
                        cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_CAPTURE_DELAY) && //IC-564
                        previewMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
                    retValue = true;
                    break;
                }
                break;
            case PropertyId.BURST_NUMBER:
                if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_BURST_NUMBER) &&
                        previewMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
                    retValue = true;
                    break;
                }
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = true;
                break;
            case PropertyId.DATE_STAMP:
                if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_DATE_STAMP)) {
                    if (previewMode == PreviewMode.APP_STATE_STILL_PREVIEW || previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
                        retValue = true;
                        break;
                    }
                }
                break;
            case PropertyId.UP_SIDE:
                if (cameraProperties.hasFuction(PropertyId.UP_SIDE)) {
                    return true;
                }
                break;
            case PropertyId.SLOW_MOTION:
                if (cameraProperties.hasFuction(PropertyId.SLOW_MOTION) &&
                        (previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW ||
                                previewMode == PreviewMode.APP_STATE_VIDEO_CAPTURE)) {
                    retValue = true;
                    break;
                }
                break;

            case PropertyId.TIMELAPSE_MODE:
                boolean supportTimelapseMode  = cameraProperties.hasFuction(PropertyId.TIMELAPSE_MODE);
                AppLog.i(TAG, "TIMELAPSE_MODE isSupportTimelapseMode=" + supportTimelapseMode);
                if (supportTimelapseMode) {
                    if (previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                        retValue = true;
                        break;
                    }
                    break;
                }
                break;
            default:
                break;
        }
        return retValue;
    }

}
