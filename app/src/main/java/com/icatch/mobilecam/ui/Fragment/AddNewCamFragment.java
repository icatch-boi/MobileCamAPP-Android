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
import android.widget.ImageButton;

import com.icatch.mobilecam.Listener.OnFragmentInteractionListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraType;
import com.icatch.mobilecam.data.Message.AppMessage;
import com.icatch.mobilecam.R;

public class AddNewCamFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String TAG = "AddNewCamFragment";
    private Button wifiConnectCamBtn;
    private Button usbConnectCamBtn;
    private Button BTPairBtn;
    private Handler appStartHandler;
    private Context appContext;
    private ImageButton backBtn;
    private int position;

    public AddNewCamFragment() {
        // Required empty public constructor
    }

    public AddNewCamFragment(Context context,Handler handler,int position) {
        this.appContext = context;
        this.appStartHandler = handler;
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_cam, container, false);
        BTPairBtn = (Button) view.findViewById(R.id.bt_pair);
        wifiConnectCamBtn = (Button) view.findViewById(R.id.wifi_connect_camera);
        usbConnectCamBtn = (Button) view.findViewById(R.id.usb_connect_camera);
        wifiConnectCamBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                appStartHandler.obtainMessage(AppMessage.MESSAGE_CAMERA_CONNECTING_START, CameraType.PANORAMA_CAMERA,position).sendToTarget();
            }
        });
        usbConnectCamBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                appStartHandler.obtainMessage(AppMessage.MESSAGE_CAMERA_CONNECTING_START, CameraType.USB_CAMERA,position).sendToTarget();
            }
        });

        BTPairBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                BTPairBeginFragment btPairBegin = new BTPairBeginFragment(appContext, appStartHandler);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.launch_setting_frame, btPairBegin);
                ft.addToBackStack("BTPairBeginFragment");
                ft.commit();
            }
        });
        backBtn = (ImageButton) view.findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.removeFragment();
                }
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        AppLog.d(TAG, "onAttach");
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
        AppLog.d(TAG, "onDetach");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        AppLog.d(TAG, "onResume");
        super.onResume();
        if(mListener != null){
            mListener.submitFragmentInfo(AddNewCamFragment.class.getSimpleName(),R.string.title_activity_add_new_cam);
        }
    }

    @Override
    public void onStart() {
        AppLog.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        AppLog.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        AppLog.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
