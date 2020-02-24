package com.icatch.mobilecam.ui.Interface;

import android.graphics.Bitmap;

import com.icatch.mobilecam.ui.adapter.SettingListAdapter;

/**
 * Created by zhang yanhu C001012 on 2015/12/4 15:09.
 */
public interface PreviewView {

    void setWbStatusVisibility(int visibility);

    void setBurstStatusVisibility(int visibility);

    void setWifiStatusVisibility(int visibility);

    void setWifiIcon(int drawableId);

    void setBatteryStatusVisibility(int visibility);

    void setBatteryIcon(int drawableId);

    void settimeLapseModeVisibility(int visibility);

    void settimeLapseModeIcon(int drawableId);

    void setSlowMotionVisibility(int visibility);

    void setCarModeVisibility(int visibility);

    void setRecordingTimeVisibility(int visibility);

    void setAutoDownloadVisibility(int visibility);

    void setCaptureBtnBackgroundResource(int id);

    void setRecordingTime(String laspeTime);

    void setDelayCaptureLayoutVisibility(int visibility);

    void setDelayCaptureTextTime(String delayCaptureTime);

    void setImageSizeLayoutVisibility(int visibility);

    void setRemainCaptureCount(String remainCaptureCount);

    void setVideoSizeLayoutVisibility(int visibility);

    void setRemainRecordingTimeText(String remainRecordingTime);

    void setBurstStatusIcon(int drawableId);

    void setWbStatusIcon(int drawableId);


    void setUpsideVisibility(int visibility);

    void setCaptureBtnEnability(boolean enablity);

    void setVideoSizeInfo(String sizeInfo);

    void setImageSizeInfo(String sizeInfo);

    void showZoomView();

    void setMaxZoomRate(float maxZoomRate);

    float getZoomViewProgress();

    float getZoomViewMaxZoomRate();

    void updateZoomViewProgress(float currentZoomRatio);

    void setSettingMenuListAdapter(SettingListAdapter settingListAdapter);

    int getSetupMainMenuVisibility();

    void setSetupMainMenuVisibility(int visibility);

    void setAutoDownloadBitmap(Bitmap bitmap);

    void setActionBarTitle(int resId);

    void setSettingBtnVisible(boolean isVisible);

    void setBackBtnVisibility(boolean isVisible);

    void setSupportPreviewTxvVisibility(int visibility);

    void setPvModeBtnBackgroundResource(int drawableId);

    void showPopupWindow(int curMode);

    void setTimepLapseRadioBtnVisibility(int visibility);
    void setCaptureRadioBtnVisibility(int visibility);
    void setVideoRadioBtnVisibility(int visibility);

    void setTimepLapseRadioChecked(boolean checked);
    void setCaptureRadioBtnChecked(boolean checked);
    void setVideoRadioBtnChecked(boolean checked);
    void dismissPopupWindow();

    void setMinZoomRate(float minZoomRate);
	void setFacebookBtnTxv(String value);
    void setYouTubeBtnTxv(String value);
	void setFacebookBtnTxv(int resId);
    void setYouTubeBtnTxv(int resId);

    void setYouTubeLiveLayoutVisibility(int visibility);

    int getSurfaceViewWidth();

    int getSurfaceViewHeight();

    void setPanoramaTypeBtnSrc(int srcId);
    void setPanoramaTypeBtnVisibility(int visibility);
}
