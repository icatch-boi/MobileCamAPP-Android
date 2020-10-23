package com.icatch.mobilecam.data.Message;

/**
 * Created by yh.zhang C001012 on 2015/10/15:11:58.
 * Fucntion:
 */
public class AppMessage {
    public final static int ACTIVITY_MESSAGE = 0x0000;
    //For Activity 0x0001~0x0FFF
    //For LaunchActivity 0x0001~0x00FF
    public final static int LAUNCH_ACTIVITY = ACTIVITY_MESSAGE + 0x0000;
    public final static int MESSAGE_DELETE_CAMERA = LAUNCH_ACTIVITY + 0x01;
    public static final int MESSAGE_CAMERA_SCAN_TIME_OUT = LAUNCH_ACTIVITY + 0x02;
    public static final int MESSAGE_CAMERA_CONNECT_FAIL = LAUNCH_ACTIVITY + 0x03;
    public static final int MESSAGE_CAMERA_CONNECT_SUCCESS = LAUNCH_ACTIVITY + 0x04;
    public static final int MESSAGE_CAMERA_CONNECTING_START = LAUNCH_ACTIVITY + 0x05;

    public final static int LOCAL_ACTIVITY = ACTIVITY_MESSAGE + 0x0100;

    public final static int PREVIEW_ACTIVITY = ACTIVITY_MESSAGE + 0x0200;
    public final static int SETTING_OPTION_AUTO_DOWNLOAD = PREVIEW_ACTIVITY + 0X01;
    public final static int MESSAGE_LIVE_NETWORK_DISCONNECT = PREVIEW_ACTIVITY + 0X02;

    public final static int MPB_ACTIVITY = ACTIVITY_MESSAGE + 0x0300;
    public static final int MESSAGE_CANCEL_DOWNLOAD_SINGLE = MPB_ACTIVITY + 0x01;
    public static final int UPDATE_LOADING_PROGRESS = MPB_ACTIVITY + 0x2;
    public static final int CANCEL_DOWNLOAD_SINGLE = MPB_ACTIVITY + 0x3;
    public static final int CANCEL_DOWNLOAD_ALL = MPB_ACTIVITY + 0x4;
    public static final int UPDATE_TOTAL_PROGRESS = MPB_ACTIVITY + 0x5;

    public final static int DOWNLOAD_BEGIN = MPB_ACTIVITY + 0x7;
    public final static int DOWNLOAD_FINISHED = MPB_ACTIVITY + 0x8;
    public final static int DOWNLOAD_SUCCEED = MPB_ACTIVITY + 0x9;
    public final static int DOWNLOAD_FAILURE = MPB_ACTIVITY + 0xa;

    public final static int PHOTO_PBACTIVITY = ACTIVITY_MESSAGE + 0x0400;

    public final static int VIDEO_PBACTIVITY = ACTIVITY_MESSAGE + 0x0500;

    public final static int LOCAL_VIDEO_PBACTIVITY = ACTIVITY_MESSAGE + 0x0600;
    public static final int EVENT_CACHE_STATE_CHANGED = LOCAL_VIDEO_PBACTIVITY + 0X01;
    public static final int EVENT_CACHE_PROGRESS_NOTIFY = LOCAL_VIDEO_PBACTIVITY + 0X02;
    public static final int EVENT_VIDEO_PLAY_COMPLETED = LOCAL_VIDEO_PBACTIVITY + 0X03;
    public static final int MESSAGE_UPDATE_VIDEOPB_BAR = LOCAL_VIDEO_PBACTIVITY + 0X04;
    public static final int MESSAGE_CANCEL_VIDEO_DOWNLOAD = LOCAL_VIDEO_PBACTIVITY + 0X05;
    public static final int MESSAGE_VIDEO_STREAM_NO_EIS_INFORMATION = LOCAL_VIDEO_PBACTIVITY + 0X06;
    public static final int MESSAGE_VIDEO_STREAM_CODEC_INFO = LOCAL_VIDEO_PBACTIVITY + 0X07;

    //For other 0x1000~0x1FFF
    public final static int FUNCTION_MESSAGE = 0x1000;
    public static final int AP_MODE_TO_STA_MODE_SUSSED = FUNCTION_MESSAGE + 0x0001;
    public static final int AP_MODE_TO_STA_MODE_FAILURE = FUNCTION_MESSAGE + 0x0002;

    public static final int MESSAGE_DISCONNECTED = FUNCTION_MESSAGE + 0X03;
    public static final int MESSAGE_CONNECTED = FUNCTION_MESSAGE + 0X04;
	
	public final static int FACEBOOK_LOGIN_SUCCEED = FUNCTION_MESSAGE + 0x05;
    public final static int OPEN_WIFI_HOTSPOT_FAILED = FUNCTION_MESSAGE + 0x06;
    public final static int GOOGLE_LOGIN_SUCCEED = 0x06;


}
