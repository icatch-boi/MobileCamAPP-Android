package com.icatch.mobilecam.Function.CameraAction;

import android.content.Context;
import android.media.MediaPlayer;

import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.CameraAction;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatchtek.control.customer.type.ICatchCamProperty;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhang yanhu C001012 on 2015/12/30 13:22.
 */
public class PhotoCapture {
    private static final String TAG = PhotoCapture.class.getSimpleName();
    private MediaPlayer stillCaptureStartBeep;
    private MediaPlayer delayBeep;
    private MediaPlayer continuousCaptureBeep;
    private OnStopPreviewListener onStopPreviewListener;
    private OnCaptureListener onCaptureListener;
    private static final int TYPE_BURST_CAPTURE = 1;
    private static final int TYPE_NORMAL_CAPTURE = 2;
    private CameraProperties cameraProperties;
    private CameraAction cameraAction;

    public PhotoCapture() {
        Context context = GlobalInfo.getInstance().getCurrentApp();
        stillCaptureStartBeep = MediaPlayer.create(context, R.raw.captureshutter);
        delayBeep = MediaPlayer.create(context, R.raw.delay_beep);
        continuousCaptureBeep = MediaPlayer.create(context, R.raw.captureburst);
        this.cameraProperties = CameraManager.getInstance().getCurCamera().getCameraProperties();
        this.cameraAction = CameraManager.getInstance().getCurCamera().getCameraAction();
    }

    public void startCapture() {
        new CaptureThread().run();
    }

    class CaptureThread implements Runnable {
        @Override
        public void run() {
            //long lastTime = System.currentTimeMillis();
            AppLog.i(TAG, "start CameraCaptureThread");
            //notify stopMPreview preview
            //JIRA BUG IC-564 Begin modify by b.jiang 2016-8-16
//            CameraProperties.getInstance().getCurrentCaptureDelay();
            //check property support then setting the value.
            int delayTime;
            if(cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_CAPTURE_DELAY)){
                delayTime = cameraProperties.getCurrentCaptureDelay();
            }else {
                delayTime = 0;
            }
            if (delayTime < 1000) {//ms
                onStopPreviewListener.onStop();
            } else if (cameraProperties.hasFuction(0xD7F0)) {//do not stopMPreview media stream and preview right now
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        onStopPreviewListener.onStop();
                    }
                };
                Timer timer = new Timer(true);
                timer.schedule(task, delayTime - 500);
            } else {
                onStopPreviewListener.onStop();
            }

            //start capture audio
            int needCaptureCount = 1;
            if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_BURST_NUMBER) == true) {
                needCaptureCount = cameraProperties.getCurrentAppBurstNum();
            }
            if (needCaptureCount == 1) {
                CaptureAudioTask captureAudioTask = new CaptureAudioTask(needCaptureCount, TYPE_NORMAL_CAPTURE);
                Timer captureAudioTimer = new Timer(true);
//                captureAudioTimer.schedule(captureAudioTask, delayTime, 200);
                captureAudioTimer.schedule(captureAudioTask, delayTime);
            } else {
                CaptureAudioTask captureAudioTask = new CaptureAudioTask(needCaptureCount, TYPE_BURST_CAPTURE);
                Timer captureAudioTimer = new Timer(true);
                captureAudioTimer.schedule(captureAudioTask, delayTime, 420);
            }

            //start delay audio
            int count = delayTime / 1000;
            int timerDelay = 0;
            if (delayTime >= 5000) {
                Timer delayTimer = new Timer(true);
                DelayTimerTask delayTimerTask = new DelayTimerTask(count / 2, delayTimer);
                delayTimer.schedule(delayTimerTask, 0, 1000);
                timerDelay = delayTime;
            } else {
                timerDelay = 0;
                count = delayTime / 500;
            }
            if (delayTime >= 3000) {
                Timer delayTimer1 = new Timer(true);
                DelayTimerTask delayTimerTask1 = new DelayTimerTask(count / 2, delayTimer1);
                delayTimer1.schedule(delayTimerTask1, timerDelay / 2, 500);
                timerDelay = delayTime;
            } else {
                timerDelay = 0;
                count = delayTime / 250;
            }
            Timer delayTimer2 = new Timer(true);
            DelayTimerTask delayTimerTask2 = new DelayTimerTask(count, delayTimer2);
            delayTimer2.schedule(delayTimerTask2, timerDelay - timerDelay / 4, 250);
            cameraAction.triggerCapturePhoto();
            if(onCaptureListener != null){
                onCaptureListener.onCompleted();
            }
            AppLog.i(TAG, "delayTime = " + delayTime + " needCaptureCount=" + needCaptureCount);
            AppLog.i(TAG, "end CameraCaptureThread");
        }
    }

    public void addOnStopPreviewListener(OnStopPreviewListener onStopPreviewListener) {
        this.onStopPreviewListener = onStopPreviewListener;
    }

    public interface OnStopPreviewListener {
        void onStop();
    }

    public void setOnCaptureListener(OnCaptureListener onCaptureListener) {
        this.onCaptureListener = onCaptureListener;
    }

    public interface OnCaptureListener {
        void onCompleted();
    }


    private class CaptureAudioTask extends TimerTask {
        private int burstNumber;
        private int type = TYPE_NORMAL_CAPTURE;

        public CaptureAudioTask(int burstNumber, int type) {
            this.burstNumber = burstNumber;
            this.type = type;
        }

        @Override
        public void run() {
            if (type == TYPE_NORMAL_CAPTURE) {
                if (burstNumber > 0) {
                    AppLog.i(TAG, "CaptureAudioTask remainBurstNumer =" + burstNumber);
                    stillCaptureStartBeep.start();
                    burstNumber--;
                } else {
                    cancel();
                }
            } else {
                if (burstNumber > 0) {
                    AppLog.i(TAG, "CaptureAudioTask remainBurstNumer =" + burstNumber);
                    continuousCaptureBeep.start();
                    burstNumber--;
                } else {
                    cancel();
                }
            }

        }
    }

    private class DelayTimerTask extends TimerTask {
        private int count;
        private Timer timer;

        public DelayTimerTask(int count, Timer timer) {
            this.count = count;
            this.timer = timer;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (count-- > 0) {
                delayBeep.start();
            } else {
                if (timer != null) {
                    timer.cancel();
                }
            }
        }
    }

}
