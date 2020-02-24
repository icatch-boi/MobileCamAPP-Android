package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.entity.SettingMenu;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.Message.AppMessage;
import com.icatch.mobilecam.R;

import java.util.List;

public class SettingListAdapter extends BaseAdapter {
    private static final String TAG=SettingListAdapter.class.getSimpleName();
    private Context context;
    private List<SettingMenu> menuList;
    private Handler handler;

    public SettingListAdapter(Context context,List<SettingMenu> menuList,Handler handler) {
        this.context = context;
        this.menuList = menuList;
        this.handler = handler;
    }

    @Override
    public int getCount() {
        return menuList.size();


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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (menuList.get(position).name == R.string.setting_auto_download) {
            convertView = LayoutInflater.from(context).inflate(R.layout.auto_download_layout, null);
            final CheckBox toggleButton = (CheckBox) convertView.findViewById(R.id.switcher);
            toggleButton.setChecked(AppInfo.autoDownloadAllow);
            toggleButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    handler.obtainMessage(AppMessage.SETTING_OPTION_AUTO_DOWNLOAD, toggleButton.isChecked()).sendToTarget();
                }
            });

            return convertView;
        } else if (menuList.get(position).name == R.string.setting_audio_switch) {
            convertView = LayoutInflater.from(context).inflate(R.layout.audio_switch_layout, null);
            final CheckBox toggleButton = (CheckBox) convertView.findViewById(R.id.switcher);
            toggleButton.setChecked(!AppInfo.disableAudio);
            toggleButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    boolean temp = false;
                    // TODO Auto-generated method stub
                    AppInfo.disableAudio = !AppInfo.disableAudio;
                    AppLog.d(TAG,"toggleButton.setOnClickListener disableAudio=" + AppInfo.disableAudio);


                }
            });
            return convertView;
        }
        if(menuList.get(position).name == R.string.setting_auto_download_size_limit){
            convertView = LayoutInflater.from(context).inflate(R.layout.auto_download_layout_size, null);
            final TextView autoDownloadSize = (TextView) convertView.findViewById(R.id.download_size);
            autoDownloadSize.setText(AppInfo.autoDownloadSizeLimit + "GB");
            return convertView;

        }

//        if(menuList.get(position).name == R.string.setting_live_switch){
//            convertView = LayoutInflater.from(context).inflate(R.layout.live_switch_layout, null);
//            final CheckBox toggleButton = (CheckBox) convertView.findViewById(R.id.live_switcher );
//            toggleButton.setChecked(AppInfo.disableLive);
//            toggleButton.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//                    // TODO Auto-generated method stub
//                    AppInfo.disableLive = !AppInfo.disableLive;
//                    AppLog.d(TAG,"toggleButton.setOnClickListener disableLive=" + AppInfo.disableLive);
//                }
//            });
//            return convertView;
//        }
//        if(menuList.get(position).name == R.string.setting_live_address){
//            convertView = LayoutInflater.from(context).inflate(R.layout.live_address, null);
//            final EditText liveAddress = (EditText) convertView.findViewById(R.id.live_url);
//            liveAddress.setText(AppInfo.liveAddress);
//            liveAddress.setOnFocusChangeListener( new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if(!hasFocus){
//                       AppInfo.liveAddress = liveAddress.getText().toString();
//                        AppSharedPreferences.writeDataByName(context,AppSharedPreferences.OBJECT_NAME_LIVE_URL,AppInfo.liveAddress);
//                    }
//                }
//            } );
//            return convertView;
//
//        }
        convertView = LayoutInflater.from(context).inflate(R.layout.setting_menu_item, null);
        holder = new ViewHolder();
        holder.title = (TextView) convertView.findViewById(R.id.item_text);
        holder.text = (TextView) convertView.findViewById(R.id.item_value);
        convertView.setTag(holder);
        holder.title.setText(menuList.get(position).name);
        if(menuList.get(position).value == ""){
            holder.text.setVisibility(View.GONE);
        }else{
            holder.text.setText(menuList.get(position).value);
        }

        int tempName = menuList.get(position).name;

        if (tempName == R.string.setting_app_version || tempName == R.string.setting_product_name
                || tempName == R.string.setting_firmware_version) {
            holder.title.setTextColor(context.getResources().getColor(R.color.secondary_text));
        } else {
            holder.title.setTextColor(context.getResources().getColor(R.color.primary_text));
        }

        holder.text.setTextColor(context.getResources().getColor(R.color.secondary_text));
        return convertView;
    }

    public final class ViewHolder {
        public TextView title;
        public TextView text;
    }
}

