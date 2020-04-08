package com.icatch.mobilecam.utils;

import com.icatchtek.control.customer.type.ICatchCamListFileFilter;
import com.icatchtek.reliant.customer.type.ICatchFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author b.jiang
 * @date 2020/1/21
 * @description
 */
public class FileFilter {

    private long startTime;
    private long endTime;
    private int sensorType = ICatchCamListFileFilter.ICH_TAKEN_BY_ALL_SENSORS;
    private int timeFilterType = TIME_TYPE_ALL_TIME;
    //    private static String timeFormat = "YYYYMMddTHHmmss";
    private static String timeFormat = "yyyyMMddHHmmss";
    private static final String formatType = "yyyy-MM-dd HH:mm:ss";

    public static final int TIME_TYPE_TODAY = 0;
    public static final int TIME_TYPE_LAST_THREE_DAY = 1;
    public static final int TIME_TYPE_ALMOST_A_WEEK = 2;
    public static final int TIME_TYPE_ALMOST_A_MONTH = 3;
    public static final int TIME_TYPE_LAST_HALF_YEAR = 4;
    public static final int TIME_TYPE_CUSTOMIZE = 5;
    public static final int TIME_TYPE_ALL_TIME = 6;



    public int getTimeFilterType() {
        return timeFilterType;
    }

    public void setTimeFilterType(int timeFilterType) {
        this.timeFilterType = timeFilterType;
    }

    /**
     * 是否在指定的时间区域
     *
     * @param itemInfo
     * @return
     */
    public boolean isMatch(ICatchFile itemInfo) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        try {
            String dateString = itemInfo.getFileDate().replace("T", "");
            Date date = format.parse(dateString);
            long fileTime = date.getTime();
            if (fileTime <= getEndTime() && fileTime >= getStartTime()) {
                return true;
            }
        } catch (ParseException e) {
            return false;
        }
        return false;
    }

    /**
     * 是否小于最小时间
     *
     * @param itemInfo
     * @return
     */
    public boolean isLess(ICatchFile itemInfo) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        try {
            String dateString = itemInfo.getFileDate().replace("T", "");
            Date date = format.parse(dateString);
            long fileTime = date.getTime();
            if (fileTime < getStartTime()) {
                return true;
            }
        } catch (ParseException e) {
            return false;
        }
        return false;
    }


    private long getEndTime(){
        if(timeFilterType == TIME_TYPE_CUSTOMIZE){
            return endTime;
        }else {
            Calendar curCalendar = Calendar.getInstance();
            curCalendar.set(Calendar.HOUR_OF_DAY, 23);
            curCalendar.set(Calendar.MINUTE, 59);
            curCalendar.set(Calendar.SECOND, 59);
            curCalendar.set(Calendar.MILLISECOND, 59);
            return curCalendar.getTime().getTime();
        }
    }

    private long getStartTime(){
        if(timeFilterType == TIME_TYPE_CUSTOMIZE){
            return startTime;
        }

        Calendar curCalendar = Calendar.getInstance();
        long startTime = 0;
        curCalendar.set(Calendar.HOUR_OF_DAY, 0);
        curCalendar.set(Calendar.MINUTE, 0);
        curCalendar.set(Calendar.SECOND, 0);
        curCalendar.set(Calendar.MILLISECOND, 0);
        switch (timeFilterType) {
            //今天
            case TIME_TYPE_TODAY:
                startTime = curCalendar.getTime().getTime();
                break;
            //近三天
            case TIME_TYPE_LAST_THREE_DAY:
                curCalendar.set(Calendar.DATE, curCalendar.get(Calendar.DATE) - 2);
                startTime = curCalendar.getTime().getTime();
                break;
            //近一周
            case TIME_TYPE_ALMOST_A_WEEK:
                curCalendar.set(Calendar.DATE, curCalendar.get(Calendar.DATE) - 6);
                startTime = curCalendar.getTime().getTime();
                break;
            //近一个月
            case TIME_TYPE_ALMOST_A_MONTH:
                curCalendar.set(Calendar.DATE, curCalendar.get(Calendar.DATE) - 30);
                startTime = curCalendar.getTime().getTime();
                break;
            //近半年
            case TIME_TYPE_LAST_HALF_YEAR:
                curCalendar.set(Calendar.DATE, curCalendar.get(Calendar.DATE) - 30 * 6);
                startTime = curCalendar.getTime().getTime();
                break;

            default:

        }
        return startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    public int getSensorType() {
        return sensorType;
    }

    public String getEndTimeString() {
        return TimeTools.getDateToString(endTime, formatType);
    }

    public String getStringTimeString() {
        return TimeTools.getDateToString(startTime, formatType);
    }
}
