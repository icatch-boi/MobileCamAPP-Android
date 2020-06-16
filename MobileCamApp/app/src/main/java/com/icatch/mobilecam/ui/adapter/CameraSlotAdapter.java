package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icatch.mobilecam.data.entity.CameraSlot;
import com.icatch.mobilecam.data.SystemInfo.SystemInfo;
import com.icatch.mobilecam.data.Message.AppMessage;
import com.icatch.mobilecam.R;

import java.util.List;

/**
 * Created by zhang yanhu C001012 on 2015/11/17 09:20.
 */
public class CameraSlotAdapter extends BaseAdapter {
    private List<CameraSlot> camSlotList;
    private Context context;
    private int listViewHeight = 0;
    private Handler myHandler;
    private String tag = "CameraSlotAdapter";

    public CameraSlotAdapter(Context context, List<CameraSlot> camSlotList, Handler handler,int height) {
        this.context = context;
        this.camSlotList = camSlotList;
        this.listViewHeight = height;
        this.myHandler = handler;
    }

    public CameraSlotAdapter(Context context, List<CameraSlot> camSlotList, Handler handler) {
        this.context = context;
        this.camSlotList = camSlotList;
        this.listViewHeight = SystemInfo.getInstance().getMetrics(context).heightPixels;
        this.myHandler = handler;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return camSlotList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {
        CameraSlot camSlotItem = camSlotList.get(arg0);
        final int arg = arg0;
        if (camSlotItem.isOccupied) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cam_slot_item, null);
            TextView slotConnectStatusTV = (TextView) convertView.findViewById(R.id.slot_connect_state);
            ImageView slotConnectStatusIV = (ImageView) convertView.findViewById(R.id.slot_connect_sign);
            TextView slotCameraName = (TextView) convertView.findViewById(R.id.slot_camera_name);
            ImageView slotPhoto = (ImageView) convertView.findViewById(R.id.slotPhoto);
            LinearLayout slot_layout = (LinearLayout) convertView.findViewById(R.id.slot_layout);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) slot_layout.getLayoutParams();
            ImageView deleteCamera = (ImageView) convertView.findViewById(R.id.delete_camera);
            deleteCamera.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    myHandler.obtainMessage(AppMessage.MESSAGE_DELETE_CAMERA, arg,0).sendToTarget();
                }
            });

            params.height = (int) (listViewHeight / 7);
            slot_layout.setLayoutParams(params);

            byte[] imageBuf = camSlotItem.cameraPhoto;
            Bitmap imageBitmap = null;
            if (imageBuf != null) {
                imageBitmap = BitmapFactory.decodeByteArray(imageBuf, 0, imageBuf.length);
            }

            if (camSlotItem.isReady) {
                slotConnectStatusTV.setTextColor(context.getResources().getColor(R.color.cambridge_blue));
                slotConnectStatusTV.setText(R.string.text_connected);
                slotConnectStatusIV.setImageDrawable(context.getResources().getDrawable(R.drawable.camera_wifi_connected));
            } else {
                slotConnectStatusTV.setTextColor(context.getResources().getColor(R.color.graywhite));
                slotConnectStatusTV.setText(R.string.text_disconnect);
                slotConnectStatusIV.setImageDrawable(context.getResources().getDrawable(R.drawable.camera_wifi_disconnected));
            }
            slotCameraName.setText(camSlotItem.cameraName);
            slotPhoto.setImageBitmap(imageBitmap);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.cam_slot_item_add, null);
            LinearLayout slot_layout_add = (LinearLayout) convertView.findViewById(R.id.slot_layout_add);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) slot_layout_add.getLayoutParams();
            params.height = listViewHeight / 7;
            slot_layout_add.setLayoutParams(params);
        }
        return convertView;

    }
}
