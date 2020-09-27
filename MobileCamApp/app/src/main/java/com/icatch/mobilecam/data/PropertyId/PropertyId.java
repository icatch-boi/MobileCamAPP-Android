package com.icatch.mobilecam.data.PropertyId;


import com.icatchtek.control.customer.type.ICatchCamProperty;

/**
 * Created by zhang yanhu C001012 on 2015/11/18 11:49.
 */
public class PropertyId {

    public final static int CAPTURE_DELAY = ICatchCamProperty.ICH_CAM_CAP_CAPTURE_DELAY;
    public final static int BURST_NUMBER = ICatchCamProperty.ICH_CAM_CAP_BURST_NUMBER;
    public final static int WHITE_BALANCE = ICatchCamProperty.ICH_CAM_CAP_WHITE_BALANCE;
    public final static int LIGHT_FREQUENCY = ICatchCamProperty.ICH_CAM_CAP_LIGHT_FREQUENCY;
    public final static int UP_SIDE = 0xd614;

    public final static int SLOW_MOTION = 0xd615;
    public final static int DATE_STAMP = ICatchCamProperty.ICH_CAM_CAP_DATE_STAMP;
    public final static int IMAGE_SIZE = ICatchCamProperty.ICH_CAM_CAP_IMAGE_SIZE;
    public final static int VIDEO_SIZE = ICatchCamProperty.ICH_CAM_CAP_VIDEO_SIZE;
    public final static int ESSID_NAME = 0xd834;
    public final static int ESSID_PASSWORD = 0xd835;
    public final static int CAMERA_NAME = 0xd831;
    public final static int CAMERA_PASSWORD = 0xD83D;
    public final static int TIMELAPSE_MODE = 0xEE00;
    public final static int CAPTURE_DELAY_MODE = 0xD7F0;
    public final static int NOTIFY_FW_TO_SHARE_MODE = 0xD7FB;
    public final static int VIDEO_SIZE_FLOW = 0xD7FC;
    public final static int VIDEO_RECORDING_TIME = 0xD7FD;
    public final static int CAMERA_DATE = 0x5011;
    public final static int CAMERA_ESSID = 0xD83C;
    public final static int CAMERA_PASSWORD_NEW = 0xD832;
    public final static int SERVICE_ESSID = 0xD836;
    public final static int SERVICE_PASSWORD = 0xD837;
    public final static int CAMERA_CONNECT_CHANGE = 0xD7A1;

    public final static int STA_MODE_SSID = 0xD834;
    public final static int STA_MODE_PASSWORD = 0xD835;
    public final static int AP_MODE_TO_STA_MODE = 0xD7FB;

    public final static int SUPPORT_PREVIEW = 0xD7FF;

    //新增属性 20170316
    public final static int SCREEN_SAVER = 0xD720; //55072
    public final static int AUTO_POWER_OFF = 0xD721;//55073
    public final static int POWER_ON_AUTO_RECORD = 0xD722;//55074
    //曝光补偿
    public final static int EXPOSURE_COMPENSATION = 0xD723;//55075
    //图像防抖
    public final static int IMAGE_STABILIZATION = 0xD724;//55076
    //录像时长
    public final static int VIDEO_FILE_LENGTH = 0xD725;//55077
    public final static int FAST_MOTION_MOVIE = 0xD726;//55078
    public final static int WIND_NOISE_REDUCTION = 0xD727;
    public final static int CAPTURE_IN_VIDEO_RECORD = 0xD72A;
    // 20180815
    public final static int CAMERA_SWITCH = 0xD733;



    //是否进入pv页面
    public final static int DEFALUT_TO_PREVIEW = 0xD72C;
    //pb 分段获取文件
    public final static int CAMERA_PB_LIMIT_NUMBER = 0xD83F;//55359
}
