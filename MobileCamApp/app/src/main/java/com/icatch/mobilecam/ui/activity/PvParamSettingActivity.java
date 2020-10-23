package com.icatch.mobilecam.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.icatch.mobilecam.Listener.MyOrientoinListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatchtek.reliant.customer.type.ICatchCodec;
import com.icatchtek.reliant.customer.type.ICatchVideoFormat;

import java.util.List;


public class PvParamSettingActivity extends AppCompatActivity {
    private final String TAG = PvParamSettingActivity.class.getSimpleName();
    private Button startPvBtn;
    private RadioGroup videoSizeRadioGroup;
    private RadioGroup frameRateRadioGroup;
    private RadioGroup videoCodecRadioGroup;
    private int curVideoFps = 30;
    private String curVideoCodec = "H264";
    private MyOrientoinListener myOrientoinListener;
    CameraProperties cameraProperties;
    private ICatchVideoFormat curVideoFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pv_size_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cameraProperties = CameraManager.getInstance().getCurCamera().getCameraProperties();
        startPvBtn = (Button) findViewById(R.id.start_pv);
        videoSizeRadioGroup = (RadioGroup) findViewById(R.id.video_size_group);
        frameRateRadioGroup = (RadioGroup) findViewById(R.id.video_fps_group);
        videoCodecRadioGroup = (RadioGroup) findViewById(R.id.video_codec_group);
        startPvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPv();
            }
        });
//        videoSizeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//                if (checkedId == R.id.video_size_1920_960) {
//                    curVideoWidth = 1920;
//                    curVideoHeight = 960;
//                } else if (checkedId == R.id.video_size_1920_1080) {
//                    curVideoWidth = 1920;
//                    curVideoHeight = 1080;
//                } else if (checkedId == R.id.video_size_2k) {
//                    curVideoWidth = 2880;
//                    curVideoHeight = 1440;
//                } else if (checkedId == R.id.video_size_4k) {
//                    curVideoWidth = 3840;
//                    curVideoHeight = 1920;
//                } else {
//                    curVideoWidth = 1920;
//                    curVideoHeight = 960;
//                }
//                AppLog.d(TAG, "frameRateRadioGroup.setOnCheckedChangeListener curVideoWidth=" + curVideoWidth + " curVideoHeight=" + curVideoHeight);
//            }
//        });

        frameRateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.video_fps_15) {
                    curVideoFps = 15;
                } else if (checkedId == R.id.video_fps_10) {
                    curVideoFps = 10;
                } else {
                    curVideoFps = 30;
                }
                AppLog.d(TAG, "frameRateRadioGroup.setOnCheckedChangeListener curVideoFps=" + curVideoFps);
            }
        });

        videoCodecRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if (checkedId == R.id.video_codec_h264) {
                    curVideoCodec = "H264";
                    initVideoSizeRadioGroup(ICatchCodec.ICH_CODEC_H264);
                } else if (checkedId == R.id.video_codec_mjpg) {
                    curVideoCodec = "MJPG";
                    initVideoSizeRadioGroup(ICatchCodec.ICH_CODEC_JPEG);
                }
                AppLog.d(TAG, "videoCodecRadioGroup.setOnCheckedChangeListener curVideoCodec=" + curVideoCodec);
            }
        });

        initVideoSizeRadioGroup(ICatchCodec.ICH_CODEC_H264);
    }

    void initVideoSizeRadioGroup(int codecType){
        List<ICatchVideoFormat> videoFormatList =  cameraProperties.getResolutionList(codecType);
        if(videoFormatList != null && videoFormatList.size() > 0){
            videoSizeRadioGroup.removeAllViews();
            for (int i = 0; i < videoFormatList.size(); i++) {
                final RadioButton radioButton = new RadioButton(this);
                RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.MATCH_PARENT);
                lp.setMargins(15,0,0,0);
                radioButton.setPadding(0, 0, 0, 0);
                //设置文字
                final ICatchVideoFormat temp = videoFormatList.get(i);
                radioButton.setText(temp.getVideoW() + "*" + temp.getVideoH());
                //设置radioButton的点击事件
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        curVideoFormat = temp;
                        Toast.makeText(PvParamSettingActivity.this, "this is radioButton  id:" + radioButton.getId(), Toast.LENGTH_SHORT).show();
                    }
                });
                videoSizeRadioGroup.addView(radioButton);
                if(i==0){
                    // 设置默认选中方式1 ，先获取控件，然后设置选中
                    //                //根据id 获取radioButton 控件
//                                    RadioButton rb_checked = (RadioButton) videoSizeRadioGroup.findViewById(radioButton.getId());
//                                    //设置默认选中
//                                    rb_checked.setChecked(true);
                    videoSizeRadioGroup.check(radioButton.getId());
                    curVideoFormat = temp;
                }
                //将radioButton添加到radioGroup中

            }

        }
    }

    void initVideoCodecRadioGroup(){

    }

    void startPv() {
        int videoWidth = 1920;
        int videoHeight = 960;
        if(curVideoFormat != null ){
            videoWidth  =curVideoFormat.getVideoW();
            videoHeight = curVideoFormat.getVideoH();
        }
        AppLog.d(TAG, "startPv videoWidth=" + videoWidth + " videoHeight=" + videoHeight + " videoFps=" + curVideoFps);
        Intent intent = new Intent();
        intent.putExtra("videoWidth", videoWidth);
        intent.putExtra("videoHeight", videoHeight);
        intent.putExtra("videoFps", curVideoFps);
        intent.putExtra("videoCodec", curVideoCodec);
        intent.setClass(PvParamSettingActivity.this, USBPreviewActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myOrientoinListener = new MyOrientoinListener(this, this);
        boolean autoRotateOn = (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
        //检查系统是否开启自动旋转
        if (autoRotateOn) {
            myOrientoinListener.enable();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myOrientoinListener != null) {
            myOrientoinListener.disable();
            myOrientoinListener = null;
        }
    }
}
