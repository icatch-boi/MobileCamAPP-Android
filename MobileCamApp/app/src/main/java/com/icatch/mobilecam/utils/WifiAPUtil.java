package com.icatch.mobilecam.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import com.icatch.mobilecam.Listener.WifiListener;
import com.icatch.mobilecam.Log.AppLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WifiAPUtil {
    private static final String TAG = "WifiAPUtil";
    public final static boolean DEBUG = true;

    public static final int MESSAGE_AP_STATE_ENABLED = 1;
    public static final int MESSAGE_AP_STATE_FAILED = 2;
    //默认wifi秘密
    private static final String DEFAULT_AP_PASSWORD = "12345678";
    private static WifiAPUtil sInstance;
    private static Handler mHandler;
    private static Context mContext;
    private WifiManager mWifiManager;
    //监听wifi热点的状态变化
    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
    public static int WIFI_AP_STATE_DISABLING = 10;
    public static int WIFI_AP_STATE_DISABLED = 11;
    public static int WIFI_AP_STATE_ENABLING = 12;
    public static int WIFI_AP_STATE_ENABLED = 13;
    public static int WIFI_AP_STATE_FAILED = 14;
    WifiListener wifiListener;

    public enum WifiSecurityType {
        WIFICIPHER_NOPASS, WIFICIPHER_WPA, WIFICIPHER_WEP, WIFICIPHER_INVALID, WIFICIPHER_WPA2
    }

    private WifiAPUtil(Context context) {
        if (DEBUG) Log.d(TAG, "WifiAPUtils construct");
        mContext = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

//        IntentFilter filter2 = new IntentFilter();
//        filter2.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        filter2.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        filter2.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        context.registerReceiver(WifiReceiver, filter2);
    }

    protected void finalize() {
        if (DEBUG) Log.d(TAG, "finalize");
        mContext.unregisterReceiver(mWifiStateBroadcastReceiver);
    }

    public static WifiAPUtil getInstance(Context c) {
        if (null == sInstance)
            sInstance = new WifiAPUtil(c);
        return sInstance;
    }

    public boolean turnOnWifiAp(String str, String password, WifiSecurityType Type) {
        String ssid = str;
        //配置热点信息。
        WifiConfiguration wcfg = new WifiConfiguration();
        wcfg.SSID = new String(ssid);
        wcfg.networkId = 1;
        wcfg.allowedAuthAlgorithms.clear();
        wcfg.allowedGroupCiphers.clear();
        wcfg.allowedKeyManagement.clear();
        wcfg.allowedPairwiseCiphers.clear();
        wcfg.allowedProtocols.clear();

        if (Type == WifiSecurityType.WIFICIPHER_NOPASS) {
            if (DEBUG) Log.d(TAG, "wifi ap----no password");
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN, true);
            wcfg.wepKeys[0] = "";
            wcfg.allowedKeyManagement.set(KeyMgmt.NONE);
            wcfg.wepTxKeyIndex = 0;
        } else if (Type == WifiSecurityType.WIFICIPHER_WPA) {
            if (DEBUG) Log.d(TAG, "wifi ap----wpa");
            //密码至少8位，否则使用默认密码
            if (null != password && password.length() >= 8) {
                wcfg.preSharedKey = password;
            } else {
                wcfg.preSharedKey = DEFAULT_AP_PASSWORD;
            }
            wcfg.hiddenSSID = false;
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wcfg.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            //wcfg.allowedKeyManagement.set(4);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wcfg.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        } else if (Type == WifiSecurityType.WIFICIPHER_WPA2) {
            if (DEBUG) Log.d(TAG, "wifi ap---- wpa2");
            //密码至少8位，否则使用默认密码
            if (null != password && password.length() >= 8) {
                wcfg.preSharedKey = password;
            } else {
                wcfg.preSharedKey = DEFAULT_AP_PASSWORD;
            }
            wcfg.hiddenSSID = false;
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wcfg.allowedKeyManagement.set(4);
            //wcfg.allowedKeyManagement.set(4);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wcfg.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        }
        try {
            Method method = mWifiManager.getClass().getMethod("setWifiApConfiguration",
                    wcfg.getClass());
            Boolean rt = (Boolean) method.invoke(mWifiManager, wcfg);
            if (DEBUG) AppLog.d(TAG, " rt = " + rt);
        } catch (Exception e){
            AppLog.e(TAG, e.getClass().getSimpleName());
            return false;
        }
        return setWifiApEnabled();
    }

    //获取热点状态
    public int getWifiAPState() {
        int state = -1;
        try {
            Method method2 = mWifiManager.getClass().getMethod("getWifiApState");
            state = (Integer) method2.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if (DEBUG) Log.i("WifiAP", "getWifiAPState.state " + state);
        return state;
    }

    private boolean setWifiApEnabled() {
        //开启wifi热点需要关闭wifi
        while (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_DISABLED) {
            mWifiManager.setWifiEnabled(false);
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        }
        // 确保wifi 热点关闭。
        while (getWifiAPState() != WIFI_AP_STATE_DISABLED) {
            try {
                Method method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                        WifiConfiguration.class, boolean.class);
                method1.invoke(mWifiManager, null, false);

                Thread.sleep(200);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        }

        //开启wifi热点
        try {
            Method method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            method1.invoke(mWifiManager, null, true);
            Thread.sleep(200);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    //关闭WiFi热点
    public void closeWifiAp() {
        if (getWifiAPState() != WIFI_AP_STATE_DISABLED) {
            try {
                Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
                Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method2.invoke(mWifiManager, config, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void regitsterHandler(Handler handler) {
        mHandler = handler;
        IntentFilter filter = new IntentFilter();
        filter.addAction(WIFI_AP_STATE_CHANGED_ACTION);
//		filter.addAction(CONNECTIVITY_CHANGE_ACTION);
        mContext.registerReceiver(mWifiStateBroadcastReceiver, filter);
    }

    public void unregitsterHandler() {
        mHandler = null;
        if(mWifiStateBroadcastReceiver != null){
            mContext.unregisterReceiver(mWifiStateBroadcastReceiver);
            mWifiStateBroadcastReceiver = null;
        }

    }

    //监听wifi热点状态变化
    private BroadcastReceiver mWifiStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.i(TAG, "WifiAPUtils onReceive: " + intent.getAction());
            if (WIFI_AP_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int cstate = intent.getIntExtra(EXTRA_WIFI_AP_STATE, -1);
                if (cstate == WIFI_AP_STATE_ENABLED) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(MESSAGE_AP_STATE_ENABLED);
                    }
                }
                if (cstate == WIFI_AP_STATE_DISABLED || cstate == WIFI_AP_STATE_FAILED) {
                    if (mHandler != null)
                        mHandler.sendEmptyMessage(MESSAGE_AP_STATE_FAILED);
                }
            }
        }
    };

    private BroadcastReceiver WifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
                //signal strength changed
            } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接上与否
//                AppLog.d(TAG,"网络状态改变");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    Log.d(TAG, "网络连接断开");
//                    if(!AppInfo.isReconnecting){
//                        handler.obtainMessage(AppMessage.MESSAGE_DISCONNECTED,null).sendToTarget();
//                        AppInfo.isReconnecting = true;
//                    }
//					handler.obtainMessage(AppMessage.MESSAGE_DISCONNECTED,null).sendToTarget();

                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    //获取当前wifi名称
                    Log.d(TAG, "连接到网络 " + wifiInfo.getSSID());
//					handler.obtainMessage(AppMessage.MESSAGE_CONNECTED,wifiInfo).sendToTarget();
                }
            } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {//wifi打开与否
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
                if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                    Log.d(TAG, "系统关闭wifi");
                } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                    Log.d(TAG, "系统开启wifi");
                }
            }
        }
    };

    //获取热点ssid
    public String getValidApSsid() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration configuration = (WifiConfiguration) method.invoke(mWifiManager);
            return configuration.SSID;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    //获取热点密码
    public String getValidPassword() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration configuration = (WifiConfiguration) method.invoke(mWifiManager);
            return configuration.preSharedKey;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

    }

    //获取热点安全类型
    public int getValidSecurity() {
        WifiConfiguration configuration;
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            configuration = (WifiConfiguration) method.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return WifiSecurityType.WIFICIPHER_INVALID.ordinal();
        }

        if (DEBUG) Log.i(TAG, "getSecurity security=" + configuration.allowedKeyManagement);
        if (configuration.allowedKeyManagement.get(KeyMgmt.NONE)) {
            return WifiSecurityType.WIFICIPHER_NOPASS.ordinal();
        } else if (configuration.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return WifiSecurityType.WIFICIPHER_WPA.ordinal();
        } else if (configuration.allowedKeyManagement.get(4)) { //4 means WPA2_PSK
            return WifiSecurityType.WIFICIPHER_WPA2.ordinal();
        }
        return WifiSecurityType.WIFICIPHER_INVALID.ordinal();
    }





}
