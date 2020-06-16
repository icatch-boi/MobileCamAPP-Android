package com.icatch.mobilecam.data.SystemInfo;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by zhang yanhu C001012 on 2016/12/13 14:42.
 */

public class HotSpot
{
    public static final int WIFI_AP_STATE_DISABLING = 10;
    public static final int WIFI_AP_STATE_DISABLED = 11;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_FAILED = 14;
    private static final String TAG = HotSpot.class.getSimpleName();

//    获取WIFI热点的状态：

    public static int getWifiApState(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            int i = (Integer) method.invoke(wifiManager);
            Log.i(TAG,"wifi state:  " + i);
            return i;
        } catch (Exception e) {
            Log.e(TAG,"Cannot get WiFi AP state" + e);
            return WIFI_AP_STATE_FAILED;
        }
    }

//    判断Wifi热点是否可用：
    public static boolean isApEnabled(Context mContext) {
        int state = getWifiApState(mContext);
        return WIFI_AP_STATE_ENABLED == state;
    }

//    获取链接到当前热点的设备IP：

    private static ArrayList<String> getConnectedHotIP() {
        ArrayList<String> connectedIP = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectedIP;
    }

    //获取第一个连入的设备ip
    public static String getFirstConnectedHotIP() {
        ArrayList<String> connectedIP = getConnectedHotIP();
        if(connectedIP == null || connectedIP.isEmpty()){
            return null;
        }
        return connectedIP.get( 1 );
    }

    //输出链接到当前设备的IP地址
    public static void printHotIp() {

        ArrayList<String> connectedIP = getConnectedHotIP();
        StringBuilder resultList = new StringBuilder();
        for (String ip : connectedIP) {
            resultList.append(ip);
            resultList.append("\n");
        }
        System.out.print(resultList);
        Log.d(TAG,"---->>heww resultList="+resultList);
    }

    //获取WLAN　密码
    public static String getWifiApSharedKey(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService( Context.WIFI_SERVICE );
        Method method = null;
        String SharedKey = null;
        try {
            method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            WifiConfiguration apConfig = (WifiConfiguration) method.invoke(mWifiManager);
            SharedKey = apConfig.preSharedKey;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG,"SharedKey="+SharedKey);
        return SharedKey;
    }

    //获取WLAN ＳＳＩＤ
    public static String getWifiApSSID(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService( Context.WIFI_SERVICE );
        Method method = null;
        String SSID = null;
        try {
            method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            WifiConfiguration apConfig = (WifiConfiguration) method.invoke(mWifiManager);
            SSID = apConfig.SSID;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "getWifiApSSID -> " + SSID);
        return SSID;
    }

    //设置WLAN状态
    public static boolean setWifiApEnabled(Context context,boolean enabled) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService( Context.WIFI_SERVICE );
        Method method = null, configMethod = null;
        boolean result = false;
        if (mWifiManager == null) {
            Log.i(TAG, "mWifiManager is null  -> " + result);
            return result;
        }
        try {
            configMethod = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            WifiConfiguration apConfig = (WifiConfiguration) configMethod.invoke(mWifiManager);
            result = (boolean) method.invoke(mWifiManager, new Object[]{apConfig, enabled});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "setWifiApEnabled -> " + result);
        return result;
    }

}
