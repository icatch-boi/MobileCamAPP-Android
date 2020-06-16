package com.icatch.mobilecam.utils;

import com.icatch.mobilecam.Log.AppLog;

import java.text.DecimalFormat;

public class ConvertTools {
    private static String TAG="ConvertTools";
    public static String secondsToMinuteOrHours(int remainTime) {
        String time = "";
        Integer h = remainTime / 3600;
        Integer m = (remainTime % 3600) / 60;
        Integer s = remainTime % 60;

        if (h > 0){
            if (h < 10) {
                time = "0" + h.toString();
            } else {
                time = h.toString();
            }
            time = time + ":";
        }

        if (m < 10) {
            time = time + "0" + m.toString();
        } else {
            time = time + m.toString();
        }
        time = time + ":";
        if (s < 10) {
            time = time + "0" + s.toString();
        } else {
            time = time + s.toString();
        }
        return time;
    }

    public static String secondsToHours(int remainTime) {
        String time = "";
        Integer h = remainTime / 3600;
        Integer m = (remainTime % 3600) / 60;
        Integer s = remainTime % 60;
        if (h < 10) {
            time = "0" + h.toString();
        } else {
            time = h.toString();
        }
        time = time + ":";
        if (m < 10) {
            time = time + "0" + m.toString();
        } else {
            time = time + m.toString();
        }
        time = time + ":";
        if (s < 10) {
            time = time + "0" + s.toString();
        } else {
            time = time + s.toString();
        }
        return time;
    }

    static int GB = 1024 * 1024 * 1024;//定义GB的计算常量
    static int MB = 1024 * 1024;//定义MB的计算常量
    static int KB = 1024;//定义KB的计算常量

    public static String ByteConversionGBMBKB(Integer KSize) {
        String fileSize;
        DecimalFormat df = new DecimalFormat("######0.0");
        if (KSize / GB >= 1)
            return df.format(KSize / (float) GB).toString() + "G";
        else if (KSize / MB >= 1)//如果当前Byte的值大于等于1MB
            return df.format(KSize / (float) MB).toString() + "M";
        else if (KSize / KB >= 1)//如果当前Byte的值大于等于1KB
            return df.format(KSize / (float) KB).toString() + "K";
        else
            return KSize.toString() + "B";
    }

    public static String resolutionConvert(String resolution) {
        AppLog.d(TAG, "start resolution = " + resolution);
        String ret = null;
        String[] temp;
        temp = resolution.split("\\?|&");
        temp[1] = temp[1].replace("W=", "");
        temp[2] = temp[2].replace("H=", "");
        temp[3] = temp[3].replace("BR=", "");
        ret = temp[0] + "?W=" + temp[1] + "&H=" + temp[2] + "&BR=" + temp[3];

        if (resolution.contains("FPS")) {
            if (temp[2].equals("720")) {
                ret = ret + "&FPS=15&";
            } else if (temp[2].equals("1080")) {
                ret = ret + "&FPS=10&";
            } else {
                ret = resolution;
            }
        } else {
            ret = resolution;
        }

        AppLog.d(TAG, "end ret = " + ret);
        return ret;
    }

    public static String getExposureCompensation(int value) {
        AppLog.d(TAG, "start getExposureCompensation value=" + value);
        String ret = "EV ";
        int temp = 0x80000000;
        int temp2 = 0x40000000;
        int temp3 = 0x00ffffff;

        //获取最高位的值 ，1表示负数，0表示正数
        if ((value & temp) == temp) {
            ret = ret + "-";
        }
        //获取第二位的值 ，1表示小数点左移一位负数，0表示不移位
        int value2 = value & temp3;
        if ((value & temp2) == temp2) {
            ret = ret + value2 / 10.0 + "";
        } else {
            ret = ret + value2 / 1.0 + "";
        }
        AppLog.d(TAG, "End getExposureCompensation ret=" + ret);
        return ret;
    }

}
