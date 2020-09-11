package com.icatch.mobilecam.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.icatch.mobilecam.ui.ExtendComponent.ProgressWheel;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.Presenter.VideoPbPresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.ui.Interface.VideoPbView;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;

public class VideoPbActivity extends AppCompatActivity implements VideoPbView {
    private String TAG = VideoPbActivity.class.getSimpleName();
    private TextView timeLapsed;
    private TextView timeDuration;
    private SeekBar seekBar;
    private ImageButton play;
    private ImageButton back;
    private ImageButton deleteBtn;
    private ImageButton downloadBtn;
//    private ImageButton stopBtn;
    private LinearLayout topBar;
    private LinearLayout bottomBar;
    private TextView videoNameTxv;
    private ProgressWheel progressWheel;
    private VideoPbPresenter presenter;
    private SurfaceView mSurfaceView;
    private ImageButton panoramaTypeBtn;

    private LinearLayout moreSettingLayout;
    private ImageButton moreBtn;
    private ImageButton cancelBtn;
    private Switch eisSwitch;
    private TextView deleteTxv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_pb);
        timeLapsed = (TextView) findViewById(R.id.video_pb_time_lapsed);
        timeDuration = (TextView) findViewById(R.id.video_pb_time_duration);
        seekBar = (SeekBar) findViewById(R.id.video_pb_seekBar);
        play = (ImageButton) findViewById(R.id.video_pb_play_btn);
        back = (ImageButton) findViewById(R.id.video_pb_back);
//        stopBtn = (ImageButton) findViewById(R.id.video_pb_stop_btn);

        topBar = (LinearLayout) findViewById(R.id.video_pb_top_layout);
        bottomBar = (LinearLayout) findViewById(R.id.video_pb_bottom_layout);
        mSurfaceView = (SurfaceView) findViewById(R.id.m_surfaceView);
        videoNameTxv = (TextView) findViewById(R.id.video_pb_video_name);
        progressWheel = (ProgressWheel) findViewById(R.id.video_pb_spinner);
        deleteBtn = (ImageButton) findViewById(R.id.delete);
        downloadBtn = (ImageButton) findViewById(R.id.download);

        moreSettingLayout = (LinearLayout) findViewById(R.id.more_setting_layout);
        moreBtn = (ImageButton) findViewById(R.id.more_btn);
        cancelBtn = (ImageButton) findViewById(R.id.cancel_btn);
        eisSwitch = (Switch) findViewById(R.id.eis_switch);
        deleteTxv  = (TextView) findViewById(R.id.delete_txv);
        panoramaTypeBtn = (ImageButton) findViewById(R.id.panorama_type_btn);

        presenter = new VideoPbPresenter(this);
        presenter.setView(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // do not display menu bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.showMoreSettingLayout(true);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.showMoreSettingLayout(false);
            }
        });

        eisSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = eisSwitch.isChecked();
                presenter.enableEIS(isChecked);
            }
        });

        deleteTxv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.delete();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.back();
            }
        });

        panoramaTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.setPanoramaType();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.play();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.delete();
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.download();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                presenter.setTimeLapsedValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                presenter.seekBarOnStartTrackingTouch();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                presenter.seekBarOnStopTrackingTouch();

            }
        });

        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG,"mSurfaceViewImage ClickListener");
                presenter.showBar(topBar.getVisibility() == View.VISIBLE ? false : true);
            }
        });

        mSurfaceView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                View parentView = (View) mSurfaceView.getParent();
                int heigth = parentView.getHeight();
                int width = parentView.getWidth();
                AppLog.d(TAG, "onLayoutChange heigth=" + heigth + " width=" + width);
                presenter.redrawSurface();
            }
        });

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                AppLog.d(TAG, "surfaceCreated");
                presenter.initSurface(mSurfaceView.getHolder());
                presenter.play();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                presenter.setDrawingArea(width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                presenter.destroyVideo(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
            }
        });

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        presenter.onSufaceViewTouchDown(event);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        presenter.onSufaceViewPointerDown(event);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        presenter.onSufaceViewTouchMove(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        presenter.onSufaceViewTouchUp();
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        presenter.onSufaceViewTouchPointerUp();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.submitAppInfo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.isAppBackground();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.removeActivity();
        presenter.destroyVideo(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
    }

    @Override
    public void setTopBarVisibility(int visibility) {
        topBar.setVisibility(visibility);
    }

    @Override
    public void setBottomBarVisibility(int visibility) {
        bottomBar.setVisibility(visibility);
    }

    @Override
    public void setTimeLapsedValue(String value) {
        timeLapsed.setText(value);
    }

    @Override
    public void setTimeDurationValue(String value) {
        timeDuration.setText(value);
    }

    @Override
    public void setSeekBarProgress(int value) {
        seekBar.setProgress(value);
    }

    @Override
    public void setSeekBarMaxValue(int value) {
        seekBar.setMax(value);
    }

    @Override
    public int getSeekBarProgress() {
        return seekBar.getProgress();
    }

    @Override
    public void setSeekBarSecondProgress(int value) {
        seekBar.setSecondaryProgress(value);
    }


    @Override
    public void setPlayBtnSrc(int resid) {
        play.setImageResource(resid);
    }

    @Override
    public void showLoadingCircle(boolean isShow) {
        if (isShow) {
            AppLog.d(TAG, "showLoadingCircle");
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.setText("0%");
            progressWheel.startSpinning();
        } else {
            AppLog.d(TAG, "display LoadingCircle");
            progressWheel.stopSpinning();
            progressWheel.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLoadPercent(int value) {
        String temp = value + "%";
        progressWheel.setText(temp);
    }

    @Override
    public void setVideoNameTxv(String value) {
        videoNameTxv.setText(value);
    }

    @Override
    public void setProgress(float progress) {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                Log.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                Log.d("AppStart", "back");
                presenter.back();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public int getSurfaceViewWidth() {
        View parentView = (View) mSurfaceView.getParent();
        int width = parentView.getWidth();
        return width;
    }

    @Override
    public int getSurfaceViewHeight() {
        View parentView = (View) mSurfaceView.getParent();
        int heigth = parentView.getHeight();
        return heigth;
    }

    @Override
    public void setPanoramaTypeImageResource(int resId) {
        panoramaTypeBtn.setImageResource(resId);
    }

    @Override
    public void setPanoramaTypeBtnVisibility(int visibility) {
        panoramaTypeBtn.setVisibility(visibility);
    }

    @Override
    public void setMoreSettingLayoutVisibility(int visibility) {
        moreSettingLayout.setVisibility(visibility);
    }

    @Override
    public void setEisSwitchChecked(boolean checked) {
        eisSwitch.setChecked(checked);
    }

    @Override
    public void setSeekbarEnabled(boolean enabled) {
        if(seekBar.isEnabled() != enabled){
            AppLog.d(TAG,"setSeekbarEnabled enabled:" + enabled);
            seekBar.setEnabled(enabled);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.redrawSurface();
            }
        }, 50);
        AppLog.d(TAG, "onConfigurationChanged newConfig Orientation=" + newConfig.orientation);
    }
}

