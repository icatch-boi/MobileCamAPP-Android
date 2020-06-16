package com.icatch.mobilecam.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
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
import com.icatch.mobilecam.Function.BaseProrertys;
import com.icatch.mobilecam.Function.SDKEvent;
import com.icatch.mobilecam.Function.Setting.OptionSetting;
import com.icatch.mobilecam.Function.Setting.UIDisplaySource;
import com.icatch.mobilecam.Function.USB.USBMonitor;
import com.icatch.mobilecam.Function.live.Facebook.FacebookInfo;
import com.icatch.mobilecam.Function.live.Facebook.GraphOperation;
import com.icatch.mobilecam.Function.live.google.CreateBroadcast;
import com.icatch.mobilecam.Function.live.google.GoogleAuthTool;
import com.icatch.mobilecam.Function.live.google.YoutubeCredential;
import com.icatch.mobilecam.Function.streaming.CameraStreaming;
import com.icatch.mobilecam.Listener.OnSettingCompleteListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.MyCamera.MyCamera;
import com.icatch.mobilecam.Presenter.Interface.BasePresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatch.mobilecam.SdkApi.PanoramaPreviewPlayback;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.GlobalApp.ExitApp;
import com.icatch.mobilecam.data.Message.AppMessage;
import com.icatch.mobilecam.data.Mode.LiveMode;
import com.icatch.mobilecam.data.Mode.PreviewMode;
import com.icatch.mobilecam.data.Mode.TouchMode;
import com.icatch.mobilecam.data.SystemInfo.SystemInfo;
import com.icatch.mobilecam.data.entity.GoogleToken;
import com.icatch.mobilecam.data.entity.SettingMenu;
import com.icatch.mobilecam.data.type.Tristate;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.ui.Interface.USBPreviewView;
import com.icatch.mobilecam.ui.activity.LocalMultiPbActivity;
import com.icatch.mobilecam.ui.activity.LoginFacebookActivity;
import com.icatch.mobilecam.ui.activity.LoginGoogleActivity;
import com.icatch.mobilecam.ui.adapter.SettingListAdapter;
import com.icatch.mobilecam.utils.ConvertTools;
import com.icatch.mobilecam.utils.fileutils.FileTools;
import com.icatch.mobilecam.utils.MediaRefresh;
import com.icatch.mobilecam.utils.PanoramaTools;
import com.icatch.mobilecam.utils.QRCode;
import com.icatch.mobilecam.utils.TimeTools;
import com.icatchtek.pancam.customer.exception.IchGLSurfaceNotSetException;
import com.icatchtek.pancam.customer.surface.ICatchSurfaceContext;
import com.icatchtek.pancam.customer.type.ICatchGLCredential;
import com.icatchtek.pancam.customer.type.ICatchGLEventID;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;
import com.icatchtek.pancam.customer.type.ICatchGLPoint;
import com.icatchtek.pancam.customer.type.ICatchGLSurfaceType;
import com.icatchtek.reliant.customer.type.ICatchFrameBuffer;
import com.icatchtek.reliant.customer.type.ICatchH264StreamParam;
import com.icatchtek.reliant.customer.type.ICatchImageSize;
import com.icatchtek.reliant.customer.type.ICatchJPEGStreamParam;
import com.icatchtek.reliant.customer.type.ICatchStreamParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by zhang yanhu C001012 on 2015/12/4 14:22.
 */
public class USBPreviewPresenter extends BasePresenter implements SensorEventListener {
    private static final String TAG = "USBPreviewPresenter";

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
    private USBPreviewView previewView;
    private PreviewHandler previewHandler;
    private int curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
    private Timer videoCaptureButtomChangeTimer;
    public boolean videoCaptureButtomChangeFlag = true;
    private Timer recordingLapseTimeTimer;
    private int lapseTime = 0;
    private boolean allowClickButtoms = true;
    private long lastCilckTime = 0;
    private long lastRecodeTime;
    private ICatchSurfaceContext iCatchSurfaceContext;
    private boolean hasInitSurface = false;
    private UsbManager usbManager;
    private UsbDeviceConnection connection;
    private boolean isLive = false;
    private LiveMode liveMode = LiveMode.MODE_FACEBOOK_LIVE;

    private int panoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;
    private int surfaceType = ICatchGLSurfaceType.ICH_GL_SURFACE_TYPE_SPHERE;
    private int curVideoWidth = 1920;
    private int curVideoHeight = 960;
    private int curVideoFps = 30;
    private String curCodecType = "H264";
    private USBMonitor mUSBMonitor;

    private List<SettingMenu> settingMenuList;
    private SettingListAdapter settingListAdapter;

    private Timer recordTimer;
    private SDKEvent sdkEvent;
    private MyCamera curCamera;
    private CameraStreaming cameraStreaming;
    private BaseProrertys baseProrertys;
    CameraProperties cameraProperties;
    private String curRecordPath = null;

    public USBPreviewPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void setView(USBPreviewView previewView) {
        this.previewView = previewView;
        initCfg();
        initData();
    }

    public void initData() {
        curCamera = CameraManager.getInstance().getCurCamera();
        Intent intent = activity.getIntent();
        curVideoWidth = intent.getIntExtra("videoWidth", 1920);
        curVideoHeight = intent.getIntExtra("videoHeight", 960);
        curVideoFps = intent.getIntExtra("videoFps", 30);
        curCodecType = intent.getStringExtra("videoCodec");
        AppLog.d(TAG, "initData videoWidth=" + curVideoWidth + " videoHeight=" + curVideoHeight + " videoFps=" + curVideoFps + " curCodecType=" + curCodecType);

        panoramaPreviewPlayback = curCamera.getPanoramaPreviewPlayback();
        cameraStreaming = new CameraStreaming(panoramaPreviewPlayback);
//        currentCamera = GlobalInfo.getInstance().getCurrentCamera();
        videoCaptureStartBeep = MediaPlayer.create(activity, R.raw.camera_timer);
        stillCaptureStartBeep = MediaPlayer.create(activity, R.raw.captureshutter);
        continuousCaptureBeep = MediaPlayer.create(activity, R.raw.captureburst);
        modeSwitchBeep = MediaPlayer.create(activity, R.raw.focusbeep);
        cameraProperties = curCamera.getCameraProperties();
//        baseProrertys = curCamera.getBaseProrertys();
//        GlobalInfo.getInstance().enableConnectCheck(true);
        previewHandler = new PreviewHandler();
        setPanoramaCfg(true);
//        feature.register();
        cameraProperties.getSupportFuction();
    }

    public void initState() {
        if (AppInfo.enableLive) {
            previewView.setLiveLayoutVisibility(View.VISIBLE);
        } else {
            previewView.setLiveLayoutVisibility(View.GONE);
        }
    }

    public void isAppBackground() {
        if (AppInfo.isAppSentToBackground(activity)) {
            destroyPreview();
            ExitApp.getInstance().exit();
        }
    }

    private void setPanoramaCfg(boolean enableAudio) {
        if (usbManager == null) {
            usbManager = (UsbManager) activity.getApplicationContext().getSystemService(Context.USB_SERVICE);
        }
        UsbDevice usbDevice = curCamera.getUsbDevice();
        if (connection == null) {
            connection = usbManager.openDevice(usbDevice);
        }
        AppLog.d(TAG, "usbDevice.getProductId() =" + usbDevice.getProductId());
    }

    public void startOrStopCapture() {
        if (isLive) {
            MyToast.show(activity, R.string.stop_live_hint);
            return;
        }
        if (TimeTools.isFastClick()) {
            return;
        }
        AppLog.i(TAG, "curMode =" + curMode);
        if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            videoCaptureStartBeep.start();
            lastRecodeTime = System.currentTimeMillis();
            if (!checkMemory()) {
                MyToast.show(activity, "There is not enough memory space!!");
                return;
            }
            if (startMovieRecord()) {
                AppLog.i(TAG, "ok startRecording)");
                curMode = PreviewMode.APP_STATE_VIDEO_CAPTURE;
                startVideoCaptureButtomChangeTimer();
                startRecordingLapseTimeTimer(0);
                startMovieRecordTimer();
            }
        } else if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            videoCaptureStartBeep.start();
            if (System.currentTimeMillis() - lastRecodeTime < 2000) {
                return;
            }
            if (stopMovieRecord()) {
                curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
                stopVideoCaptureButtomChangeTimer();
                stopRecordingLapseTimeTimer();
                stopMovieRecordTimer();
            }
        } else if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
//            curMode = PreviewMode.APP_STATE_STILL_CAPTURE;
            startPhotoCapture();
        }
        AppLog.d(TAG, "end processing for responsing captureBtn clicking");
    }

    private boolean checkMemory() {
        long size = SystemInfo.getSDFreeSize();
        if (size >= 1024 * 1024 * 1024) {
            return true;
        } else {
            return false;
        }
    }

    private void startMovieRecordTimer() {
        if (recordTimer != null) {
            recordTimer.cancel();
        }
        recordTimer = new Timer();
        MyRecordTimerTask recordTimerTask = new MyRecordTimerTask();
        recordTimer.schedule(recordTimerTask, 1000 * 60 * 15);//15分钟后执行
//        recordTimer.schedule(recordTimerTask, 1000 * 60 * 1);//1分钟后执行
    }

    private void stopMovieRecordTimer() {
        if (recordTimer != null) {
            recordTimer.cancel();
            recordTimer = null;
        }
    }

    public void loadSettingMenu() {
        AppLog.d(TAG, "load setting menu");
        if (isLive) {
            MyToast.show(activity, R.string.stop_live_hint);
            return;
        }
        if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            MyToast.show(activity, R.string.stream_error_recording);
            return;
        }
        if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE) {
            MyToast.show(activity, R.string.stream_error_capturing);
            return;
        }
        previewView.setSetupMainMenuVisibility(View.VISIBLE);
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
                        settingMenuList = UIDisplaySource.getinstance().getUSBList(activity);
                        settingListAdapter = new SettingListAdapter(activity, settingMenuList, previewHandler);
                        previewView.setSettingMenuListAdapter(settingListAdapter);
                        MyProgressDialog.closeProgressDialog();
                    }
                }, 500);
            }
        }).start();
    }

    public void initImageSize() {
        List<ICatchImageSize> list = curCamera.getPanoramaPreviewPlayback().getSupportedImageSize();
        if (list == null || list.size() < 0) {
            AppLog.e(TAG, "list == null");
            return;
        }
        ICatchImageSize curImageSize = list.get(0);
        curCamera.getPanoramaPreviewPlayback().setImageSize(curImageSize);
    }

    public void imageSizeSetting() {
        OptionSetting optionSetting = new OptionSetting();
        optionSetting.addSettingCompleteListener(new OnSettingCompleteListener() {
            @Override
            public void settingTimeLapseModeComplete(int previewMode) {

            }

            @Override
            public void onOptionSettingComplete() {
                ICatchImageSize imageSize = curCamera.getPanoramaPreviewPlayback().getCurImageSize();
                if (imageSize != null) {
                    String imageSizeInfo = "Image size:" + imageSize.getImageW() + "x" + imageSize.getImageH();
                    previewView.setImageSizeSettingText(imageSizeInfo);
                }
            }

            @Override
            public void settingVideoSizeComplete() {

            }
        });
        optionSetting.showUSBImageSizeOptionDialog(activity);
    }

    class MyRecordTimerTask extends TimerTask {
        @Override
        public void run() {
            previewHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean ret = stopMovieRecord();
//                    stopRecordingLapseTimeTimer();
                    if (!ret) {
                        MyToast.show(activity, "stopMovieRecord false");
                        curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
                        stopVideoCaptureButtomChangeTimer();
                        stopRecordingLapseTimeTimer();
                        return;
                    }

                    ret = checkMemory();
                    if (!ret) {
                        MyToast.show(activity, "There is not enough memory space");
                        curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
                        stopVideoCaptureButtomChangeTimer();
                        stopRecordingLapseTimeTimer();
                        return;
                    }
                    ret = startMovieRecord();
                    if (!ret) {
                        MyToast.show(activity, "startMovieRecord false");
                        curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
                        stopVideoCaptureButtomChangeTimer();
                        stopRecordingLapseTimeTimer();
                        return;
                    }
//                    startRecordingLapseTimeTimer(0);
                    MyToast.show(activity, "Add a new file");
                    startMovieRecordTimer();
                }
            }, 500);
        }
    }

    private boolean startMovieRecord() {
        boolean ret = false;
        if (panoramaPreviewPlayback != null) {
            curRecordPath = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_VIDEO + System.currentTimeMillis() + "_rec.mp4";
            ret = panoramaPreviewPlayback.startMovieRecord(curRecordPath, false);
            MediaRefresh.notifySystemToScan(curRecordPath, activity);
        }
        AppLog.d(TAG, "startMovieRecord ret = " + ret);
        return ret;
    }

    private boolean stopMovieRecord() {
        boolean ret = false;
        if (panoramaPreviewPlayback != null) {
            ret = panoramaPreviewPlayback.stopMovieRecord();
        }
        if (curRecordPath != null) {
            MediaRefresh.notifySystemToScan(curRecordPath, activity);
        }
        AppLog.d(TAG, "stopMovieRecord ret = " + ret);
        return ret;
    }


    public void createUIByMode(int previewMode) {
        AppLog.i(TAG, "start createUIByMode previewMode=" + previewMode);
        if (previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW || previewMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || previewMode == PreviewMode
                .APP_STATE_VIDEO_MODE) {
            previewView.setPvModeBtnBackgroundResource(R.drawable.video_toggle_btn_on);
            previewView.setCaptureBtnBackgroundResource(R.drawable.video_recording_btn_on);
        } else if (previewMode == PreviewMode.APP_STATE_STILL_PREVIEW || previewMode == PreviewMode.APP_STATE_STILL_CAPTURE || previewMode == PreviewMode
                .APP_STATE_STILL_MODE) {
            previewView.setPvModeBtnBackgroundResource(R.drawable.capture_toggle_btn_on);
            previewView.setCaptureBtnBackgroundResource(R.drawable.still_capture_btn);
        }
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
                            if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
                                previewView.setCaptureBtnBackgroundResource(R.drawable.video_recording_btn_on);
                            }
                        }
                    });

                } else {
                    videoCaptureButtomChangeFlag = true;
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
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
        AppLog.i(TAG, "startRecordingLapseTimeTimer curMode=" + curMode);
        if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
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
        if (isLive) {
            MyToast.show(activity, R.string.stop_live_hint);
            return;
        }
        long timeInterval = System.currentTimeMillis() - lastCilckTime;
        AppLog.d(TAG, "repeat click: timeInterval=" + timeInterval);
        if (System.currentTimeMillis() - lastCilckTime < 1000) {
            AppLog.d(TAG, "repeat click: timeInterval < 1000");
            return;
        } else {
            lastCilckTime = System.currentTimeMillis();
        }
        curMode = previewMode;
        modeSwitchBeep.start();
        previewView.dismissPopupWindow();
        createUIByMode(previewMode);
        AppLog.d(TAG, "End changePreviewMode curMode=" + curMode);
    }

    private void startPhotoCapture() {
        AppLog.d(TAG, "startPhotoCapture curMode=" + curMode);
        if (curMode != PreviewMode.APP_STATE_STILL_PREVIEW) {
            MyToast.show(activity, "Snap image  is in progress");
            return;
        }
        curMode = PreviewMode.APP_STATE_STILL_CAPTURE;
        previewView.setCaptureBtnEnability(false);
//        previewView.setCaptureBtnBackgroundResource(R.drawable.still_capture_btn_off);
        stillCaptureStartBeep.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ICatchImageSize curImageSize = curCamera.getPanoramaPreviewPlayback().getCurImageSize();
                if (curImageSize == null) {
                    initImageSize();
                    curImageSize = curCamera.getPanoramaPreviewPlayback().getCurImageSize();
                }
                ICatchFrameBuffer frameBuffer = new ICatchFrameBuffer(1024 * 1024 * 9);
                final boolean retValue = panoramaPreviewPlayback.snapImage(frameBuffer, 5000);
                if (retValue) {
                    if (frameBuffer != null && frameBuffer.getFrameSize() > 0) {
                        saveImage(frameBuffer);
                    }
                }
                curMode = PreviewMode.APP_STATE_STILL_PREVIEW;
                previewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (retValue) {
                            MyToast.show(activity, "snapImage success");
                        } else {
                            MyToast.show(activity, "snapImage failed");
                        }
                        previewView.setCaptureBtnEnability(true);
                    }
                });

            }
        }).start();

    }

    public void saveImage(ICatchFrameBuffer buffer) {
        String path = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_PHOTO;
        File directory = null;

        if (path != null) {
            directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
//        path = Environment.getExternalStorageDirectory().toString() + "/bitmapSave11/";
        String fileName = "snap_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        String writeFile = path + fileName;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(writeFile, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.write(buffer.getBuffer(), 0, buffer.getFrameSize());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        MediaRefresh.notifySystemToScan(writeFile, activity);
    }

    public void showZoomView() {
    }

    public void showSettingDialog(int position) {
    }

    public void showPvModePopupWindow() {
        AppLog.d(TAG, "showPvModePopupWindow curMode=" + curMode);
        if (isLive) {
            MyToast.show(activity, R.string.stop_live_hint);
            return;
        }
        if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            MyToast.show(activity, R.string.stream_error_capturing);
            return;
        }
        previewView.showPopupWindow(curMode);
        if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
            previewView.setCaptureRadioBtnChecked(true);
        } else if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            previewView.setVideoRadioBtnChecked(true);
        }
    }

    public void processAudioSwitcher(final boolean checked) {
        setPanoramaCfg(checked);
        stopPreview();
        startPreview();
    }

    public void startOrStopFacebookLive() {
        if (!panoramaPreviewPlayback.isStreamSupportPublish()) {
            Toast.makeText(activity, "Not support Publish Streaming", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isLive) {
            if (liveMode != LiveMode.MODE_FACEBOOK_LIVE) {
                MyToast.show(activity, R.string.stop_other_live_hint);
                return;
            }
            boolean ret = panoramaPreviewPlayback.stopPublishStreaming();
            MyProgressDialog.showProgressDialog(activity, R.string.wait);

//            AccessToken accessToken = AccessToken.getCurrentAccessToken();
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
                stopPreview();
                Intent intent = new Intent();
                intent.setClass(activity, LoginFacebookActivity.class);
//                activity.startActivity(intent);
                activity.startActivityForResult(intent, 0);
            }
        }
    }

    public void startYoutubeLive() {
        String directoryPath = activity.getExternalCacheDir() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
        String fileName = AppInfo.FILE_GOOGLE_TOKEN;
        GoogleToken googleToken = (GoogleToken) FileTools.readSerializable(directoryPath + fileName);
        String accessToken = googleToken.getAccessToken();
        String refreshToken = googleToken.getRefreshToken();
//        String accessToken  = AppInfo.accessToken;
//        String refreshToken = AppInfo.refreshToken;
        final GoogleClientSecrets clientSecrets = YoutubeCredential.readClientSecrets(activity);
        AppLog.d(TAG, "readSerializable accessToken=" + accessToken);
        AppLog.d(TAG, "readSerializable refreshToken=" + refreshToken);
        if (accessToken == null) {
            MyToast.show(activity, "Failed to Youtube live,OAuth2AccessToken is null!");
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
                            MyToast.show(activity, "Failed to Youtube live,pushUrl is null!");
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
//                                                previewView.setLiveSwitcherChecked( false );
                            previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                            MyToast.show(activity, "Failed to start publish streaming!");
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
//                                                previewView.setLiveSwitcherChecked( false );
                            MyToast.show(activity, "Failed to YouTube live,shareUrl is null!");
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

    void stopYoutubeLive() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean ret = panoramaPreviewPlayback.stopPublishStreaming();
                try {
                    CreateBroadcast.stopLive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                previewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        if (ret == false) {
                            MyToast.show(activity, "Failed to stop living publish!");
                        } else {
                            MyToast.show(activity, "Succed to stop living publish!");
                        }
                        isLive = false;
                        previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                    }
                });
            }
        }).start();
    }

    void startYoutubeLiveForSdk() {
        AppLog.d(TAG, "startYoutubeLiveForSdk");
        final String client_id = GoogleAuthTool.CLIENT_ID;
        final String client_secret = GoogleAuthTool.CLIENT_SECRET;
//        final String client_secret = "7qi2U9Z7E2ifQIRsPvc7pi6j";
//        final String client_id = "183522097987-mk0vb5aivkpaierobt583b221hjoss6b.apps.googleusercontent.com";
        String directoryPath = activity.getExternalCacheDir() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
        String fileName = AppInfo.FILE_GOOGLE_TOKEN;
        GoogleToken googleToken = (GoogleToken) FileTools.readSerializable(directoryPath + fileName);
        final String accessToken = googleToken.getAccessToken();
        final String refreshToken = googleToken.getRefreshToken();
        AppLog.d(TAG, "readSerializable accessToken=" + accessToken);
        AppLog.d(TAG, "readSerializable refreshToken=" + refreshToken);
        if (accessToken == null) {
            MyToast.show(activity, "Failed to Youtube live,OAuth2 AccessToken is null!");
            return;
        }
        MyProgressDialog.showProgressDialog(activity, R.string.wait);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ICatchGLCredential credential = new ICatchGLCredential(accessToken, refreshToken, client_id, client_secret);
                AppLog.d(TAG, "credential getAccessToken=" + credential.getAccessToken());
                AppLog.d(TAG, "credential getRefreshToken=" + credential.getRefreshToken());
                AppLog.d(TAG, "credential getClientId=" + credential.getClientId());
                AppLog.d(TAG, "credential getClientSecret=" + credential.getClientSecret());
                String push_addr = panoramaPreviewPlayback.createChannel(credential, "720p", "360Live", true);
                if (push_addr == null || push_addr.equals("")) {
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                            MyToast.show(activity, "Failed to Youtube live,pushUrl is null!");
                        }
                    });
                    return;
                }
                AppLog.d(TAG, " publish broadcast stream push addr: " + push_addr);
                boolean ret = panoramaPreviewPlayback.startPublishStreaming(push_addr);// 开始推流
                if (!ret) {
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                            MyToast.show(activity, "Failed to startPublishStreaming!");
                        }
                    });
                    return;
                }
                final String shared_addr = panoramaPreviewPlayback.startLive();//开始直播
                AppLog.d(TAG, "publish broadcast stream share addr: " + shared_addr);
                if (shared_addr == null || shared_addr.equals("")) {//直播失败
                    panoramaPreviewPlayback.stopPublishStreaming();//停止推流
                    panoramaPreviewPlayback.deleteChannel();//删除直播频道
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                            MyToast.show(activity, "Failed to YouTube live,shareUrl is null!");
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
                            showSharedUrlDialog(activity, shared_addr);
                        }
                    });
                    return;
                }
            }
        }).start();
    }

    void stopYoutubeLiveForSdk() {
        AppLog.d(TAG, "stopYoutubeLiveForSdk");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean ret = panoramaPreviewPlayback.stopPublishStreaming();
                panoramaPreviewPlayback.stopLive();
                previewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        if (ret == false) {
                            MyToast.show(activity, "Failed to stop living publish!");
                        } else {
                            MyToast.show(activity, "Succed to stop living publish!");
                        }
                        isLive = false;
                        previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                    }
                });
            }
        }).start();
    }

    public void startOrStopYouTubeLive() {
        if (!isLive) {
            final String directoryPath = activity.getExternalCacheDir() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
            final String fileName = AppInfo.FILE_GOOGLE_TOKEN;
            final GoogleToken googleToken = (GoogleToken) FileTools.readSerializable(directoryPath + fileName);
            AppLog.d(TAG, "refreshAccessToken googleToken=" + googleToken);

//            final GoogleToken googleToken = null;
            if (googleToken != null && googleToken.getRefreshToken() != null && googleToken.getRefreshToken() != "") {
                final String refreshToken = googleToken.getRefreshToken();
                MyToast.show(activity, "readSerializable RefreshToken=" + refreshToken);
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
                                    MyToast.show(activity, "refreshAccessToken IOException");
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
                                    MyToast.show(activity, "start live");
//                                    startYoutubeLive();
                                    startYoutubeLiveForSdk();
                                }
                            }, 1000);
                        } else {
                            previewHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MyProgressDialog.closeProgressDialog();
                                    MyToast.show(activity, "Failed to get accessToken , Please enter the google account click disconnect and re-login!");
                                }
                            });
                        }
                    }
                }).start();

            } else {
                MyToast.show(activity, "You are not logged in, please login to google account!");
            }

        } else {
            if (liveMode != LiveMode.MODE_YOUTUBE_LIVE) {
                MyToast.show(activity, "Please stop other live!");
                return;
            }
            AppLog.d(TAG, "stop push publish...");
            MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
            stopYoutubeLiveForSdk();
        }
    }

    public void gotoGoogleAccountManagement() {
        if (isLive) {
            MyToast.show(activity, R.string.stop_other_live_hint);
        } else {
            stopPreview();
            Intent intent = new Intent();
            intent.setClass(activity, LoginGoogleActivity.class);
            activity.startActivity(intent);
        }
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

    private class PreviewHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            Tristate ret = Tristate.FALSE;
            switch (msg.what) {
                case AppMessage.MESSAGE_LIVE_NETWORK_DISCONNECT:
                    if (isLive) {
                        MyToast.show(activity, "network disconnect!");

                        panoramaPreviewPlayback.stopPublishStreaming();
                        if (liveMode == LiveMode.MODE_YOUTUBE_LIVE) {
                            previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                        } else if (liveMode == LiveMode.MODE_FACEBOOK_LIVE) {
                            previewView.setFacebookBtnTxv(R.string.facebook_start_live);
                        }
                        isLive = false;
                    }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    @Override
    public void finishActivity() {
        if (previewView.getSetupMainMenuVisibility() == View.VISIBLE) {
            AppLog.i(TAG, "onKeyDown curMode==" + curMode);
            previewView.setSetupMainMenuVisibility(View.GONE);
            previewView.setSettingBtnVisible(true);
            previewView.setBackBtnVisibility(false);
            previewView.setActionBarTitle(R.string.title_preview);
            startPreview();
//            changeCameraMode(curMode, ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE);
        } else {
            if (isLive) {
                MyToast.show(activity, R.string.stop_live_hint);
                return;
            } else {
                destroyPreview();
                super.finishActivity();
            }
        }
    }

    public void redirectToPbActivity() {
        AppLog.i(TAG, "pbBtn is clicked curMode=" + curMode);
        if (allowClickButtoms == false) {
            AppLog.i(TAG, "do not allow to response button clicking");
            return;
        }
        allowClickButtoms = false;
        AppLog.i(TAG, "curMode =" + curMode);
        if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW || curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            stopPreview();
            previewHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    allowClickButtoms = true;
                    Intent intent = new Intent();
                    intent.putExtra("CUR_POSITION", 0);
                    intent.setClass(activity, LocalMultiPbActivity.class);
                    activity.startActivity(intent);
                }
            }, 500);
            AppLog.i(TAG, "intent:end start PbMainActivity.class");
            return;
        }
        allowClickButtoms = true;
        AppLog.i(TAG, "end processing for responsing pbBtn clicking");
    }

    @Override
    public void redirectToAnotherActivity(final Context context, final Class<?> cls) {
        AppLog.i(TAG, "pbBtn is clicked curMode=" + curMode);
        if (allowClickButtoms == false) {
            AppLog.i(TAG, "do not allow to response button clicking");
            return;
        }
        allowClickButtoms = false;
        AppLog.i(TAG, "curMode =" + curMode);
        if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW || curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            stopPreview();
            previewHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    allowClickButtoms = true;
                    Intent intent = new Intent();
                    AppLog.i(TAG, "intent:start PbMainActivity.class");
                    intent.setClass(context, cls);
                    context.startActivity(intent);
                }
            }, 500);
            AppLog.i(TAG, "intent:end start PbMainActivity.class");
            return;
        }
        allowClickButtoms = true;
        AppLog.i(TAG, "end processing for responsing pbBtn clicking");
    }

    public void initSurface(SurfaceHolder surfaceHolder) {
        AppLog.i(TAG, "begin initSurface");
        if (panoramaPreviewPlayback == null) {
            return;
        }
        if (AppInfo.enableRender) {
            iCatchSurfaceContext = new ICatchSurfaceContext(surfaceHolder.getSurface());
            if(PanoramaTools.isPanorama(curVideoWidth,curVideoHeight)){
                panoramaPreviewPlayback.enableGLRender();
                panoramaPreviewPlayback.init(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
                panoramaPreviewPlayback.setSurface(ICatchGLSurfaceType.ICH_GL_SURFACE_TYPE_SPHERE, iCatchSurfaceContext);
            }else {
                panoramaPreviewPlayback.enableCommonRender(iCatchSurfaceContext);
            }

        } else {
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

    public void startPreview() {
        AppLog.d(TAG, "start startPreview isStreamReady=" + curCamera.isStreamReady);
        if (panoramaPreviewPlayback == null) {
            AppLog.d(TAG, "null point");
            return;
        }
        if (curCamera.isStreamReady) {
            return;
        }
        AppLog.d(TAG, "start startPreview videoWidth=" + curVideoWidth + " videoHeight=" + curVideoHeight + " videoFps=" + curVideoFps + " curCodecType=" +
                curCodecType + " enableRender=" + AppInfo.enableRender);
        previewHandler.post(new Runnable() {
            @Override
            public void run() {
                MyToast.show(activity, "PV param Width=" + curVideoWidth + " Height=" + curVideoHeight + " Fps=" + curVideoFps + " curCodecType=" +
                        curCodecType);
            }
        });
        ICatchStreamParam iCatchStreamParam;
        if (curCodecType != null && curCodecType.equals("MJPG")) {
            iCatchStreamParam = new ICatchJPEGStreamParam(curVideoWidth, curVideoHeight, curVideoFps);
        } else {
            iCatchStreamParam = new ICatchH264StreamParam(curVideoWidth, curVideoHeight, curVideoFps);
        }
        final Tristate retValue;
        if (AppInfo.enableRender) {
            registerGyroscopeSensor();
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
                } else if (retValue == Tristate.FALSE) {
                }
            }
        });
        AppLog.d(TAG, "end startPreview retValue= " + retValue);
    }

    public boolean stopPreview() {
        if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            stopMovieRecord();
            curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
            stopVideoCaptureButtomChangeTimer();
            stopRecordingLapseTimeTimer();
            stopMovieRecordTimer();
        }
        if (AppInfo.enableRender) {
            removeGyroscopeListener();
            if (panoramaPreviewPlayback != null && curCamera.isStreamReady) {
                curCamera.isStreamReady = false;
                return panoramaPreviewPlayback.stop();
            }
        } else {
            if (curCamera.isStreamReady) {
                curCamera.isStreamReady = false;
                return cameraStreaming.stop();
            }
        }
        return true;
    }

    public void locate(float progerss) {
        if (AppInfo.enableRender) {
            panoramaPreviewPlayback.locate(progerss);
        }
    }

    //pancamGLRelease surface;
    public void destroyPreview() {
        if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            stopMovieRecord();
            curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
            stopVideoCaptureButtomChangeTimer();
            stopRecordingLapseTimeTimer();
            stopMovieRecordTimer();
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
        curCamera.disconnect();
    }

    public void rotateB(MotionEvent e, float prevX, float prevY) {
        if (!AppInfo.enableRender) {
            return;
        }
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
            rotate(speedX, speedY, speedZ, event.timestamp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void rotate(float speedX, float speedY, float speedZ, long timestamp) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        if (AppInfo.enableRender) {
            panoramaPreviewPlayback.rotate(rotation, speedX, speedY, speedZ, timestamp);
        }

    }

    private void registerGyroscopeSensor() {
        sensorManager = (SensorManager) activity.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
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
        if (AppInfo.enableRender) {
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
    }

    public void registerUSB() {
        if (mUSBMonitor != null) {
            mUSBMonitor.register();
        }
    }

    public void unregisterUSB() {
        if (mUSBMonitor != null) {
            mUSBMonitor.unregister();
            mUSBMonitor = null;
        }

    }

    public void initUsbMonitor() {
        mUSBMonitor = new USBMonitor(activity, mOnDeviceConnectListener);
    }

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(UsbDevice device) {

        }

        @Override
        public void onDettach(UsbDevice device) {
//            GlobalInfo.getInstance().setCurUsbDevice(null);
            destroyPreview();
            showDialogQuit(activity, "The USB device has been disconnected, please try again after connection.");
        }

        @Override
        public void onConnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {

        }

        @Override
        public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {

        }

        @Override
        public void onCancel() {

        }
    };


    public void showDialogQuit(final Context context, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle("Warning").setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppLog.i(TAG, "ExitApp because of " + message);
//                destroyPreview();
                activity.finish();
//                ExitApp.getInstance().exit();
            }
        });
        builder.create().show();
    }


    public void addEventListener() {
        if (sdkEvent == null) {
            sdkEvent = new SDKEvent(previewHandler);
        }
        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_LIVE_NETWORK_DISCONNECT);
    }

    public void removeEventListener() {
        if (sdkEvent == null) {
            return;
        }
        sdkEvent.removePanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_LIVE_NETWORK_DISCONNECT);
    }
}
