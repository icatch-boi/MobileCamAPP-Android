package com.icatch.mobilecam.ui.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.Listener.OnFragmentInteractionListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.Presenter.BTPairBeginPresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.ui.Interface.BTPairBeginFragmentView;


public class BTPairBeginFragment extends Fragment  implements BTPairBeginFragmentView {

    private OnFragmentInteractionListener mListener;
    private String TAG = "BTPairBeginFragment";
    private Button btnSearchBluetooth;
    private Button btnSearchBLE;
    private TextView listHeader;
    private ListView bluetoothListView;
    private Context appContext;
    private Handler appStartHandler;
    private View myView;
    BTPairBeginPresenter presenter;
    private ImageButton backBtn;
    private ImageButton refreshBtn;

    public BTPairBeginFragment() {
        // Required empty public constructor
    }

    public BTPairBeginFragment(Context context, Handler handler) {
        this.appContext = context;
        this.appStartHandler = handler;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppLog.d(TAG,"onCreateView myView=" +myView);
        // Inflate the layout for this fragment
        if(myView != null){
            return  myView;
        }
        myView = inflater.inflate(R.layout.fragment_btpair_begin, container, false);
        View headerView = inflater.inflate(R.layout.bluetooth_header, null);
        listHeader = (TextView)headerView.findViewById(R.id.bt_header);
        btnSearchBluetooth = (Button) myView.findViewById(R.id.button_bluetooth_search);

        if(AppInfo.isBLE){
            listHeader.setText(R.string.text_ble_devices);//Bluetooth Low Energy
            btnSearchBluetooth.setText(R.string.text_btpair_search_ble);
        }else{
            listHeader.setText(R.string.text_classic_bluetooth_devices);
            btnSearchBluetooth.setText(R.string.text_btpair_search_camera);
        }
        bluetoothListView = (ListView) myView.findViewById(R.id.choose_blutTooth_list);
        bluetoothListView.addHeaderView(headerView);
        bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.connectBT(position);
            }
        });

        btnSearchBluetooth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                presenter.searchBluetooth();
            }
        });
        presenter = new BTPairBeginPresenter(getActivity(),appContext,appStartHandler,getFragmentManager());
        presenter.setView(this);
        presenter.loadBtList();
        backBtn = (ImageButton) myView.findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.removeFragment();
                }
            }
        });
        refreshBtn = (ImageButton) myView.findViewById(R.id.refresh_btn);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.searchBluetooth();
            }
        });
        return myView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AppLog.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        AppLog.d(TAG,"onAttach");
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
        AppLog.d(TAG, "onDetach");
        mListener = null;
    }

    @Override
    public void onResume() {
        AppLog.d(TAG, "onResume");
        if(mListener != null){
            mListener.submitFragmentInfo(BTPairBeginFragment.class.getSimpleName(),R.string.title_fragment_btpair_begin);
        }
        AppInfo.isReleaseBTClient = true;
        super.onResume();
    }

    @Override
    public void setBTListViewAdapter(BaseAdapter adapter) {
        if(bluetoothListView != null) {
            bluetoothListView.setAdapter(adapter);
        }
    }

    @Override
    public void setListHeader(int resId) {
        listHeader.setText(resId);
    }

    @Override
    public void onDestroy() {
        AppLog.d(TAG, "onDestroy");
        presenter.unregister();
        super.onDestroy();
    }
}
