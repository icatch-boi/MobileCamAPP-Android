package com.icatch.mobilecam.Function;

import android.os.Handler;
import android.util.Log;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.SdkApi.CameraAction;
import com.icatch.mobilecam.SdkApi.PanoramaControl;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.Message.AppMessage;
import com.icatchtek.control.customer.ICatchCameraListener;
import com.icatchtek.control.customer.type.ICatchCamEvent;
import com.icatchtek.control.customer.type.ICatchCamEventID;
import com.icatchtek.pancam.customer.ICatchIPancamListener;
import com.icatchtek.pancam.customer.type.ICatchGLEvent;
import com.icatchtek.pancam.customer.type.ICatchGLEventID;

import java.util.Locale;

/**
 * Created by zhang yanhu C001012 on 2015/11/23 18:00.
 */
public class SDKEvent {
    private static final String TAG = "SDKEvent";
    public static final int EVENT_BATTERY_ELETRIC_CHANGED = 0;
    public static final int EVENT_CAPTURE_COMPLETED = 1;
    public static final int EVENT_CAPTURE_START = 3;
    public static final int EVENT_SD_CARD_FULL = 4;
    public static final int EVENT_VIDEO_OFF = 5;
    public static final int EVENT_VIDEO_ON = 6;
    public static final int EVENT_FILE_ADDED = 7;
    public static final int EVENT_CONNECTION_FAILURE = 8;
    public static final int EVENT_TIME_LAPSE_STOP = 9;
    public static final int EVENT_SERVER_STREAM_ERROR = 10;
    public static final int EVENT_FILE_DOWNLOAD = 11;
    public static final int EVENT_VIDEO_RECORDING_TIME = 12;
    public static final int EVENT_FW_UPDATE_COMPLETED = 13;
    public static final int EVENT_FW_UPDATE_POWEROFF = 14;
    public static final int EVENT_SEARCHED_NEW_CAMERA = 15;
    public static final int EVENT_SDCARD_REMOVED = 16;
    public static final int EVENT_SDCARD_INSERT = 17;
    public static final int EVENT_VIDEO_PLAY_PTS = 23;
    public static final int EVENT_VIDEO_PLAY_CLOSED = 24;
    public static final int EVENT_FW_UPDATE_CHECK = 25;
    public static final int EVENT_FW_UPDATE_CHKSUMERR = 26;
    public static final int EVENT_FW_UPDATE_NG = 27;
    private CameraAction cameraAction;
    private Handler handler;
    private SdcardStateListener sdcardStateListener;
    private BatteryStateListener batteryStateListener;
    private CaptureStartListener captureStartListener;
    private CaptureDoneListener captureDoneListener;
    private VideoOffListener videoOffListener;
    private FileAddedListener fileAddedListener;
    private VideoOnListener videoOnListener;
    private ConnectionFailureListener connectionFailureListener;
    private TimeLapseStopListener timeLapseStopListener;
    private ServerStreamErrorListener serverStreamErrorListener;
    private VideoRecordingTimeStartListener videoRecordingTimeStartListener;
    private FileDownloadListener fileDownloadListener;
    private UpdateFWCompletedListener updateFWCompletedListener;
    private UpdateFWPoweroffListener updateFWPoweroffListener;
    private InsertSdcardListener insertSdcardListener;
    private NoSdcardListener noSdcardListener;


    private VideoStreamStatusListener videoStreamStatusListener;
    private VideoStreamCloseListener videoStreamCloseListener;
    private CacheStateChangedListener cacheStateChangedListener;
    private CacheProgressListener cacheProgressListener;
    private NetworkDisconnectListener networkDisconnectListener;
    private PanoramaControl panoramaControl;
    private NoEISInformationListener noEISInformationListener;
    // add
    private UpdateFWCheckListener updateFWCheckListener;
    private UpdateFWCHKSumErrListener updateFWCHKSumErrListener;
    private UpdateFWNGListener updateFWNGListener;
    private VideoCodecInformationListener videoCodecInformationListener;

    public SDKEvent(Handler handler) {
        this.handler = handler;
        this.cameraAction = CameraManager.getInstance().getCurCamera().getCameraAction();
        this.panoramaControl = CameraManager.getInstance().getCurCamera().getPanoramaControl();
    }

    public SDKEvent(Handler handler, PanoramaControl panoramaControl) {
        this.handler = handler;
        this.panoramaControl = panoramaControl;
    }

    public void addEventListener(int iCatchEventID) {
        // switch(iCatchEventID){
        if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_SDCARD_FULL) {
            sdcardStateListener = new SdcardStateListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_FULL, sdcardStateListener);
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_BATTERY_LEVEL_CHANGED) {
            batteryStateListener = new BatteryStateListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_BATTERY_LEVEL_CHANGED, batteryStateListener);
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_START) {
            captureStartListener = new CaptureStartListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_START, captureStartListener);
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_COMPLETE) {
            captureDoneListener = new CaptureDoneListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_COMPLETE, captureDoneListener);
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_VIDEO_OFF) {
            videoOffListener = new VideoOffListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_VIDEO_OFF, videoOffListener);
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FILE_ADDED) {
            fileAddedListener = new FileAddedListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FILE_ADDED, fileAddedListener);
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_VIDEO_ON) {
            videoOnListener = new VideoOnListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_VIDEO_ON, videoOnListener);
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_TIMELAPSE_STOP) {
            timeLapseStopListener = new TimeLapseStopListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_TIMELAPSE_STOP, timeLapseStopListener);
        }
//        else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_SERVER_STREAM_ERROR) {
//            serverStreamErrorListener = new ServerStreamErrorListener();
//            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_SERVER_STREAM_ERROR, serverStreamErrorListener);
//        }
        else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FILE_DOWNLOAD) {
            fileDownloadListener = new FileDownloadListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FILE_DOWNLOAD, fileDownloadListener);
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_COMPLETED) {
            updateFWCompletedListener = new UpdateFWCompletedListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_COMPLETED, updateFWCompletedListener);
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_POWEROFF) {
            updateFWPoweroffListener = new UpdateFWPoweroffListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_POWEROFF, updateFWPoweroffListener);
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_SDCARD_REMOVED) {
            noSdcardListener = new NoSdcardListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_REMOVED, noSdcardListener);
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_CONNECTION_DISCONNECTED) {
            connectionFailureListener = new ConnectionFailureListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_CONNECTION_DISCONNECTED, connectionFailureListener);
        } else if(iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_SDCARD_IN){
            insertSdcardListener = new InsertSdcardListener();
            cameraAction.addEventListener(iCatchEventID, insertSdcardListener);
        }
        if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHECK) {
            updateFWCheckListener = new UpdateFWCheckListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHECK, updateFWCheckListener);
        }
        if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHKSUMERR) {
            updateFWCHKSumErrListener = new UpdateFWCHKSumErrListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHKSUMERR, updateFWCHKSumErrListener);
        }
        if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_NG) {
            updateFWNGListener = new UpdateFWNGListener();
            cameraAction.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_NG, updateFWNGListener);
        }
    }

    public void addPanoramaEventListener(int iCatchEventID) {
        switch (iCatchEventID) {
            case ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_STATUS:
                videoStreamStatusListener = new VideoStreamStatusListener();
                AppLog.d(TAG, "addPanoramaEventListener videoStreamStatusListener");
                panoramaControl.addEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_STATUS, videoStreamStatusListener);
                break;
//            case ICatchGLEventID.ICH_GL_EVENT_STREAM_CLOSED:
            case ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_ENDED:
                videoStreamCloseListener = new VideoStreamCloseListener();
                panoramaControl.addEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_ENDED, videoStreamCloseListener);
                break;
            case ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_CHANGED:
                cacheStateChangedListener = new CacheStateChangedListener();
                panoramaControl.addEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_CHANGED, cacheStateChangedListener);
                break;

            case ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_PROGRESS:
                cacheProgressListener = new CacheProgressListener();
                panoramaControl.addEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_PROGRESS, cacheProgressListener);
                break;
            case ICatchGLEventID.ICH_GL_EVENT_LIVE_NETWORK_DISCONNECT:
                networkDisconnectListener = new NetworkDisconnectListener();
                panoramaControl.addEventListener(ICatchGLEventID.ICH_GL_EVENT_LIVE_NETWORK_DISCONNECT, networkDisconnectListener);
                break;
            case ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_NO_EIS_INFORMATION:
                noEISInformationListener = new NoEISInformationListener();
                panoramaControl.addEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_NO_EIS_INFORMATION, noEISInformationListener);
                break;
            case ICatchGLEventID.ICH_GL_EVENT_VIDEO_CODEC_INSUFFICIENT_PERFORMANCE:
                videoCodecInformationListener = new VideoCodecInformationListener();
                panoramaControl.addEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_CODEC_INSUFFICIENT_PERFORMANCE, videoCodecInformationListener);
                break;


        }
    }

    public void removePanoramaEventListener(int iCatchEventID) {
        switch (iCatchEventID) {
            case ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_STATUS:
                if (videoStreamStatusListener != null) {
                    panoramaControl.removeEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_STATUS, videoStreamStatusListener);
                    videoStreamStatusListener = null;
                }
                break;
//            case ICatchGLEventID.ICH_GL_EVENT_STREAM_CLOSED:
            case ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_ENDED:
                if (videoStreamCloseListener != null) {
                    panoramaControl.removeEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_ENDED, videoStreamCloseListener);
                    videoStreamCloseListener = null;
                }
                break;
            case ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_CHANGED:
                if (cacheStateChangedListener != null) {
                    panoramaControl.removeEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_CHANGED, cacheStateChangedListener);
                    cacheStateChangedListener = null;
                }
                break;
            case ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_PROGRESS:
                if (cacheProgressListener != null) {
                    panoramaControl.removeEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_PROGRESS, cacheProgressListener);
                    cacheProgressListener = null;
                }
                break;
            case ICatchGLEventID.ICH_GL_EVENT_LIVE_NETWORK_DISCONNECT:
                if (networkDisconnectListener != null) {
                    panoramaControl.removeEventListener(ICatchGLEventID.ICH_GL_EVENT_LIVE_NETWORK_DISCONNECT, networkDisconnectListener);
                    networkDisconnectListener = null;
                }

                break;
            case ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_NO_EIS_INFORMATION:
                if (noEISInformationListener != null) {
                    panoramaControl.removeEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_NO_EIS_INFORMATION, noEISInformationListener);
                    noEISInformationListener = null;
                }
                break;
            case ICatchGLEventID.ICH_GL_EVENT_VIDEO_CODEC_INSUFFICIENT_PERFORMANCE:
                if (videoCodecInformationListener != null) {
                    panoramaControl.removeEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_CODEC_INSUFFICIENT_PERFORMANCE, videoCodecInformationListener);
                    videoCodecInformationListener = null;
                }
                break;
        }
    }

    public void addCustomizeEvent(int eventID) {
        switch (eventID) {
            case 0x5001:
                videoRecordingTimeStartListener = new VideoRecordingTimeStartListener();
                cameraAction.addCustomEventListener(eventID, videoRecordingTimeStartListener);
                break;
            case 0x3701:
                insertSdcardListener = new InsertSdcardListener();
                cameraAction.addCustomEventListener(eventID, insertSdcardListener);
                break;
                default:
                    CustomEventListener eventListener = new CustomEventListener();
                    cameraAction.addCustomEventListener(eventID, eventListener);
        }
    }

    public void delCustomizeEventListener(int eventID) {
        switch (eventID) {
            case 0x5001:
                if (videoRecordingTimeStartListener != null) {
                    cameraAction.delCustomEventListener(eventID, videoRecordingTimeStartListener);
                    videoRecordingTimeStartListener = null;
                }
                break;
            case 0x3701:
                if (insertSdcardListener != null) {
                    cameraAction.delCustomEventListener(eventID, insertSdcardListener);
                    insertSdcardListener = null;
                }
                break;
                default:


        }

    }

    public void delEventListener(int iCatchEventID) {
        // switch(iCatchEventID){
        if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_SDCARD_FULL && sdcardStateListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_FULL, sdcardStateListener);
            sdcardStateListener = null;
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_BATTERY_LEVEL_CHANGED && batteryStateListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_BATTERY_LEVEL_CHANGED, batteryStateListener);
            batteryStateListener = null;
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_COMPLETE && captureDoneListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_COMPLETE, captureDoneListener);
            captureDoneListener = null;
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_START && captureStartListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_START, captureStartListener);
            captureStartListener = null;
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_VIDEO_OFF && videoOffListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_VIDEO_OFF, videoOffListener);
            videoOffListener = null;
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FILE_ADDED && fileAddedListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FILE_ADDED, fileAddedListener);
            fileAddedListener = null;
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_VIDEO_ON && videoOnListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_VIDEO_ON, videoOnListener);
            videoOnListener = null;
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_TIMELAPSE_STOP && timeLapseStopListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_TIMELAPSE_STOP, timeLapseStopListener);
            timeLapseStopListener = null;
        }
//        else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_SERVER_STREAM_ERROR && serverStreamErrorListener != null) {
//            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_SERVER_STREAM_ERROR, serverStreamErrorListener);
//            serverStreamErrorListener = null;
//        }
        else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FILE_DOWNLOAD && fileDownloadListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FILE_DOWNLOAD, fileDownloadListener);
            fileDownloadListener = null;
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_COMPLETED && updateFWCompletedListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_COMPLETED, updateFWCompletedListener);
            updateFWCompletedListener = null;
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_POWEROFF && updateFWPoweroffListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_POWEROFF, updateFWPoweroffListener);
            updateFWPoweroffListener = null;
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_SDCARD_REMOVED && noSdcardListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_REMOVED, noSdcardListener);
            noSdcardListener = null;
        } else if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_CONNECTION_DISCONNECTED && connectionFailureListener != null) {
            Log.d("1111", "connectionFailureListener != null");
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_CONNECTION_DISCONNECTED, connectionFailureListener);
            connectionFailureListener = null;
        } else if(iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_SDCARD_IN){
            cameraAction.delEventListener(iCatchEventID, insertSdcardListener);
            insertSdcardListener = null;
        }
        if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHECK && updateFWCheckListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHECK, updateFWCheckListener);
        }

        if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHKSUMERR && updateFWCHKSumErrListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHKSUMERR, updateFWCHKSumErrListener);
        }
        if (iCatchEventID == ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_NG && updateFWNGListener != null) {
            cameraAction.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_NG, updateFWNGListener);
        }

    }

    public class ICatchCodecEventListener  implements ICatchCameraListener {

        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            handler.obtainMessage(EVENT_SD_CARD_FULL).sendToTarget();
            AppLog.i(TAG, "event: EVENT_SD_CARD_FULL");
        }
    }

    public class SdcardStateListener implements ICatchCameraListener {

        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            handler.obtainMessage(EVENT_SD_CARD_FULL).sendToTarget();
            AppLog.i(TAG, "event: EVENT_SD_CARD_FULL");
        }
    }

    /**
     * Added by zhangyanhu C01012,2014-3-7
     */
    public class BatteryStateListener implements ICatchCameraListener {

        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:BatteryStateListener intValue1:"+ arg0.getIntValue1() + " intValue2:" + arg0.getIntValue2());
            handler.obtainMessage(EVENT_BATTERY_ELETRIC_CHANGED,arg0.getIntValue1(),0).sendToTarget();
        }
    }

    public class CaptureDoneListener implements ICatchCameraListener {

        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:capture done");
            handler.obtainMessage(EVENT_CAPTURE_COMPLETED).sendToTarget();
        }
    }

    public class CaptureStartListener implements ICatchCameraListener {

        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:capture start");
            handler.obtainMessage(EVENT_CAPTURE_START).sendToTarget();
        }
    }

    /**
     * Added by zhangyanhu C01012,2014-3-10
     */
    public class VideoOffListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:videooff");
            handler.obtainMessage(EVENT_VIDEO_OFF).sendToTarget();
        }
    }

    /**
     * Added by zhangyanhu C01012,2014-3-10
     */
    public class VideoOnListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:videoON");
            handler.obtainMessage(EVENT_VIDEO_ON).sendToTarget();
        }
    }

    /**
     * Added by zhangyanhu C01012,2014-4-1
     */
    public class FileAddedListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:FileAddedListener");
            handler.obtainMessage(EVENT_FILE_ADDED).sendToTarget();
        }
    }

    public class ConnectionFailureListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:ConnectionFailureListener");
            handler.obtainMessage(EVENT_CONNECTION_FAILURE).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class TimeLapseStopListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:TimeLapseStopListener");
            handler.obtainMessage(EVENT_TIME_LAPSE_STOP).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class ServerStreamErrorListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:ServerStreamErrorListener");
            handler.obtainMessage(EVENT_SERVER_STREAM_ERROR).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class FileDownloadListener implements ICatchCameraListener {

        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:FileDownloadListener");
            Log.d("1111", "receive event:FileDownloadListener");
            handler.obtainMessage(EVENT_FILE_DOWNLOAD, arg0.getFileValue1()).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class VideoRecordingTimeStartListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive VideoRecordingTimeStartListener");
            handler.obtainMessage(EVENT_VIDEO_RECORDING_TIME).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class UpdateFWCompletedListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive UpdateFWCompletedListener");
            handler.obtainMessage(EVENT_FW_UPDATE_COMPLETED).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class UpdateFWPoweroffListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive UpdateFWPoweroffListener");
            handler.obtainMessage(EVENT_FW_UPDATE_POWEROFF).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }


    public class VideoStreamStatusListener implements ICatchIPancamListener {

        @Override
        public void eventNotify(ICatchGLEvent iCatchGLEvent) {
//            AppLog.i( TAG, "--------------receive VideoStreamStatusListener  iCatchGLEvent.getDoubleValue1()="+iCatchGLEvent.getDoubleValue1() );
            handler.obtainMessage(EVENT_VIDEO_PLAY_PTS, iCatchGLEvent.getDoubleValue1()).sendToTarget();
        }
    }

    public class VideoStreamCloseListener implements ICatchIPancamListener {
        @Override
        public void eventNotify(ICatchGLEvent iCatchGLEvent) {
            AppLog.i(TAG, "--------------receive VideoStreamCloseListener");
            handler.obtainMessage(EVENT_VIDEO_PLAY_CLOSED).sendToTarget();
        }
    }

    public class CacheStateChangedListener implements ICatchIPancamListener {
        @Override
        public void eventNotify(ICatchGLEvent iCatchGLEvent) {
            AppLog.d(TAG, "receive CacheStateChangedListener........iCatchGLEvent.getLongValue1()=" + iCatchGLEvent.getLongValue1());
            handler.obtainMessage(AppMessage.EVENT_CACHE_STATE_CHANGED, (int) iCatchGLEvent.getLongValue1(), 0)
                    .sendToTarget();
        }
    }

    public class CacheProgressListener implements ICatchIPancamListener {
        @Override
        public void eventNotify(ICatchGLEvent iCatchGLEvent) {
            int temp = new Double(iCatchGLEvent.getDoubleValue1() * 100).intValue();
            AppLog.d(TAG, "receive CacheProgressListener.......temp=" + temp);
            handler.obtainMessage(AppMessage.EVENT_CACHE_PROGRESS_NOTIFY, (int) iCatchGLEvent.getLongValue1(), temp).sendToTarget();
        }
    }


    public class InsertSdcardListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent iCatchCamEvent) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive InsertSdcardListener");
            AppInfo.isSdCardExist = true;
            handler.obtainMessage(EVENT_SDCARD_INSERT).sendToTarget();
            AppLog.i(TAG, "receive InsertSdcardListener GlobalInfo.isSdCard = " + AppInfo.isSdCardExist);
        }

    }

    public class NoSdcardListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive NoSdcardListener");
            AppInfo.isSdCardExist = false;
            handler.obtainMessage(EVENT_SDCARD_REMOVED).sendToTarget();
            AppLog.i(TAG, "receive NoSdcardListener GlobalInfo.isSdCard = " + AppInfo.isSdCardExist);
        }
    }



    private class NetworkDisconnectListener implements ICatchIPancamListener {

        @Override
        public void eventNotify(ICatchGLEvent iCatchGLEvent) {
            AppLog.i(TAG, "--------------receive VideoStreamCloseListener");
            handler.obtainMessage(AppMessage.MESSAGE_LIVE_NETWORK_DISCONNECT).sendToTarget();
        }
    }

    private class VideoCodecInformationListener implements ICatchIPancamListener {

        @Override
        public void eventNotify(ICatchGLEvent event) {
            AppLog.i(TAG, "--------------receive VideoCodecInformationListener");
            String videoCodecInfo = String.format(Locale.getDefault(),
                    "Warning, Insufficient performance.\ncodec: %d,Video width:%d,height: %d,Frame interval: %.4f,decode time: %.4f\nThe playback will stutter.",
                    event.getLongValue1(), event.getLongValue2(), event.getLongValue3(),
                    event.getDoubleValue1(), event.getDoubleValue2());
            Log.i("__codec_performance__", videoCodecInfo);
            handler.obtainMessage(AppMessage.MESSAGE_VIDEO_STREAM_CODEC_INFO,videoCodecInfo).sendToTarget();
        }
    }

    private class NoEISInformationListener implements ICatchIPancamListener {

        @Override
        public void eventNotify(ICatchGLEvent iCatchGLEvent) {
            AppLog.i(TAG, "--------------receive NoEISInformationListener");
            handler.obtainMessage(AppMessage.MESSAGE_VIDEO_STREAM_NO_EIS_INFORMATION).sendToTarget();
        }
    }

    public class CustomEventListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent iCatchCamEvent) {
            AppLog.d(TAG,"eventNotify iCatchCamEvent EventID:" + iCatchCamEvent.getEventID());
            AppLog.d(TAG,"eventNotify iCatchCamEvent StringValue1:" + iCatchCamEvent.getStringValue1());
            AppLog.d(TAG,"eventNotify iCatchCamEvent IntValue1:" + iCatchCamEvent.getIntValue1());
            AppLog.d(TAG,"eventNotify iCatchCamEvent DoubleValue1:" + iCatchCamEvent.getDoubleValue1());
        }
    }
    // add
    public class UpdateFWCheckListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive UpdateFWCheckListener");
            handler.obtainMessage(EVENT_FW_UPDATE_CHECK).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class UpdateFWCHKSumErrListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive UpdateFWCHKSumErrListener");
            handler.obtainMessage(EVENT_FW_UPDATE_CHKSUMERR).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class UpdateFWNGListener implements ICatchCameraListener {
        @Override
        public void eventNotify(ICatchCamEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive UpdateFWNGListener");
            handler.obtainMessage(EVENT_FW_UPDATE_NG).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

}
