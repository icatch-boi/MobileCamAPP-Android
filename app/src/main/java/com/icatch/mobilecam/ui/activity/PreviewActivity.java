package com.icatch.mobilecam.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.ui.adapter.SettingListAdapter;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.ExtendComponent.ZoomView;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.Mode.PreviewMode;
import com.icatch.mobilecam.Presenter.PreviewPresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.SystemInfo.SystemInfo;
import com.icatch.mobilecam.ui.Interface.PreviewView;
import com.icatchtek.control.customer.type.ICatchCamEventID;

import static com.icatch.mobilecam.data.Message.AppMessage.FACEBOOK_LOGIN_SUCCEED;
import static com.icatch.mobilecam.data.Message.AppMessage.GOOGLE_LOGIN_SUCCEED;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener, PreviewView {

    private static final String TAG = PreviewActivity.class.getSimpleName();
    private PreviewPresenter presenter;
    private SurfaceView mSurfaceView;
    private ImageButton pbBtn;
    private ImageButton captureBtn;
    private ImageView wbStatus;
    private ImageView burstStatus;
    private ImageView wifiStatus;
    private ImageView batteryStatus;
    private ImageView timelapseMode;
    private ImageView slowMotion;
    private ImageView carMode;
    private TextView recordingTime;
    private ImageView autoDownloadImagview;
    private TextView delayCaptureText;
    private RelativeLayout delayCaptureLayout;
    private RelativeLayout imageSizeLayout;
    private RelativeLayout videoSizeLayout;
    private TextView remainRecordingTimeText;
    private TextView remainCaptureCountText;
    private TextView imageSizeTxv;
    private TextView videoSizeTxv;
    private ZoomView zoomView;
    private RelativeLayout setupMainMenu;
    private ListView mainMenuList;
    private MenuItem settingMenu;
    private ActionBar actionBar;
    private TextView noSupportPreviewTxv;
    private PopupWindow pvModePopupWindow;
    private RadioButton captureRadioBtn;
    private RadioButton videoRadioBtn;
    private RadioButton timepLapseRadioBtn;
    private ImageButton pvModeBtn;
    private View contentView;
    private Toolbar toolbar;
	private Button facebookLiveBtn;
    private Button youtubeLiveBtn;
    private Button googleAccountBtn;
    private LinearLayout liveLayout;
    private ImageButton panoramaTypeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.title_preview);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);

        presenter = new PreviewPresenter(PreviewActivity.this);
        presenter.setView(this);

        facebookLiveBtn = (Button) findViewById(R.id.facebook_live_btn);
        youtubeLiveBtn = (Button) findViewById(R.id.youtube_live_btn);
        googleAccountBtn = (Button) findViewById(R.id.google_account_btn);
        liveLayout = (LinearLayout) findViewById(R.id.live_layout);
        mSurfaceView = (SurfaceView) findViewById(R.id.preview);
        mSurfaceView.setOnClickListener(this);
        pbBtn = (ImageButton) findViewById(R.id.multi_pb);
        pbBtn.setOnClickListener(this);
        captureBtn = (ImageButton) findViewById(R.id.doCapture);
        captureBtn.setOnClickListener(this);

        wbStatus = (ImageView) findViewById(R.id.wb_status);
        burstStatus = (ImageView) findViewById(R.id.burst_status);
        wifiStatus = (ImageView) findViewById(R.id.wifi_status);
        batteryStatus = (ImageView) findViewById(R.id.battery_status);
        timelapseMode = (ImageView) findViewById(R.id.timelapse_mode);
        slowMotion = (ImageView) findViewById(R.id.slow_motion);
        carMode = (ImageView) findViewById(R.id.car_mode);
        recordingTime = (TextView) findViewById(R.id.recording_time);
        autoDownloadImagview = (ImageView) findViewById(R.id.auto_download_imageview);
        delayCaptureText = (TextView) findViewById(R.id.delay_capture_text);
        delayCaptureLayout = (RelativeLayout) findViewById(R.id.delay_capture_layout);

        imageSizeLayout = (RelativeLayout) findViewById(R.id.image_size_layout);
        imageSizeTxv = (TextView) findViewById(R.id.image_size_txv);
        remainCaptureCountText = (TextView) findViewById(R.id.remain_capture_count_text);

        videoSizeLayout = (RelativeLayout) findViewById(R.id.video_size_layout);
        videoSizeTxv = (TextView) findViewById(R.id.video_size_txv);
        remainRecordingTimeText = (TextView) findViewById(R.id.remain_recording_time_text);
        setupMainMenu = (RelativeLayout) findViewById(R.id.setupMainMenu);
        mainMenuList = (ListView) findViewById(R.id.setup_menu_listView);
        noSupportPreviewTxv = (TextView) findViewById(R.id.not_support_preview_txv);
        pvModeBtn = (ImageButton) findViewById(R.id.pv_mode);

        contentView = LayoutInflater.from(PreviewActivity.this).inflate(R.layout.camer_mode_switch_layout, null);
        pvModePopupWindow = new PopupWindow(contentView, GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT, true);
        pvModePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        pvModePopupWindow.setFocusable(true);
        pvModePopupWindow.setOutsideTouchable(true);

        captureRadioBtn = (RadioButton) contentView.findViewById(R.id.capture_radio);
        videoRadioBtn = (RadioButton) contentView.findViewById(R.id.video_radio);
        timepLapseRadioBtn = (RadioButton) contentView.findViewById(R.id.timeLapse_radio);

        facebookLiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.startOrStopFacebookLive();
            }
        });

        youtubeLiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                presenter.startOrStopYouTubeLive();
                presenter.startYouTubeLive();
            }
        });

        googleAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.gotoGoogleAccountManagement();
            }
        });
        zoomView = (ZoomView) findViewById(R.id.zoom_view);
        zoomView.setZoomInOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.zoomIn();
            }
        });

        zoomView.setZoomOutOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.zoomOut();
            }
        });

        zoomView.setOnSeekBarChangeListener(new ZoomView.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(ZoomView zoomView, float progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(ZoomView zoomView) {

            }

            @Override
            public void onStopTrackingTouch(ZoomView zoomView) {
                presenter.zoomBySeekBar();
            }
        });

        mainMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.showSettingDialog(position);
            }
        });

        pvModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.showPvModePopupWindow();
            }
        });

        captureRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changePreviewMode(PreviewMode.APP_STATE_STILL_MODE);
            }
        });

        videoRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changePreviewMode(PreviewMode.APP_STATE_VIDEO_MODE);
            }
        });
        timepLapseRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changePreviewMode(PreviewMode.APP_STATE_TIMELAPSE_MODE);
            }
        });

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                AppLog.d(TAG, "surfaceCreated!!!");
                presenter.initSurface(mSurfaceView.getHolder());
                presenter.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                AppLog.d(TAG, "surfaceChanged!!!");
                presenter.setDrawingArea(width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                presenter.destroyPreview();
            }
        });

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        presenter.onSufaceViewTouchDown(event);
                        break;
                    // 多点触摸
                    case MotionEvent.ACTION_POINTER_DOWN:
                        presenter.onSufaceViewPointerDown(event);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        presenter.onSufaceViewTouchMove(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        presenter.onSufaceViewTouchUp();
                        break;

                    // 多点松开
                    case MotionEvent.ACTION_POINTER_UP:
                        presenter.onSufaceViewTouchPointerUp();
                        break;
                }
                return true;
            }
        });
//        presenter.initRender();
        GlobalInfo.getInstance().addEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_REMOVED);
        panoramaTypeBtn = (ImageButton) findViewById(R.id.panorama_type_btn);
        panoramaTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.setPanoramaType();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLog.d(TAG, "onResume");
        presenter.submitAppInfo();
        presenter.initPreview();
        presenter.initStatus();
        presenter.addEvent();
//        AppDialog.showDialogWarn( PanoramaPreviewActivity.this, R.string.text_preview_hint_info );
    }

    @Override
    protected void onStop() {
        AppLog.d(TAG, "onStop");
        super.onStop();
        presenter.isAppBackground();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                AppLog.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                AppLog.d("AppStart", "back");
                presenter.finishActivity();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        AppLog.d(TAG, "onDestroy");
        super.onDestroy();
        presenter.removeActivity();
        presenter.destroyPreview();
        presenter.delEvent();
        presenter.disconnectCamera();
        presenter.delConnectFailureListener();
        presenter.unregisterWifiSSReceiver();
        GlobalInfo.getInstance().delEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_REMOVED);
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case FACEBOOK_LOGIN_SUCCEED:
//                MyToast.show(this,"click the facebook live button!");
                MyProgressDialog.showProgressDialog(this, R.string.action_processing);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
//                        presenter.startOrStopFacebookLive();
                    }
                }, 2000);

                break;
            case GOOGLE_LOGIN_SUCCEED:
//                MyProgressDialog.showProgressDialog(this,R.string.action_processing);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        MyProgressDialog.closeProgressDialog();
//                        presenter.startYoutubeStreamPublish();
//                    }
//                },2000);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up ImageButton, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_setting) {
//            return true;
//        }
        if (id == android.R.id.home) {
            AppLog.e("tigertiger", "id == android.R.id.home");
            presenter.finishActivity();
        } else if (id == R.id.action_setting) {
            settingMenu = item;
            presenter.loadSettingMenuList();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        AppLog.i(TAG, "click the v.getId() =" + v.getId());
        switch (v.getId()) {
            case R.id.multi_pb:
                AppLog.i(TAG, "click the multi_pb");
                presenter.redirectToAnotherActivity(PreviewActivity.this, RemoteMultiPbActivity.class);
                break;
            case R.id.doCapture:
                AppLog.i(TAG, "click the doCapture");
                presenter.startOrStopCapture();
                break;
            default:
                break;
        }
    }

    @Override
    public void setWbStatusVisibility(int visibility) {
        wbStatus.setVisibility(visibility);
    }

    @Override
    public void setBurstStatusVisibility(int visibility) {
        burstStatus.setVisibility(visibility);
    }

    @Override
    public void setWifiStatusVisibility(int visibility) {
        wifiStatus.setVisibility(visibility);
    }

    @Override
    public void setWifiIcon(int drawableId) {
        wifiStatus.setBackgroundResource(drawableId);
    }

    @Override
    public void setBatteryStatusVisibility(int visibility) {
        batteryStatus.setVisibility(visibility);
    }

    @Override
    public void setBatteryIcon(int drawableId) {
        batteryStatus.setBackgroundResource(drawableId);
    }

    @Override
    public void settimeLapseModeVisibility(int visibility) {
        timelapseMode.setVisibility(visibility);
    }

    @Override
    public void settimeLapseModeIcon(int drawableId) {
        timelapseMode.setBackgroundResource(drawableId);
    }

    @Override
    public void setSlowMotionVisibility(int visibility) {
        slowMotion.setVisibility(visibility);
    }

    @Override
    public void setCarModeVisibility(int visibility) {
        carMode.setVisibility(visibility);
    }

    @Override
    public void setRecordingTimeVisibility(int visibility) {
        recordingTime.setVisibility(visibility);
    }

    @Override
    public void setAutoDownloadVisibility(int visibility) {
        autoDownloadImagview.setVisibility(visibility);
    }

    @Override
    public void setCaptureBtnBackgroundResource(int id) {
        captureBtn.setBackgroundResource(id);
    }

    @Override
    public void setRecordingTime(String laspeTime) {
        recordingTime.setText(laspeTime);
    }

    @Override
    public void setDelayCaptureLayoutVisibility(int visibility) {
        delayCaptureLayout.setVisibility(visibility);
    }

    @Override
    public void setDelayCaptureTextTime(String delayCaptureTime) {
        delayCaptureText.setText(delayCaptureTime);
    }

    @Override
    public void setImageSizeLayoutVisibility(int visibility) {
        imageSizeLayout.setVisibility(visibility);
    }

    @Override
    public void setRemainCaptureCount(String remainCaptureCount) {
        remainCaptureCountText.setText(remainCaptureCount);
    }

    @Override
    public void setVideoSizeLayoutVisibility(int visibility) {
        videoSizeLayout.setVisibility(visibility);
        remainRecordingTimeText.setVisibility(visibility);
    }

    @Override
    public void setRemainRecordingTimeText(String remainRecordingTime) {
        remainRecordingTimeText.setText(remainRecordingTime);
    }

    @Override
    public void setBurstStatusIcon(int drawableId) {
        burstStatus.setBackgroundResource(drawableId);
    }

    @Override
    public void setWbStatusIcon(int drawableId) {
        wbStatus.setBackgroundResource(drawableId);
    }

    @Override
    public void setUpsideVisibility(int visibility) {
        carMode.setVisibility(visibility);
    }


    @Override
    public void setCaptureBtnEnability(boolean enablity) {
        captureBtn.setEnabled(enablity);
    }

    @Override
    public void setVideoSizeInfo(String sizeInfo) {
        AppLog.i(TAG, "sizeInfo = " + sizeInfo);
        videoSizeTxv.setText(sizeInfo);
    }

    @Override
    public void setImageSizeInfo(String sizeInfo) {
        AppLog.i(TAG, "sizeInfo = " + sizeInfo);
        imageSizeTxv.setText(sizeInfo);
    }

    @Override
    public void showZoomView() {
        zoomView.startDisplay();
    }

    @Override
    public void setMaxZoomRate(final float maxZoomRate) {
        zoomView.setMaxValue(maxZoomRate);
    }

    @Override
    public void setMinZoomRate(final float minZoomRate) {
        zoomView.setMinValue(minZoomRate);
    }
	
	@Override
    public void setFacebookBtnTxv(String value) {
        facebookLiveBtn.setText(value);
    }
	
	@Override
    public void setFacebookBtnTxv(int resId) {
        facebookLiveBtn.setText(resId);
    }


    @Override
    public void setYouTubeBtnTxv(String value) {
        youtubeLiveBtn.setText(value);
    }

    @Override
    public void setYouTubeBtnTxv(int resId) {
        youtubeLiveBtn.setText(resId);
    }

    @Override
    public void setYouTubeLiveLayoutVisibility(int visibility) {
        liveLayout.setVisibility(visibility);
    }


    @Override
    public float getZoomViewProgress() {
        return zoomView.getProgress();
    }

    @Override
    public float getZoomViewMaxZoomRate() {
        return ZoomView.MAX_VALUE;
    }

    @Override
    public void updateZoomViewProgress(float currentZoomRatio) {
        zoomView.updateZoomBarValue(currentZoomRatio);
        zoomView.setProgress(currentZoomRatio);
    }

    @Override
    public int getSetupMainMenuVisibility() {
        return setupMainMenu.getVisibility();
    }

    @Override
    public void setSetupMainMenuVisibility(int visibility) {
        setupMainMenu.setVisibility(visibility);
    }

    @Override
    public void setAutoDownloadBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            autoDownloadImagview.setImageBitmap(bitmap);
        }
    }

    @Override
    public void setActionBarTitle(int resId) {
        actionBar.setTitle(resId);
    }

    @Override
    public void setSettingBtnVisible(boolean isVisible) {
        settingMenu.setVisible(isVisible);
    }

    @Override
    public void setBackBtnVisibility(boolean isVisible) {
        actionBar.setDisplayHomeAsUpEnabled(isVisible);
    }

    @Override
    public void setSettingMenuListAdapter(SettingListAdapter settingListAdapter) {
        mainMenuList.setAdapter(settingListAdapter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.redrawSurface();
            }
        }, 200);
        AppLog.d(TAG, "onConfigurationChanged newConfig Orientation=" + newConfig.orientation);
    }

    @Override
    public void setSupportPreviewTxvVisibility(int visibility) {
        noSupportPreviewTxv.setVisibility(visibility);
    }

    @Override
    public void setPvModeBtnBackgroundResource(int drawableId) {
        pvModeBtn.setBackgroundResource(drawableId);
    }

    @Override
    public void setTimepLapseRadioBtnVisibility(int visibility) {
        timepLapseRadioBtn.setVisibility(visibility);
    }

    @Override
    public void setCaptureRadioBtnVisibility(int visibility) {
        captureRadioBtn.setVisibility(visibility);
    }

    @Override
    public void setVideoRadioBtnVisibility(int visibility) {
        videoRadioBtn.setVisibility(visibility);
    }

    @Override
    public void setTimepLapseRadioChecked(boolean checked) {
        timepLapseRadioBtn.setChecked(checked);
    }

    @Override
    public void setCaptureRadioBtnChecked(boolean checked) {
        captureRadioBtn.setChecked(checked);
    }

    @Override
    public void setVideoRadioBtnChecked(boolean checked) {
        videoRadioBtn.setChecked(checked);
    }

    @Override
    public void showPopupWindow(int curMode) {
        if (pvModePopupWindow != null) {
            int height = SystemInfo.getMetrics(this.getApplicationContext()).heightPixels;
            int contentViewH = contentView.getHeight();
            if (contentViewH == 0) {
                contentViewH = pvModeBtn.getHeight() * 5;
            }
            pvModePopupWindow.showAsDropDown(pvModeBtn, 0, -pvModeBtn.getHeight() - contentViewH);
        }
    }

    @Override
    public void dismissPopupWindow() {
        if (pvModePopupWindow != null) {
            if (pvModePopupWindow.isShowing()) {
                pvModePopupWindow.dismiss();
            }
        }
    }

    @Override
    public int getSurfaceViewWidth() {
        View parentView = (View)  mSurfaceView.getParent();
        int width = parentView.getWidth();
        return width;
    }

    @Override
    public int getSurfaceViewHeight() {
        View parentView = (View)  mSurfaceView.getParent();
        int heigth = parentView.getHeight();
        return heigth;
    }

    @Override
    public void setPanoramaTypeBtnSrc(int srcId) {
        panoramaTypeBtn.setImageResource(srcId);
    }

    @Override
    public void setPanoramaTypeBtnVisibility(int visibility) {
        panoramaTypeBtn.setVisibility(visibility);
    }
}
