package com.icatch.mobilecam.data.Hash;

import android.content.Context;

import com.icatch.mobilecam.Application.PanoramaApp;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.entity.ItemInfo;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.PropertyId.PropertyId;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatch.mobilecam.utils.ConvertTools;
import com.icatchtek.control.customer.ICatchCameraUtil;
import com.icatchtek.reliant.customer.exception.IchInvalidArgumentException;

import java.util.HashMap;
import java.util.List;

public class PropertyHashMapDynamic {
    private final String TAG = "PropertyHashMapDynamic";
    private static PropertyHashMapDynamic propertyHashMap;

    public static PropertyHashMapDynamic getInstance() {
        if (propertyHashMap == null) {
            propertyHashMap = new PropertyHashMapDynamic();
        }
        return propertyHashMap;
    }

    public HashMap<Integer, ItemInfo> getDynamicHashInt(CameraProperties cameraProperties, int propertyId) {
        switch (propertyId) {
            case PropertyId.CAPTURE_DELAY:
                return getCaptureDelayMap(cameraProperties);
            case PropertyId.AUTO_POWER_OFF:
                return getAutoPowerOffMap(cameraProperties);
            case PropertyId.EXPOSURE_COMPENSATION:
                return getExposureCompensationMap(cameraProperties);
            case PropertyId.VIDEO_FILE_LENGTH:
                return getVideoFileLengthMap(cameraProperties);
            case PropertyId.FAST_MOTION_MOVIE:
                return getFastMotionMovieMap(cameraProperties);
            case PropertyId.SCREEN_SAVER:
                return getScreenSaverMap(cameraProperties);

            default:
                return null;
        }
    }

    private HashMap<Integer, ItemInfo> getAutoPowerOffMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> autoPowerOffMap = new HashMap<Integer, ItemInfo>();

        List<Integer> autoPowerOffList = cameraProperties.getSupportedPropertyValues(PropertyId.AUTO_POWER_OFF);
        String temp;
        for (int ii = 0; ii < autoPowerOffList.size(); ii++) {
            int value = autoPowerOffList.get(ii);
            if (value == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = value + "s";
            }
            AppLog.d(TAG, "autoPowerOffList ii=" + ii + " value=" + value);
            autoPowerOffMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return autoPowerOffMap;
    }

    private HashMap<Integer, ItemInfo> getScreenSaverMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> screenSaverMap = new HashMap<Integer, ItemInfo>();
        List<Integer> screenSaverList = cameraProperties.getSupportedPropertyValues(PropertyId.SCREEN_SAVER);
        String temp;
        for (int ii = 0; ii < screenSaverList.size(); ii++) {
            int value = screenSaverList.get(ii);
            if (value == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = value + "s";
            }
            AppLog.d(TAG, "screenSaverList ii=" + ii + " value=" + value);
            screenSaverMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return screenSaverMap;
    }

    public HashMap<String, ItemInfo> getDynamicHashString(CameraProperties cameraProperties, int propertyId) {
        switch (propertyId) {
            case PropertyId.IMAGE_SIZE:
                return getImageSizeMap(cameraProperties);
            case PropertyId.VIDEO_SIZE:
                return getVideoSizeMap(cameraProperties);
            default:
                return null;
        }
    }

    private HashMap<Integer, ItemInfo> getFastMotionMovieMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> fastMotionMovieMap = new HashMap<Integer, ItemInfo>();
        List<Integer> fastMotionMovieList = cameraProperties.getSupportedPropertyValues(PropertyId.FAST_MOTION_MOVIE);
        String temp;
        for (int ii = 0; ii < fastMotionMovieList.size(); ii++) {
            int value = fastMotionMovieList.get(ii);
            if (value == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = value + "x";
            }
            AppLog.d(TAG, "fastMotionMovieList ii=" + ii + " value=" + value);
            fastMotionMovieMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return fastMotionMovieMap;
    }

    private HashMap<Integer, ItemInfo> getCaptureDelayMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> captureDelayMap = new HashMap<Integer, ItemInfo>();
        List<Integer> delyaList = cameraProperties.getSupportedPropertyValues(PropertyId.CAPTURE_DELAY);
        String temp;
        for (int ii = 0; ii < delyaList.size(); ii++) {
            if (delyaList.get(ii) == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = delyaList.get(ii) / 1000 + "S";
            }
            AppLog.d(TAG, "delyaList.get(ii) ==" + delyaList.get(ii));
            captureDelayMap.put(delyaList.get(ii), new ItemInfo(temp, temp, 0));
        }
        return captureDelayMap;
    }

    private HashMap<Integer, ItemInfo> getExposureCompensationMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> exposureCompensationMap = new HashMap<Integer, ItemInfo>();
        List<Integer> exposureCompensationList = cameraProperties.getSupportedPropertyValues(PropertyId.EXPOSURE_COMPENSATION);
//        String temp;
        for (int ii = 0; ii < exposureCompensationList.size(); ii++) {
            int value = exposureCompensationList.get(ii);
            String temp = ConvertTools.getExposureCompensation(value);

            AppLog.d(TAG, "exposureCompensationList ii=" + ii + " value=" + value + " temp=" + temp);
            exposureCompensationMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return exposureCompensationMap;
    }

    private HashMap<Integer, ItemInfo> getVideoFileLengthMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> videoFileLengthMap = new HashMap<Integer, ItemInfo>();
        List<Integer> videoFileLengthList = cameraProperties.getSupportedPropertyValues(PropertyId.VIDEO_FILE_LENGTH);
        String temp;
        Context context = PanoramaApp.getContext();
        for (int ii = 0; ii < videoFileLengthList.size(); ii++) {
            int value = videoFileLengthList.get(ii);
            if (value == 0) {
                temp = context.getString(R.string.text_file_length_unlimited);
            } else if(value < 1000){
                temp = value/60 + context.getString(R.string.time_minutes);
            } else {
                //AIBSP-1934 for CVR 20200701
                String fileSize = value/1000  + "MB";
                String fileLength;
                if(value%1000 == 0){
                    fileLength =  fileSize;
                }else {
                    fileLength =  (value%1000) /60+ context.getString(R.string.time_minutes);
                }
                temp =fileLength + " + " + fileSize;
            }

            AppLog.d(TAG, "videoFileLengthList ii=" + ii + " value=" + value);
            videoFileLengthMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return videoFileLengthMap;
    }

    private HashMap<String, ItemInfo> getImageSizeMap(CameraProperties cameraProperties) {
//        AppLog.i( TAG, "begin initImageSizeMap" );
        HashMap<String, ItemInfo> imageSizeMap = new HashMap<String, ItemInfo>();
        List<String> imageSizeList = null;
        imageSizeList = cameraProperties.getSupportedImageSizes();
        List<Integer> convertImageSizeList = null;
        try {
            convertImageSizeList = ICatchCameraUtil.convertImageSizes(imageSizeList);
        } catch (IchInvalidArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String temp = "Undefined";
        String temp1 = "Undefined";
        for (int ii = 0; ii < imageSizeList.size(); ii++) {
            if (convertImageSizeList.get(ii) == 0) {
                temp = "VGA" + "(" + imageSizeList.get(ii) + ")";
                imageSizeMap.put(imageSizeList.get(ii), new ItemInfo(temp, "VGA", 0));
            } else {
                temp = convertImageSizeList.get(ii) + "M" + "(" + imageSizeList.get(ii) + ")";
                temp1 = convertImageSizeList.get(ii) + "M";
                imageSizeMap.put(imageSizeList.get(ii), new ItemInfo(temp, temp1, 0));
            }
            AppLog.i(TAG, "imageSize =" + temp);
        }
        return imageSizeMap;
    }

    private HashMap<String, ItemInfo> getVideoSizeMap(CameraProperties cameraProperties) {
        HashMap<String, ItemInfo> videoSizeMap = new HashMap<String, ItemInfo>();
        List<String> videoSizeList = cameraProperties.getSupportedVideoSizes();
//        List<String> videoSizeList = camera.getShSetting().getCameraSettingProperty().getVideoSizeList();
        if (videoSizeList == null) {
            return videoSizeMap;
        }
        for (int ii = 0; ii < videoSizeList.size(); ii++) {
            String videoSize = videoSizeList.get(ii);
            AppLog.i(TAG, "videoSizeList_" + ii + " = " + videoSize);
            String fullName = getFullName(videoSize);
            String abbreviationSize = getAbbreviation(videoSize);
            if (fullName != null && abbreviationSize != null) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo(fullName, abbreviationSize, 0));
            }
        }
        AppLog.i(TAG, "end initVideoSizeMap videoSizeList size=" + videoSizeList.size() + " videoSizeMap size=" + videoSizeMap.size());
        return videoSizeMap;
    }

    //採用分段映射
    String getAbbreviation(String videoSize) {
        if (videoSize == null) {
            AppLog.d(TAG, "getAbbreviation videoSize is null!");
            return null;
        }
        String[] strings = videoSize.split(" ");
        if (strings == null || strings.length != 2) {
            AppLog.d(TAG, "getAbbreviation videoSize 格式不正确!");
            return null;
        }
        String size = strings[0];
        String pts = strings[1];
        String abbreviationSize = null;
        if (size.equals("1920x1080")) {
            abbreviationSize = "FHD";
        } else if (size.equals("1280x720")) {
            abbreviationSize = "HD";
        } else if (size.equals("1920x1440")) {
            abbreviationSize = "1440P";
        } else if (size.equals("2560x1280")) {
            abbreviationSize = "1280P";
        } else if (size.equals("1920x960")
                || size.equals("1440x960")
                || size.equals("1280x960") ) {
            abbreviationSize = "960P";
        } else if (size.equals("1280x640")
                || size.equals("480x640")
                || size.equals("1152x648")
        ) {
            abbreviationSize = "640P";
        } else if (size.equals("240x320")) {
            abbreviationSize = "320P";
        } else if (size.equals("320x240")) {
            abbreviationSize = "240P";
        } else if (size.equals("640x480")
                 ||size.equals("640x360")) {
            abbreviationSize = "VGA";
        } else if (size.equals("7680x4320")) {
            abbreviationSize = "8K";
        } else if (size.equals("5760x2880")
                || size.equals("5760x3240")) {
            abbreviationSize = "6K";
        } else if (size.equals("3840x2160")
                || size.equals("3840x1920")) {
            abbreviationSize = "4K";
        } else if (size.equals("2704x1524")
                || size.equals("2800x1400")
                || size.equals("2880x1440")
                || size.equals("2720x1520")
                || size.equals("2704x1400")
                || size.equals("2704x1520")) {
            abbreviationSize = "2.7K";
        } else if (size.equals("848x480")) {
            abbreviationSize = "WVGA";
            return abbreviationSize;
        }else if (size.equals("2560x1440")) {
            abbreviationSize = "1440P";
            return abbreviationSize;
        }
        if (abbreviationSize == null) {
            AppLog.d(TAG, "getAbbreviation videoSize 不支持!");
            abbreviationSize = size + " ";
        } else {
            abbreviationSize = abbreviationSize + pts;
        }
        AppLog.d(TAG, "getAbbreviation abbreviation=" + abbreviationSize);
        return abbreviationSize;
    }

    String getFullName(String videoSize) {
        if (videoSize == null) {
            AppLog.d(TAG, "getFullName videoSize is null!");
            return null;
        }
        String[] strings = videoSize.split(" ");
        if (strings == null || strings.length != 2) {
            AppLog.d(TAG, "getFullName videoSize 格式不正确!");
            return null;
        }
        String size = strings[0];
        String pts = strings[1];
//        String quality = strings[2];
        String fullName = size + " " + pts + "fps";
        AppLog.d(TAG, "getFullName fullName=" + fullName);
        return fullName;
    }

}
