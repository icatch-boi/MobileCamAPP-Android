package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.icatch.mobilecam.data.entity.BluetoothAppDevice;
import com.icatch.mobilecam.R;

import java.util.List;

/**
 */
public class BlueToothListAdapter extends BaseAdapter {

    LayoutInflater inflater;
    private Context context;
    private List<BluetoothAppDevice> list;
    private boolean isBLE;

    public BlueToothListAdapter(Context context, List<BluetoothAppDevice> list, boolean isBLE) {
        // TODO Auto-generated constructor stub
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.isBLE = isBLE;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = null;
        view = inflater.inflate(R.layout.bluetooth_status, null);

        String name = list.get(position).getBluetoothName();
        if (name == null) {
            name = "Unknown";
        }
        TextView bluetoothName = (TextView) view.findViewById(R.id.bluetooth_name);
        bluetoothName.setText(name);

        TextView bluetoothMac = (TextView) view.findViewById(R.id.bluetooth_mac);
        bluetoothMac.setText(list.get(position).getBluetoothAddr());
        TextView bluetoothConnect = (TextView) view.findViewById(R.id.bluetooth_connect);
        if (!isBLE) {
            bluetoothConnect.setVisibility(View.VISIBLE);
            // 判断连接状态，显示对应的状态
            if (list.get(position).getBluetoothConnect() == true) {
                bluetoothConnect.setText("Binded");
                bluetoothConnect.setTextColor(context.getResources().getColor(R.color.green));
            } else {
                bluetoothConnect.setText("Unbinded");
                bluetoothConnect.setTextColor(context.getResources().getColor(R.color.red));
            }
        } else {
            bluetoothConnect.setVisibility(View.GONE);
        }


        return view;
    }
}