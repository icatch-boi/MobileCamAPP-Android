package com.icatch.mobilecam.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.icatch.mobilecam.Listener.MyOrientoinListener;
import com.icatch.mobilecam.Listener.OnFragmentInteractionListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.Presenter.LaunchPresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.AppInfo.ConfigureInfo;
import com.icatch.mobilecam.data.GlobalApp.ExitApp;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.ui.Interface.LaunchView;
import com.icatch.mobilecam.ui.adapter.CameraSlotAdapter;
import com.icatch.mobilecam.ui.appdialog.AppDialog;
import com.icatch.mobilecam.utils.GlideUtils;
import com.icatch.mobilecam.utils.LruCacheTool;
import com.icatch.mobilecam.utils.PermissionTools;
import com.icatch.mobilecam.utils.imageloader.ImageLoaderConfig;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LaunchActivity extends AppCompatActivity implements View.OnClickListener, LaunchView, OnFragmentInteractionListener {
    private final static String TAG = "LaunchActivity";
    private TextView noPhotosFound, noVideosFound;
    private ImageView localVideo, localPhoto;
    private ListView camSlotListView;
    private LaunchPresenter presenter;
    private LinearLayout launchLayout;
    private FrameLayout launchSettingFrame;
    private ActionBar actionBar;
    private AppBarLayout appBarLayout;
    private MyOrientoinListener myOrientoinListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppLog.d(TAG, "cpu type is " + android.os.Build.CPU_ABI);
//        if (android.os.Build.CPU_ABI.contains("armeabi") == false) {
//            return;
//        }
        setContentView(R.layout.activity_launch);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //申请读写外部存储权限
        //PermissionTools.RequestPermissions(LaunchActivity.this);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        launchLayout = (LinearLayout) findViewById(R.id.launch_view);
        launchSettingFrame = (FrameLayout) findViewById(R.id.launch_setting_frame);

        noPhotosFound = (TextView) findViewById(R.id.no_local_photos);
        noVideosFound = (TextView) findViewById(R.id.no_local_videos);

        localVideo = (ImageView) findViewById(R.id.local_video);
        localVideo.setOnClickListener(this);
        localPhoto = (ImageView) findViewById(R.id.local_photo);
        localPhoto.setOnClickListener(this);
        presenter = new LaunchPresenter(LaunchActivity.this);
        presenter.setView(this);
//        presenter.addGlobalLisnter(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_REMOVED, false);
        camSlotListView = (ListView) findViewById(R.id.cam_slot_listview);
        camSlotListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.removeCamera(position);
                return false;
            }
        });

        camSlotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fm = getSupportFragmentManager();
//                getSupportFragmentManager();
                presenter.launchCamera(position, fm);
            }
        });

        LruCacheTool.getInstance().initLruCache();
        presenter.initUsbMonitor();
        if (Build.VERSION.SDK_INT < 23 || PermissionTools.checkAllSelfPermission(this)) {
            ConfigureInfo.getInstance().initCfgInfo(this.getApplicationContext());
            checkLicenseAgreement(LaunchActivity.this);
        } else {
            PermissionTools.requestAllPermissions(LaunchActivity.this);
        }
        AppLog.i(TAG, "end onCreate");
    }

    @Override
    protected void onStart() {
        AppLog.i(TAG, "onStart");
        super.onStart();
//        if (android.os.Build.CPU_ABI.contains("armeabi") == false) {
//            AppDialog.showDialogQuit(LaunchActivity.this, "Do not support X86!");
//            return;
//        }
    }

    @Override
    protected void onResume() {
        AppLog.i(TAG, "Start onResume");
        super.onResume();
//        presenter.submitAppInfo();
        if (Build.VERSION.SDK_INT < 23 || PermissionTools.checkAllSelfPermission(this)) {
            presenter.loadLocalThumbnails02();
        }
        presenter.registerWifiReceiver();
        presenter.registerUSB();
        presenter.loadListview();

        myOrientoinListener = new MyOrientoinListener(this, this);
        boolean autoRotateOn = (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
        //检查系统是否开启自动旋转
        if (autoRotateOn) {
            myOrientoinListener.enable();
        }
        AppLog.i(TAG, "End onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppLog.i(TAG, "onPause");
        if (myOrientoinListener != null) {
            myOrientoinListener.disable();
            myOrientoinListener = null;
        }
    }

    @Override
    protected void onStop() {
        AppLog.i(TAG, "onStop");
        super.onStop();
        presenter.unregisterWifiReceiver();
        presenter.unregisterUSB();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                Log.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                Log.d("AppStart", "back");
//                finish();
                removeFragment();
                break;
            case KeyEvent.KEYCODE_MENU:
                Log.d("AppStart", "KEYCODE_MENU");
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        presenter.removeActivity();
//        presenter.delGlobalLisnter(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_REMOVED, false);
        GlobalInfo.getInstance().endSceenListener();
        LruCacheTool.getInstance().clearCache();
        AppLog.refreshAppLog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launch, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        AppLog.d(TAG, "onPrepareOptionsMenu");
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        AppLog.i(TAG, "id =" + id);
        AppLog.i(TAG, "R.id.home =" + R.id.home);
        AppLog.i(TAG, "R.id.action_search =" + R.id.action_search);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //return true;
            presenter.startSearchCamera();
        }
        if (id == R.id.action_input_ip) {
            presenter.inputIp();
        }
        if (id == android.R.id.home) {
//            finish();
            removeFragment();
            return true;
        }
        if (id == R.id.action_license) {
            Intent mainIntent = new Intent(LaunchActivity.this, LicenseAgreementActivity.class);
            startActivity(mainIntent);;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        AppLog.i(TAG, "click info:::v.getId() =" + v.getId());
        AppLog.i(TAG, "click info:::R.id.local_photo =" + R.id.local_photo);
        AppLog.i(TAG, "click info:::R.id.local_video =" + R.id.local_video);
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.local_photo:
                AppLog.i(TAG, "click the local photo");
                intent.putExtra("CUR_POSITION", 0);
                intent.setClass(LaunchActivity.this, LocalMultiPbActivity.class);
                startActivity(intent);
//                presenter.requesetUsbPermission();
//                UsbDeviceManager usbDeviceManager  = new UsbDeviceManager();
//                usbDeviceManager.getUsbPermission(LaunchActivity.this);
                break;
            case R.id.local_video:
                AppLog.i(TAG, "click the local video");
                intent.putExtra("CUR_POSITION", 1);
                intent.setClass(LaunchActivity.this, LocalMultiPbActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void setLocalPhotoThumbnail(Bitmap bitmap) {
        localPhoto.setImageBitmap(bitmap);
    }

    @Override
    public void setLocalVideoThumbnail(Bitmap bitmap) {
        localVideo.setImageBitmap(bitmap);
    }

    @Override
    public void loadDefaultLocalPhotoThumbnail() {
        localPhoto.setImageResource(R.drawable.local_default_thumbnail);
    }

    @Override
    public void loadDefaultLocalVideoThumbnail() {
        localVideo.setImageResource(R.drawable.local_default_thumbnail);
    }

    @Override
    public void setNoPhotoFilesFoundVisibility(int visibility) {
        noPhotosFound.setVisibility(visibility);
    }

    @Override
    public void setNoVideoFilesFoundVisibility(int visibility) {
        noVideosFound.setVisibility(visibility);
    }

    @Override
    public void setPhotoClickable(boolean clickable) {
        localPhoto.setEnabled(clickable);
    }

    @Override
    public void setVideoClickable(boolean clickable) {
        localVideo.setEnabled(clickable);
    }

    @Override
    public void setListviewAdapter(CameraSlotAdapter cameraSlotAdapter) {
        camSlotListView.setAdapter(cameraSlotAdapter);
    }

    @Override
    public void setBackBtnVisibility(boolean visibility) {
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(visibility);
        }
    }

    @Override
    public void setNavigationTitle(int resId) {
        if (actionBar != null) {
            actionBar.setTitle(resId);
        }
    }

    @Override
    public void setNavigationTitle(String res) {
        if (actionBar != null) {
            actionBar.setTitle(res);
        }
    }

    @Override
    public void setLaunchLayoutVisibility(int visibility) {
        launchLayout.setVisibility(visibility);
        appBarLayout.setVisibility(visibility);
    }

    @Override
    public void setLaunchSettingFrameVisibility(int visibility) {
        launchSettingFrame.setVisibility(visibility);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionTools.ALL_REQUEST_CODE:
                AppLog.i(TAG, "permissions.length = " + permissions.length);
                AppLog.i(TAG, "grantResults.length = " + grantResults.length);
                boolean retValue = true;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        retValue = false;
                        break;
                    }
                }
                if (retValue) {
                    presenter.loadLocalThumbnails02();
                    ConfigureInfo.getInstance().initCfgInfo(this.getApplicationContext());
                    checkLicenseAgreement(LaunchActivity.this);
                } else {
//                    AppDialog.showDialogQuit(this, R.string.permission_is_denied_info);
                    AppDialog.showDialogQuit(this, R.string.permission_is_denied_info);
//                    Toast.makeText(this, "Request write storage failed!", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
        }
    }

    @Override
    public void submitFragmentInfo(String fragment, int resId) {
//        setNavigationTitle(resId);
    }

    @Override
    public void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                setNavigationTitle(R.string.app_name);
                launchSettingFrame.setVisibility(View.GONE);
                launchLayout.setVisibility(View.VISIBLE);
                appBarLayout.setVisibility(View.VISIBLE);
                setBackBtnVisibility(false);
            }
            getSupportFragmentManager().popBackStack();
        }
    }

    // 将所有的fragment 出栈;

    @Override
    public void fragmentPopStackOfAll() {
        int fragmentBackStackNum = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < fragmentBackStackNum; i++) {
            getSupportFragmentManager().popBackStack();
        }
        setBackBtnVisibility(false);
        setNavigationTitle(R.string.app_name);
        launchSettingFrame.setVisibility(View.GONE);
        launchLayout.setVisibility(View.VISIBLE);
        appBarLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void setLocalPhotoThumbnail(String filePath) {
        GlideUtils.loadImageViewLodingSize(this, filePath, 500, 500, localPhoto, R.drawable.local_default_thumbnail, R.drawable.local_default_thumbnail);
    }

    @Override
    public void setLocalVideoThumbnail(String filePath) {
        GlideUtils.loadImageViewLodingSize(this,filePath,500,500,localVideo,R.drawable.local_default_thumbnail,R.drawable.local_default_thumbnail);
    }


    public void checkLicenseAgreement(Context context){
        SharedPreferences preferences = context.getSharedPreferences("appData", MODE_PRIVATE);
        boolean isAgreeLicenseAgreement = preferences.getBoolean("agreeLicenseAgreement", false);
        AppLog.d(TAG, "showLicenseAgreementDialog isAgreeLicenseAgreement=" + isAgreeLicenseAgreement);
        String AgreeLicenseAgreementVersion = preferences.getString("agreeLicenseAgreementVersion", "");
        AppLog.d(TAG, "showLicenseAgreementDialog Version =" + AgreeLicenseAgreementVersion);

        if ((!isAgreeLicenseAgreement) || (!AppInfo.EULA_VERSION.equalsIgnoreCase(AgreeLicenseAgreementVersion))) {
            showLicenseAgreementDialog(context, AppInfo.EULA_VERSION);
        }
    }

    AlertDialog agreementDialog;
    public void showLicenseAgreementDialog(final Context context, final String eulaversion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View contentView = View.inflate(context, R.layout.dialog_privacy_policy, null);
        TextView textView = contentView.findViewById(R.id.txv_privacy_policy);
        SpannableString spanString = new SpannableString(context.getString(R.string.content_privacy_policy_2));
        spanString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //点击的响应事件
                //AppLog.d(TAG,"spanString onclick");
                //MyToast.show(context,"onclick");
                Intent mainIntent = new Intent(LaunchActivity.this, LicenseAgreementActivity.class);
                startActivity(mainIntent);
            }
        }, 0, spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(R.string.content_privacy_policy_1);
        textView.append(spanString);
        textView.append(context.getString(R.string.content_privacy_policy_3));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setTitle(R.string.title_privacy_policy);
        builder.setView(contentView);

        builder.setPositiveButton(R.string.text_agree, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = context.getSharedPreferences("appData", MODE_PRIVATE).edit();
                editor.putBoolean("agreeLicenseAgreement", true);
                editor.putString("agreeLicenseAgreementVersion", eulaversion);
                editor.commit();
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.text_disagree, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ExitApp.getInstance().exit();
            }
        });
        agreementDialog = builder.create();
        agreementDialog.show();
    }

    public void closeLicenseAgreementDialog(){
        if(agreementDialog != null){
            agreementDialog.dismiss();
            agreementDialog = null;
        }
    }
}
