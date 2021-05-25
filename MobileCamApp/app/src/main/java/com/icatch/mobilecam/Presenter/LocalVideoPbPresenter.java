package com.icatch.mobilecam.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import com.icatch.mobilecam.Function.SDKEvent;
import com.icatch.mobilecam.Function.streaming.VideoStreaming;
import com.icatch.mobilecam.Listener.VideoFramePtsChangedListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.LocalSession;
import com.icatch.mobilecam.Presenter.Interface.BasePresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.PanoramaControl;
import com.icatch.mobilecam.SdkApi.PanoramaVideoPlayback;
import com.icatch.mobilecam.SdkApi.StreamStablization;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.Message.AppMessage;
import com.icatch.mobilecam.data.Mode.TouchMode;
import com.icatch.mobilecam.data.Mode.VideoPbMode;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.ui.Interface.LocalVideoPbView;
import com.icatch.mobilecam.utils.ConvertTools;
import com.icatch.mobilecam.utils.PanoramaTools;
import com.icatchtek.pancam.customer.exception.IchGLFormatNotSupportedException;
import com.icatchtek.pancam.customer.type.ICatchGLEventID;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;
import com.icatchtek.pancam.customer.type.ICatchGLPoint;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchFileType;

public class LocalVideoPbPresenter extends BasePresenter implements SensorEventListener {
    private String TAG = LocalVideoPbPresenter.class.getSimpleName();
    private LocalVideoPbView localVideoPbView;
    private Activity activity;
    private VideoPbMode videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
    private boolean needUpdateSeekBar = false;
    private String curLocalVideoPath;
    private VideoPbHandler handler = new VideoPbHandler();
    private boolean cacheFlag = false;
    private Boolean waitForCaching = false;
    private double currentTime = -1.0;
    private int videoDuration = 0;
    private int lastSeekBarPosition;
    private SDKEvent sdkEvent;
    private final static float MIN_ZOOM = 0.5f;
    private final static float MAX_ZOOM = 2.2f;

    private final static float FIXED_OUTSIDE_DISTANCE = 1 / MIN_ZOOM;
    private final static float FIXED_INSIDE_DISTANCE = 1 / MAX_ZOOM;
    private PanoramaVideoPlayback panoramaVideoPlayback;
    private PanoramaControl panoramaControl;
    private TouchMode touchMode = TouchMode.NONE;
    private float mPreviousY;
    private float mPreviousX;
    private float beforeLenght;
    private float afterLenght;
    private float currentZoomRate = MAX_ZOOM;
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private VideoStreaming videoStreaming;
    private boolean enableRender = AppInfo.enableRender;
    private int curPanoramaType= ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;

    public LocalVideoPbPresenter(Activity activity, String videoPath) {
        super(activity);
        this.activity = activity;
        this.curLocalVideoPath = videoPath;
//        addEventListener();
    }

    public void updatePbSeekbar(double pts) {
        if (videoPbMode != VideoPbMode.MODE_VIDEO_PLAY || needUpdateSeekBar == false) {
            return;
        }
//        if (videoPbMode != VideoPbMode.MODE_VIDEO_PLAY ) {
//            return;
//        }
        currentTime = pts;
        int temp = new Double(currentTime * 100).intValue();
        localVideoPbView.setSeekBarProgress(temp);
    }

    public void setView(LocalVideoPbView localVideoPbView) {
        this.localVideoPbView = localVideoPbView;
        initCfg();
        initView();
        initClient();
    }

    private void initClient() {
        LocalSession.getInstance().preparePanoramaSession();
        panoramaVideoPlayback = LocalSession.getInstance().getPanoramaVideoPlayback();
        panoramaControl = LocalSession.getInstance().getPanoramaControl();
        videoStreaming = new VideoStreaming(panoramaVideoPlayback);
//        panoramaVideoPlayback.enableGLRender();
    }

    private void initView() {
        int start = curLocalVideoPath.lastIndexOf("/");
        String videoName = curLocalVideoPath.substring(start + 1);
        localVideoPbView.setVideoNameTxv(videoName);
        if(enableRender && PanoramaTools.isPanoramaForVideo(curLocalVideoPath)){
            localVideoPbView.setPanoramaTypeBtnVisibility(View.VISIBLE);
        }else {
            localVideoPbView.setPanoramaTypeBtnVisibility(View.GONE);
        }
    }

    public void initZoomView() {
        localVideoPbView.setZoomMinValue(MIN_ZOOM);
        localVideoPbView.setZoomMaxValue(MAX_ZOOM);
        localVideoPbView.updateZoomRateTV(FIXED_INSIDE_DISTANCE);
        localVideoPbView.setProgress(FIXED_INSIDE_DISTANCE);
    }

    public void addEventListener() {
        if (panoramaVideoPlayback == null) {
            return;
        }
        if (sdkEvent == null) {
            sdkEvent = new SDKEvent(handler, panoramaControl);
        }
        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_STATUS);
        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_ENDED);
//        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_STREAM_CLOSED);
        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_CHANGED);
        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_PROGRESS);
        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_NO_EIS_INFORMATION);
        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_CODEC_INSUFFICIENT_PERFORMANCE);
    }

    public void removeEventListener() {
        if (panoramaVideoPlayback == null) {
            return;
        }
        sdkEvent.removePanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_STATUS);
        sdkEvent.removePanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_ENDED);
//        sdkEvent.removePanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_STREAM_CLOSED);
        sdkEvent.removePanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_CHANGED);
        sdkEvent.removePanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_PROGRESS);
        sdkEvent.removePanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_NO_EIS_INFORMATION);
        sdkEvent.removePanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_CODEC_INSUFFICIENT_PERFORMANCE);
    }

    public void play() {
        AppLog.i(TAG, "start play video videoPbMode=" + videoPbMode);
        // TODO Auto-generated method stub
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            AppLog.i(TAG, "start play video");
            addEventListener();
            if (enableRender) {
                registerGyroscopeSensor();
            }
            if (!enableRender) {
                videoStreaming.setFramePtsChangedListener(new VideoFramePtsChangedListener() {
                    @Override
                    public void onFramePtsChanged(double pts) {
                        updatePbSeekbar(pts);
                    }
                });
            }
            boolean ret = false;
            AppLog.i(TAG, "start play video curLocalVideoPath=" + curLocalVideoPath);
            ICatchFile file = new ICatchFile(0, ICatchFileType.ICH_FILE_TYPE_VIDEO, curLocalVideoPath, curLocalVideoPath, 0);
            try {
                ret = videoStreaming.play(file, false, false);
            } catch (IchGLFormatNotSupportedException e) {
                e.printStackTrace();
                MyToast.show(activity, R.string.video_format_not_support);
                AppLog.e(TAG, "failed to startPlaybackStream");
                removeEventListener();
                removeGyroscopeListener();
                return;
            }
            if (ret == false) {
                MyToast.show(activity, R.string.dialog_failed);
                AppLog.e(TAG, "failed to startPlaybackStream");
                removeEventListener();
                if (enableRender) {
                    removeGyroscopeListener();
                }
                return;
            }
//            if (panoramaVideoPlayback.resumePlayback() == false) {
//                MyToast.show(activity, R.string.dialog_failed);
//                AppLog.i(TAG, "failed to resumePlayback");
//                return;
//            }
            cacheFlag = true;
            waitForCaching = true;
            localVideoPbView.showLoadingCircle(true);
            AppLog.i(TAG, "seekBar.getProgress() =" + localVideoPbView.getSeekBarProgress());
            videoDuration = panoramaVideoPlayback.getLength();
            AppLog.i(TAG, "end getLength = " + videoDuration);
            localVideoPbView.setPlayBtnSrc(R.drawable.ic_pause_white_36dp);
            localVideoPbView.setTimeLapsedValue("00:00");
            localVideoPbView.setTimeDurationValue(ConvertTools.secondsToMinuteOrHours(videoDuration / 100));
            localVideoPbView.setSeekBarMaxValue(videoDuration);
//            this.videoDuration = videoDuration;// temp attemp to avoid sdk
//            localVideoPbView.showZoomView( View.VISIBLE );
            videoPbMode = VideoPbMode.MODE_VIDEO_PLAY;
            localVideoPbView.showLoadingCircle(false);
            return;
        }
        if (videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
            AppLog.i(TAG, "mode == MODE_VIDEO_PAUSE");
            if (panoramaVideoPlayback.resumePlayback() == false) {
                MyToast.show(activity, R.string.dialog_failed);
                AppLog.i(TAG, "failed to resumePlayback");
                return;
            }
            localVideoPbView.setPlayBtnSrc(R.drawable.ic_pause_white_36dp);
//            localVideoPbView.showZoomView( View.GONE );
            videoPbMode = VideoPbMode.MODE_VIDEO_PLAY;
            return;

        }
        if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY) {
            AppLog.i(TAG, "begin pause the playing");
            if (panoramaVideoPlayback.pausePlayback() == false) {
                MyToast.show(activity, R.string.dialog_failed);
                AppLog.i(TAG, "failed to pausePlayback");
                return;
            }
//            removeEventListener();
//            removeGyroscopeListener();
            localVideoPbView.setPlayBtnSrc(R.drawable.ic_play_arrow_white_36dp);
//            localVideoPbView.showZoomView(View.VISIBLE);
            videoPbMode = VideoPbMode.MODE_VIDEO_PAUSE;
            return;
        }
    }


    public void completedSeekToPosition() {
        lastSeekBarPosition = localVideoPbView.getSeekBarProgress();
        if (panoramaVideoPlayback.videoSeek(lastSeekBarPosition / 100.0)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            localVideoPbView.setTimeLapsedValue(ConvertTools.secondsToMinuteOrHours(lastSeekBarPosition / 100));
        } else {
            localVideoPbView.setSeekBarProgress(lastSeekBarPosition);
            MyToast.show(activity, R.string.dialog_failed);
        }
        needUpdateSeekBar = false;
    }

    public void startSeekTouch() {
        lastSeekBarPosition = localVideoPbView.getSeekBarProgress();

    }

    public void setTimeLapsedValue(int progress) {
        localVideoPbView.setTimeLapsedValue(ConvertTools.secondsToMinuteOrHours(progress / 100));
    }

    public void initSurface(SurfaceHolder surfaceHolder) {
        AppLog.i(TAG, "begin initSurface");
        videoStreaming.initSurface(enableRender,surfaceHolder,0,0);
        if (enableRender) {
            locate(FIXED_INSIDE_DISTANCE);
        }
            int width = localVideoPbView.getSurfaceViewWidth();
            int heigth = localVideoPbView.getSurfaceViewHeight();
            AppLog.i(TAG, "SurfaceViewWidth=" + width + " SurfaceViewHeight=" + heigth);
            if (width <= 0 || heigth <= 0) {
                width = 1080;
                heigth = 1920;
            }
            videoStreaming.setViewParam(width, heigth);
        AppLog.i(TAG, "end initSurface");
    }

    public void stopVideoStream() {
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            return;
        }
        removeEventListener();
        removeGyroscopeListener();
        localVideoPbView.setTimeLapsedValue("00:00");
        videoStreaming.stop();
        localVideoPbView.setPlayBtnSrc(R.drawable.ic_play_arrow_white_36dp);
        localVideoPbView.setSeekBarProgress(0);
        localVideoPbView.setSeekBarSecondProgress(0);
        localVideoPbView.setTopBarVisibility(View.VISIBLE);
        localVideoPbView.setBottomBarVisibility(View.VISIBLE);
        localVideoPbView.setMoreSettingLayoutVisibility(View.GONE);
        videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
        localVideoPbView.setProgress(0);
    }

    public void insidePanorama() {
        locate(FIXED_INSIDE_DISTANCE);
    }

    public void locate(float progerss) {
        if (enableRender) {
            panoramaVideoPlayback.locate(progerss);
            localVideoPbView.updateZoomRateTV(progerss);
        }
    }

    public void destroyVideo() {
        removeEventListener();
        if (enableRender) {
            removeGyroscopeListener();
        }
        videoStreaming.stop();
        videoStreaming.removeSurface(curPanoramaType);
        videoStreaming.release();
    }

    public void release(){
        videoStreaming.removeSurface(curPanoramaType);
        videoStreaming.release();
    }

    public void rotateB(MotionEvent e, float prevX, float prevY) {
        if (!enableRender) {
            return;
        }
        ICatchGLPoint prev = new ICatchGLPoint(prevX, prevY);
        ICatchGLPoint curr = new ICatchGLPoint(e.getX(), e.getY());
        panoramaVideoPlayback.rotate(prev, curr);
    }

    public void destroySession() {
        LocalSession.getInstance().destroyPanoramaSession();
    }

    public void enableEIS(boolean enable) {
        StreamStablization streamStablization = panoramaVideoPlayback.getStreamStablization();
        if(streamStablization == null){
            return;
        }
        if(enable){
            streamStablization.enableStablization();
        }else {
            streamStablization.disableStablization();
        }
    }
    private Toast codeInfoToast = null;
    private long codeInfoLastShowTime = 0;
    private class VideoPbHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case AppMessage.MESSAGE_VIDEO_STREAM_CODEC_INFO:
                    AppLog.i(TAG, "receive MESSAGE_VIDEO_STREAM_CODEC_INFO");
                    if(msg.obj instanceof String){
                        if(codeInfoToast == null){
                            codeInfoToast = MyToast.getToast(activity,(String)msg.obj);
                        }
                        if(System.currentTimeMillis() - codeInfoLastShowTime >10000){
                            codeInfoToast.show();
                            codeInfoLastShowTime = System.currentTimeMillis();
                        }
//                        MyToast.show(activity,(String)msg.obj);
//                        localVideoPbView.setCodecInfoTxv((String)msg.obj);
                    }
                    break;
                case AppMessage.MESSAGE_UPDATE_VIDEOPB_BAR:
                    if (videoPbMode != VideoPbMode.MODE_VIDEO_PLAY || needUpdateSeekBar == false) {
                        return;
                    }
                    localVideoPbView.setSeekBarProgress(msg.arg1);
                    break;
                case AppMessage.EVENT_CACHE_STATE_CHANGED:
                    AppLog.i(TAG, "receive EVENT_CACHE_STATE_CHANGED cacheFlag=" + cacheFlag);
                    AppLog.i(TAG, "EVENT_CACHE_STATE_CHANGED ---------msg.arg1 = " + msg.arg1);
                    if (cacheFlag == false) {
                        return;
                    }
                    if (msg.arg1 == 1) {
                        localVideoPbView.showLoadingCircle(true);
                        waitForCaching = true;
                    } else if (msg.arg1 == 2) {
                        localVideoPbView.showLoadingCircle(false);
                        needUpdateSeekBar = true;
                        waitForCaching = false;
                    }
                    break;

                case AppMessage.EVENT_CACHE_PROGRESS_NOTIFY:
                    AppLog.i(TAG, "receive EVENT_CACHE_PROGRESS_NOTIFY msg.arg1=" + msg.arg1 + "   waitForCaching=" +
                            waitForCaching);
                    if (cacheFlag == false) {
                        return;
                    }
                    if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE || videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
                        return;
                    }
                    if (waitForCaching) {
                        localVideoPbView.setLoadPercent(msg.arg1);
                    }
                    localVideoPbView.setSeekBarSecondProgress(msg.arg2);
                    break;

                case SDKEvent.EVENT_VIDEO_PLAY_PTS:
                    double temp = (double) msg.obj;
                    updatePbSeekbar(temp);
                    break;
                case SDKEvent.EVENT_VIDEO_PLAY_CLOSED:
                    AppLog.i(TAG, "receive EVENT_VIDEO_PLAY_CLOSED");
                    stopVideoStream();
                    break;

                case AppMessage.MESSAGE_VIDEO_STREAM_NO_EIS_INFORMATION:
                    enableEIS(false);
                    localVideoPbView.setEisSwitchChecked(false);
                    break;
            }
        }
    }

    public void showBar(boolean isShowBar) {
        if (isShowBar) {
            if (videoPbMode != VideoPbMode.MODE_VIDEO_IDLE) {
                localVideoPbView.setBottomBarVisibility(View.GONE);
                localVideoPbView.setTopBarVisibility(View.GONE);
            }
        } else {
            localVideoPbView.setBottomBarVisibility(View.VISIBLE);
            localVideoPbView.setTopBarVisibility(View.VISIBLE);
        }

    }

    public void showMoreSettingLayout(boolean isShowBar) {
        if (isShowBar) {
            localVideoPbView.setBottomBarVisibility(View.GONE);
            localVideoPbView.setTopBarVisibility(View.GONE);
            localVideoPbView.setMoreSettingLayoutVisibility(View.VISIBLE);
        }else {
            localVideoPbView.setBottomBarVisibility(View.VISIBLE);
            localVideoPbView.setTopBarVisibility(View.VISIBLE);
            localVideoPbView.setMoreSettingLayoutVisibility(View.GONE);
        }
    }

    public void onSufaceViewTouchDown(MotionEvent event) {
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            return;
        }
        touchMode = TouchMode.DRAG;
        mPreviousY = event.getY();
        mPreviousX = event.getX();
        beforeLenght = 0;
        afterLenght = 0;
    }

    public void onSufaceViewPointerDown(MotionEvent event) {
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            return;
        }
        if (event.getPointerCount() == 2) {
            touchMode = TouchMode.ZOOM;
            beforeLenght = getDistance(event);//
        }
    }

    public void onSufaceViewTouchMove(MotionEvent event) {
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            return;
        }
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
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            return;
        }
        touchMode = TouchMode.NONE;
    }

    public void onSufaceViewTouchPointerUp() {
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            return;
        }
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
            if (Math.abs(speedY) < 0.02 && Math.abs(speedZ) < 0.02) {
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
        panoramaVideoPlayback.rotate(rotation, speedX, speedY, speedZ, timestamp);
    }

    private void registerGyroscopeSensor() {
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
            sensorManager = null;
        }
    }

    public void setDrawingArea(int windowW, int windowH) {
        videoStreaming.setDrawingArea(windowW, windowH);
    }


    public void redrawSurface() {
        int width = localVideoPbView.getSurfaceViewWidth();
        int heigth = localVideoPbView.getSurfaceViewHeight();
        videoStreaming.setViewParam(width, heigth);
        videoStreaming.setSurfaceViewArea();
    }

    public void back() {
        AppLog.d(TAG, " 12233 back");
        destroyVideo();
        removeEventListener();
        destroySession();
        activity.finish();
    }


    public void setPanoramaType() {
        if(curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE ){
            videoStreaming.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID;
            localVideoPbView.setPanoramaTypeImageResource(R.drawable.asteroid);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }else if(curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID){
            videoStreaming.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R;
            localVideoPbView.setPanoramaTypeImageResource(R.drawable.vr);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }else{
            videoStreaming.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;
            localVideoPbView.setPanoramaTypeImageResource(R.drawable.panorama);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
}

