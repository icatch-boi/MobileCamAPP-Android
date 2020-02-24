package com.icatch.mobilecam.data.SystemInfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.icatch.mobilecam.Log.AppLog;

/**
 * Created by zhang yanhu C001012 on 2015/11/24 17:57.
 */
public class MWifiManager {
    private static String TAG = "MWifiManager";
    public static String getSsid(Context context) {
        if (HotSpot.isApEnabled(context)) {
            String ssid = HotSpot.getWifiApSSID(context);
            return ssid;
        }
        if (!isWifiConnected(context)) {
            AppLog.i(TAG, "getSsid wifi not connect!");
            return null;
        }
        WifiManager mWifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        if (wifiInfo == null || wifiInfo.getSSID() == null) {
            AppLog.i(TAG, "getSsid wifiInfo is null");
            return null;
        }
        String ssid = wifiInfo.getSSID();
        if (ssid.contains("0x") || ssid.contains("<unknown ssid>")) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo2 = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            AppLog.i(TAG, "getSsid wifiInfo2:" + wifiInfo2);
            if (wifiInfo2 == null || wifiInfo2.getExtraInfo() == null) {
                return null;
            } else {
                String wifiName = wifiInfo2.getExtraInfo();
                return wifiName.replaceAll("\"", "");
            }

        } else {
            AppLog.i(TAG, "getSsid getSSID:" + wifiInfo.getSSID());
            return wifiInfo.getSSID().replaceAll("\"", "");
        }
    }

    /**
     ** 判断WIFI网络是否可用
     ** @param context
     ** @return
     *      
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
            return true;
        }

        return false;
    }
}
