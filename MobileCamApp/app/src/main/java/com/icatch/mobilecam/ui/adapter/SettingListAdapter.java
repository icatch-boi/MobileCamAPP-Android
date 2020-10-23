package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.os.Handler;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.icatch.mobilecam.Function.BaseProrertys;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.MyCamera.MyCamera;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.PropertyId.PropertyId;
import com.icatch.mobilecam.data.entity.SettingMenu;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.Message.AppMessage;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;

import java.util.List;

public class SettingListAdapter extends BaseAdapter {
    private static final String TAG=SettingListAdapter.class.getSimpleName();
    private Context context;
    private List<SettingMenu> menuList;
    private Handler handler;
    private MyCamera currCamera = CameraManager.getInstance().getCurCamera();
    private CameraProperties cameraProperties = currCamera.getCameraProperties();
    private BaseProrertys baseProrertys = currCamera.getBaseProrertys();

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
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_switch_layout, null);
            TextView textView = (TextView) convertView.findViewById(R.id.item_name);
            final SwitchCompat switchCompat = (SwitchCompat) convertView.findViewById(R.id.switchCompat);
            textView.setText(R.string.setting_auto_download);
            switchCompat.setChecked(AppInfo.autoDownloadAllow);
            switchCompat.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    handler.obtainMessage(AppMessage.SETTING_OPTION_AUTO_DOWNLOAD, switchCompat.isChecked()).sendToTarget();
                }
            });

            return convertView;
        } else if (menuList.get(position).name == R.string.setting_audio_switch) {
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_switch_layout, null);
            TextView textView = (TextView) convertView.findViewById(R.id.item_name);
            SwitchCompat switchCompat = (SwitchCompat) convertView.findViewById(R.id.switchCompat);
            textView.setText(R.string.setting_audio_switch);
            switchCompat.setChecked(!AppInfo.disableAudio);
            switchCompat.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    boolean temp = false;
                    // TODO Auto-generated method stub
                    AppInfo.disableAudio = !AppInfo.disableAudio;
                    AppLog.d(TAG,"toggleButton.setOnClickListener disableAudio=" + AppInfo.disableAudio);


                }
            });
            return convertView;
        }else if (menuList.get(position).name == R.string.setting_title_power_on_auto_record) {
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_switch_layout, null);
            TextView textView = (TextView) convertView.findViewById(R.id.item_name);
            textView.setText(R.string.setting_title_power_on_auto_record);
            int curValue = cameraProperties.getCurrentPropertyValue(PropertyId.POWER_ON_AUTO_RECORD);
            final SwitchCompat switchCompat = (SwitchCompat) convertView.findViewById(R.id.switchCompat);
            boolean isCheched = curValue == 0 ? false : true;
            switchCompat.setChecked(isCheched);
            switchCompat.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    int value = switchCompat.isChecked() ? 1 : 0;
                    cameraProperties.setPropertyValue(PropertyId.POWER_ON_AUTO_RECORD, value);
                    // read one more time
                    int retValue = cameraProperties.getCurrentPropertyValue(PropertyId.POWER_ON_AUTO_RECORD);
                    boolean isChecked2 = retValue == 0 ? false : true;
                    switchCompat.setChecked(isChecked2);
                }
            } );
            /*            switchCompat.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    int value = switchCompat.isChecked() ? 1 : 0;
                    CameraProperties.getInstance().setPropertyValue(PropertyId.POWER_ON_AUTO_RECORD, value);
                    // read one more time
                    int retValue = CameraProperties.getInstance().getCurrentPropertyValue(PropertyId.POWER_ON_AUTO_RECORD);
                    boolean isChecked = retValue == 0 ? false : true;
                    switchCompat.setChecked(isChecked);
                }            });            */
            return convertView;
        } else if (menuList.get(position).name == R.string.setting_title_image_stabilization) {
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_switch_layout, null);
            TextView textView = (TextView) convertView.findViewById(R.id.item_name);
            final SwitchCompat switchCompat = (SwitchCompat) convertView.findViewById(R.id.switchCompat);
            textView.setText(R.string.setting_title_image_stabilization);
            int curValue = cameraProperties.getCurrentPropertyValue(PropertyId.IMAGE_STABILIZATION);
            List<Integer> supportValues = cameraProperties.getSupportedPropertyValues(PropertyId.IMAGE_STABILIZATION);
            if (supportValues == null || supportValues.size() <= 1) {
                switchCompat.setEnabled(false);
            }
            boolean isCheched = curValue == 0 ? false : true;
            switchCompat.setChecked(isCheched);
            switchCompat.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if (!switchCompat.isEnabled()) {
                        MyToast.show(context, R.string.current_size_not_support_image_stabilization);
                    } else {
                        int value = switchCompat.isChecked() ? 1 : 0;
                        cameraProperties.setPropertyValue(PropertyId.IMAGE_STABILIZATION, value);
                        // read one more time
                        int retValue = cameraProperties.getCurrentPropertyValue(PropertyId.IMAGE_STABILIZATION);
                        isChecked = retValue == 0 ? false : true;
                        switchCompat.setChecked(isChecked);
                    }
                }
            } );
            return convertView;
        }else if(menuList.get(position).name == R.string.setting_auto_download_size_limit){
            convertView = LayoutInflater.from(context).inflate(R.layout.auto_download_layout_size, null);
            final TextView autoDownloadSize = (TextView) convertView.findViewById(R.id.download_size);
            autoDownloadSize.setText(AppInfo.autoDownloadSizeLimit + "GB");
            return convertView;
        }else if (menuList.get(position).name == R.string.setting_title_wind_noise_reduction) {
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_switch_layout, null);
            TextView textView = (TextView) convertView.findViewById(R.id.item_name);
            textView.setText(R.string.setting_title_wind_noise_reduction);
            int curValue = cameraProperties.getCurrentPropertyValue(PropertyId.WIND_NOISE_REDUCTION);
            final SwitchCompat switchCompat = (SwitchCompat) convertView.findViewById(R.id.switchCompat);
            boolean isCheched = curValue == 0 ? false : true;
            switchCompat.setChecked(isCheched);
            switchCompat.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    int value = switchCompat.isChecked() ? 1 : 0;
                    cameraProperties.setPropertyValue(PropertyId.WIND_NOISE_REDUCTION, value);
                    // read one more time
                    int retValue = cameraProperties.getCurrentPropertyValue(PropertyId.WIND_NOISE_REDUCTION);
                    boolean isChecked2 = retValue == 0 ? false : true;
                    switchCompat.setChecked(isChecked2);
                }
            } );
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

