package com.icatch.mobilecam.ui.Interface;


public interface LocalVideoPbView {
    void setTopBarVisibility(int visibility);

    void setBottomBarVisibility(int visibility);

    void setTimeLapsedValue(String value);

    void setTimeDurationValue(String value);

    void setSeekBarProgress(int value);

    void setSeekBarMaxValue(int value);

    int getSeekBarProgress();

    void setSeekBarSecondProgress(int value);

    void setPlayBtnSrc(int resid);

    void showLoadingCircle(boolean isShow);

    void setLoadPercent(int value);

    void setVideoNameTxv(String curLocalVideoPath);

    void setZoomMinValue(float minValue);

    void setZoomMaxValue(float maxValue);

    void updateZoomRateTV(float zoomRate);

    void setProgress(float progress);

    void showZoomView(int visibility);

    int getSurfaceViewWidth();

    int getSurfaceViewHeight();

    void setPanoramaTypeImageResource(int resId);

    void setPanoramaTypeBtnVisibility(int visibility);

    void setMoreSettingLayoutVisibility(int visibility);

    void setEisSwitchChecked(boolean checked);

    void setCodecInfoTxv(String info);
}
