package com.icatch.mobilecam.data.SystemInfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.icatch.mobilecam.Log.AppLog;

import java.util.List;

/**
 * Created by zhang yanhu C001012 on 2015/11/24 17:57.
 */
public class MWifiManager {
    private static String TAG = "MWifiManager";
    private static String WIFI_SSID_UNKNOWN = "unknown";
//    public static String getSsid(Context context) {
//        if (HotSpot.isApEnabled(context)) {
//            String ssid = HotSpot.getWifiApSSID(context);
//            return ssid;
//        }
//        if (!isWifiConnected(context)) {
//            AppLog.i(TAG, "getSsid wifi not connect!");
//            return null;
//        }
//        WifiManager mWifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = mWifi.getConnectionInfo();
//        if (wifiInfo == null || wifiInfo.getSSID() == null) {
//            AppLog.i(TAG, "getSsid wifiInfo is null");
//            return null;
//        }
//        String ssid = wifiInfo.getSSID();
//        if (ssid.contains("0x") || ssid.contains("<unknown ssid>")) {
//            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo wifiInfo2 = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//            AppLog.i(TAG, "getSsid wifiInfo2:" + wifiInfo2);
//            if (wifiInfo2 == null || wifiInfo2.getExtraInfo() == null) {
//                return null;
//            } else {
//                String wifiName = wifiInfo2.getExtraInfo();
//                return wifiName.replaceAll("\"", "");
//            }
//
//        } else {
//            AppLog.i(TAG, "getSsid getSSID:" + wifiInfo.getSSID());
//            return wifiInfo.getSSID().replaceAll("\"", "");
//        }
//    }

    public static String getSsid(Context context) {
        if (!isWifiConnected(context)) {
            AppLog.e(TAG, "----------ssid is null=");
            return null;
        }
        //android 8.0及以下
        String ssid = WIFI_SSID_UNKNOWN;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            WifiManager mWifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = mWifi.getConnectionInfo();
            if (wifiInfo != null) {
                ssid = wifiInfo.getSSID();
                if(ssid.contains("\"")){
                    ssid =ssid.replace("\"","");
                }
            }else {
                AppLog.i(TAG, "getSsid wifiInfo is null");
            }
        }else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo2 = mConnectivityManager.getActiveNetworkInfo();
            AppLog.i(TAG, "getSsid wifiInfo2:" + wifiInfo2);
            if(wifiInfo2 !=null){
                AppLog.i(TAG, "getSsid wifiInfo2.getExtraInfo()" + wifiInfo2.getExtraInfo());
            }
            if (wifiInfo2 == null || wifiInfo2.getExtraInfo() == null) {
                ssid = getSsidByNetworkId(context);
            } else {
                String wifiName = wifiInfo2.getExtraInfo();
                ssid =  wifiName.replaceAll("\"", "");
            }
        }else {
            WifiManager mWifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = mWifi.getConnectionInfo();
            if (wifiInfo != null) {
                ssid = wifiInfo.getSSID();
                if(ssid.contains("\"")){
                    ssid =ssid.replace("\"","");
                }
            }else {
                AppLog.i(TAG, "getSsid wifiInfo is null");
            }
        }
        //android 8.0以后

        AppLog.i(TAG, "getSsid ssid:" + ssid);
        return ssid;
    }
    /**
     * 华为android 9.0 获取 ssid
     */
    private static String getSsidByNetworkId(Context context){
        AppLog.d(TAG, "getSsidByNetworkId ");
        String ssid = null;
        WifiManager wifiManager = ((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        if(null != wifiManager){
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int networkId = wifiInfo.getNetworkId();
            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration wifiConfiguration:configuredNetworks){
                if (wifiConfiguration.networkId==networkId){
                    ssid=wifiConfiguration.SSID;
                    break;
                }
            }
        }

        if(ssid != null && ssid.contains("\"")){
            ssid =ssid.replace("\"","");
        }
        AppLog.d(TAG, "getSsidByNetworkId ssid:" + ssid);
        return ssid;
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
