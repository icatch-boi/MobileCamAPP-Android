package com.icatch.mobilecam.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.Presenter.LocalVideoPbPresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.ui.ExtendComponent.ProgressWheel;
import com.icatch.mobilecam.ui.Interface.LocalVideoPbView;

public class LocalVideoPbActivity extends AppCompatActivity implements LocalVideoPbView {
    private String TAG = LocalVideoPbActivity.class.getSimpleName();
    private TextView timeLapsed;
    private TextView timeDuration;
    private SeekBar seekBar;
    private ImageButton play;
    private ImageButton back;
    private RelativeLayout topBar;
    private LinearLayout bottomBar;
    private TextView localVideoNameTxv;
    private SurfaceView mSurfaceViewImage;
    private boolean isShowBar = true;
    private ProgressWheel progressWheel;
    private LocalVideoPbPresenter presenter;
    private String videoPath;
//    private ZoomView zoomView;
    private ImageButton panoramaTypeBtn;
    private LinearLayout moreSettingLayout;
    private ImageButton moreBtn;
    private ImageButton cancelBtn;
    private Switch eisSwitch;
    private TextView codecInfoTxv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panorama_local_video_pb);
        codecInfoTxv = findViewById(R.id.codec_info_txv);
        timeLapsed = (TextView) findViewById(R.id.local_pb_time_lapsed);
        timeDuration = (TextView) findViewById(R.id.local_pb_time_duration);
        seekBar = (SeekBar) findViewById(R.id.local_pb_seekBar);
        play = (ImageButton) findViewById(R.id.local_pb_play_btn);
        back = (ImageButton) findViewById(R.id.local_pb_back);

        topBar = (RelativeLayout) findViewById(R.id.local_pb_top_layout);
        bottomBar = (LinearLayout) findViewById(R.id.local_pb_bottom_layout);
        mSurfaceViewImage = (SurfaceView) findViewById(R.id.m_surfaceView);
        localVideoNameTxv = (TextView) findViewById(R.id.local_pb_video_name);
        progressWheel = (ProgressWheel) findViewById(R.id.local_pb_spinner);
//        zoomView = (ZoomView) findViewById(R.id.zoom_view);

        panoramaTypeBtn = (ImageButton) findViewById(R.id.panorama_type_btn);
        panoramaTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.setPanoramaType();
            }
        });

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        videoPath = data.getString("curfilePath");
        AppLog.i(TAG, "videoPath=" + videoPath);
        presenter = new LocalVideoPbPresenter(this, videoPath);
        presenter.setView(this);
        presenter.initZoomView();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // do not display menu bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSurfaceViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG,"mSurfaceViewImage ClickListener");
                presenter.showBar(topBar.getVisibility() == View.VISIBLE ? false : true);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.back();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.play();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                presenter.setTimeLapsedValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                presenter.startSeekTouch();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                presenter.completedSeekToPosition();
            }
        });

        AppLog.d(TAG, "mSurfaceView = " + mSurfaceViewImage);
        mSurfaceViewImage.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                presenter.initSurface(mSurfaceViewImage.getHolder());
                presenter.play();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                presenter.setDrawingArea(width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                AppLog.d(TAG, " 12233 surfaceDestroyed");
                presenter.destroyVideo();
            }
        });

        mSurfaceViewImage.setOnTouchListener(new View.OnTouchListener() {
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

        moreSettingLayout = (LinearLayout) findViewById(R.id.more_setting_layout);
        moreBtn = (ImageButton) findViewById(R.id.more_btn);
        cancelBtn = (ImageButton) findViewById(R.id.cancel_btn);
        eisSwitch = (Switch) findViewById(R.id.eis_switch);

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


//        mSurfaceViewImage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
//                View parentView = (View) mSurfaceViewImage.getParent();
//                int heigth = parentView.getHeight();
//                int width = parentView.getWidth();
//                AppLog.d(TAG, "onLayoutChange heigth=" + heigth + " width=" + width);
////                presenter.redrawSurface();
//            }
//        });
    }
//            public float prevY;
//            public float prevX;

    //            @Override
//            public boolean onTouch(View v, MotionEvent event) {
////                AppLog.d(TAG,"ACTION_onTouch.....");
//                switch (event.getAction())
//                {
//                    case MotionEvent.ACTION_DOWN:
//				/* save curr point as prev */
//                        this.prevX = event.getX();
//                        this.prevY = event.getY();
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
////                        AppLog.d(TAG,"ACTION_MOVE.....");
//                        presenter.rotateB(event,prevX,prevY);
//                        this.prevX = event.getX();
//                        this.prevY = event.getY();
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        break;
//                }
//                return true;
//            }
//        } );
//    }
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
        AppLog.d(TAG, "showLoadingCircle isShow=" + isShow);
        if (isShow) {
//            MyProgressDialog.showProgressDialog(this,"Loading...");

            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.setText("0%");
            progressWheel.startSpinning();
        } else {
//            MyProgressDialog.closeProgressDialog();
            progressWheel.stopSpinning();
            progressWheel.setVisibility(View.GONE);

        }

    }

    @Override
    public void setLoadPercent(int value) {
        if(value >= 0) {
            String temp = value + "%";
            progressWheel.setText(temp);
        }
    }

    @Override
    public void setVideoNameTxv(String value) {
        localVideoNameTxv.setText(value);
    }

    @Override
    public void setZoomMinValue(float minValue) {
//        zoomView.setMinValue(minValue);
    }

    @Override
    public void setZoomMaxValue(float maxValue) {
//        zoomView.setMaxValue(maxValue);
    }

    @Override
    public void updateZoomRateTV(float zoomRate) {
//        zoomView.updateZoomBarValue(zoomRate);
    }

    @Override
    public void setProgress(float progress) {
//        zoomView.setProgress(progress);
    }

    @Override
    public void showZoomView(int visibility) {
//        zoomView.setVisibility(visibility);
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
        View parentView = (View) mSurfaceViewImage.getParent();
        int width = parentView.getWidth();
        return width;
    }

    @Override
    public int getSurfaceViewHeight() {
        View parentView = (View) mSurfaceViewImage.getParent();
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
    public void setCodecInfoTxv(String info) {
        if(codecInfoTxv.getVisibility() != View.VISIBLE){
            codecInfoTxv.setVisibility(View.VISIBLE);
        }
        codecInfoTxv.setText(info);
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
