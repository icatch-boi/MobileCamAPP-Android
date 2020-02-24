package com.icatch.mobilecam.ui.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.icatch.mobilecam.ui.appdialog.AppDialog;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.Listener.OnFragmentInteractionListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.SystemInfo.SystemInfo;
import com.icatchtek.bluetooth.customer.exception.IchBluetoothDeviceBusyException;
import com.icatchtek.bluetooth.customer.exception.IchBluetoothTimeoutException;
import com.icatchtek.bluetooth.customer.type.ICatchWifiInformation;

import java.io.IOException;


public class BTPairSetupFragment extends Fragment {

    private static String TAG = "BTPairSetupFragment";
    private OnFragmentInteractionListener mListener;
    private View myView;
    private Button btnSetup;
    private EditText cameraSsid;
    private EditText cameraPassword;
    private ICatchWifiInformation iCatchWifiInformation;
    private Handler handler = new Handler();
    private Handler appStartHandler;
    private ImageButton backBtn;
    private TextView skipTxv;


    public BTPairSetupFragment(Handler handler) {
        // Required empty public constructor
        this.appStartHandler = handler;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(myView != null){
            return myView;
        }
        myView = inflater.inflate(R.layout.fragment_btpair_setup, container, false);
        btnSetup = (Button) myView.findViewById(R.id.bt_wifisetup);
        cameraSsid  = (EditText) myView.findViewById(R.id.bt_wifisetup_camera_ssid);
        cameraPassword  = (EditText) myView.findViewById(R.id.bt_wifisetup_camera_password);
        backBtn = (ImageButton) myView.findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.removeFragment();
                }
            }
        });
        skipTxv = (TextView) myView.findViewById(R.id.skip_txv);

        skipTxv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BTPairCompletedFragment BTPairCompleted = new BTPairCompletedFragment(appStartHandler);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.launch_setting_frame, BTPairCompleted);
                ft.addToBackStack("BTPairCompletedFragment");
                ft.commit();
            }
        });
        new Thread(new Runnable(){
            @Override
            public void run(){
                AppLog.d(TAG, "start getWifiInformation");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                iCatchWifiInformation =  new ICatchWifiInformation();
                try {
                    iCatchWifiInformation = AppInfo.iCatchBluetoothClient.getSystemControl().getWifiInformation();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    AppLog.d(TAG, "getWifiInformation IOException");

                    e1.printStackTrace();
                } catch (IchBluetoothTimeoutException e1) {
                    // TODO Auto-generated catch block
                    AppLog.d(TAG, "getWifiInformation IchBluetoothTimeoutException");

                    e1.printStackTrace();
                } catch (IchBluetoothDeviceBusyException e1) {
                    // TODO Auto-generated catch block
                    AppLog.d(TAG, "getWifiInformation IchBluetoothDeviceBusyException");

                    e1.printStackTrace();
                }
                AppLog.d(TAG, "end getWifiInformation iCatchWifiInformation=" + iCatchWifiInformation);
                if(iCatchWifiInformation != null){
                    final String ssid = iCatchWifiInformation.getWifiSSID();
                    final String password = iCatchWifiInformation.getWifiPassword();
                    AppLog.d(TAG, "getWifiInformation ssid=" + ssid);
                    AppLog.d(TAG, "getWifiInformation password=" + password);

                    handler.post(new Runnable(){
                        @Override
                        public void run() {
                            AppLog.d(TAG, "handler.post setText");
                            MyProgressDialog.closeProgressDialog();
                            // TODO Auto-generated method stub
                            if(ssid != null){
                                cameraSsid.setText(ssid);
                            }
                            if(password != null){
                                cameraPassword.setText(password);
                            }
//							cameraPassword.setText("12121212");

                        }
                    });
                }else {
                    handler.post(new Runnable() {
                                     @Override
                                     public void run() {
//                                         MyToast.show(getContext(),"get wifi info is null!");
                                         MyProgressDialog.closeProgressDialog();
                                         AppDialog.showDialogWarn(getActivity(), "get Wifi information is null!");
                                     }
                                 });

                }
            }
        }).start();


        btnSetup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppLog.d(TAG, "start btnSetup onClick");

//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                //ICOM-3478 Start ADD by b.jiang 20160713
                SystemInfo.hideInputMethod(getActivity());
                //ICOM-3478 End ADD by b.jiang 20160713
                boolean retValue = true;
                String ssid = cameraSsid.getText().toString();
                String password = cameraPassword.getText().toString();
                AppLog.d(TAG, "btnSetup onClick ssid=[" + ssid + "]");
                AppLog.d(TAG, "btnSetup onClick password=[" + password + "]");
                if (ssid == null || password == null) {
                    return;
                }
                if (ssid.length() > 20) {
                    CharSequence cs; //声明一个CharSequence类型的变量cs
                    cs = getText(R.string.camera_name_limit);
                    cameraSsid.setError(cs);
                    retValue = false;
                }
                if (cameraPassword.length() > 10 || cameraPassword.length() < 8) {
                    CharSequence cs; //声明一个CharSequence类型的变量cs
                    cs = getText(R.string.password_limit);
                    cameraPassword.setError(cs);
                    retValue = false;
                }
                if (retValue) {
                    boolean ret = false;

                    ICatchWifiInformation iCatchWifiAPInformation = new ICatchWifiInformation();
                    iCatchWifiAPInformation.setWifiSSID(ssid);
                    iCatchWifiAPInformation.setWifiPassword(password);
                    AppLog.d(TAG, "setWifiInformation ssid=" + ssid);
                    AppLog.d(TAG, "setWifiInformation password=" + password);
                    try {
                        ret = AppInfo.iCatchBluetoothClient.getSystemControl().setWifiInformation(iCatchWifiAPInformation);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        AppLog.d(TAG, "setWifiInformation IOException");

                        e.printStackTrace();
                    } catch (IchBluetoothTimeoutException e) {
                        AppLog.d(TAG, "setWifiInformation IOException");

                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IchBluetoothDeviceBusyException e) {
                        AppLog.d(TAG, "setWifiInformation IOException");
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    AppLog.d(TAG, "setWifiInformation ret=" + ret);
                    if (ret) {
                        BTPairCompletedFragment BTPairCompleted = new BTPairCompletedFragment(appStartHandler);
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.launch_setting_frame, BTPairCompleted);
                        ft.addToBackStack("tag");
                        ft.commit();
                    } else {
                        MyToast.show(getActivity(), R.string.message_setup_false);
                    }
                }
            }
        });

        MyProgressDialog.showProgressDialog(getActivity(),R.string.stream_zoom_wait);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        if(mListener != null){
            mListener.submitFragmentInfo(BTPairSetupFragment.class.getSimpleName(),R.string.title_fragment_btpair_wifisetup);
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if(AppInfo.iCatchBluetoothClient == null){
            return;
        }
        if(AppInfo.isReleaseBTClient){
            try {
                AppLog.d(TAG, "onDestroy() iCatchBluetoothClient.pancamGLRelease()");
                AppInfo.iCatchBluetoothClient.release();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                AppLog.d(TAG, "iCatchBluetoothClient.pancamGLRelease() IOException");
                e.printStackTrace();
            }
        }

        super.onDestroy();
    }
}
