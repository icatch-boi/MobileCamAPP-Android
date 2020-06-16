package com.icatch.mobilecam.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.data.GlobalApp.ExitApp;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

public class WifiCheck {

    private String TAG = "WifiCheck";
    private static final int CONNECT_FAILED = 0x02;
    private static final int IN_BACKGROUND = 0x03;
    private static final int RECONNECT_SUCCESS = 0x04;
    private static final int RECONNECT_FAILED = 0x05;
    public static final int WIFICIPHER_NOPASS = 0x06;
    public static final int WIFICIPHER_WEP = 0x07;
    public static final int WIFICIPHER_WAP = 0x08;
    private static final int RECONNECT_CAMERA = 0x09;
    private static int RECONNECT_WAITING = 10000;
    private static int RECONNECT_CHECKING_PERIOD = 5000;
    private WifiInfo mWifiInfo;
    private WifiManager mWifiManager;
    private Activity activity;
    private Timer reconnectTimer;
    private static int RECONNECT_TIME = 10;
    protected AlertDialog reconnectDialog;
    private AlertDialog dialog;
    private Boolean isShowed = false;
    ExecutorService executor;
    private int curReconnectTime = 0;


    public WifiCheck(Activity activity) {
        // 取得WifiManager对象
        this.activity = activity;
        mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 检查当前WIFI状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    public boolean connectWifi(String SSID, String Password, int Type) {
        AppLog.d(TAG, "connectWifi SSID=" + SSID + " Password=" + Password + " activity=" + activity);
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = CreateWifiInfo(SSID, Password, Type);
        AppLog.d(TAG, "connectWifi start addNetwork　config=" + config);
        int netID = wifiManager.addNetwork(config);
        AppLog.d(TAG, "connectWifi start enableNetwork netID=" + netID);
        boolean bRet = wifiManager.enableNetwork(netID, true);
        AppLog.d(TAG, "connectWifi end----bRet =" + bRet);
        return bRet;
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        AppLog.d(TAG, "start CreateWifiInfo");
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == WIFICIPHER_NOPASS) // Data.WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WIFICIPHER_WEP) // Data.WIFICIPHER_WEP
        {
            if (Password.length() != 0) {
                int length = Password.length();
                // WEP-40, WEP-104, and 256-bit WEP
                // (WEP-232?)
                if ((length == 10 || length == 26 || length == 58) && Password.matches("[0-9A-Fa-f]*")) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = '"' + Password + '"';
                }
            }
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WIFICIPHER_WAP) // Data.WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        AppLog.d(TAG, "end CreateWifiInfo config=" + config);
        return config;
    }

    public boolean isWifiConnected(Context context, String nameFilter) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                WifiManager mWifi = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = mWifi.getConnectionInfo();
                if (wifiInfo.getIpAddress() != 0 && wifiInfo.getSSID().contains(nameFilter) == true) {
                    return true;
                }
            }
        }
        return false;
    }

    public void showConectFailureWarningDlg(Context context) {
        if (isShowed == true) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle("Warning").setMessage(R.string.dialog_timeout);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                ExitApp.getInstance().exit();
                AppLog.d(TAG, "showConectFailureWarningDlg exit connect");
                ExitApp.getInstance().finishAllActivity();
            }
        });
        builder.setNegativeButton(R.string.dialog_btn_reconnect, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                curReconnectTime = 0;
                reconnectTimer = new Timer(true);
                ReconnectTask task = new ReconnectTask();
                reconnectTimer.schedule(task, 3000, RECONNECT_CHECKING_PERIOD);
                wifiCheckHandler.obtainMessage(RECONNECT_CAMERA).sendToTarget();
            }
        });
        if (dialog == null) {
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private void showReconnectDialog() {

        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(GlobalInfo.getInstance().getAppContext());
        builder.setIcon(R.drawable.warning).setTitle(R.string.dialog_btn_reconnect).setMessage(R.string.message_reconnect);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                ExitApp.getInstance().exit();
                AppLog.d(TAG, "showReconnectDialog exit connect");
                ExitApp.getInstance().finishAllActivity();
            }
        });
        reconnectDialog = builder.create();
        reconnectDialog.setCancelable(false);
        reconnectDialog.show();
    }

    private void showReconnectTimeoutDialog() {

        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(GlobalInfo.getInstance().getAppContext());
        builder.setIcon(R.drawable.warning).setTitle(R.string.text_reconnect_timeout).setMessage(R.string.message_reconnect_timeout);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                ExitApp.getInstance().exit();
                AppLog.d(TAG, "showReconnectTimeoutDialog exit connect");
                ExitApp.getInstance().finishAllActivity();
            }
        });
        reconnectDialog = builder.create();
        reconnectDialog.setCancelable(false);
        reconnectDialog.show();
    }

    public void showAutoReconnectDialog() {
        reconnectTimer = new Timer(true);
        ReconnectTask task = new ReconnectTask();
        reconnectTimer.schedule(task, 3000, RECONNECT_CHECKING_PERIOD);

        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(GlobalInfo.getInstance().getAppContext());
        builder.setIcon(R.drawable.warning).setTitle(R.string.dialog_btn_reconnect).setMessage(R.string.message_reconnect);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                ExitApp.getInstance().exit();
                AppLog.d(TAG, "showAutoReconnectDialog exit connect");
                ExitApp.getInstance().finishAllActivity();
            }
        });
        reconnectDialog = builder.create();
        reconnectDialog.setCancelable(false);
        reconnectDialog.show();
    }


    private class ReconnectTask extends TimerTask {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            // 取得WifiManager对象
            mWifiManager = (WifiManager) (GlobalInfo.getInstance().getAppContext()).getSystemService(Context.WIFI_SERVICE);
            mWifiInfo = mWifiManager.getConnectionInfo();
            String ssid = mWifiInfo.getSSID().replaceAll("\"", "");
            AppLog.d("tigertiger", "reconnect mWifiInfo.getSSID()=" + ssid);
            String cameraSsid = CameraManager.getInstance().getCurCamera().getCameraName();
            if (ssid.equals(cameraSsid)) {
                AppLog.d(TAG, "reconnect success!");
                if (reconnectTimer != null) {
                    reconnectTimer.cancel();
                }
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {
                        if (reconnectDialog != null) {
                            reconnectDialog.dismiss();
                        }
                        AppLog.d(TAG, "reconnect success! start finishAllActivity()");
                        ExitApp.getInstance().finishAllActivity();
//                        ExitApp.getInstance().exit();
                    }
                };

                Timer tempTimer = new Timer(true);
                tempTimer.schedule(task, RECONNECT_WAITING);
            }
            curReconnectTime++;
            AppLog.d(TAG, "reconnect curReconnectTime=" + curReconnectTime);
            if (curReconnectTime > RECONNECT_TIME) {
                if (reconnectDialog != null) {
                    reconnectDialog.dismiss();
                }
                if (reconnectTimer != null) {
                    reconnectTimer.cancel();
                }
                wifiCheckHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showReconnectTimeoutDialog();
                    }
                });
            }
        }
    }

    private final Handler wifiCheckHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECONNECT_CAMERA:
                    showReconnectDialog();
            }
        }
    };


}
