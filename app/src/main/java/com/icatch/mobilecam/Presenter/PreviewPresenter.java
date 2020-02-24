package com.icatch.mobilecam.Presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.icatch.mobilecam.DataConvert.StreamInfoConvert;
import com.icatch.mobilecam.Function.BaseProrertys;
import com.icatch.mobilecam.Function.CameraAction.PhotoCapture;
import com.icatch.mobilecam.Function.CameraAction.ZoomInOut;
import com.icatch.mobilecam.Function.SDKEvent;
import com.icatch.mobilecam.Function.Setting.OptionSetting;
import com.icatch.mobilecam.Function.Setting.UIDisplaySource;
import com.icatch.mobilecam.Function.ThumbnailGetting.ThumbnailOperation;
import com.icatch.mobilecam.Function.live.Facebook.FacebookInfo;
import com.icatch.mobilecam.Function.live.Facebook.GraphOperation;
import com.icatch.mobilecam.Function.live.google.CreateBroadcast;
import com.icatch.mobilecam.Function.live.google.GoogleAuthTool;
import com.icatch.mobilecam.Function.live.google.YoutubeCredential;
import com.icatch.mobilecam.Function.streaming.CameraStreaming;
import com.icatch.mobilecam.Listener.OnSettingCompleteListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.MyCamera.CameraType;
import com.icatch.mobilecam.MyCamera.MyCamera;
import com.icatch.mobilecam.Presenter.Interface.BasePresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.CameraAction;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatch.mobilecam.SdkApi.CameraState;
import com.icatch.mobilecam.SdkApi.FileOperation;
import com.icatch.mobilecam.SdkApi.PanoramaPreviewPlayback;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.CustomException.NullPointerException;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.data.Message.AppMessage;
import com.icatch.mobilecam.data.Mode.LiveMode;
import com.icatch.mobilecam.data.Mode.PreviewMode;
import com.icatch.mobilecam.data.Mode.TouchMode;
import com.icatch.mobilecam.data.PropertyId.PropertyId;
import com.icatch.mobilecam.data.entity.GoogleToken;
import com.icatch.mobilecam.data.entity.SettingMenu;
import com.icatch.mobilecam.data.entity.StreamInfo;
import com.icatch.mobilecam.data.type.SlowMotion;
import com.icatch.mobilecam.data.type.TimeLapseInterval;
import com.icatch.mobilecam.data.type.TimeLapseMode;
import com.icatch.mobilecam.data.type.Tristate;
import com.icatch.mobilecam.data.type.Upside;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.ui.Interface.PreviewView;
import com.icatch.mobilecam.ui.activity.LoginFacebookActivity;
import com.icatch.mobilecam.ui.activity.LoginGoogleActivity;
import com.icatch.mobilecam.ui.adapter.SettingListAdapter;
import com.icatch.mobilecam.ui.appdialog.AppDialog;
import com.icatch.mobilecam.ui.appdialog.AppToast;
import com.icatch.mobilecam.utils.BitmapTools;
import com.icatch.mobilecam.utils.ConvertTools;
import com.icatch.mobilecam.utils.fileutils.FileOper;
import com.icatch.mobilecam.utils.fileutils.FileTools;
import com.icatch.mobilecam.utils.PanoramaTools;
import com.icatch.mobilecam.utils.QRCode;
import com.icatch.mobilecam.utils.TimeTools;
import com.icatchtek.control.customer.type.ICatchCamDateStamp;
import com.icatchtek.control.customer.type.ICatchCamEventID;
import com.icatchtek.control.customer.type.ICatchCamMode;
import com.icatchtek.control.customer.type.ICatchCamPreviewMode;
import com.icatchtek.control.customer.type.ICatchCamProperty;
import com.icatchtek.pancam.customer.ICatchPancamConfig;
import com.icatchtek.pancam.customer.exception.IchGLSurfaceNotSetException;
import com.icatchtek.pancam.customer.surface.ICatchSurfaceContext;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;
import com.icatchtek.pancam.customer.type.ICatchGLPoint;
import com.icatchtek.pancam.customer.type.ICatchGLSurfaceType;
import com.icatchtek.reliant.customer.type.ICatchCustomerStreamParam;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchH264StreamParam;
import com.icatchtek.reliant.customer.type.ICatchJPEGStreamParam;
import com.icatchtek.reliant.customer.type.ICatchStreamParam;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.icatch.mobilecam.data.Mode.PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW;

/**
 * Created by zhang yanhu C001012 on 2015/12/4 14:22.
 */
public class PreviewPresenter extends BasePresenter implements SensorEventListener {
    private static final String TAG = "PanoramaPreviewPresenter";

    private final static float MIN_ZOOM = 0.4f;
    private final static float MAX_ZOOM = 2.2f;

    private final static float FIXED_OUTSIDE_DISTANCE = 3.0f;
    private final static float FIXED_INSIDE_DISTANCE = 0.5f;
    private PanoramaPreviewPlayback panoramaPreviewPlayback;
    private TouchMode touchMode = TouchMode.NONE;
    private float mPreviousY;
    private float mPreviousX;
    private float beforeLenght;
    private float afterLenght;
    private float currentZoomRate = MAX_ZOOM;
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private MediaPlayer videoCaptureStartBeep;
    private MediaPlayer modeSwitchBeep;
    private MediaPlayer stillCaptureStartBeep;
    private MediaPlayer continuousCaptureBeep;
    private Activity activity;
    private PreviewView previewView;
    private CameraProperties cameraProperties;
    private CameraAction cameraAction;
    private CameraState cameraState;
    private FileOperation fileOperation;
    private BaseProrertys baseProrertys;
    private MyCamera curCamera;
    private PreviewHandler previewHandler;
    private SDKEvent sdkEvent;
    private int curMode = PreviewMode.APP_STATE_NONE_MODE;
    private Timer videoCaptureButtomChangeTimer;
    public boolean videoCaptureButtomChangeFlag = true;
    private Timer recordingLapseTimeTimer;
    private int lapseTime = 0;
    private List<SettingMenu> settingMenuList;
    private SettingListAdapter settingListAdapter;
    private boolean allowClickButtoms = true;
    private int currentSettingMenuMode;
    private WifiSSReceiver wifiSSReceiver;
    private long lastCilckTime = 0;
    private long lastRecodeTime;
    private int curIcatchMode;
    private ICatchSurfaceContext iCatchSurfaceContext;
    private boolean hasInitSurface = false;
    private boolean isLive = false;
    private LiveMode liveMode = LiveMode.MODE_YOUTUBE_LIVE;
    private ZoomInOut zoomInOut;
    private int curVideoWidth = 1920;
    private int curVideoHeight = 960;
    private int curVideoFps = 30;
    private String curCodecType = "H264";
    private CameraStreaming cameraStreaming;
    private int curPanoramaType= ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;

    public PreviewPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void setView(PreviewView previewView) {
        this.previewView = previewView;
        initCfg();
        initData();
    }

    public void initData() {
        curCamera = CameraManager.getInstance().getCurCamera();
        panoramaPreviewPlayback = curCamera.getPanoramaPreviewPlayback();
        cameraStreaming = new CameraStreaming(panoramaPreviewPlayback);
        cameraProperties = curCamera.getCameraProperties();
        cameraAction = curCamera.getCameraAction();
        cameraState = curCamera.getCameraState();
        fileOperation = curCamera.getFileOperation();
        baseProrertys = curCamera.getBaseProrertys();
        zoomInOut = new ZoomInOut();
        videoCaptureStartBeep = MediaPlayer.create(activity, R.raw.camera_timer);
        stillCaptureStartBeep = MediaPlayer.create(activity, R.raw.captureshutter);
        continuousCaptureBeep = MediaPlayer.create(activity, R.raw.captureburst);
        modeSwitchBeep = MediaPlayer.create(activity, R.raw.focusbeep);
        GlobalInfo.getInstance().enableConnectCheck(true);
        previewHandler = new PreviewHandler();
        sdkEvent = new SDKEvent(previewHandler);
        if (cameraProperties.hasFuction(0xD7F0)) {
            cameraProperties.setCaptureDelayMode(1);
        }

        if (curCamera.getCameraType() == CameraType.USB_CAMERA) {
            Intent intent = activity.getIntent();
            curVideoWidth = intent.getIntExtra("videoWidth", 1920);
            curVideoHeight = intent.getIntExtra("videoHeight", 960);
            curVideoFps = intent.getIntExtra("videoFps", 30);
            curCodecType = intent.getStringExtra("videoCodec");
            if (curCodecType == null) {
                curCodecType = "H264";
            }
            AppLog.d(TAG, "initData videoWidth=" + curVideoWidth + " videoHeight=" + curVideoHeight + " videoFps=" + curVideoFps + " curCodecType=" +
                    curCodecType);
        }
        AppLog.i(TAG, "cameraProperties.getMaxZoomRatio() =" + cameraProperties.getMaxZoomRatio());
    }

    public void initStatus() {
        if (AppInfo.enableLive) {
            previewView.setYouTubeLiveLayoutVisibility(View.VISIBLE);
        } else {
            previewView.setYouTubeLiveLayoutVisibility(View.GONE);
        }
        int resid = ThumbnailOperation.getBatteryLevelIcon(cameraProperties.getBatteryElectric());
        if (resid > 0) {
            previewView.setBatteryIcon(resid);
            if (resid == R.drawable.ic_battery_charging_green24dp) {
                AppDialog.showLowBatteryWarning(activity);
            }
        }
        IntentFilter wifiSSFilter = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
        wifiSSReceiver = new WifiSSReceiver();
        activity.registerReceiver(wifiSSReceiver, wifiSSFilter);
    }


    public void changeCameraMode(final int previewMode, final int ichVideoPreviewMode) {
        AppLog.i(TAG, "start changeCameraMode ichVideoPreviewMode=" + ichVideoPreviewMode);
        AppLog.i(TAG, "start changeCameraMode previewMode=" + previewMode + "  hasInitSurface=" + hasInitSurface);
        curIcatchMode = ichVideoPreviewMode;
        MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
        new Thread(new Runnable() {
            @Override
            public void run() {
                cameraAction.changePreviewMode(ichVideoPreviewMode);
                startPreview();
                previewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        curMode = previewMode;
                        createUIByMode(curMode);
                        MyProgressDialog.closeProgressDialog();
                        previewView.dismissPopupWindow();
                    }
                });
            }
        }).start();
    }

    public void redrawSurface() {
        if (curCamera.isStreamReady && !AppInfo.enableRender) {
            int width = previewView.getSurfaceViewWidth();
            int heigth = previewView.getSurfaceViewHeight();
            AppLog.i(TAG, "SurfaceViewWidth=" + width + " SurfaceViewHeight=" + heigth);
            if (width > 0 || heigth > 0) {
                cameraStreaming.setViewParam(width, heigth);
                cameraStreaming.setSurfaceViewArea();
            }
        }
    }


    public void startOrStopCapture() {
        if (TimeTools.isFastClick()) {
            return;
        }
        if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            if (cameraProperties.isSDCardExist() == false) {
                AppDialog.showDialogWarn(activity, R.string.dialog_card_not_exist);
                return;
            }
            if (cameraProperties.getRecordingRemainTime() <= 0) {
                AppDialog.showDialogWarn(activity, R.string.dialog_sd_card_is_full);
                return;
            }
            videoCaptureStartBeep.start();
            lastRecodeTime = System.currentTimeMillis();
            if (cameraAction.startMovieRecord()) {
                AppLog.i(TAG, "startRecordingLapseTimeTimer(0)");
                curMode = PreviewMode.APP_STATE_VIDEO_CAPTURE;
                startVideoCaptureButtomChangeTimer();
                startRecordingLapseTimeTimer(0);
            }
        } else if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            videoCaptureStartBeep.start();
            if (System.currentTimeMillis() - lastRecodeTime < 2000) {
                return;
            }
            if (cameraAction.stopVideoCapture()) {
                curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
                stopVideoCaptureButtomChangeTimer();
                stopRecordingLapseTimeTimer();
                previewView.setRemainRecordingTimeText(ConvertTools.secondsToMinuteOrHours(cameraProperties.getRecordingRemainTime()));
            }
        } else if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW) {

            if (cameraProperties.isSDCardExist() == false) {
                AppDialog.showDialogWarn(activity, R.string.dialog_card_not_exist);
                return;
            }
            if (cameraProperties.getRemainImageNum() < 1) {
                AppDialog.showDialogWarn(activity, R.string.dialog_sd_card_is_full);
                return;
            }
//            stillCaptureStartBeep.start();
            curMode = PreviewMode.APP_STATE_STILL_CAPTURE;
            startPhotoCapture();
        } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW) {

            if (cameraProperties.isSDCardExist() == false) {
                AppDialog.showDialogWarn(activity, R.string.dialog_card_not_exist);
                return;
            }
            if (cameraProperties.getRemainImageNum() < 1) {
                AppDialog.showDialogWarn(activity, R.string.dialog_sd_card_is_full);
                return;
            }
            if (cameraProperties.getCurrentTimeLapseInterval() == TimeLapseInterval.TIME_LAPSE_INTERVAL_OFF) {
                AppDialog.showDialogWarn(activity, R.string.timeLapse_not_allow);
                return;
            }
            continuousCaptureBeep.start();
            if (cameraAction.startTimeLapse() == false) {
                AppLog.e(TAG, "failed to start startTimeLapse");
                return;
            }
            previewView.setCaptureBtnBackgroundResource(R.drawable.still_capture_btn_off);
            curMode = PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE;
        } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            AppLog.d(TAG, "curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE");
            if (cameraAction.stopTimeLapse() == false) {
                AppLog.e(TAG, "failed to stopTimeLapse");
                return;
            }
            stopRecordingLapseTimeTimer();

            curMode = PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW;
        } else if (curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
            AppLog.d(TAG, "curMode == PreviewMode.APP_STATE_TIMELAPSE_PREVIEW_VIDEO");
            if (cameraProperties.isSDCardExist() == false) {
                AppDialog.showDialogWarn(activity, R.string.dialog_card_not_exist);
                return;
            }
            if (cameraProperties.getRemainImageNum() < 1) {
                AppDialog.showDialogWarn(activity, R.string.dialog_sd_card_is_full);
                return;
            }
            if (cameraProperties.getCurrentTimeLapseInterval() == TimeLapseInterval.TIME_LAPSE_INTERVAL_OFF) {
                AppLog.d(TAG, "time lapse is not allowed because of timelapse interval is OFF");
                AppDialog.showDialogWarn(activity, R.string.timeLapse_not_allow);
                return;
            }

            videoCaptureStartBeep.start();
            if (cameraAction.startTimeLapse() == false) {
                AppLog.e(TAG, "failed to start startTimeLapse");
                return;
            }
            curMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE;
            startVideoCaptureButtomChangeTimer();
            startRecordingLapseTimeTimer(0);

        } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
            AppLog.d(TAG, "curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE");
            videoCaptureStartBeep.start();
            if (cameraAction.stopTimeLapse() == false) {
                AppLog.e(TAG, "failed to stopTimeLapse");
                return;
            }
            stopVideoCaptureButtomChangeTimer();
            stopRecordingLapseTimeTimer();
            curMode = APP_STATE_TIMELAPSE_VIDEO_PREVIEW;
        }
        AppLog.d(TAG, "end processing for responsing captureBtn clicking");
    }

    public void createUIByMode(int previewMode) {
        AppLog.i(TAG, "start createUIByMode previewMode=" + previewMode);
        if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_VIDEO)) {
            if (previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW || previewMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
                previewView.setPvModeBtnBackgroundResource(R.drawable.video_toggle_btn_on);
            }
        }
        if (previewMode == PreviewMode.APP_STATE_STILL_PREVIEW || previewMode == PreviewMode.APP_STATE_STILL_CAPTURE) {
            previewView.setPvModeBtnBackgroundResource(R.drawable.capture_toggle_btn_on);
        }
        if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_TIMELAPSE)) {
            if (previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                    previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                    previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE ||
                    previewMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
                previewView.setPvModeBtnBackgroundResource(R.drawable.timelapse_toggle_btn_on);
            }
        }

        if (previewMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                previewMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
            previewView.setCaptureBtnBackgroundResource(R.drawable.still_capture_btn);
        } else if (previewMode == PreviewMode.APP_STATE_VIDEO_CAPTURE ||
                previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE ||
                previewMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW ||
                previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            previewView.setCaptureBtnBackgroundResource(R.drawable.video_recording_btn_on);
        }

        if (baseProrertys.getCaptureDelay().needDisplayByMode(previewMode)) {
            previewView.setDelayCaptureLayoutVisibility(View.VISIBLE);
            previewView.setDelayCaptureTextTime(baseProrertys.getCaptureDelay().getCurrentUiStringInPreview());
        } else {
            previewView.setDelayCaptureLayoutVisibility(View.GONE);
        }

        if (baseProrertys.getImageSize().needDisplayByMode(previewMode)) {
            previewView.setImageSizeLayoutVisibility(View.VISIBLE);
            previewView.setImageSizeInfo(baseProrertys.getImageSize().getCurrentUiStringInPreview());
            previewView.setRemainCaptureCount(new Integer(cameraProperties.getRemainImageNum()).toString());
        } else {
            previewView.setImageSizeLayoutVisibility(View.GONE);
        }

        if (baseProrertys.getVideoSize().needDisplayByMode(previewMode)) {
            previewView.setVideoSizeLayoutVisibility(View.VISIBLE);
            previewView.setVideoSizeInfo(baseProrertys.getVideoSize().getCurrentUiStringInPreview());
            previewView.setRemainRecordingTimeText(ConvertTools.secondsToMinuteOrHours(cameraProperties.getRecordingRemainTime()));
        } else {
            previewView.setVideoSizeLayoutVisibility(View.GONE);
        }

        if (baseProrertys.getBurst().needDisplayByMode(previewMode)) {
            previewView.setBurstStatusVisibility(View.VISIBLE);
            try {
                previewView.setBurstStatusIcon(baseProrertys.getBurst().getCurrentIcon());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            previewView.setBurstStatusVisibility(View.GONE);
        }

        if (baseProrertys.getWhiteBalance().needDisplayByMode(previewMode)) {
            previewView.setWbStatusVisibility(View.VISIBLE);
            try {
                previewView.setWbStatusIcon(baseProrertys.getWhiteBalance().getCurrentIcon());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            previewView.setWbStatusVisibility(View.GONE);
        }

        if (baseProrertys.getUpside().needDisplayByMode(previewMode) && cameraProperties.getCurrentUpsideDown() == Upside.UPSIDE_ON) {
            previewView.setUpsideVisibility(View.VISIBLE);
        } else {
            previewView.setUpsideVisibility(View.GONE);
        }

        if (baseProrertys.getSlowMotion().needDisplayByMode(previewMode) && cameraProperties.getCurrentSlowMotion() == SlowMotion.SLOW_MOTION_ON) {
            previewView.setSlowMotionVisibility(View.VISIBLE);
        } else {
            previewView.setSlowMotionVisibility(View.GONE);
        }

        if (baseProrertys.getTimeLapseMode().needDisplayByMode(previewMode)) {
            previewView.settimeLapseModeVisibility(View.VISIBLE);
            try {
                previewView.settimeLapseModeIcon(baseProrertys.getTimeLapseMode().getCurrentIcon());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            previewView.settimeLapseModeVisibility(View.GONE);
        }
    }

    public void initPreview() {
        AppLog.i(TAG, "initPreview curMode=" + curMode);
        //set min first ,then max;
        previewView.setMinZoomRate(1.0f);
        previewView.setMaxZoomRate(cameraProperties.getMaxZoomRatio() * 1.0f);
        previewView.updateZoomViewProgress(cameraProperties.getCurrentZoomRatio());
        if (cameraState.isMovieRecording()) {
            AppLog.i(TAG, "camera is recording...");
            curMode = PreviewMode.APP_STATE_VIDEO_CAPTURE;
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE;
            startVideoCaptureButtomChangeTimer();
            startRecordingLapseTimeTimer(cameraProperties.getVideoRecordingTime());
        } else if (cameraState.isTimeLapseVideoOn()) {
            AppLog.i(TAG, "camera is TimeLapseVideoOn...");
            curCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_VIDEO;
            curMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE;
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_VIDEO_PREVIEW_MODE;
            startVideoCaptureButtomChangeTimer();
            startRecordingLapseTimeTimer(cameraProperties.getVideoRecordingTime());

        } else if (cameraState.isTimeLapseStillOn()) {
            AppLog.i(TAG, "camera is TimeLapseStillOn...");
            curCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_STILL;
            curMode = PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE;
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_STILL_PREVIEW_MODE;
            startVideoCaptureButtomChangeTimer();
            startRecordingLapseTimeTimer(cameraProperties.getVideoRecordingTime());
        } else if (curMode == PreviewMode.APP_STATE_NONE_MODE) {
            curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE;
        } else if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            AppLog.i(TAG, "initPreview curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW");
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE;
            // normal state, app show preview
        } else if (curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
            AppLog.i(TAG, "initPreview curMode == PreviewMode.APP_STATE_TIMELAPSE_PREVIEW_VIDEO");
            curCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_VIDEO;
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_VIDEO_PREVIEW_MODE;
            // normal state, app show preview
        } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW) {
            AppLog.i(TAG, "initPreview curMode == PreviewMode.APP_STATE_TIMELAPSE_PREVIEW_STILL");
            curCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_STILL;
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_STILL_PREVIEW_MODE;
            // normal state, app show preview
        } else if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
            AppLog.i(TAG, "initPreview curMode == ICH_STILL_PREVIEW_MODE");
            changeCameraMode(curMode, ICatchCamPreviewMode.ICH_CAM_STILL_PREVIEW_MODE);
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_STILL_PREVIEW_MODE;
        } else {
            curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE;
        }
        cameraAction.changePreviewMode(curIcatchMode);
        createUIByMode(curMode);
    }

    public void startVideoCaptureButtomChangeTimer() {
        AppLog.d(TAG, "startVideoCaptureButtomChangeTimer videoCaptureButtomChangeTimer=" + videoCaptureButtomChangeTimer);
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                if (videoCaptureButtomChangeFlag) {
                    videoCaptureButtomChangeFlag = false;
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                                previewView.setCaptureBtnBackgroundResource(R.drawable.video_recording_btn_on);
                            }
                        }
                    });

                } else {
                    videoCaptureButtomChangeFlag = true;
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                                previewView.setCaptureBtnBackgroundResource(R.drawable.video_recording_btn_off);
                            }
                        }
                    });
                }
            }
        };

        videoCaptureButtomChangeTimer = new Timer(true);
        videoCaptureButtomChangeTimer.schedule(task, 0, 1000);
    }

    public void stopVideoCaptureButtomChangeTimer() {
        AppLog.d(TAG, "stopVideoCaptureButtomChangeTimer videoCaptureButtomChangeTimer=" + videoCaptureButtomChangeTimer);
        if (videoCaptureButtomChangeTimer != null) {
            videoCaptureButtomChangeTimer.cancel();
        }
        previewView.setCaptureBtnBackgroundResource(R.drawable.video_recording_btn_on);
    }

    private void startRecordingLapseTimeTimer(int startTime) {
        if (cameraProperties.hasFuction(PropertyId.VIDEO_RECORDING_TIME) == false) {
            return;
        }
        AppLog.i(TAG, "startRecordingLapseTimeTimer curMode=" + curMode);
        if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE || curMode == PreviewMode
                .APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            AppLog.i(TAG, "startRecordingLapseTimeTimer");
            if (recordingLapseTimeTimer != null) {
                recordingLapseTimeTimer.cancel();
            }

            lapseTime = startTime;
            recordingLapseTimeTimer = new Timer(true);
            previewView.setRecordingTimeVisibility(View.VISIBLE);

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            previewView.setRecordingTime(ConvertTools.secondsToHours(lapseTime++));
                        }
                    });
                }
            };
            recordingLapseTimeTimer.schedule(timerTask, 0, 1000);
        }
    }

    private void stopRecordingLapseTimeTimer() {
        if (recordingLapseTimeTimer != null) {
            recordingLapseTimeTimer.cancel();
        }
        previewView.setRecordingTime("00:00:00");
        previewView.setRecordingTimeVisibility(View.GONE);
    }

    public void changePreviewMode(int previewMode) {
        AppLog.d(TAG, "changePreviewMode previewMode=" + previewMode);
        AppLog.d(TAG, "changePreviewMode curMode=" + curMode);
        long timeInterval = System.currentTimeMillis() - lastCilckTime;
        AppLog.d(TAG, "repeat click: timeInterval=" + timeInterval);
        if (System.currentTimeMillis() - lastCilckTime < 2000) {
            AppLog.d(TAG, "repeat click: timeInterval < 2000");
            return;
        } else {
            lastCilckTime = System.currentTimeMillis();
        }
        modeSwitchBeep.start();
        if (previewMode == PreviewMode.APP_STATE_VIDEO_MODE) {
            if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                    curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE ||
                    curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                MyToast.show(activity, R.string.stream_error_capturing);
                return;
            } else if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW ||
                    curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                    curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
                stopPreview();
                changeCameraMode(PreviewMode.APP_STATE_VIDEO_PREVIEW, ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE);
            }
        } else if (previewMode == PreviewMode.APP_STATE_STILL_MODE) {
            if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE ||
                    curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                    curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                MyToast.show(activity, R.string.stream_error_capturing);
                return;
            } else if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW ||
                    curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                    curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
                stopPreview();
//                cameraAction.changePreviewMode(ICatchCamPreviewMode.ICH_CAM_STILL_PREVIEW_MODE);
                changeCameraMode(PreviewMode.APP_STATE_STILL_PREVIEW, ICatchCamPreviewMode.ICH_CAM_STILL_PREVIEW_MODE);
            }

        } else if (previewMode == PreviewMode.APP_STATE_TIMELAPSE_MODE) {
            if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
                MyToast.show(activity, R.string.stream_error_capturing);
                return;
            } else if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW || curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
                stopPreview();
                if (curCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_VIDEO) {
                    changeCameraMode(APP_STATE_TIMELAPSE_VIDEO_PREVIEW, ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_VIDEO_PREVIEW_MODE);
                } else if (curCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_STILL) {
                    changeCameraMode(PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW, ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_STILL_PREVIEW_MODE);
                }
            }
        }
    }

    private void startPhotoCapture() {
        previewView.setCaptureBtnEnability(false);
        previewView.setCaptureBtnBackgroundResource(R.drawable.still_capture_btn_off);
        PhotoCapture photoCapture = new PhotoCapture();
        if (cameraProperties.hasFuction(0xD7F0)) {
            photoCapture.addOnStopPreviewListener(new PhotoCapture.OnStopPreviewListener() {
                @Override
                public void onStop() {
                    if (!cameraProperties.hasFuction(0xd704)) {
                        stopPreview();
                    }
                }
            });
            photoCapture.startCapture();
        } else {
            stillCaptureStartBeep.start();
            if (!cameraProperties.hasFuction(0xd704)) {
                stopPreview();
            }
//            stopPreview();
            previewHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!cameraAction.capturePhoto()){
                        curMode = PreviewMode.APP_STATE_STILL_PREVIEW;
                        MyToast.show(activity,R.string.text_operation_failed);
                    }

                }
            }, 500);
//            CameraAction.getInstance().capturePhoto();
        }
    }

    public boolean disconnectCamera() {
        return curCamera.disconnect();
    }

    public void delConnectFailureListener() {
        GlobalInfo.getInstance().enableConnectCheck(false);
    }

    public void unregisterWifiSSReceiver() {
        if (wifiSSReceiver != null) {
            activity.unregisterReceiver(wifiSSReceiver);
        }

    }

    public void zoomIn() {
        if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            return;
        }
        zoomInOut.zoomIn();
        previewView.updateZoomViewProgress(cameraProperties.getCurrentZoomRatio());
    }

    public void zoomOut() {
        if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            return;
        }
        zoomInOut.zoomOut();
        previewView.updateZoomViewProgress(cameraProperties.getCurrentZoomRatio());
    }

    public void zoomBySeekBar() {

        zoomInOut.addZoomCompletedListener(new ZoomInOut.ZoomCompletedListener() {
            @Override
            public void onCompleted(final float currentZoomRate) {
                previewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        AppLog.i(TAG, "addZoomCompletedListener currentZoomRate =" + currentZoomRate);
                        previewView.updateZoomViewProgress(currentZoomRate);
                    }
                });
            }
        });
        zoomInOut.startZoomInOutThread(this);
        MyProgressDialog.showProgressDialog(activity, null);
    }

    public void showZoomView() {
        if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE || curMode == PreviewMode
                .APP_STATE_TIMELAPSE_VIDEO_CAPTURE || (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_DATE_STAMP) == true && ICatchCamDateStamp
                .ICH_CAM_DATE_STAMP_OFF != cameraProperties.getCurrentDateStamp())) {
            return;
        }
        previewView.showZoomView();
    }

    public float getMaxZoomRate() {
        return previewView.getZoomViewMaxZoomRate();
    }

    public float getZoomViewProgress() {
        AppLog.d(TAG, "getZoomViewProgress value=" + previewView.getZoomViewProgress());
        return previewView.getZoomViewProgress();
    }

    public void showSettingDialog(int position) {
        OptionSetting optionSetting = new OptionSetting();
        if (settingMenuList != null && settingMenuList.size() > 0) {
            optionSetting.addSettingCompleteListener(new OnSettingCompleteListener() {
                @Override
                public void onOptionSettingComplete() {
                    AppLog.d(TAG, "onOptionSettingComplete");
                    settingMenuList = UIDisplaySource.getinstance().getList(currentSettingMenuMode, curCamera);
                    settingListAdapter.notifyDataSetChanged();
                }

                @Override
                public void settingVideoSizeComplete() {
                    AppLog.d(TAG, "settingVideoSizeComplete curMode=" + curMode);
//                    if (curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
//                        cameraProperties.changePreviewMode(ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_VIDEO_PREVIEW_MODE);
//                    } else if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
//                        cameraProperties.changePreviewMode(ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE);
//                    }
                }

                @Override
                public void settingTimeLapseModeComplete(int timeLapseMode) {
                    if (timeLapseMode == TimeLapseMode.TIME_LAPSE_MODE_STILL) {
//                        boolean ret = cameraProperties.changePreviewMode(ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_STILL_PREVIEW_MODE);
//                        if (ret) {
//                            curMode = PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW;
//                        }
                        curMode = PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW;
                    } else if (timeLapseMode == TimeLapseMode.TIME_LAPSE_MODE_VIDEO) {
//                        boolean ret = cameraProperties.changePreviewMode(ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_VIDEO_PREVIEW_MODE);
//                        if (ret) {
//                            curMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW;
//                        }
                        curMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW;
                    }
                }
            });
            optionSetting.showSettingDialog(settingMenuList.get(position).name, activity);
        }
    }

    public void showPvModePopupWindow() {
        AppLog.d(TAG, "showPvModePopupWindow curMode=" + curMode);
        if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE ||
                curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            MyToast.show(activity, R.string.stream_error_capturing);
            return;
        }
        previewView.showPopupWindow(curMode);
        if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_VIDEO)) {
            previewView.setVideoRadioBtnVisibility(View.VISIBLE);
        }
        if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_TIMELAPSE)) {
            previewView.setTimepLapseRadioBtnVisibility(View.VISIBLE);
        }
        if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
            previewView.setCaptureRadioBtnChecked(true);
        } else if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            previewView.setVideoRadioBtnChecked(true);
        } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
            previewView.setTimepLapseRadioChecked(true);
        }
    }

    public void startYouTubeLive() {
        if (!isLive) {
            if (!panoramaPreviewPlayback.isStreamSupportPublish()) {
                Toast.makeText(activity, "Not support Publish Streaming", Toast.LENGTH_SHORT).show();
                return;
            }
            final String directoryPath = activity.getExternalCacheDir() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
            final String fileName = AppInfo.FILE_GOOGLE_TOKEN;
            final GoogleToken googleToken = (GoogleToken) FileTools.readSerializable(directoryPath + fileName);
            AppLog.d(TAG, "refreshAccessToken googleToken=" + googleToken);

//            final GoogleToken googleToken = null;
            if (googleToken != null && googleToken.getRefreshToken() != null && googleToken.getRefreshToken() != "") {
                final String refreshToken = googleToken.getRefreshToken();
                AppLog.d(TAG, "readSerializable RefreshToken=" + refreshToken);
                MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String accessToken = null;
                        try {
                            accessToken = GoogleAuthTool.refreshAccessToken(activity, refreshToken);
                        } catch (IOException e) {
                            previewHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MyProgressDialog.closeProgressDialog();
                                    MyToast.show(activity, R.string.message_refreshAccessToken_IOException);
                                }
                            });
                            e.printStackTrace();
                        }
                        if (accessToken != null) {
                            AppLog.d(TAG, "refreshAccessToken accessToken=" + accessToken);
                            googleToken.setCurrentAccessToken(accessToken);
                            FileTools.saveSerializable(directoryPath + fileName, googleToken);
                            previewHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MyProgressDialog.closeProgressDialog();
                                    MyToast.show(activity, R.string.message_start_live);
                                    startYoutubeStreamPublish();
//                                    startOrStopYouTubeLiveAlreadySignin();
                                }
                            }, 1000);
                        } else {
                            previewHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MyProgressDialog.closeProgressDialog();
                                    MyToast.show(activity, R.string.message_click_disconnect_and_relogin);
                                }
                            });
                        }
                    }
                }).start();

            } else {
                MyToast.show(activity, R.string.message_login_to_google_account);
            }

        } else {
            AppLog.d(TAG, "stop push publish...");
//            boolean ret = PreviewStream.getInstance().stopLive(curCamera.getpreviewStreamClient());
            boolean ret = panoramaPreviewPlayback.stopPublishStreaming();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        CreateBroadcast.stopLive();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            isLive = false;
            previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
        }
    }

    public void startYoutubeStreamPublish() {
        String directoryPath = activity.getExternalCacheDir() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
        String fileName = AppInfo.FILE_GOOGLE_TOKEN;
        GoogleToken googleToken = (GoogleToken) FileTools.readSerializable(directoryPath + fileName);
        String accessToken = googleToken.getAccessToken();
        String refreshToken = googleToken.getRefreshToken();
        final GoogleClientSecrets clientSecrets = YoutubeCredential.readClientSecrets(activity);
        AppLog.d(TAG, "readSerializable accessToken=" + accessToken);
        AppLog.d(TAG, "readSerializable refreshToken=" + refreshToken);
        if (accessToken == null) {
            MyToast.show(activity, R.string.message_failed_to_Youtube_live_OAuth2AccessToken_is_null);
            return;
        }
        previewHandler.post(new Runnable() {
            @Override
            public void run() {
                MyProgressDialog.showProgressDialog(activity, R.string.wait);
            }
        });
        final Credential credential;
        try {
            credential = YoutubeCredential.authorize(clientSecrets, accessToken, refreshToken);
        } catch (IOException e) {
            AppLog.d(TAG, "authorize IOException");
            e.printStackTrace();
            return;
        }
        AppLog.d(TAG, "success credential=" + credential);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String pushUrl = CreateBroadcast.createLive(activity, credential);
                AppLog.d(TAG, "push url..." + pushUrl);
                if (pushUrl == null) {
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                            MyToast.show(activity, R.string.message_failed_to_Youtube_live_pushUrl_is_null);
                        }
                    });
                    return;
                }
                final boolean ret = panoramaPreviewPlayback.startPublishStreaming(pushUrl);
                if (ret == false) {
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                            MyToast.show(activity, R.string.message_failed_to_start_publish_streaming);
                        }
                    });
                    return;
                }
                final String shareUrl = CreateBroadcast.startLive();
                AppLog.d(TAG, "shareUrl =" + shareUrl);
                if (shareUrl == null) {
                    panoramaPreviewPlayback.stopPublishStreaming();
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                            MyToast.show(activity, R.string.message_failed_to_YouTube_live_shareUrl_is_null);
                        }
                    });
                    return;
                } else {
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            isLive = true;
                            liveMode = LiveMode.MODE_YOUTUBE_LIVE;
                            previewView.setYouTubeBtnTxv(R.string.end_youtube_live);
                            showSharedUrlDialog(activity, shareUrl);
                        }
                    });
                }
            }
        }).start();
    }

    public void gotoGoogleAccountManagement() {
        if (isLive) {
            MyToast.show(activity, R.string.message_please_stop_live);
        } else {
            destroyPreview();
            Intent intent = new Intent();
            intent.setClass(activity, LoginGoogleActivity.class);
            activity.startActivity(intent);
        }
    }

    public void startOrStopFacebookLive() {
        if (!panoramaPreviewPlayback.isStreamSupportPublish()) {
            Toast.makeText(activity, "Not support Publish Streaming", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isLive) {
            boolean ret = panoramaPreviewPlayback.stopPublishStreaming();
            MyProgressDialog.showProgressDialog(activity, R.string.wait);
            final String videoId = FacebookInfo.getVideoId();
            AccessToken.refreshCurrentAccessTokenAsync(new AccessToken.AccessTokenRefreshCallback() {
                @Override
                public void OnTokenRefreshed(AccessToken accessToken) {
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            Toast.makeText(activity, "end facebook live", Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (accessToken != null && videoId != null) {
                        GraphOperation.endLiveStream(accessToken, videoId);
                    }
                }

                @Override
                public void OnTokenRefreshFailed(FacebookException exception) {
                    MyProgressDialog.closeProgressDialog();
                }
            });
            isLive = false;
            previewView.setFacebookBtnTxv(R.string.facebook_start_live);
        } else {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
//            MyToast.show(activity, "accessToken=" + accessToken);
            if (accessToken != null) {
                MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
                GraphOperation.getStreamByToken(accessToken, new GraphOperation.RequestCallback() {
                    @Override
                    public void onCompleted(String url, String videoId) {
                        //返回URL后执行其它操作
                        AppLog.d(TAG, " onCompleted url=" + url);
                        boolean ret = panoramaPreviewPlayback.startPublishStreaming(url);
                        Toast.makeText(activity, "start Facebook Live ret=" + ret + " url=" + url + " id=" + videoId, Toast.LENGTH_SHORT).show();
                        isLive = true;
                        liveMode = LiveMode.MODE_FACEBOOK_LIVE;
                        previewHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                MyProgressDialog.closeProgressDialog();
                                previewView.setFacebookBtnTxv(R.string.facebook_end_live);
                            }
                        });
                    }

                    @Override
                    public void onError(final String errorInfo) {
                        previewHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                MyProgressDialog.closeProgressDialog();
                                Toast.makeText(activity, "startPublishStreaming " + errorInfo, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } else {
                destroyPreview();
                Intent intent = new Intent();
                intent.setClass(activity, LoginFacebookActivity.class);
//                activity.startActivity(intent);
                activity.startActivityForResult(intent, 0);
            }
        }
    }


    private class PreviewHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            Tristate ret = Tristate.FALSE;

            switch (msg.what) {
                case SDKEvent.EVENT_BATTERY_ELETRIC_CHANGED:
                    AppLog.i(TAG, "receive EVENT_BATTERY_ELETRIC_CHANGED power =" + msg.arg1);
                    //need to update battery eletric
                    int resid = ThumbnailOperation.getBatteryLevelIcon(msg.arg1);
                    if (resid > 0) {
                        previewView.setBatteryIcon(resid);
                        if (resid == R.drawable.ic_battery_charging_green24dp) {
                            AppDialog.showLowBatteryWarning(activity);
                        }
                    }
                    break;
                case SDKEvent.EVENT_CONNECTION_FAILURE:
                    AppLog.i(TAG, "receive EVENT_CONNECTION_FAILURE");
                    stopPreview();
                    delEvent();
                    disconnectCamera();
                    break;
                case SDKEvent.EVENT_SD_CARD_FULL:
                    AppLog.i(TAG, "receive EVENT_SD_CARD_FULL");
                    AppDialog.showDialogWarn(activity, R.string.dialog_card_full);
                    break;
                case SDKEvent.EVENT_VIDEO_OFF://only receive if fw request to stopMPreview video recording
                    AppLog.i(TAG, "receive EVENT_VIDEO_OFF:curMode=" + curMode);
                    if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                        if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
                            curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
                        } else {
                            curMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW;
                        }
                        stopVideoCaptureButtomChangeTimer();
                        stopRecordingLapseTimeTimer();
                        previewView.setRemainRecordingTimeText(ConvertTools.secondsToMinuteOrHours(cameraProperties.getRecordingRemainTime()));
                    }
                    break;
                case SDKEvent.EVENT_VIDEO_ON:
                    AppLog.i(TAG, "receive EVENT_VIDEO_ON:curMode =" + curMode);
                    // video from camera when file exceeds 4g
                    if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
                        curMode = PreviewMode.APP_STATE_VIDEO_CAPTURE;
                        startVideoCaptureButtomChangeTimer();
                        startRecordingLapseTimeTimer(0);
                    } else if (curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
                        curMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE;
                        startVideoCaptureButtomChangeTimer();
                        startRecordingLapseTimeTimer(0);
                    }
                    break;
                case SDKEvent.EVENT_CAPTURE_START:
                    AppLog.i(TAG, "receive EVENT_CAPTURE_START:curMode=" + curMode);
                    if (curMode != PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                        return;
                    }
                    continuousCaptureBeep.start();
                    MyToast.show(activity, R.string.capture_start);
                    break;
                case SDKEvent.EVENT_CAPTURE_COMPLETED:
                    AppLog.i(TAG, "receive EVENT_CAPTURE_COMPLETED:curMode=" + curMode);
                    if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE) {
                        previewView.setCaptureBtnEnability(true);
                        if (!cameraProperties.hasFuction(0xd704)) {
                            startPreview();
                        }
                        previewView.setCaptureBtnBackgroundResource(R.drawable.still_capture_btn);

                        previewView.setRemainCaptureCount(new Integer(cameraProperties.getRemainImageNum()).toString());
                        curMode = PreviewMode.APP_STATE_STILL_PREVIEW;
                        return;
                    }
                    if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                        previewView.setCaptureBtnEnability(true);
                        previewView.setCaptureBtnBackgroundResource(R.drawable.still_capture_btn);
                        previewView.setRemainCaptureCount(new Integer(cameraProperties.getRemainImageNum()).toString());
                        MyToast.show(activity, R.string.capture_completed);
                    }

                    break;
                case SDKEvent.EVENT_FILE_ADDED:
                    AppLog.i(TAG, "EVENT_FILE_ADDED");
//                    if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
//                        lapseTime = 0;
//                    }
                    break;

                case SDKEvent.EVENT_TIME_LAPSE_STOP:
                    AppLog.i(TAG, "receive EVENT_TIME_LAPSE_STOP:curMode=" + curMode);
                    if (curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                        if (cameraAction.stopTimeLapse()) {
                            stopVideoCaptureButtomChangeTimer();
                            stopRecordingLapseTimeTimer();
                            previewView.setRemainCaptureCount(new Integer(cameraProperties.getRemainImageNum()).toString());
                            curMode = APP_STATE_TIMELAPSE_VIDEO_PREVIEW;
                        }

                    } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                        if (cameraAction.stopTimeLapse()) {
                            stopRecordingLapseTimeTimer();
                            previewView.setRemainCaptureCount(new Integer(cameraProperties.getRemainImageNum()).toString());
                            curMode = PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW;
                        }
                    }
                    break;
                case SDKEvent.EVENT_VIDEO_RECORDING_TIME:
                    AppLog.i(TAG, "receive EVENT_VIDEO_RECORDING_TIME");
                    startRecordingLapseTimeTimer(0);
                    break;
                case SDKEvent.EVENT_FILE_DOWNLOAD:
                    AppLog.i(TAG, "receive EVENT_FILE_DOWNLOAD");
                    AppLog.d(TAG, "receive EVENT_FILE_DOWNLOAD  msg.arg1 =" + msg.arg1);
                    if (AppInfo.autoDownloadAllow == false) {
                        AppLog.d(TAG, "GlobalInfo.autoDownload == false");

                        return;
                    }
                    final String path;
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        path = Environment.getExternalStorageDirectory().toString() + AppInfo.AUTO_DOWNLOAD_PATH;
                    } else {
                        return;
                    }
                    File directory = new File(path);

                    if (FileTools.getFileSize(directory) / 1024 >= AppInfo.autoDownloadSizeLimit * 1024 * 1024) {
                        AppLog.d(TAG, "can not download because size limit");
                        return;
                    }
                    final ICatchFile file = (ICatchFile) msg.obj;
                    FileOper.createDirectory(path);
                    new Thread() {
                        @Override
                        public void run() {
                            AppLog.d(TAG, "receive downloadFile file =" + file);
                            AppLog.d(TAG, "receive downloadFile path =" + path);
                            boolean retvalue = fileOperation.downloadFile(file, path + file.getFileName());
                            if (retvalue == true) {
                                previewHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String path1 = path + file.getFileName();
                                        Bitmap bitmap = BitmapTools.getImageByPath(path1, 150, 150);
                                        previewView.setAutoDownloadBitmap(bitmap);
                                    }
                                });
                            }
                            AppLog.d(TAG, "receive downloadFile retvalue =" + retvalue);
                        }
                    }.start();
                    break;
                case AppMessage.SETTING_OPTION_AUTO_DOWNLOAD:
                    AppLog.d(TAG, "receive SETTING_OPTION_AUTO_DOWNLOAD");
                    Boolean switcher = (Boolean) msg.obj;
                    if (switcher == true) {
                        // AutoDownLoad
                        AppInfo.autoDownloadAllow = true;
                        previewView.setAutoDownloadVisibility(View.VISIBLE);
                    } else {
                        AppInfo.autoDownloadAllow = false;
                        previewView.setAutoDownloadVisibility(View.GONE);
                    }
                    break;
                case SDKEvent.EVENT_SDCARD_INSERT:
                    AppLog.i(TAG, "receive EVENT_SDCARD_INSERT");
                    AppDialog.showDialogWarn(activity, R.string.dialog_card_inserted);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    public void addEvent() {
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_FULL);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_BATTERY_LEVEL_CHANGED);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_VIDEO_OFF);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_VIDEO_ON);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_START);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_COMPLETE);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FILE_ADDED);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_CONNECTION_DISCONNECTED);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_TIMELAPSE_STOP);
        sdkEvent.addCustomizeEvent(0x5001);// video recording event
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FILE_DOWNLOAD);
//        sdkEvent.addCustomizeEvent(0x3701);// Insert SD card event
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_IN);

//        addPanoramaEventListener();
    }

//    public void addPanoramaEventListener() {
//        if (panoramaPreviewPlayback == null) {
//            return;
//        }
//        sdkEvent.addPanoramaEventListener( ICatchGLEventID.ICH_GL_EVENT_STREAM_STATUS );
//        sdkEvent.addPanoramaEventListener( ICatchGLEventID.ICH_GL_EVENT_STREAM_CLOSED );
//    }
//
//    public void removePanoramaEventListener() {
//        if (panoramaPreviewPlayback == null) {
//            return;
//        }
//        sdkEvent.removePanoramaEventListener( ICatchGLEventID.ICH_GL_EVENT_STREAM_STATUS );
//        sdkEvent.removePanoramaEventListener( ICatchGLEventID.ICH_GL_EVENT_STREAM_CLOSED );
//    }


    public void delEvent() {
        sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_FULL);
        sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_BATTERY_LEVEL_CHANGED);
        sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_COMPLETE);
        sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_START);
        sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_VIDEO_OFF);
        sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FILE_ADDED);
        sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_VIDEO_ON);
        sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_CONNECTION_DISCONNECTED);
        sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_TIMELAPSE_STOP);
        sdkEvent.delCustomizeEventListener(0x5001);
        sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FILE_DOWNLOAD);
//        sdkEvent.delCustomizeEventListener(0x3701);// Insert SD card event
        sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_IN);
    }

    public void loadSettingMenuList() {
        AppLog.i(TAG, "setupBtn is clicked:allowClickButtoms=" + allowClickButtoms);
        if (allowClickButtoms == false) {
            return;
        }
        allowClickButtoms = false;
        if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            AppToast.show(activity, R.string.stream_error_recording, Toast.LENGTH_SHORT);
        } else if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE) {
            AppToast.show(activity, R.string.stream_error_capturing, Toast.LENGTH_SHORT);
        } else if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
            previewView.setSetupMainMenuVisibility(View.VISIBLE);
            currentSettingMenuMode = UIDisplaySource.CAPTURE_SETTING_MENU;
            if (settingMenuList != null) {
                settingMenuList.clear();
            }
            if (settingListAdapter != null) {
                settingListAdapter.notifyDataSetChanged();
            }
            previewView.setSettingBtnVisible(false);
            previewView.setBackBtnVisibility(true);
            previewView.setActionBarTitle(R.string.title_setting);
            MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
            stopPreview();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    previewHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            settingMenuList = UIDisplaySource.getinstance().getList(UIDisplaySource.CAPTURE_SETTING_MENU, curCamera);
                            settingListAdapter = new SettingListAdapter(activity, settingMenuList, previewHandler);
                            previewView.setSettingMenuListAdapter(settingListAdapter);
                            MyProgressDialog.closeProgressDialog();
                        }
                    }, 500);
                }
            }).start();

        } else if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            previewView.setSetupMainMenuVisibility(View.VISIBLE);
            currentSettingMenuMode = UIDisplaySource.VIDEO_SETTING_MENU;
            MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
            if (settingMenuList != null) {
                settingMenuList.clear();
            }
            if (settingListAdapter != null) {
                settingListAdapter.notifyDataSetChanged();
            }
            previewView.setSettingBtnVisible(false);
            previewView.setBackBtnVisibility(true);
            previewView.setActionBarTitle(R.string.title_setting);
            stopPreview();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    previewHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            settingMenuList = UIDisplaySource.getinstance().getList(UIDisplaySource.VIDEO_SETTING_MENU, curCamera);
                            settingListAdapter = new SettingListAdapter(activity, settingMenuList, previewHandler);
                            previewView.setSettingMenuListAdapter(settingListAdapter);
                            MyProgressDialog.closeProgressDialog();
                        }
                    }, 500);
                }
            }).start();

        } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW || curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
            previewView.setSetupMainMenuVisibility(View.VISIBLE);
            currentSettingMenuMode = UIDisplaySource.TIMELAPSE_SETTING_MENU;
            MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
            if (settingMenuList != null) {
                settingMenuList.clear();
            }
            if (settingListAdapter != null) {
                settingListAdapter.notifyDataSetChanged();
            }
            previewView.setSettingBtnVisible(false);
            previewView.setBackBtnVisibility(true);
            previewView.setActionBarTitle(R.string.title_setting);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    previewHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            settingMenuList = UIDisplaySource.getinstance().getList(UIDisplaySource.TIMELAPSE_SETTING_MENU, curCamera);
                            settingListAdapter = new SettingListAdapter(activity, settingMenuList, previewHandler);
                            previewView.setSettingMenuListAdapter(settingListAdapter);
                            stopPreview();
                            MyProgressDialog.closeProgressDialog();
                        }
                    }, 500);
                }
            }).start();

        } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
            AppToast.show(activity, R.string.stream_error_recording, Toast.LENGTH_SHORT);
        } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            AppToast.show(activity, R.string.stream_error_capturing, Toast.LENGTH_SHORT);
        }
        allowClickButtoms = true;
    }

    @Override
    public void isAppBackground() {
        super.isAppBackground();
    }

    @Override
    public void finishActivity() {
        Tristate ret = Tristate.NORMAL;
        if (previewView.getSetupMainMenuVisibility() == View.VISIBLE) {
            AppLog.i(TAG, "onKeyDown curMode==" + curMode);
            previewView.setSetupMainMenuVisibility(View.GONE);
            previewView.setSettingBtnVisible(true);
            previewView.setBackBtnVisibility(false);
            previewView.setActionBarTitle(R.string.title_preview);
            if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
                AppLog.i(TAG, "onKeyDown curMode == APP_STATE_VIDEO_PREVIEW");
//                changePreviewMode(curMode);
                changeCameraMode(curMode, ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE);
//                startPreview( ICatchCamPreviewMode.ICH_VIDEO_PREVIEW_MODE );
                // normal state, app show preview
            } else if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
                changeCameraMode(curMode, ICatchCamPreviewMode.ICH_CAM_STILL_PREVIEW_MODE);
            } else if (curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
//                changePreviewMode(curMode);
                AppLog.i(TAG, "onKeyDown curMode == APP_STATE_TIMELAPSE_PREVIEW_VIDEO");
                curCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_VIDEO;
                changeCameraMode(curMode, ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_VIDEO_PREVIEW_MODE);
//                startPreview( ICatchCamPreviewMode.ICH_TIMELAPSE_VIDEO_PREVIEW_MODE );
                // normal state, app show preview
            } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW) {
//                changePreviewMode(curMode);
                AppLog.i(TAG, "onKeyDown curMode == APP_STATE_TIMELAPSE_PREVIEW_STILL");
                curCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_STILL;
                changeCameraMode(curMode, ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_STILL_PREVIEW_MODE);
                // normal state, app show preview
            } else {
                startPreview();
                createUIByMode(curMode);
            }
//            AppDialog.showDialogWarn( activity, R.string.text_preview_hint_info );
        } else {
            destroyPreview();
            super.finishActivity();
        }
    }

    @Override
    public void redirectToAnotherActivity(final Context context, final Class<?> cls) {
        AppLog.i(TAG, "pbBtn is clicked curMode=" + curMode);
        if (allowClickButtoms == false) {
            AppLog.i(TAG, "do not allow to response button clicking");
            return;
        }
        allowClickButtoms = false;
        if (cameraProperties.isSDCardExist() == false) {
            AppDialog.showDialogWarn(activity, R.string.dialog_card_lose);
            allowClickButtoms = true;
            return;
        }
        AppLog.i(TAG, "curMode =" + curMode);
        if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW || curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW || curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW
                || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW) {
            destroyPreview();
            delEvent();
            allowClickButtoms = true;
            //BSP-1209
            MyProgressDialog.showProgressDialog(context, R.string.action_processing);
            previewHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();
                    Intent intent = new Intent();
                    AppLog.i(TAG, "intent:start PbMainActivity.class");
                    intent.setClass(context, cls);
                    context.startActivity(intent);
                    AppLog.i(TAG, "intent:end start PbMainActivity.class");
                }
            }, 500);
            //BSP-1209
            return;
        } else if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
            MyToast.show(activity, R.string.stream_error_recording);
        } else if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            MyToast.show(activity, R.string.stream_error_capturing);
        }
        allowClickButtoms = true;
        AppLog.i(TAG, "end processing for responsing pbBtn clicking");
    }

    private class WifiSSReceiver extends BroadcastReceiver {
        private WifiManager wifi;

        public WifiSSReceiver() {
            super();

            wifi = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            changeWifiStatusIcon();
        }

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            changeWifiStatusIcon();
        }

        private void changeWifiStatusIcon() {
            WifiInfo info = wifi.getConnectionInfo();
            if (info.getBSSID() != null) {
                int strength = WifiManager.calculateSignalLevel(info.getRssi(), 8);

                AppLog.d(TAG, "change Wifi Status：" + strength);
                switch (strength) {
                    case 0:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_0_bar_green_24dp);
                        break;
                    case 1:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_1_bar_green_24dp);
                        break;
                    case 2:
                    case 3:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_2_bar_green_24dp);
                        break;
                    case 4:
                    case 5:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_3_bar_green_24dp);
                        break;
                    case 6:
                    case 7:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_4_bar_green_24dp);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void startPreview() {
        AppLog.d(TAG, "start startPreview hasInitSurface=" + hasInitSurface);
        //ICOM-4274 begin add 20170906 b.jiang
        boolean isSupportPreview = cameraProperties.isSupportPreview();
        AppLog.d(TAG, "start startPreview isSupportPreview=" + isSupportPreview);
        if (!isSupportPreview) {
            previewHandler.post(new Runnable() {
                @Override
                public void run() {
                    previewView.setSupportPreviewTxvVisibility(View.VISIBLE);
                }
            });
            return;
        }
        //ICOM-4274 end add 20170906 b.jiang
        if (hasInitSurface == false) {
            return;
        }
        if (panoramaPreviewPlayback == null) {
            AppLog.d(TAG, "null point");
            return;
        }
        if (curCamera.isStreamReady) {
            return;
        }
        if (AppInfo.enableDumpVideo) {
            String streamOutputPath = Environment.getExternalStorageDirectory().toString() + AppInfo.STREAM_OUTPUT_DIRECTORY_PATH;
            FileOper.createDirectory(streamOutputPath);
            try {
                ICatchPancamConfig.getInstance().enableDumpTransportStream(true, streamOutputPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int cacheTime = cameraProperties.getPreviewCacheTime();
        //cache 設置太小會造成畫面不順暢；
//        if (cacheTime > 0 && cacheTime < 200) {
//            cacheTime = 200;
//        }

        cacheTime = 0;
        AppLog.d(TAG,"setPreviewCacheParam cacheTime:" +cacheTime);
        ICatchPancamConfig.getInstance().setPreviewCacheParam(cacheTime,200);
        ICatchStreamParam iCatchStreamParam = getStreamParam();
//        ICatchStreamParam iCatchStreamParam = new ICatchJPEGStreamParam(720, 400, 30, 4000000);
        final Tristate retValue;
        if (AppInfo.enableRender) {
            if(PanoramaTools.isPanorama(iCatchStreamParam.getWidth(),iCatchStreamParam.getHeight())){
                registerGyroscopeSensor();
            }
            retValue = panoramaPreviewPlayback.start(iCatchStreamParam, !AppInfo.disableAudio);
        } else {
            retValue = cameraStreaming.start(iCatchStreamParam, !AppInfo.disableAudio);
        }

        if (retValue == Tristate.NORMAL) {
            curCamera.isStreamReady = true;
        } else {
            curCamera.isStreamReady = false;
        }

        previewHandler.post(new Runnable() {
            @Override
            public void run() {
                if (retValue == Tristate.ABNORMAL) {
                    previewView.setSupportPreviewTxvVisibility(View.VISIBLE);
                } else if (retValue == Tristate.NORMAL) {
                    previewView.setSupportPreviewTxvVisibility(View.GONE);
                } else {
                    previewView.setSupportPreviewTxvVisibility(View.GONE);
                    MyToast.show(activity, R.string.open_preview_failed);
                }
            }
        });

        AppLog.d(TAG, "end startPreview retValue=" + retValue);
    }

    private ICatchStreamParam getStreamParam(){
        StreamInfo streamInfo = null;
        if (curCamera.getCameraType() == CameraType.USB_CAMERA) {
            streamInfo = new StreamInfo(curCodecType, curVideoWidth, curVideoHeight, 5000000, curVideoFps);
            AppLog.d(TAG, "start startPreview videoWidth=" + curVideoWidth + " videoHeight=" + curVideoHeight + " videoFps=" + curVideoFps + " curCodecType=" +
                    curCodecType);
        } else {
            String streamUrl = cameraProperties.getCurrentStreamInfo();
            AppLog.d(TAG, " start startStreamAndPreview streamUrl=[" + streamUrl + "]");
            if (streamUrl != null) {
                streamInfo = StreamInfoConvert.convertToStreamInfoBean(streamUrl);
            }
        }
        ICatchStreamParam iCatchStreamParam = null;
        if (streamInfo == null) {
            iCatchStreamParam = new ICatchH264StreamParam(1920, 960, 30);
        } else if (streamInfo.mediaCodecType.equals("MJPG")) {
            iCatchStreamParam = new ICatchJPEGStreamParam(streamInfo.width, streamInfo.height, streamInfo.fps, streamInfo.bitrate);
        } else if (streamInfo.mediaCodecType.equals("H264")) {
            iCatchStreamParam = new ICatchH264StreamParam(streamInfo.width, streamInfo.height, streamInfo.fps, streamInfo.bitrate);
        } else {
            iCatchStreamParam = new ICatchH264StreamParam(1920, 960, 30);
        }

        return iCatchStreamParam;
    }

    public void stopPreview() {
        if (AppInfo.enableDumpVideo) {
            ICatchPancamConfig.getInstance().disableDumpTransportStream(true);
        }

        if (AppInfo.enableRender) {
            removeGyroscopeListener();
            if (panoramaPreviewPlayback != null && curCamera.isStreamReady) {
                curCamera.isStreamReady = false;
                panoramaPreviewPlayback.stop();
            }
        } else {
            if (curCamera.isStreamReady) {
                curCamera.isStreamReady = false;
                cameraStreaming.stop();
            }
        }
    }

    public void locate(float progerss) {
        panoramaPreviewPlayback.locate(progerss);
    }

    //pancamGLRelease surface;
    public void destroyPreview() {
//        removePanoramaEventListener();
        if (AppInfo.enableDumpVideo) {
            ICatchPancamConfig.getInstance().disableDumpTransportStream(true);
        }
        hasInitSurface = false;
        if (AppInfo.enableRender) {
            removeGyroscopeListener();
            if (panoramaPreviewPlayback != null && curCamera.isStreamReady) {
                if (iCatchSurfaceContext != null) {
                    AppLog.d(TAG, "destroyPreview.....");
                    panoramaPreviewPlayback.removeSurface(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE, iCatchSurfaceContext);
                }
                panoramaPreviewPlayback.stop();
                panoramaPreviewPlayback.release();
                curCamera.isStreamReady = false;
            }
        } else {
            if (curCamera.isStreamReady) {
                curCamera.isStreamReady = false;
                cameraStreaming.stop();
            }
        }
    }

    public void rotateB(MotionEvent e, float prevX, float prevY) {
        ICatchGLPoint prev = new ICatchGLPoint(prevX, prevY);
        ICatchGLPoint curr = new ICatchGLPoint(e.getX(), e.getY());
        panoramaPreviewPlayback.rotate(prev, curr);
    }

    public void onSufaceViewTouchDown(MotionEvent event) {
        touchMode = TouchMode.DRAG;
        mPreviousY = event.getY();
        mPreviousX = event.getX();
        beforeLenght = 0;
        afterLenght = 0;
    }

    public void onSufaceViewPointerDown(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            touchMode = TouchMode.ZOOM;
            beforeLenght = getDistance(event);//
        }
    }

    public void onSufaceViewTouchMove(MotionEvent event) {
        if (touchMode == TouchMode.DRAG) {
            rotateB(event, mPreviousX, mPreviousY);
            mPreviousY = event.getY();
            mPreviousX = event.getX();
        } else if (touchMode == TouchMode.ZOOM) {
            afterLenght = getDistance(event);//
            float gapLenght = afterLenght - beforeLenght;
            if (Math.abs(gapLenght) > 5f) {
                float scale_temp = afterLenght / beforeLenght;
                this.setScale(scale_temp);
                beforeLenght = afterLenght;
            }
        }
    }

    float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) StrictMath.sqrt(x * x + y * y);
    }

    void setScale(float scale) {
        if ((currentZoomRate >= MAX_ZOOM && scale > 1) || (currentZoomRate <= MIN_ZOOM && scale < 1)) {
            return;
        }
        float temp = currentZoomRate * scale;
        if (scale > 1) {
            if (temp <= MAX_ZOOM) {
                currentZoomRate = currentZoomRate * scale;
                zoom(currentZoomRate);
            } else {
                currentZoomRate = MAX_ZOOM;
                zoom(currentZoomRate);
            }
        } else if (scale < 1) {
            if (temp >= MIN_ZOOM) {
                currentZoomRate = currentZoomRate * scale;
                zoom(currentZoomRate);
            } else {
                currentZoomRate = MIN_ZOOM;
                zoom(currentZoomRate);
            }
        }

    }

    private void zoom(float currentZoomRate) {
        locate(1 / currentZoomRate);
    }

    public void onSufaceViewTouchUp() {
        showZoomView();
        touchMode = TouchMode.NONE;

    }

    public void onSufaceViewTouchPointerUp() {
        touchMode = TouchMode.NONE;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // 从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
            float speedX = event.values[0];
            float speedY = event.values[1];
            float speedZ = event.values[2];
//            AppLog.d(TAG, "onSensorChanged speedX=" + speedX + " speedY=" +speedY + " speedZ=" + speedZ);
            if (Math.abs(speedY) < 0.05 && Math.abs(speedZ) < 0.05) {
                return;
            }
            rotate(speedX, speedY, speedZ, event.timestamp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void rotate(float speedX, float speedY, float speedZ, long timestamp) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        panoramaPreviewPlayback.rotate(rotation, speedX, speedY, speedZ, timestamp);
    }

    private void registerGyroscopeSensor() {
        AppLog.d(TAG, "registerGyroscopeSensor");
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // 注册陀螺仪传感器，并设定传感器向应用中输出的时间间隔类型是SensorManager.SENSOR_DELAY_GAME(20000微秒)
        // SensorManager.SENSOR_DELAY_FASTEST(0微秒)：最快。最低延迟，一般不是特别敏感的处理不推荐使用，该模式可能在成手机电力大量消耗，由于传递的为原始数据，诉法不处理好会影响游戏逻辑和UI的性能
        // SensorManager.SENSOR_DELAY_GAME(20000微秒)：游戏。游戏延迟，一般绝大多数的实时性较高的游戏都是用该级别
        // SensorManager.SENSOR_DELAY_NORMAL(200000微秒):普通。标准延时，对于一般的益智类或EASY级别的游戏可以使用，但过低的采样率可能对一些赛车类游戏有跳帧现象
        // SensorManager.SENSOR_DELAY_UI(60000微秒):用户界面。一般对于屏幕方向自动旋转使用，相对节省电能和逻辑处理，一般游戏开发中不使用
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void removeGyroscopeListener() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    public void setDrawingArea(int width, int height) {
        if (panoramaPreviewPlayback != null && iCatchSurfaceContext != null) {
            AppLog.d(TAG, "start setDrawingArea width=" + width + " height=" + height);
            try {
                iCatchSurfaceContext.setViewPort(0, 0, width, height);
            } catch (IchGLSurfaceNotSetException e) {
                e.printStackTrace();
            }
            AppLog.d(TAG, "end setDrawingArea");
        }
    }

    public void initSurface(SurfaceHolder surfaceHolder) {
        hasInitSurface = false;
        AppLog.i(TAG, "begin initSurface");
        if (panoramaPreviewPlayback == null) {
            return;
        }
        if (AppInfo.enableRender) {
            iCatchSurfaceContext = new ICatchSurfaceContext(surfaceHolder.getSurface());
            ICatchStreamParam iCatchStreamParam = getStreamParam();
            if(iCatchStreamParam!= null&& PanoramaTools.isPanorama(iCatchStreamParam.getWidth(),iCatchStreamParam.getHeight())){
                panoramaPreviewPlayback.enableGLRender();
                panoramaPreviewPlayback.init(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
                panoramaPreviewPlayback.setSurface(ICatchGLSurfaceType.ICH_GL_SURFACE_TYPE_SPHERE, iCatchSurfaceContext);
                previewView.setPanoramaTypeBtnVisibility(View.VISIBLE);
            }else {
                panoramaPreviewPlayback.enableCommonRender(iCatchSurfaceContext);
                previewView.setPanoramaTypeBtnVisibility(View.GONE);
            }
        } else {
            previewView.setPanoramaTypeBtnVisibility(View.GONE);
            cameraStreaming.disnableRender();
            int width = previewView.getSurfaceViewWidth();
            int heigth = previewView.getSurfaceViewHeight();
            AppLog.i(TAG, "SurfaceViewWidth=" + width + " SurfaceViewHeight=" + heigth);
            if (width <= 0 || heigth <= 0) {
                width = 1080;
                heigth = 1920;
            }
            cameraStreaming.setSurface(surfaceHolder);
            cameraStreaming.setViewParam(width, heigth);
        }
        hasInitSurface = true;
        AppLog.i(TAG, "end initSurface");
    }

    public void showSharedUrlDialog(final Context context, final String shareUrl) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.live_shared_url, null);
        final EditText resetTxv = (EditText) view.findViewById(R.id.shared_url);
        final ImageView qrcodeImage = (ImageView) view.findViewById(R.id.shared_url_qrcode);
        Bitmap bitmap = QRCode.createQRCodeWithLogo(shareUrl, QRCode.WIDTH,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_panorama_green_500_48dp));
        qrcodeImage.setImageBitmap(bitmap);

        resetTxv.setText(shareUrl);
        builder.setTitle("Success, share url is:");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setView(view);
        builder.setCancelable(false);
        builder.create().show();
    }

    public void setPanoramaType() {
        if(curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE ){
            panoramaPreviewPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID;
            previewView.setPanoramaTypeBtnSrc(R.drawable.asteroid);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }else if(curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID){
            panoramaPreviewPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R;
            previewView.setPanoramaTypeBtnSrc(R.drawable.vr);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }else{
            panoramaPreviewPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;
            previewView.setPanoramaTypeBtnSrc(R.drawable.panorama);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
}
