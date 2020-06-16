package com.icatch.mobilecam.Listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.Message.AppMessage;

/**
 * Created by b.jiang on 2016/8/19.
 */
public class WifiListener {
    private String TAG = "WifiListener";
    private WifiReceiver wifiReceiver;
    private Context context;
    private Handler handler;

    public WifiListener(Context context, Handler handler){
        this.context = context;
        this.handler = handler;
    }

    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if(intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)){
                //signal strength changed
            }
            else if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){//wifi连接上与否
//                AppLog.d(TAG,"网络状态改变");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(info.getState().equals(NetworkInfo.State.DISCONNECTED)){
                    AppLog.d(TAG,"网络连接断开");
//                    if(!AppInfo.isReconnecting){
//                        handler.obtainMessage(AppMessage.MESSAGE_DISCONNECTED,null).sendToTarget();
//                        AppInfo.isReconnecting = true;
//                    }
                    handler.obtainMessage(AppMessage.MESSAGE_DISCONNECTED,null).sendToTarget();

                }
                else if(info.getState().equals(NetworkInfo.State.CONNECTED)){
                    WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    //获取当前wifi名称
                    AppLog.d(TAG,"连接到网络 " + wifiInfo.getSSID());
                    handler.obtainMessage(AppMessage.MESSAGE_CONNECTED,wifiInfo).sendToTarget();
                }
            }
            else if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){//wifi打开与否
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
                if(wifistate == WifiManager.WIFI_STATE_DISABLED){
                    AppLog.d(TAG,"系统关闭wifi");
                }
                else if(wifistate == WifiManager.WIFI_STATE_ENABLED){
                    AppLog.d(TAG,"系统开启wifi");
                }
            }
        }
    }

    public void registerReceiver(){
        AppLog.d(TAG,"registerReceiver");
        IntentFilter filter= new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiReceiver =new WifiReceiver();
        context.registerReceiver(wifiReceiver, filter);
    }

    public void unregisterReceiver(){
        AppLog.d(TAG,"unregisterReceiver");
        if(wifiReceiver != null){
            context.unregisterReceiver(wifiReceiver);
        }
    }
}
