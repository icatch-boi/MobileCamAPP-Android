/**
 * Added by zhangyanhu C01012,2014-8-29
 */
package com.icatch.mobilecam.data.type;

import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.Mode.PreviewMode;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatchtek.control.customer.type.ICatchCamMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Added by zhangyanhu C01012,2014-8-29
 */
public class TimeLapseDuration {
    private final String tag = "TimeLapseDuration";
    public static final int TIME_LAPSE_DURATION_2MIN = 2;
    public static final int TIME_LAPSE_DURATION_5MIN = 5;
    public static final int TIME_LAPSE_DURATION_10MIN = 10;
    public static final int TIME_LAPSE_DURATION_15MIN = 15;
    public static final int TIME_LAPSE_DURATION_20MIN = 20;
    public static final int TIME_LAPSE_DURATION_30MIN = 30;
    public static final int TIME_LAPSE_DURATION_60MIN = 60;
    public static final int TIME_LAPSE_DURATION_UNLIMITED = 0xffff;

    private String[] valueListString;
    private int[] valueListInt;
    private CameraProperties cameraProperties;

    public TimeLapseDuration(CameraProperties cameraProperties) {
        this.cameraProperties = cameraProperties;
        initTimeLapseDuration();
    }

    public String getCurrentValue() {
        return convertTimeLapseDuration(cameraProperties.getCurrentTimeLapseDuration());
    }

    public String[] getValueStringList() {
        return valueListString;
    }

    public int[] getValueStringInt() {
        return valueListInt;
    }

    public boolean setValueByPosition(int position) {
        boolean retValue;
        retValue = cameraProperties.setTimeLapseDuration(valueListInt[position]);
        return retValue;
    }

    public void initTimeLapseDuration() {
        AppLog.i(tag, "begin initTimeLapseDuration");
        if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_TIMELAPSE) == false) {
            return;
        }
        List<Integer> list = cameraProperties.getSupportedTimeLapseDurations();
        int length = list.size();
        ArrayList<String> tempArrayList = new ArrayList<String>();
        valueListInt = new int[length];

        for (int ii = 0; ii < length; ii++) {
            tempArrayList.add(convertTimeLapseDuration(list.get(ii)));
            valueListInt[ii] = list.get(ii);
        }

        valueListString = new String[tempArrayList.size()];
        for (int ii = 0; ii < tempArrayList.size(); ii++) {
            valueListString[ii] = tempArrayList.get(ii);
        }
        AppLog.i(tag, "end initTimeLapseDuration timeLapseDuration =" + valueListString.length);
    }

    public Boolean needDisplayByMode(int previewMode) {
        if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_TIMELAPSE)) {
            if (previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                    previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                    previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW ||
                    previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                return true;
            }
        }
        return false;
    }

    public static String convertTimeLapseDuration(int value) {
        if (value == 0xffff) {
            return GlobalInfo.getInstance().getCurrentApp().getResources().getString(R.string.setting_time_lapse_duration_unlimit);
        }
        String time = "";
        int h = value / 60;
        int m = value % 60;
        if (h > 0) {
            time = time + h + "HR";
        }
        if (m > 0) {
            time = time + m + "Min";
        }
        return time;
    }
}
