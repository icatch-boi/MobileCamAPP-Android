package com.icatch.mobilecam.Function.Setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.icatch.mobilecam.Function.BaseProrertys;
import com.icatch.mobilecam.Function.SDKEvent;
import com.icatch.mobilecam.Listener.OnSettingCompleteListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.MyCamera.MyCamera;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.CameraAction;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.GlobalApp.ExitApp;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.data.Message.AppMessage;
import com.icatch.mobilecam.data.PropertyId.PropertyId;
import com.icatch.mobilecam.data.type.TimeLapseInterval;
import com.icatch.mobilecam.data.type.TimeLapseMode;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.ui.appdialog.AppDialog;
import com.icatch.mobilecam.utils.StorageUtil;
import com.icatch.mobilecam.utils.fileutils.FileTools;
import com.icatch.mobilecam.utils.WifiAPUtil;
import com.icatch.mobilecam.utils.WifiCheck;
import com.icatchtek.control.customer.type.ICatchCamEventID;
import com.icatchtek.reliant.customer.type.ICatchImageSize;

import java.lang.reflect.Field;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by zhang yanhu C001012 on 2015/12/30 16:33.
 */
public class OptionSetting {
    private final static String TAG = OptionSetting.class.getSimpleName();
    private OnSettingCompleteListener onSettingCompleteListener;
    private AlertDialog alertDialog;
    private final SettingHander handler = new SettingHander();
    private SDKEvent sdkEvent;
    private Context context;
    private Activity activity;
    private String wifiSsid, password;
    private MyCamera myCamera;
    private BaseProrertys baseProrertys;
    private CameraProperties cameraProperties;
    private CameraAction cameraAction;

    public OptionSetting() {
        myCamera = CameraManager.getInstance().getCurCamera();
        baseProrertys = myCamera.getBaseProrertys();
        cameraProperties = myCamera.getCameraProperties();
        cameraAction = myCamera.getCameraAction();
    }

    public void addSettingCompleteListener(OnSettingCompleteListener onSettingCompleteListener) {
        this.onSettingCompleteListener = onSettingCompleteListener;
    }

    public void showSettingDialog(int nameId, Activity activity) {
        this.context = activity;
        this.activity = activity;
        switch (nameId) {
            case R.string.setting_image_size:
                Log.d("1111", "setting_image_size");
                showImageSizeOptionDialog(context);
                break;
            case R.string.setting_video_size:
                Log.d("1111", "setting_video_size");
                showVideoSizeOptionDialog(context);
                break;
            case R.string.setting_capture_delay:
                Log.d("1111", "setting_capture_delay");
                showDelayTimeOptionDialog(context);
                break;
            case R.string.title_burst:
                showBurstOptionDialog(context);
                break;
            case R.string.title_awb:
                Log.d("1111", "showWhiteBalanceOptionDialog =");
                showWhiteBalanceOptionDialog(context);
                break;
            case R.string.setting_power_supply:
                showElectricityFrequencyOptionDialog(context);
                break;
            case R.string.setting_datestamp:
                showDateStampOptionDialog(context);
                break;
            case R.string.setting_format:
                if (cameraProperties.isSDCardExist() == false) {
                    sdCardIsNotReadyAlert(context);
                    break;
                }
                showFormatConfirmDialog(context);
                break;
            case R.string.setting_time_lapse_interval:
                showTimeLapseIntervalDialog(context);
                break;
            case R.string.setting_time_lapse_duration:
                showTimeLapseDurationDialog(context);
                break;
            case R.string.title_timeLapse_mode:
                showTimeLapseModeDialog(context);
                break;
            case R.string.slowmotion:
                showSlowMotionDialog(context);
                break;
            case R.string.upside:
                showUpsideDialog(context);
                break;
            case R.string.camera_wifi_configuration:
                showCameraConfigurationDialog(context);
                break;
            case R.string.setting_update_fw:
                if (cameraProperties.isSDCardExist() == false) {
                    sdCardIsNotReadyAlert(context);
                    break;
                }
                String fwPth = Environment.getExternalStorageDirectory().toString() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;;
                String fwUpgradeName = AppInfo.FW_UPGRADE_FILENAME;
                if (!FileTools.checkFwUpgradeFile(fwPth,fwUpgradeName)) {
                    String msg = context.getString(R.string.setting_updatefw_upgrade_file_not_exist)
                            .replace("$1$",fwUpgradeName)
                            .replace("$2$",AppInfo.PROPERTY_CFG_DIRECTORY_PATH);
                    AppDialog.showDialogWarn(context,msg);
                    return;
                }
                showUpdateFWDialog(context);
                break;
            case R.string.setting_auto_download_size_limit:
                showSetDownloadSizeLimitDialog(context);
                break;

            case R.string.setting_enable_wifi_hotspot:
                showEnableWifihotspotDialog();
                break;
            case R.string.setting_title_exposure_compensation:
                AppLog.d("1111", "showExposureCompensationDialog");
                showExposureCompensationDialog(context);
                break;
            case R.string.setting_title_video_file_length:
                AppLog.d("1111", "showVideoFileLengthDialog");
                showVideoFileLengthDialog(context);
                break;

            case R.string.setting_title_screen_saver:
                AppLog.d("1111", "showScreenSaverDialog");
                showScreenSaverDialog(context);
                break;
            case R.string.setting_title_auto_power_off:
                AppLog.d("1111", "showAutoPowerOffDialog");
                showAutoPowerOffDialog(context);
                break;

            case R.string.setting_title_fast_motion_movie:
                AppLog.d("1111", "showFastMotionMovieDialog");
                showFastMotionMovieDialog(context);
                break;
            case R.string.setting_storage_location:
                AppLog.d("1111", "showStorageLocationDialog");
                showStorageLocationDialog(context);
                break;

        }
    }

    public  void showStorageLocationDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.setting_storage_location);
        boolean sdCardExist = StorageUtil.sdCardExist(context);
        final String[] storageLocationString;
        int curIdx = 0;
        if (sdCardExist) {
            storageLocationString = new String[2];
            storageLocationString[0] = context.getResources().getString(R.string.setting_internal_storage);
            storageLocationString[1] = context.getResources().getString(R.string.setting_sd_card_storage);
        } else {
            storageLocationString = new String[1];
            storageLocationString[0] = context.getResources().getString(R.string.setting_internal_storage);
        }
        SharedPreferences preferences = context.getSharedPreferences("appData", MODE_PRIVATE);
        String storageLocation = preferences.getString("storageLocation", "InternalStorage");
        if (storageLocation.equals("InternalStorage")) {
            curIdx = 0;
        } else {
            curIdx = 1;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (arg1 == 0) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("appData", MODE_PRIVATE).edit();
                    editor.putString("storageLocation", "InternalStorage");
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = context.getSharedPreferences("appData", MODE_PRIVATE).edit();
                    editor.putString("storageLocation", "SdCard");
                    editor.commit();
                }
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
                AppLog.d("tigertiger", "showStorageLocationDialog  storageLocation =" + arg1);
            }
        };
        showOptionDialog(title, storageLocationString, curIdx, listener, true);
    }

    public void showFastMotionMovieDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.setting_title_fast_motion_movie);
        final String[] fastMotionMovieUIString = baseProrertys.getFastMotionMovie().getValueList();
        if (fastMotionMovieUIString == null) {
            AppLog.e(TAG, "fastMotionMovieUIString == null");
            return;
        }
        int length = fastMotionMovieUIString.length;
        int curIdx = 0;
        String temp = baseProrertys.getFastMotionMovie().getCurrentUiStringInPreview();
        for (int i = 0; i < length; i++) {
            if (fastMotionMovieUIString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getFastMotionMovie().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, fastMotionMovieUIString, curIdx, listener, true);
    }

    public void showAutoPowerOffDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.setting_title_auto_power_off);
        final String[] autoPowerOffUIString = baseProrertys.getAutoPowerOff().getValueList();
        if (autoPowerOffUIString == null) {
            AppLog.e(TAG, "autoPowerOffUIString == null");
            return;
        }
        int length = autoPowerOffUIString.length;
        int curIdx = 0;
        String temp = baseProrertys.getAutoPowerOff().getCurrentUiStringInPreview();
        for (int i = 0; i < length; i++) {
            if (autoPowerOffUIString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getAutoPowerOff().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, autoPowerOffUIString, curIdx, listener, true);
    }

    public void showScreenSaverDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.setting_title_screen_saver);
        final String[] screenSaverUIString = baseProrertys.getScreenSaver().getValueList();
        if (screenSaverUIString == null) {
            AppLog.e(TAG, "screenSaverUIString == null");
            return;
        }
        int length = screenSaverUIString.length;
        int curIdx = 0;
        String temp = baseProrertys.getScreenSaver().getCurrentUiStringInPreview();
        for (int i = 0; i < length; i++) {
            if (screenSaverUIString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getScreenSaver().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, screenSaverUIString, curIdx, listener, true);
    }

    public void showEnableWifihotspotDialog() {
        LayoutInflater factory = LayoutInflater.from(context);
        View textEntryView = factory.inflate(R.layout.setting_enable_wifi_hotspot, null);
        final EditText wifiName = (EditText) textEntryView.findViewById(R.id.wifi_ssid);
//        final String name = CameraProperties.getInstance().getCameraSsid();
//        wifiName.setText(name);
        final EditText cameraPassword = (EditText) textEntryView.findViewById(R.id.wifi_password);
//        final String password = CameraProperties.getInstance().getCameraPassword();
//        cameraPassword.setText(password);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(context);
        ad1.setTitle(R.string.setting_enable_wifi_hotspot);
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(textEntryView);
        ad1.setCancelable(true);

        ad1.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.d("1111", "KeyEvent.KEYCODE_BACK");
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
                return false;
            }
        });

        ad1.setPositiveButton(R.string.camera_configuration_set, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                wifiSsid = wifiName.getText().toString();
                if(wifiSsid.isEmpty() || password.isEmpty()){
                    Toast.makeText(context, "Wifi name or password cannot be empty.", Toast.LENGTH_LONG).show();
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;

                }
                if (wifiSsid.length() > 20) {
                    Toast.makeText(context, R.string.camera_name_limit, Toast.LENGTH_LONG).show();
                    // do not allow dialog close
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                password = cameraPassword.getText().toString();
                if (password.length() > 20) {
                    Toast.makeText(context, R.string.password_limit, Toast.LENGTH_LONG).show();
                    // do not allow dialog close
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

                // allow dialog close
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, true);
                } catch (Exception e) {
                    e.printStackTrace();

                }
                MyProgressDialog.showProgressDialog(context, R.string.action_processing);
                WifiAPUtil.getInstance(context.getApplicationContext()).regitsterHandler(handler);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        boolean ret = cameraProperties.setStringPropertyValue(PropertyId.STA_MODE_SSID, wifiSsid);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!ret) {
                            handler.obtainMessage(AppMessage.AP_MODE_TO_STA_MODE_FAILURE).sendToTarget();
                        }
                        ret = cameraProperties.setStringPropertyValue(PropertyId.STA_MODE_PASSWORD, password);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!ret) {
                            handler.obtainMessage(AppMessage.AP_MODE_TO_STA_MODE_FAILURE).sendToTarget();
                        }
                        ret = cameraProperties.setPropertyValue(PropertyId.AP_MODE_TO_STA_MODE, 1);
                        if (!ret) {
                            handler.obtainMessage(AppMessage.AP_MODE_TO_STA_MODE_FAILURE).sendToTarget();
                            return;
                        }
                        ret = WifiAPUtil.getInstance(context).turnOnWifiAp(wifiSsid, password, WifiAPUtil.WifiSecurityType.WIFICIPHER_WPA2);
                        if (!ret) {
                            handler.obtainMessage(AppMessage.AP_MODE_TO_STA_MODE_FAILURE).sendToTarget();
                        } else {
//                            handler.obtainMessage( AppMessage.AP_MODE_TO_STA_MODE_SUSSED).sendToTarget();
                        }
                    }
                }).start();

            }
        });
        ad1.show();
    }

    private void showUpdateFWDialog(final Context context) {
        AppLog.i(TAG, "showUpdateFWDialog");
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.setting_updateFW_prompt);
        builder.setNegativeButton(R.string.setting_no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton(R.string.setting_yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                if (sdkEvent == null) {
                    sdkEvent = new SDKEvent(handler);
                }
                sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_COMPLETED);
                sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_POWEROFF);

                sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHECK);
                sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHKSUMERR);
                sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_NG);

                MyProgressDialog.showProgressDialog(context, R.string.setting_update_fw);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int messageId;
                        String filePath = Environment.getExternalStorageDirectory().toString() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
                        //FileTools.copyFile(R.raw.sphost,filePath);
                        String fileName = filePath + AppInfo.FW_UPGRADE_FILENAME;
                        if (!cameraAction.updateFW(fileName)) {
                            messageId = R.string.text_operation_success;
                            AlertDialog.Builder updateFWFailedBuilder = new AlertDialog.Builder(context);
                            updateFWFailedBuilder.setMessage(R.string.setting_updatefw_failedInfo);
                            updateFWFailedBuilder.setNegativeButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("1111", "update FW has failed,App quit");
                                    ExitApp.getInstance().exit();
                                }
                            });
                            alertDialog = updateFWFailedBuilder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        }
                    }
                }).start();
            }
        });
        builder.create().show();
    }

    private void showCameraConfigurationDialog(final Context context) {
        // TODO Auto-generated method stub
        LayoutInflater factory = LayoutInflater.from(context);
        View textEntryView = factory.inflate(R.layout.camera_name_password_set, null);
        final EditText cameraName = (EditText) textEntryView.findViewById(R.id.camera_name);
        final String name = cameraProperties.getCameraSsid();
        cameraName.setText(name);
        final EditText cameraPassword = (EditText) textEntryView.findViewById(R.id.wifi_password);
        final String password = cameraProperties.getCameraPassword();
        cameraPassword.setText(password);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(context);
        ad1.setTitle(R.string.camera_wifi_configuration);
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(textEntryView);
        ad1.setCancelable(true);

        ad1.setNegativeButton(R.string.gallery_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        ad1.setPositiveButton(R.string.camera_configuration_set, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                String temp1 = cameraName.getText().toString();
                if (temp1.length() > 20 || temp1.length() < 1) {
                    Toast.makeText(context, R.string.camera_name_limit, Toast.LENGTH_LONG).show();
                    // do not allow dialog close
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                String temp = cameraPassword.getText().toString();
                if (temp.length() > 10 || temp.length() < 8) {
                    Toast.makeText(context, R.string.password_limit, Toast.LENGTH_LONG).show();
                    // do not allow dialog close
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

                // allow dialog close
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, true);
                } catch (Exception e) {
                    e.printStackTrace();

                }

                if (name.equals(cameraName.getText().toString()) == false) {
                    cameraProperties.setCameraSsid(cameraName.getText().toString());
                }
                if (password.equals(temp) == false) {
                    cameraProperties.setCameraPassword(cameraPassword.getText().toString());
                }
            }
        });
        ad1.show();
    }

    private void showUpsideDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.upside);
        // final MyCamera currCamera =
        // GlobalInfo.getInstance().getCurrentCamera();
        final String[] upsideUIString = baseProrertys.getUpside().getValueList();
        if (upsideUIString == null) {
            AppLog.e(TAG, "upsideUIString == null");
            return;
        }
        int length = upsideUIString.length;
        int curIdx = 0;
        String curValue = baseProrertys.getUpside().getCurrentUiStringInSetting();
        for (int i = 0; i < length; i++) {
            if (upsideUIString[i].equals(curValue)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getUpside().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, upsideUIString, curIdx, listener, true);
    }


    private void showSlowMotionDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.title_slow_motion);
        final String[] slowmotionUIString = baseProrertys.getSlowMotion().getValueList();
        if (slowmotionUIString == null) {
            AppLog.e(TAG, "slowmotionUIString == null");
            return;
        }
        int length = slowmotionUIString.length;
        int curIdx = 0;
        String curValue = baseProrertys.getSlowMotion().getCurrentUiStringInSetting();
        for (int i = 0; i < length; i++) {
            if (slowmotionUIString[i].equals(curValue)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getSlowMotion().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, slowmotionUIString, curIdx, listener, true);
    }


    private void showTimeLapseModeDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.title_timeLapse_mode);
        final String[] timeLapseModeString = baseProrertys.getTimeLapseMode().getValueList();
        if (timeLapseModeString == null) {
            AppLog.e(TAG, "timeLapseModeString == null");
            return;
        }
        int length = timeLapseModeString.length;
        int curIdx = 0;
        String curValue = baseProrertys.getTimeLapseMode().getCurrentUiStringInSetting();
        for (int i = 0; i < length; i++) {
            Log.d("tigertiger", "timeLapseModeString[i] =" + timeLapseModeString[i]);
            if (timeLapseModeString[i] != null && timeLapseModeString[i].equals(curValue)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                myCamera.timeLapsePreviewMode = arg1;
                arg0.dismiss();
                onSettingCompleteListener.settingTimeLapseModeComplete(arg1);
                onSettingCompleteListener.onOptionSettingComplete();
                Log.d("tigertiger", "showTimeLapseModeDialog  timeLapseMode =" + arg1);
            }
        };
        showOptionDialog(title, timeLapseModeString, curIdx, listener, true);
    }

    private void showTimeLapseDurationDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.setting_time_lapse_duration);
        final String[] videoTimeLapseDurationString = baseProrertys.gettimeLapseDuration().getValueStringList();
        if (videoTimeLapseDurationString == null) {
            AppLog.e(TAG, "videoTimeLapseDurationString == null");
            return;
        }
        int length = videoTimeLapseDurationString.length;

        int curIdx = 0;
        String temp = baseProrertys.gettimeLapseDuration().getCurrentValue();
        for (int i = 0; i < length; i++) {
            if (videoTimeLapseDurationString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.gettimeLapseDuration().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, videoTimeLapseDurationString, curIdx, listener, true);
    }

    private void showTimeLapseIntervalDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.setting_time_lapse_interval);
        final TimeLapseInterval timeLapseInterval;
        AppLog.e(TAG, "showTimeLapseIntervalDialog timeLapsePreviewMode:" + myCamera.timeLapsePreviewMode);
        if (myCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_STILL) {
            timeLapseInterval = baseProrertys.getTimeLapseStillInterval();
        } else {
            timeLapseInterval = baseProrertys.getTimeLapseVideoInterval();
        }

        final String[] videoTimeLapseIntervalString = timeLapseInterval.getValueStringList();
        if (videoTimeLapseIntervalString == null) {
            AppLog.e(TAG, "videoTimeLapseIntervalString == null");
            return;
        }
        int length = videoTimeLapseIntervalString.length;

        int curIdx = 0;
        String temp = timeLapseInterval.getCurrentValue();
        for (int i = 0; i < length; i++) {
            if (videoTimeLapseIntervalString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                timeLapseInterval.setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, videoTimeLapseIntervalString, curIdx, listener, true);
    }

    private void showDelayTimeOptionDialog(final Context context, final OnSettingCompleteListener settingCompleteListener) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.stream_set_timer);
        final String[] delayTimeUIString = baseProrertys.getCaptureDelay().getValueList();
        if (delayTimeUIString == null) {
            AppLog.e(TAG, "delayTimeUIString == null");
            return;
        }
        int length = delayTimeUIString.length;
        int curIdx = 0;
        String temp = baseProrertys.getCaptureDelay().getCurrentUiStringInPreview();
        for (int i = 0; i < length; i++) {
            if (delayTimeUIString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getCaptureDelay().setValueByPosition(arg1);
                arg0.dismiss();
                settingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, delayTimeUIString, curIdx, listener, true);
    }

    public void showImageSizeOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.stream_set_res_photo);

        final String[] imageSizeUIString = baseProrertys.getImageSize().getValueArrayString();
        if (imageSizeUIString == null) {
            AppLog.e(TAG, "imageSizeUIString == null");
            return;
        }
        int length = imageSizeUIString.length;
        int curIdx = 0;
        String curValue = baseProrertys.getImageSize().getCurrentUiStringInSetting();
        for (int ii = 0; ii < length; ii++) {
            if (imageSizeUIString[ii].equals(curValue)) {
                curIdx = ii;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getImageSize().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, imageSizeUIString, curIdx, listener, true);
    }

    public void showUSBImageSizeOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        final CharSequence title = context.getResources().getString(R.string.stream_set_res_photo);
        final Handler handler = new Handler();
        final MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        MyProgressDialog.showProgressDialog(context, R.string.action_processing);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<ICatchImageSize> list = myCamera.getPanoramaPreviewPlayback().getSupportedImageSize();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                    }
                });
                if (list == null) {
                    AppLog.e(TAG, "list == null");

                    return;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int curIdx = 0;
                        String[] imageSizeUIString = new String[list.size()];
                        ICatchImageSize curImagesize = myCamera.getPanoramaPreviewPlayback().getCurImageSize();
                        if (curImagesize == null) {
                            curIdx = 0;
                        } else {
                            for (int ii = 0; ii < list.size(); ii++) {
                                ICatchImageSize size = list.get(ii);
                                if (size.getImageH() == curImagesize.getImageH() && size.getImageW() == curImagesize.getImageW()) {
                                    curIdx = ii;
                                    break;
                                }
                            }
                        }
                        for (int ii = 0; ii < list.size(); ii++) {
                            ICatchImageSize size = list.get(ii);
                            imageSizeUIString[ii] = size.getImageW() + "x" + size.getImageH();
                        }
                        if (imageSizeUIString == null) {
                            AppLog.e(TAG, "imageSizeUIString == null");
                            return;
                        }

                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                myCamera.getPanoramaPreviewPlayback().setImageSize(list.get(arg1));
                                arg0.dismiss();
                                if (onSettingCompleteListener != null) {
                                    onSettingCompleteListener.onOptionSettingComplete();
                                }
                            }
                        };
                        showOptionDialog(title, imageSizeUIString, curIdx, listener, true);
                    }
                });
            }
        }).start();
    }

    private void showDelayTimeOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.stream_set_timer);
        final String[] delayTimeUIString = baseProrertys.getCaptureDelay().getValueList();
        if (delayTimeUIString == null) {
            AppLog.e(TAG, "delayTimeUIString == null");
            return;
        }
        int length = delayTimeUIString.length;
        int curIdx = 0;
        String temp = baseProrertys.getCaptureDelay().getCurrentUiStringInPreview();
        for (int i = 0; i < length; i++) {
            if (delayTimeUIString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getCaptureDelay().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, delayTimeUIString, curIdx, listener, true);
    }

    private void showVideoSizeOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.stream_set_res_vid);
        final String[] videoSizeUIString = baseProrertys.getVideoSize().getValueArrayString();
        final List<String> videoSizeValueString;
        videoSizeValueString = baseProrertys.getVideoSize().getValueList();
        if (videoSizeUIString == null) {
            AppLog.e(TAG, "videoSizeUIString == null");
            return;
        }
        int length = videoSizeUIString.length;

        int curIdx = 0;
        String curVideoSize = baseProrertys.getVideoSize().getCurrentUiStringInSetting();
        for (int i = 0; i < length; i++) {
            if (videoSizeUIString[i].equals(curVideoSize)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                final String value = videoSizeValueString.get(arg1);
                baseProrertys.getVideoSize().setValue(value);
                arg0.dismiss();
                onSettingCompleteListener.settingVideoSizeComplete();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, videoSizeUIString, curIdx, listener, false);
    }

    private void showFormatConfirmDialog(final Context context) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.setting_format_desc);
        builder.setNegativeButton(R.string.setting_no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton(R.string.setting_yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                final Handler handler = new Handler();
                MyProgressDialog.showProgressDialog(context, R.string.setting_format);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int messageId;
                        if (cameraAction.formatStorage()) {
                            messageId = R.string.text_operation_success;
                        } else {
                            messageId = R.string.text_operation_failed;
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                MyProgressDialog.closeProgressDialog();
                                MyToast.show(context, messageId);
                            }
                        });
                    }
                }).start();

            }
        });
        builder.create().show();
    }

    private void showDateStampOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.setting_datestamp);
        final String[] dateStampUIString = baseProrertys.getDateStamp().getValueList();
        if (dateStampUIString == null) {
            AppLog.e(TAG, "dateStampUIString == null");
            return;
        }
        int length = dateStampUIString.length;

        int curIdx = 0;
        String curValue = baseProrertys.getDateStamp().getCurrentUiStringInSetting();
        for (int i = 0; i < length; i++) {
            if (dateStampUIString[i].equals(curValue)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getDateStamp().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, dateStampUIString, curIdx, listener, true);
    }

    private void showElectricityFrequencyOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.setting_power_supply);

        final String[] eleFreUIString = baseProrertys.getElectricityFrequency().getValueList();
        if (eleFreUIString == null) {
            AppLog.e(TAG, "eleFreUIString == null");
            return;
        }
        int length = eleFreUIString.length;

        int curIdx = 0;
        String curValue = baseProrertys.getElectricityFrequency().getCurrentUiStringInSetting();
        for (int i = 0; i < length; i++) {
            if (eleFreUIString[i].equals(curValue)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getElectricityFrequency().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, eleFreUIString, curIdx, listener, true);
    }


    private void showWhiteBalanceOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.title_awb);
        final String[] whiteBalanceUIString = baseProrertys.getWhiteBalance().getValueList();
        if (whiteBalanceUIString == null) {
            AppLog.e(TAG, "whiteBalanceUIString == null");
            return;
        }
        int length = whiteBalanceUIString.length;

        String curValue = baseProrertys.getWhiteBalance().getCurrentUiStringInSetting();
        int curIdx = 0;
        for (int i = 0; i < length; i++) {
            if (whiteBalanceUIString[i].equals(curValue)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getWhiteBalance().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, whiteBalanceUIString, curIdx, listener, true);
    }

    private void showBurstOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.title_burst);

        final String[] burstUIString = baseProrertys.getBurst().getValueList();
        if (burstUIString == null) {
            AppLog.e(TAG, "burstUIString == null");
            return;
        }
        int length = burstUIString.length;
        String curValue = baseProrertys.getBurst().getCurrentUiStringInSetting();
        int curIdx = 0;
        for (int i = 0; i < length; i++) {
            if (burstUIString[i].equals(curValue)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getBurst().setValueByPosition(arg1);
                arg0.dismiss();
                Log.d("1111", "refresh optionListAdapter!");
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, burstUIString, curIdx, listener, true);
    }

    private void showOptionDialog(CharSequence title, CharSequence[] items, int checkedItem,
                                  DialogInterface.OnClickListener listener,
                                  boolean cancelable) {
        AlertDialog.Builder optionDialog = new AlertDialog.Builder(GlobalInfo.getInstance().getCurrentApp());

        optionDialog.setTitle(title).setSingleChoiceItems(items, checkedItem, listener).create();
        optionDialog.show();
        optionDialog.setCancelable(cancelable);
    }

    private void sdCardIsNotReadyAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.dialog_no_sd);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    private class SettingHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case SDKEvent.EVENT_FW_UPDATE_COMPLETED:
                    AppLog.d(TAG, "receive EVENT_FW_UPDATE_COMPLETED");
                    MyProgressDialog.closeProgressDialog();
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                    builder2.setMessage(R.string.setting_updatefw_closeAppInfo);
                    builder2.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("1111", "update FW completed!");
                        }
                    });
                    alertDialog = builder2.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    break;
                case SDKEvent.EVENT_FW_UPDATE_POWEROFF:
                    AppLog.d(TAG, "receive EVENT_FW_UPDATE_POWEROFF");
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_COMPLETED);
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_POWEROFF);
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHECK);
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHKSUMERR);
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_NG);
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(context);
                    builder3.setMessage(R.string.setting_updatefw_closeAppInfo);
                    builder3.setNegativeButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("1111", "App quit");
                            ExitApp.getInstance().exit();
                        }
                    });
                    alertDialog = builder3.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    break;
//                case AppMessage.AP_MODE_TO_STA_MODE_SUSSED:
//                    MyProgressDialog.closeProgressDialog();
//                    AppDialog.showDialogQuit(context,R.string.message_apmode_to_sta_mode_hint);
//                    AppInfo.isNeedReconnect = false;
//                    connectWifi()
//                    break;

                case SDKEvent.EVENT_FW_UPDATE_CHECK:
                    AppLog.d(TAG, "receive EVENT_FW_UPDATE_CHECK");
                    break;
                case SDKEvent.EVENT_FW_UPDATE_CHKSUMERR:
                    AppLog.d(TAG, "receive EVENT_FW_UPDATE_CHKSUMERR");
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_COMPLETED);
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_POWEROFF);
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHECK);
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHKSUMERR);
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_NG);
                    AlertDialog.Builder builder5 = new AlertDialog.Builder(context);
                    builder5.setMessage(R.string.setting_updatefw_chec_sum_failed);
                    builder5.setNegativeButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("1111", "App FW updatefw chech sume failed");
                            dialog.dismiss();
                        }
                    });
                    alertDialog = builder5.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    break;
                case SDKEvent.EVENT_FW_UPDATE_NG:
                    AppLog.d(TAG, "receive EVENT_FW_UPDATE_NG");
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_COMPLETED);
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_POWEROFF);
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHECK);
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_CHKSUMERR);
                    sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FW_UPDATE_NG);
                    AlertDialog.Builder builder6 = new AlertDialog.Builder(context);
                    builder6.setMessage(R.string.setting_updatefw_failed);
                    builder6.setNegativeButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("1111", "App FW updatefw failed");
                            dialog.dismiss();
                            // ExitApp.getInstance().exit();
                            // do something by yourself
                        }
                    });
                    alertDialog = builder6.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    break;

                case AppMessage.AP_MODE_TO_STA_MODE_FAILURE:
                    MyProgressDialog.closeProgressDialog();
                    MyToast.show(context, R.string.dialog_failed);
                    break;
                case WifiAPUtil.MESSAGE_AP_STATE_ENABLED:
                    MyProgressDialog.closeProgressDialog();
                    String ssid = WifiAPUtil.getInstance(context).getValidApSsid();
                    String pw = WifiAPUtil.getInstance(context).getValidPassword();
                    String hint = "wifi hotspot is open:" + "\n"
                            + "SSID = " + ssid + "\n"
                            + "Password = " + pw + "\n"
                            + "App will exit, please wait for the camera to connect to the wifi hotspot and click search to enter preview.";
                    AppDialog.showDialogQuit(context, hint);
                    AppInfo.isNeedReconnect = false;
                    WifiAPUtil.getInstance(context.getApplicationContext()).unregitsterHandler();

                    break;
                case WifiAPUtil.MESSAGE_AP_STATE_FAILED:
//                    MyToast.show(context,"wifi hotspot closed");
                    break;


            }
        }
    }

    public void showSetDownloadSizeLimitDialog(final Context context) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        View contentView = View.inflate(context, R.layout.content_download_size_dialog, null);
        final EditText resetTxv = (EditText) contentView.findViewById(R.id.download_size);
        String value = AppInfo.autoDownloadSizeLimit + "";
        resetTxv.setHint(value);
        builder.setTitle(R.string.setting_auto_download_size_limit);
        builder.setView(contentView);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.action_save)
                // 为按钮设置监听器
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (resetTxv.getText().toString().equals("")) {
                        } else {
                            float sizeLimit = Float.parseFloat(resetTxv.getText().toString());
                            AppInfo.autoDownloadSizeLimit = sizeLimit;
                            onSettingCompleteListener.onOptionSettingComplete();
                        }
                    }
                });
        // 为对话框设置一个“取消”按钮
        builder.setNegativeButton(context.getResources().getString(R.string.gallery_cancel)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //取消登录，不做任何事情。
                    }
                });
        //创建、并显示对话框
        builder.create().show();
    }

    public void connectWifi(String ssid, String password) {
        WifiCheck wifiCheck = new WifiCheck(activity);
        wifiCheck.openWifi();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                // 为了避免程序一直while循环，让它睡个200毫秒检测……
                Thread.sleep(200);
            } catch (InterruptedException ie) {
            }
        }
        wifiCheck.connectWifi(ssid, password, WifiCheck.WIFICIPHER_WAP);
        //JIRA ICOM-3786 begin modify by b.jiang 20170308
//        connectWifiTimer = new Timer();
//        connectWifiTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
////                        wifiCheck.connectWifi(ssid, password, "WPA");
//                wifiCheck.connectWifi(ssid, password, WifiCheck.WIFICIPHER_WAP);
//            }
//        }, 1000, CONNECT_PERIOD);
//
//        connectTimeoutTimer = new Timer();
//        connectTimeoutTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (connectWifiTimer != null) {
//                    connectWifiTimer.cancel();
//                    handler.obtainMessage(CONNECT_WIFI_FAILED).sendToTarget();
//                }
//            }
//        }, CONNECT_WIFI_TIMEOUT);//一分钟后执行
//        wifiListener = new WifiListener(getActivity().getApplicationContext(), handler);
//        wifiListener.registerReceiver();
    }

    public void showExposureCompensationDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.setting_title_exposure_compensation);
        final String[] exposureCompensationUIString = baseProrertys.getExposureCompensation().getValueList();
        if (exposureCompensationUIString == null) {
            AppLog.e(TAG, "exposureCompensationUIString == null");
            return;
        }
        int length = exposureCompensationUIString.length;
        int curIdx = 0;
        String temp = baseProrertys.getExposureCompensation().getCurrentUiStringInPreview();
        for (int i = 0; i < length; i++) {
            if (exposureCompensationUIString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getExposureCompensation().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, exposureCompensationUIString, curIdx, listener, true);
    }

    public void showVideoFileLengthDialog(final Context context) {
        // TODO Auto-generated method stub
        CharSequence title = context.getResources().getString(R.string.setting_title_video_file_length);
        final String[] videoFileLengthUIString = baseProrertys.getVideoFileLength().getValueList();
        if (videoFileLengthUIString == null) {
            AppLog.e(TAG, "videoFileLengthUIString == null");
            return;
        }
        int length = videoFileLengthUIString.length;
        int curIdx = 0;
        String temp = baseProrertys.getVideoFileLength().getCurrentUiStringInPreview();
        for (int i = 0; i < length; i++) {
            if (videoFileLengthUIString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                baseProrertys.getVideoFileLength().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, videoFileLengthUIString, curIdx, listener, true);
    }
}
