package com.icatch.mobilecam.ui.ExtendComponent;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.R;
import com.icatchtek.control.customer.type.ICatchCamProperty;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by zhang yanhu C001012 on 2015/12/28 14:40.
 */
public class ZoomView extends RelativeLayout {
    private static final String TAG = ZoomView.class.getSimpleName();
    public static float MAX_VALUE;
    public static float MIN_VALUE;
    private static int zoomGrained = 10;
    private final ImageButton zoomIn;
    private final ImageButton zoomOut;
    private final SeekBar zoomBar;
    private final TextView zoomRateText;
    private static final int DISPLAY_DURATION = 5000; //ms
    private Timer timer;
    public boolean firstCreate = true;
    private OnSeekBarChangeListener onSeekBarChangeListener;


    public ZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View zoomView = LayoutInflater.from(context).inflate(R.layout.zoombar_view, this, true);
        zoomIn = (ImageButton) zoomView.findViewById(R.id.zoom_in);
        zoomOut = (ImageButton) zoomView.findViewById(R.id.zoom_out);
        zoomBar = (SeekBar) zoomView.findViewById(R.id.zoomBar);
        zoomRateText = (TextView) zoomView.findViewById(R.id.zoom_rate);
        this.post(new Runnable() {
            @Override
            public void run() {
                startDisplay();
            }
        });
    }

    public void setZoomInOnclickListener(OnClickListener onclickListener) {
        zoomIn.setOnClickListener(onclickListener);
    }

    public void setZoomOutOnclickListener(OnClickListener onclickListener) {
        zoomOut.setOnClickListener(onclickListener);
    }

    public void setOnSeekBarChangeListener(final OnSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
        zoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onSeekBarChangeListener.onProgressChanged(ZoomView.this, progress * 1.0f / zoomGrained + MIN_VALUE, fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                onSeekBarChangeListener.onStartTrackingTouch(ZoomView.this);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                onSeekBarChangeListener.onStopTrackingTouch(ZoomView.this);
            }
        });
    }

    private void updateZoomRateText(float zoomRate) {
        AppLog.i(TAG, "updateZoomRateText zoomRate =" + zoomRate);
        zoomRateText.setText("x " + zoomRate);
    }

    public void updateZoomBarValue(int value) {
        AppLog.i(TAG, "updateZoomBarValue value =" + value);
//        zoomBar.setProgress(value);
        updateZoomRateText(value / 1.0f);
    }

    public void updateZoomBarValue(float value) {
        AppLog.i(TAG, "updateZoomBarValue value =" + value);
//        zoomBar.setProgress(value);
        updateZoomRateText(value / 1.0f);
    }

    public void setProgress(float value) {
        AppLog.i(TAG, "setProgress value =" + value);
        zoomBar.setProgress((int) ((value - MIN_VALUE) * zoomGrained));
    }

    public void setMinValue(int minValue) {
        MIN_VALUE = minValue;
    }

    public void setMinValue(float minValue) {
        MIN_VALUE = minValue;
    }

    public void setMaxValue(float maxValue) {
        MAX_VALUE = maxValue;
        zoomBar.setMax((int) ((maxValue - MIN_VALUE) * zoomGrained));
    }

    public void setMaxValue(int maxValue) {
        MAX_VALUE = maxValue;
        zoomBar.setMax(maxValue);
    }

    public float getProgress() {
        return zoomBar.getProgress() * 1.0f / zoomGrained + MIN_VALUE;
    }

    public void startDisplay() {
        if (firstCreate == true) {
            firstCreate = false;
            return;
        }
        if (CameraManager.getInstance().getCurCamera().getCameraProperties().hasFuction(ICatchCamProperty.ICH_CAM_CAP_DIGITAL_ZOOM) == false) {
            return;
        }
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Handler handler = getHandler();
                if (handler != null) {
                    handler.post(new Runnable() {
                        public void run() {
                            setVisibility(View.GONE);
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask, DISPLAY_DURATION);
        setVisibility(View.VISIBLE);
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(ZoomView zoomView, float progress, boolean fromUser);

        void onStartTrackingTouch(ZoomView zoomView);

        void onStopTrackingTouch(ZoomView zoomView);
    }
}
