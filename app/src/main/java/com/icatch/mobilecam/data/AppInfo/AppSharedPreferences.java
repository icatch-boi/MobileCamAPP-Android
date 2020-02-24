package com.icatch.mobilecam.data.AppInfo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhang yanhu C001012 on 2016/11/24 15:43.
 */
public class AppSharedPreferences {
    private final static String FILE_NAME = "storeInfo";
    public final static String OBJECT_NAME_LIVE_URL = "liveUrl";
    public final static String OBJECT_NAME_INPUT_IP = "inputIp";

    public static void writeDataByName(Context context, String name, String value) {
        //实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = context.getSharedPreferences( FILE_NAME, Activity.MODE_PRIVATE );
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        //用putString的方法保存数据
        editor.putString( name, value );
        //提交当前数据
        editor.commit();
    }

    public static String readDataByName(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( FILE_NAME, Activity.MODE_PRIVATE );
        // 使用getString方法获得value，注意第2个参数是value的默认值
        String value = sharedPreferences.getString( name, "192.168.1.1" );
        //使用toast信息提示框显示信息
        return value;
    }
}
