package com.icatch.mobilecam.ui.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.icatch.mobilecam.MyCamera.CameraType;
import com.icatch.mobilecam.ui.appdialog.AppDialog;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.Listener.OnFragmentInteractionListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.Message.AppMessage;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.utils.SharedPreferencesUtil;
import com.icatch.mobilecam.utils.WifiCheck;
import com.icatchtek.bluetooth.customer.exception.IchBluetoothDeviceBusyException;
import com.icatchtek.bluetooth.customer.exception.IchBluetoothTimeoutException;
import com.icatchtek.bluetooth.customer.type.ICatchWifiEncType;
import com.icatchtek.bluetooth.customer.type.ICatchWifiInformation;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BTPairCompletedFragment extends Fragment {

    private String TAG = "BTPairCompletedFragment";
    private OnFragmentInteractionListener mListener;
    private Handler appStartHandler;
    private View myView;
    private TextView txvPairCompleted;

    private ICatchWifiInformation iCatchWifiAPInformation;
    private WifiCheck wifiCheck;
    private ExecutorService executor;
    private Timer connectTimer;
    String ssid = "";
    String password;
    private static final int ENABLE_WIFI_FAILED = 14;
    private static final int CONNECT_WIFI_FAILED = 16;
    private static final int CONNECT_CAMERA_FAILED = 17;
    private static final int START_CHECK_CONNECT_WIFI = 19;
    private ImageButton backBtn;

    public BTPairCompletedFragment() {
        // Required empty public constructor
    }

    public BTPairCompletedFragment(Handler handler) {
        // Required empty public constructor
        this.appStartHandler = handler;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_btpair_completed, container, false);
        txvPairCompleted = (TextView) myView.findViewById(R.id.done_txv);
        txvPairCompleted.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyProgressDialog.showProgressDialog(getActivity(), R.string.message_connecting);
                executor = Executors.newSingleThreadExecutor();
                executor.submit(new ConnectWifiThread(), null);
            }
        });

        backBtn = (ImageButton) myView.findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.removeFragment();
                }
            }
        });

        return myView;
    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
        wifiCheck = new WifiCheck(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        if (mListener != null) {
            mListener.submitFragmentInfo(BTPairCompletedFragment.class.getSimpleName(), R.string.title_fragment_btpair_completed);
        }
        super.onResume();
    }

    class ConnectWifiThread implements Runnable {
        int connectNum = 4;

        @Override
        public void run() {
            if (enableWifi()) {
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
                if (iCatchWifiAPInformation == null) {
                    try {
                        AppLog.d(TAG, "------start getWifiInformation");
                        iCatchWifiAPInformation = AppInfo.iCatchBluetoothClient.getSystemControl().getWifiInformation();
                        AppLog.d(TAG, "------end getWifiInformation iCatchWifiAPInformation=" + iCatchWifiAPInformation);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        AppLog.d(TAG, "getWifiInformation IOException");
                        e.printStackTrace();
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                MyProgressDialog.closeProgressDialog();
                                Toast.makeText(getActivity(), "connent IOException!", Toast.LENGTH_LONG).show();
                            }
                        });
                        if (connectTimer != null) {
                            connectTimer.cancel();
                        }
                        return;
                    } catch (IchBluetoothTimeoutException e) {
                        // TODO Auto-generated catch block
                        AppLog.d(TAG, "getWifiInformation IchBluetoothTimeoutException");
                        e.printStackTrace();
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                MyProgressDialog.closeProgressDialog();
                                Toast.makeText(getActivity(), "connent timeout!", Toast.LENGTH_LONG).show();
                            }

                        });
                        if (connectTimer != null) {
                            connectTimer.cancel();
                        }
                        return;
                    } catch (IchBluetoothDeviceBusyException e) {
                        // TODO Auto-generated catch block
                        AppLog.d(TAG, "getWifiInformation IchBluetoothDeviceBusyException");
                        e.printStackTrace();
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                MyProgressDialog.closeProgressDialog();
                                Toast.makeText(getActivity(), "Device busy!", Toast.LENGTH_LONG).show();
                            }
                        });
                        if (connectTimer != null) {
                            connectTimer.cancel();
                        }
                        return;
                    }
                    if (iCatchWifiAPInformation == null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                MyProgressDialog.closeProgressDialog();
                                AppDialog.showDialogWarn(getActivity(), "get Wifi information is null!");
//                                Toast.makeText(getActivity(), "get Wifi information is null!", Toast.LENGTH_LONG).show();
                            }
                        });
                        if (connectTimer != null) {
                            connectTimer.cancel();
                        }
                        return;
                    }
                }

                ssid = iCatchWifiAPInformation.getWifiSSID();
                password = iCatchWifiAPInformation.getWifiPassword();
                ICatchWifiEncType encType = iCatchWifiAPInformation.getWifiEncType();
                AppLog.d(TAG, "connectWifi encType=[" + encType + "]");

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "ssid=[" + ssid + "],pwd=[" + password + "]", Toast.LENGTH_LONG).show();
                    }
                });
                AppLog.d(TAG, "connectWifi ssid=[" + ssid + "]");
                AppLog.d(TAG, "connectWifi password=[" + password + "]");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                wifiCheck.openWifi();
                handler.obtainMessage(START_CHECK_CONNECT_WIFI).sendToTarget();
                while (!wifiCheck.connectWifi(ssid, password, WifiCheck.WIFICIPHER_WAP)) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    connectNum--;
                    if (connectNum < 0) {
                        break;
                    }
                }

            } else {
                handler.obtainMessage(ENABLE_WIFI_FAILED).sendToTarget();
            }
        }
    }

    class WifiCheckTask extends TimerTask {
        int reconnectTime = 0;

        @Override
        public void run() {
            AppLog.d(TAG, "WifiCheckTask ssid=" + ssid);
            if (wifiCheck.isWifiConnected(getActivity(), ssid) == true) {
                AppLog.d(TAG, "isWifiConnect() == true");
//				handler.obtainMessage(CONNECT_WIFI_SUCCESS).sendToTarget();
                AppInfo.isReleaseBTClient = false;
                if (connectTimer != null) {
                    connectTimer.cancel();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                    }
                });
                int position = (int) SharedPreferencesUtil.get(getContext(),SharedPreferencesUtil.CONFIG_FILE,"camera_position",0);
                appStartHandler.obtainMessage(AppMessage.MESSAGE_CAMERA_CONNECTING_START, CameraType.WIFI_CAMERA,position).sendToTarget();
                executor.shutdown();
                return;
            } else {
                AppLog.d(TAG, "isWifiConnect() == false  reconnectTime =" + reconnectTime);
                reconnectTime++;
                if (reconnectTime >= 15) {
                    if (connectTimer != null) {
                        connectTimer.cancel();
                    }
                    handler.obtainMessage(CONNECT_WIFI_FAILED).sendToTarget();
                    return;
                }
            }
        }
    }


    private final Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENABLE_WIFI_FAILED:
                    MyProgressDialog.closeProgressDialog();
                    Toast.makeText(getActivity(), "enable wifi failed.", Toast.LENGTH_LONG).show();
                    break;
                case CONNECT_WIFI_FAILED:
                    MyProgressDialog.closeProgressDialog();
                    Toast.makeText(getActivity(), "failed to connect wifi.", Toast.LENGTH_LONG).show();
                    break;

                case CONNECT_CAMERA_FAILED:
                    MyProgressDialog.closeProgressDialog();
                    Toast.makeText(getActivity(), "failed to connect camera.", Toast.LENGTH_LONG).show();
                    break;
                case START_CHECK_CONNECT_WIFI:
                    connectTimer = new Timer();
                    connectTimer.schedule(new WifiCheckTask(), 0, 6000);
                    break;
            }
        }
    };

    private boolean enableWifi() {
        Boolean retValue = false;
        try {
            AppLog.d(TAG, "start enableWifi");
            retValue = AppInfo.iCatchBluetoothClient.getSystemControl().enableWifi();
            AppLog.d(TAG, "end  enableWifi retValue =" + retValue);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            AppLog.d(TAG, "enableWifi() IOException");
            retValue = false;
            e.printStackTrace();
        } catch (IchBluetoothTimeoutException e) {
            AppLog.d(TAG, "enableWifi() IchBluetoothTimeoutException");
            // TODO Auto-generated catch block
            retValue = false;
            e.printStackTrace();
        } catch (IchBluetoothDeviceBusyException e) {
            // TODO Auto-generated catch block
            AppLog.d(TAG, "enableWifi() IchBluetoothDeviceBusyException");
            e.printStackTrace();
        }
        AppLog.d(TAG, "enableWifi ret=" + retValue);
        return retValue;
    }
}
