package com.icatch.mobilecam.data.Hash;

import com.icatch.mobilecam.data.entity.ItemInfo;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.PropertyId.PropertyId;
import com.icatch.mobilecam.SdkApi.CameraProperties;
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

            default:
                return null;
        }
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

    private HashMap<Integer, ItemInfo> getCaptureDelayMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> captureDelayMap = new HashMap<Integer, ItemInfo>();
        List<Integer> delyaList = cameraProperties.getSupportedPropertyValues(PropertyId.CAPTURE_DELAY);
        String temp;
        for (int ii = 0; ii < delyaList.size(); ii++) {
            if (delyaList.get(ii) == 0) {
                temp = "OFF";
            } else {
                temp = delyaList.get(ii) / 1000 + "S";
            }
            AppLog.d(TAG, "delyaList.get(ii) ==" + delyaList.get(ii));
            captureDelayMap.put(delyaList.get(ii), new ItemInfo(temp, temp, 0));
        }
        return captureDelayMap;
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

    private HashMap<String, ItemInfo> getVideoSizeMap02(CameraProperties cameraProperties) {
        AppLog.i(TAG, "begin initVideoSizeMap");
        HashMap<String, ItemInfo> videoSizeMap = new HashMap<String, ItemInfo>();
        List<String> videoSizeList = cameraProperties.getSupportedVideoSizes();
        for (int ii = 0; ii < videoSizeList.size(); ii++) {
            AppLog.i(TAG, "videoSizeList_" + ii + " = " + videoSizeList.get(ii));
            if (videoSizeList.get(ii).equals("1920x1080 30")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1920x1080 30fps", "FHD30", 0));
                // cs[1] = "FHD";
            } else if (videoSizeList.get(ii).equals("1920x1080 60")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1920x1080 60fps", "FHD60", 0));
                // cs[1] = "FHD";
            } else if (videoSizeList.get(ii).equals("1920x1440 30")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1920x1440 30fps", "1440P", 0));
                // cs[1] = "FHD";
            } else if (videoSizeList.get(ii).equals("1280x720 120")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1280x720 120fps", "HD120", 0));
                // cs[1] = "HD";
            } else if (videoSizeList.get(ii).equals("1280x720 30")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1280x720 30fps", "HD30", 0));
                // cs[1] = "HD";
            } else if (videoSizeList.get(ii).equals("1280x720 60")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1280x720 60fps", "HD60", 0));
                // cs[1] = "HD";
            } else if (videoSizeList.get(ii).equals("1280x720 50")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1280x720 50fps", "HD50", 0));
                // cs[1] = "HD";
            } else if (videoSizeList.get(ii).equals("1280x720 25")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1280x720 25fps", "HD25", 0));
                // cs[1] = "HD";
            } else if (videoSizeList.get(ii).equals("1280x960 60")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1280x960 60fps", "960P", 0));
            } else if (videoSizeList.get(ii).equals("1280x960 120")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1280x960 120fps", "960P", 0));
            } else if (videoSizeList.get(ii).equals("1280x960 30")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1280x960 30fps", "960P", 0));
            } else if (videoSizeList.get(ii).equals("640x480 240")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("640x480 240fps", "VGA240", 0));
            } else if (videoSizeList.get(ii).equals("640x480 120")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("640x480 120fps", "VGA120", 0));
            } else if (videoSizeList.get(ii).equals("640x360 240")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("640x360 240fps", "VGA240", 0));
            } else if (videoSizeList.get(ii).equals("640x360 120")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("640x360 120fps", "VGA120", 0));
            } else if (videoSizeList.get(ii).equals("1920x1080 24")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1920x1080 24fps", "FHD24", 0));
            } else if (videoSizeList.get(ii).equals("1920x1080 50")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1920x1080 50fps", "FHD50", 0));
            } else if (videoSizeList.get(ii).equals("1920x1080 25")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1920x1080 25fps", "FHD25", 0));
            } else if (videoSizeList.get(ii).equals("3840x2160 60")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("3840x2160 60fps", "4K60", 0));

            } else if (videoSizeList.get(ii).equals("3840x2160 50")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("3840x2160 50fps", "4K50", 0));

            } else if (videoSizeList.get(ii).equals("3840x2160 25")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("3840x2160 25fps", "4K25", 0));

            } else if (videoSizeList.get(ii).equals("3840x2160 24")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("3840x2160 24fps", "4K24", 0));

            } else if (videoSizeList.get(ii).equals("3840x2160 30")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("3840x2160 30fps", "4K30", 0));

            } else if (videoSizeList.get(ii).equals("3840x2160 15")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("3840x2160 15fps", "4K15", 0));

            } else if (videoSizeList.get(ii).equals("3840x2160 10")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("3840x2160 10fps", "4K10", 0));
            } else if (videoSizeList.get(ii).equals("2704x1524 30")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("2704x1524 30fps", "2.7K30", 0));
            } else if (videoSizeList.get(ii).equals("2704x1524 15")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("2704x1524 15fps", "2.7K15", 0));
            } else if (videoSizeList.get(ii).equals("2704x1524 60")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("2704x1524 60fps", "2.7K60", 0));
            } else if (videoSizeList.get(ii).equals("2704x1524 50")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("2704x1524 50fps", "2.7K50", 0));
            } else if (videoSizeList.get(ii).equals("2704x1524 25")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("2704x1524 25fps", "2.7K25", 0));
            } else if (videoSizeList.get(ii).equals("2704x1524 24")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("2704x1524 24fps", "2.7K24", 0));
            } else if (videoSizeList.get(ii).equals("848x480 240")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("848x480 240fps", "WVGA", 0));
            } else if (videoSizeList.get(ii).equals("3840x1920 15")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("3840x1920 15fps", "4K15", 0));
            } else if (videoSizeList.get(ii).equals("3840x1920 30")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("3840x1920 30fps", "4K30", 0));
            } else if (videoSizeList.get(ii).equals("2800x1400 25")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("2800x1400 25fps", "2.7K25", 0));
            } else if (videoSizeList.get(ii).equals("2880x1440 30")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("2880x1440 30fps", "2.7K30", 0));
            } else if (videoSizeList.get(ii).equals("2880x1440 25")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("2880x1440 25fps", "2.7K25", 0));
            } else if (videoSizeList.get(ii).equals("2560x1280 30")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("2560x1280 30fps", "1280P30", 0));
            } else if (videoSizeList.get(ii).equals("2560x1280 60")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("2560x1280 60fps", "1280P60", 0));
            } else if (videoSizeList.get(ii).equals("1920x960 30")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1920x960 30fps", "960P30", 0));
            } else if (videoSizeList.get(ii).equals("1440x960 30")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1440x960 30fps", "960P30", 0));
            } else if (videoSizeList.get(ii).equals("1280x720 15")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1280x720 15fps", "HD15", 0));
            } else if (videoSizeList.get(ii).equals("1280x640 15")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("1280x640 15fps", "640P15", 0));
            } else if (videoSizeList.get(ii).equals("480x640 120")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("480x640 120fps", "640P120", 0));
            } else if (videoSizeList.get(ii).equals("240x320 240")) {
                videoSizeMap.put(videoSizeList.get(ii), new ItemInfo("240x320 240fps", "320P240", 0));
            }
        }
        AppLog.i(TAG, "end initVideoSizeMap videoSizeList=" + videoSizeList.size());
        return videoSizeMap;
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
        } else if (size.equals("1920x960") || size.equals("1440x960")) {
            abbreviationSize = "960P";
        } else if (size.equals("1280x640") || size.equals("480x640")) {
            abbreviationSize = "640P";
        } else if (size.equals("240x320")) {
            abbreviationSize = "320P";
        } else if (size.equals("320x240")) {
            abbreviationSize = "240P";
        } else if (size.equals("640x480")) {
            abbreviationSize = "VGA";
        } else if (size.equals("5760x2880")) {
            abbreviationSize = "6K";
        } else if (size.equals("3840x2160") || size.equals("3840x1920")) {
            abbreviationSize = "4K";
        } else if (size.equals("2704x1524") || size.equals("2800x1400") || size.equals("2880x1440")) {
            abbreviationSize = "2.7K";
        } else if (size.equals("848x480")) {
            abbreviationSize = "WVGA";
            return abbreviationSize;
        }
        if (abbreviationSize == null) {
            AppLog.d(TAG, "getAbbreviation videoSize 不支持!");
            return null;
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
