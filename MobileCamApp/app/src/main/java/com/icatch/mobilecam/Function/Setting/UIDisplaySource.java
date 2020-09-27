package com.icatch.mobilecam.Function.Setting;

import android.content.Context;

import com.icatch.mobilecam.Application.PanoramaApp;
import com.icatch.mobilecam.Function.BaseProrertys;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.type.TimeLapseMode;
import com.icatch.mobilecam.data.entity.SettingMenu;
import com.icatch.mobilecam.MyCamera.MyCamera;
import com.icatch.mobilecam.data.PropertyId.PropertyId;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.CameraFixedInfo;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatch.mobilecam.SdkApi.CameraState;
import com.icatch.mobilecam.utils.StorageUtil;
import com.icatchtek.control.customer.type.ICatchCamMode;
import com.icatchtek.control.customer.type.ICatchCamProperty;

import java.util.LinkedList;

public class UIDisplaySource {
    public static final int CAPTURE_SETTING_MENU = 1;
    public static final int VIDEO_SETTING_MENU = 2;
    public static final int TIMELAPSE_SETTING_MENU = 3;
    private static UIDisplaySource uiDisplayResource;
    private CameraState cameraState;
    private CameraProperties cameraProperties;
    private BaseProrertys baseProrertys;
    private CameraFixedInfo cameraFixedInfo;
    private MyCamera curCamera;
    private LinkedList<SettingMenu> settingMenuList;

    public static UIDisplaySource getinstance() {
        if (uiDisplayResource == null) {
            uiDisplayResource = new UIDisplaySource();
        }
        return uiDisplayResource;
    }

    public LinkedList<SettingMenu> getList(int type, MyCamera currCamera) {
        this.curCamera = currCamera;
        this.cameraState = currCamera.getCameraState();
        this.cameraProperties = currCamera.getCameraProperties();
        this.baseProrertys = currCamera.getBaseProrertys();
        this.cameraFixedInfo = currCamera.getCameraFixedInfo();
        switch (type) {
            case CAPTURE_SETTING_MENU:
                return getForCaptureMode();
            case VIDEO_SETTING_MENU:
                return getForVideoMode();
            case TIMELAPSE_SETTING_MENU:
                return getForTimelapseMode();
            default:
                return null;
        }
    }

    public LinkedList<SettingMenu> getForCaptureMode() {
        if (settingMenuList == null) {
            settingMenuList = new LinkedList<SettingMenu>();
        } else {
            settingMenuList.clear();
        }
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_IMAGE_SIZE) == true) {
            settingMenuList.add(new SettingMenu(R.string.setting_image_size, baseProrertys.getImageSize().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_CAPTURE_DELAY) == true) {
            settingMenuList.add(new SettingMenu(R.string.setting_capture_delay, baseProrertys.getCaptureDelay().getCurrentUiStringInPreview()));
        }
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_BURST_NUMBER) == true) {
            settingMenuList.add(new SettingMenu(R.string.title_burst, baseProrertys.getBurst().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_WHITE_BALANCE)) {
            settingMenuList.add(new SettingMenu(R.string.title_awb, baseProrertys.getWhiteBalance().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_LIGHT_FREQUENCY)) {
            settingMenuList.add(new SettingMenu(R.string.setting_power_supply, baseProrertys.getElectricityFrequency().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_DATE_STAMP) == true) {
            settingMenuList.add(new SettingMenu(R.string.setting_datestamp, baseProrertys.getDateStamp().getCurrentUiStringInSetting()));
        }
        if (cameraState.isSupportImageAutoDownload()) {
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download, ""));
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download_size_limit, ""));
        }
//        settingMenuList.add(new SettingMenu(R.string.setting_audio_switch, ""));
//        settingMenuList.add( new SettingMenu( R.string.setting_live_address, AppInfo.liveAddress ) );
        settingMenuList.add(new SettingMenu(R.string.setting_format, ""));
        settingMenuList.add(new SettingMenu(R.string.setting_storage_location, StorageUtil.getCurStorageLocation(PanoramaApp.getContext())));
        if (cameraProperties.hasFuction(PropertyId.STA_MODE_SSID)){
            settingMenuList.add(new SettingMenu(R.string.setting_enable_wifi_hotspot, ""));
        }
        if (cameraProperties.hasFuction(PropertyId.UP_SIDE)) {
            settingMenuList.add(new SettingMenu(R.string.upside, baseProrertys.getUpside().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFuction(PropertyId.CAMERA_ESSID)) {//camera password and wifi
            settingMenuList.add(new SettingMenu(R.string.camera_wifi_configuration, ""));
        }
        if (cameraProperties.hasFuction(PropertyId.POWER_ON_AUTO_RECORD)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_power_on_auto_record, ""));
        }
        if (cameraProperties.hasFuction(PropertyId.AUTO_POWER_OFF)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_auto_power_off, baseProrertys.getAutoPowerOff().getCurrentUiStringInPreview()));
        }
        if (cameraProperties.hasFuction(PropertyId.EXPOSURE_COMPENSATION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_exposure_compensation, baseProrertys.getExposureCompensation()
                    .getCurrentUiStringInPreview()));
        }



        settingMenuList.add(new SettingMenu(R.string.setting_app_version, AppInfo.APP_VERSION));
        settingMenuList.add(new SettingMenu(R.string.setting_product_name, cameraFixedInfo.getCameraName()));
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_FW_VERSION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_firmware_version, cameraFixedInfo.getCameraVersion()));
        }
        return settingMenuList;
    }

    private LinkedList<SettingMenu> getForVideoMode() {
        if (settingMenuList == null) {
            settingMenuList = new LinkedList<SettingMenu>();
        } else {
            settingMenuList.clear();
        }
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_VIDEO_SIZE) == true) {
            settingMenuList.add(new SettingMenu(R.string.setting_video_size, baseProrertys.getVideoSize().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_WHITE_BALANCE)) {
            settingMenuList.add(new SettingMenu(R.string.title_awb, baseProrertys.getWhiteBalance().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_LIGHT_FREQUENCY)) {
            settingMenuList.add(new SettingMenu(R.string.setting_power_supply, baseProrertys.getElectricityFrequency().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_DATE_STAMP) == true) {
            settingMenuList.add(new SettingMenu(R.string.setting_datestamp, baseProrertys.getDateStamp().getCurrentUiStringInSetting()));
        }
        if (cameraState.isSupportImageAutoDownload()) {
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download, ""));
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download_size_limit, ""));
        }
//        settingMenuList.add(new SettingMenu(R.string.setting_audio_switch, ""));
//        settingMenuList.add( new SettingMenu( R.string.setting_live_address, AppInfo.liveAddress ) );
        settingMenuList.add(new SettingMenu(R.string.setting_format, ""));
        settingMenuList.add(new SettingMenu(R.string.setting_storage_location, StorageUtil.getCurStorageLocation(PanoramaApp.getContext())));
        if (cameraProperties.hasFuction(PropertyId.STA_MODE_SSID)){
            settingMenuList.add(new SettingMenu(R.string.setting_enable_wifi_hotspot, ""));
        }
        if (cameraProperties.hasFuction(PropertyId.SLOW_MOTION)) {
            settingMenuList.add(new SettingMenu(R.string.slowmotion, baseProrertys.getSlowMotion().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFuction(PropertyId.UP_SIDE)) {
            settingMenuList.add(new SettingMenu(R.string.upside, baseProrertys.getUpside().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFuction(PropertyId.CAMERA_ESSID)) {//camera password and wifi
            settingMenuList.add(new SettingMenu(R.string.camera_wifi_configuration, ""));
        }

        if (cameraProperties.hasFuction(PropertyId.SCREEN_SAVER)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_screen_saver, baseProrertys.getScreenSaver().getCurrentUiStringInPreview()));
        }
        if (cameraProperties.hasFuction(PropertyId.POWER_ON_AUTO_RECORD)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_power_on_auto_record, ""));
        }
        if (cameraProperties.hasFuction(PropertyId.AUTO_POWER_OFF)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_auto_power_off, baseProrertys.getAutoPowerOff().getCurrentUiStringInPreview()));
        }

        if (cameraProperties.hasFuction(PropertyId.EXPOSURE_COMPENSATION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_exposure_compensation, baseProrertys.getExposureCompensation()
                    .getCurrentUiStringInPreview()));
        }
        if (cameraProperties.hasFuction(PropertyId.IMAGE_STABILIZATION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_image_stabilization, ""));
        }
        settingMenuList.add(new SettingMenu(R.string.setting_update_fw,""));
        if (cameraProperties.hasFuction(PropertyId.VIDEO_FILE_LENGTH)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_video_file_length, baseProrertys.getVideoFileLength().getCurrentUiStringInPreview()));
        }
        if (cameraProperties.hasFuction(PropertyId.FAST_MOTION_MOVIE)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_fast_motion_movie, baseProrertys.getFastMotionMovie().getCurrentUiStringInPreview()));
        }
        if (cameraProperties.hasFuction(PropertyId.WIND_NOISE_REDUCTION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_wind_noise_reduction, ""));
        }

        settingMenuList.add(new SettingMenu(R.string.setting_app_version, AppInfo.APP_VERSION));
        settingMenuList.add(new SettingMenu(R.string.setting_product_name, cameraFixedInfo.getCameraName()));
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_FW_VERSION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_firmware_version, cameraFixedInfo.getCameraVersion()));
        }

        return settingMenuList;
    }

    public LinkedList<SettingMenu> getForTimelapseMode() {
        if (settingMenuList == null) {
            settingMenuList = new LinkedList<SettingMenu>();
        } else {
            settingMenuList.clear();
        }
        if (curCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_STILL) {
            if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_IMAGE_SIZE) == true) {
                settingMenuList.add(new SettingMenu(R.string.setting_image_size, baseProrertys.getImageSize().getCurrentUiStringInSetting()));
            }
        } else if (curCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_VIDEO) {
            if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_VIDEO_SIZE) == true) {
                settingMenuList.add(new SettingMenu(R.string.setting_video_size, baseProrertys.getVideoSize().getCurrentUiStringInSetting()));
            }
        }
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_WHITE_BALANCE)) {
            settingMenuList.add(new SettingMenu(R.string.title_awb, baseProrertys.getWhiteBalance().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_LIGHT_FREQUENCY)) {
            settingMenuList.add(new SettingMenu(R.string.setting_power_supply, baseProrertys.getElectricityFrequency().getCurrentUiStringInSetting()));
        }
        if (cameraState.isSupportImageAutoDownload()) {
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download, ""));
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download_size_limit, ""));
        }
//        settingMenuList.add(new SettingMenu(R.string.setting_audio_switch, ""));
//        settingMenuList.add( new SettingMenu( R.string.setting_live_address, AppInfo.liveAddress ) );
        settingMenuList.add(new SettingMenu(R.string.setting_format, ""));
        settingMenuList.add(new SettingMenu(R.string.setting_storage_location, StorageUtil.getCurStorageLocation(PanoramaApp.getContext())));
        if (cameraProperties.hasFuction(PropertyId.STA_MODE_SSID)){
            settingMenuList.add(new SettingMenu(R.string.setting_enable_wifi_hotspot, ""));
        }
        if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_TIMELAPSE)) {
            String curTimeLapseInterval;
            if (curCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_STILL) {
                curTimeLapseInterval = baseProrertys.getTimeLapseStillInterval().getCurrentValue();
            } else {
                curTimeLapseInterval = baseProrertys.getTimeLapseVideoInterval().getCurrentValue();
            }
            settingMenuList.add(new SettingMenu(R.string.title_timeLapse_mode, baseProrertys.getTimeLapseMode().getCurrentUiStringInSetting()));
            settingMenuList.add(new SettingMenu(R.string.setting_time_lapse_interval, curTimeLapseInterval));
            settingMenuList.add(new SettingMenu(R.string.setting_time_lapse_duration, baseProrertys.gettimeLapseDuration().getCurrentValue()));
        }

        if (cameraProperties.hasFuction(PropertyId.UP_SIDE)) {
            settingMenuList.add(new SettingMenu(R.string.upside, baseProrertys.getUpside().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFuction(PropertyId.CAMERA_ESSID)) {//camera password and wifi
            settingMenuList.add(new SettingMenu(R.string.camera_wifi_configuration, ""));

        }
        if (cameraProperties.hasFuction(PropertyId.POWER_ON_AUTO_RECORD)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_power_on_auto_record, ""));
        }
        if (cameraProperties.hasFuction(PropertyId.AUTO_POWER_OFF)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_auto_power_off, baseProrertys.getAutoPowerOff().getCurrentUiStringInPreview()));
        }
        if (cameraProperties.hasFuction(PropertyId.EXPOSURE_COMPENSATION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_exposure_compensation, baseProrertys.getExposureCompensation()
                    .getCurrentUiStringInPreview()));
        }
        settingMenuList.add(new SettingMenu(R.string.setting_app_version, AppInfo.APP_VERSION));
        settingMenuList.add(new SettingMenu(R.string.setting_product_name, cameraFixedInfo.getCameraName()));
        if (cameraProperties.hasFuction(ICatchCamProperty.ICH_CAM_CAP_FW_VERSION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_firmware_version, cameraFixedInfo.getCameraVersion()));
        }
        return settingMenuList;
    }


    public LinkedList<SettingMenu> getUSBList(Context context) {
        if (settingMenuList == null) {
            settingMenuList = new LinkedList<SettingMenu>();
        } else {
            settingMenuList.clear();
        }
//        settingMenuList.add(new SettingMenu(R.string.setting_audio_switch, "",R.string.setting_title_switch));
//        settingMenuList.add(new SettingMenu(R.string.setting_title_display_temperature, "", R.string.setting_type_switch));
//        settingMenuList.add(new SettingMenu(R.string.setting_audio_switch, ""));
//        settingMenuList.add(new SettingMenu(R.string.setting_image_size, GlobalInfo.getInstance().getCurImageSize(), R.string.setting_type_general));
//        settingMenuList.add(new SettingMenu(R.string.setting_title_storage_location, context.getResources().getString(R.string.setting_value_internal_storage), R.string.setting_type_other));
        settingMenuList.add(new SettingMenu(R.string.setting_app_version, AppInfo.APP_VERSION));
//            settingMenuList.add(new SettingMenu(R.string.setting_auto_download_size_limit, "",R.string.setting_type_switch));
        return settingMenuList;
    }
}
