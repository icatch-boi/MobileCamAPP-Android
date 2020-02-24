/**
 * Added by zhangyanhu C01012,2014-8-29
 */
package com.icatch.mobilecam.data.type;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.Mode.PreviewMode;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatchtek.control.customer.type.ICatchCamMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Added by zhangyanhu C01012,2014-8-29
 */
public class TimeLapseInterval {
    private final String tag = "TimeLapseInterval";
    public static final int TIME_LAPSE_INTERVAL_OFF = 0;
//	public static final int TIME_LAPSE_INTERVAL_2S = 2;
//	public static final int TIME_LAPSE_INTERVAL_5S = 5;
//	public static final int TIME_LAPSE_INTERVAL_10S = 10;
//	public static final int TIME_LAPSE_INTERVAL_20S = 20;
//	public static final int TIME_LAPSE_INTERVAL_30S = 30;
//	public static final int TIME_LAPSE_INTERVAL_1MIN = 60;
//	public static final int TIME_LAPSE_INTERVAL_5MIN = 300;
//	public static final int TIME_LAPSE_INTERVAL_10MIN = 600;
//	public static final int TIME_LAPSE_INTERVAL_30MIN = 1800;
//	public static final int TIME_LAPSE_INTERVAL_1HR = 3600;

    private String[] valueListString;
    private int[] valueListInt;
    private CameraProperties cameraProperties;

    public TimeLapseInterval(CameraProperties cameraProperties) {
        this.cameraProperties = cameraProperties;
        initTimeLapseInterval();
    }

    public String getCurrentValue() {
        return convertTimeLapseInterval(cameraProperties.getCurrentTimeLapseInterval());
    }

    public String[] getValueStringList() {
        return valueListString;
    }

    public int[] getValueStringInt() {
        return valueListInt;
    }

    public boolean setValueByPosition(int position) {
        boolean retValue;
        retValue = cameraProperties.setTimeLapseInterval(valueListInt[position]);
        return retValue;
    }

    public void initTimeLapseInterval() {
        AppLog.i(tag, "begin initTimeLapseInterval");
        if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_TIMELAPSE) == false) {
            return;
        }
        List<Integer> list = cameraProperties.getSupportedTimeLapseIntervals();
        int length = list.size();
        ArrayList<String> tempArrayList = new ArrayList<String>();
        valueListInt = new int[length];

        for (int ii = 0; ii < length; ii++) {
            tempArrayList.add(convertTimeLapseInterval(list.get(ii)));
            valueListInt[ii] = list.get(ii);
        }

        valueListString = new String[tempArrayList.size()];
        for (int ii = 0; ii < tempArrayList.size(); ii++) {
            valueListString[ii] = tempArrayList.get(ii);
        }
        AppLog.i(tag, "end initTimeLapseInterval timeLapseInterval =" + valueListString.length);
    }

    public Boolean needDisplayByMode(int previewMode) {
        if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_TIMELAPSE)) {
            if(previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                    previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                    previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW ||
                    previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE){
                return true;
            }
        }
        return false;
    }

    public static String convertTimeLapseInterval(int value) {
        if(value == 0){
            return "OFF";
        }
        String time = "";
        if(value == -2){
            return "0.5 Sec";
        }
        int h = value / 3600;
        int m = (value % 3600) / 60;
        int s = value % 60;
        if(h > 0){
            time = time + h+" HR ";
        }
        if(m > 0){
            time = time + m + " Min ";
        }
        if(s > 0){
            time = time + s + " Sec";
        }
        return time;
    }
}
