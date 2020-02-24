package com.icatch.mobilecam.data.entity;

import android.content.Context;

import com.icatch.mobilecam.data.Hash.PropertyHashMapDynamic;
import com.icatch.mobilecam.data.Mode.PreviewMode;
import com.icatch.mobilecam.data.PropertyId.PropertyId;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatchtek.control.customer.type.ICatchCamProperty;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class PropertyTypeString {

    private int propertyId;
    private List<String> valueListString;
    private List<String> valueListStringUI;
    private HashMap<String, ItemInfo> hashMap;
    private String[] valueArrayString;
    private CameraProperties cameraProperties;

    public PropertyTypeString(CameraProperties cameraProperties,int propertyId, Context context) {
        this.propertyId = propertyId;
        this.cameraProperties = cameraProperties;
        initItem();
    }

    public void initItem() {
        // TODO Auto-generated method stub
        if (hashMap == null) {
            hashMap = PropertyHashMapDynamic.getInstance().getDynamicHashString(cameraProperties,propertyId);
        }
        if(hashMap == null){
            return;
        }
        if (propertyId == PropertyId.IMAGE_SIZE) {
            valueListString = cameraProperties.getSupportedImageSizes();
        }
        if (propertyId == PropertyId.VIDEO_SIZE) {
            valueListString = cameraProperties.getSupportedVideoSizes();
        }
        for (int ii = 0; ii < valueListString.size(); ii++) {
            if (hashMap.containsKey(valueListString.get(ii)) == false) {
                valueListString.remove(ii);
                ii--;
            }
        }
        valueListStringUI = new LinkedList<String>();
        valueArrayString = new String[valueListString.size()];
        if (valueListString != null) {
            for (int ii = 0; ii < valueListString.size(); ii++) {
                valueListStringUI.add(ii, hashMap.get(valueListString.get(ii)).uiStringInSettingString);
                valueArrayString[ii] = hashMap.get(valueListString.get(ii)).uiStringInSettingString;
            }
        }

    }

    public String getCurrentValue() {
        // TODO Auto-generated method stub
        return cameraProperties.getCurrentStringPropertyValue(propertyId);
    }

    public String getCurrentUiStringInSetting() {
        if(hashMap == null){
            return "Unknown";
        }
        ItemInfo itemInfo = hashMap.get(getCurrentValue());
        String ret = null;
        if (itemInfo == null) {
            ret = "Unknown";
        } else {
            ret = itemInfo.uiStringInSettingString;
        }
        return ret;
    }

    public String getCurrentUiStringInPreview() {
        // TODO Auto-generated method stub
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
        return ret;
    }

    public String getCurrentUiStringInSetting(int position) {
        // TODO Auto-generated method stub
        return valueListString.get(position);
    }

    public List<String> getValueList() {
        // TODO Auto-generated method stub
        return valueListString;
    }

    public List<String> getValueListUI() {
        // TODO Auto-generated method stub
        return valueListString;
    }

    public Boolean setValue(String value) {
        // TODO Auto-generated method stub
        return cameraProperties.setStringPropertyValue(propertyId, value);
    }

    public boolean setValueByPosition(int position) {
        return cameraProperties.setStringPropertyValue(propertyId,
                valueListString.get(position));
    }

    public String[] getValueArrayString() {
        return valueArrayString;
    }

    public Boolean needDisplayByMode(int previewMode) {
        boolean retValue = false;
        switch (propertyId) {
            case PropertyId.IMAGE_SIZE:
                //retValue = cameraProperties.setWhiteBalance(valueListInt.get(position));
                if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_IMAGE_SIZE)) {
                    if (previewMode == PreviewMode.APP_STATE_STILL_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                        retValue = true;
                        break;
                    }
                }
                break;
            case PropertyId.VIDEO_SIZE:
                if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_VIDEO_SIZE)) {
                    if (previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_VIDEO_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_VIDEO_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                        retValue = true;
                        break;
                    }
                }
                break;
            default:
                break;
        }
        return retValue;
    }
}
