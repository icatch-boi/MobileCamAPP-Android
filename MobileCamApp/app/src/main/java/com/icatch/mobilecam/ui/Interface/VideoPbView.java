package com.icatch.mobilecam.ui.Interface;

/**
 * Created by yh.zhang on 2016/9/14.
 */
public interface VideoPbView {
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

    void setProgress(float progress);

    int getSurfaceViewWidth();

    int getSurfaceViewHeight();

    void setPanoramaTypeImageResource(int resId);

    void setPanoramaTypeBtnVisibility(int visibility);

    void setMoreSettingLayoutVisibility(int visibility);

    void setEisSwitchChecked(boolean checked);

    void setSeekbarEnabled(boolean b);
}
