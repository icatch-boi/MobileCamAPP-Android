package com.icatch.mobilecam.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.icatch.mobilecam.Function.GlobalEvent;
import com.icatch.mobilecam.Function.SDKEvent;
import com.icatch.mobilecam.Function.USB.DeviceFilter;
import com.icatch.mobilecam.Function.USB.USBMonitor;
import com.icatch.mobilecam.Listener.WifiListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.MyCamera.CameraType;
import com.icatch.mobilecam.MyCamera.CommandSession;
import com.icatch.mobilecam.MyCamera.MyCamera;
import com.icatch.mobilecam.Presenter.Interface.BasePresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.AppInfo.AppSharedPreferences;
import com.icatch.mobilecam.data.AppInfo.ConfigureInfo;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.data.Message.AppMessage;
import com.icatch.mobilecam.data.Mode.CameraNetworkMode;
import com.icatch.mobilecam.data.PropertyId.PropertyId;
import com.icatch.mobilecam.data.SystemInfo.HotSpot;
import com.icatch.mobilecam.data.SystemInfo.MWifiManager;
import com.icatch.mobilecam.data.SystemInfo.SystemInfo;
import com.icatch.mobilecam.data.entity.CameraSlot;
import com.icatch.mobilecam.data.entity.SearchedCameraInfo;
import com.icatch.mobilecam.data.entity.SelectedCameraInfo;
import com.icatch.mobilecam.db.CameraSlotSQLite;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.ui.Fragment.AddNewCamFragment;
import com.icatch.mobilecam.ui.Interface.LaunchView;
import com.icatch.mobilecam.ui.activity.LaunchHelpActivity;
import com.icatch.mobilecam.ui.activity.PreviewActivity;
import com.icatch.mobilecam.ui.activity.PvParamSettingActivity;
import com.icatch.mobilecam.ui.activity.RemoteMultiPbActivity;
import com.icatch.mobilecam.ui.adapter.CameraSlotAdapter;
import com.icatch.mobilecam.ui.appdialog.AppDialog;
import com.icatch.mobilecam.utils.PermissionTools;
import com.icatch.mobilecam.utils.SharedPreferencesUtil;
import com.icatch.mobilecam.utils.StorageUtil;
import com.icatch.mobilecam.utils.fileutils.MFileTools;
import com.icatch.mobilecam.utils.imageloader.ICatchtekImageDownloader;
import com.icatch.mobilecam.utils.imageloader.ImageLoaderConfig;
import com.icatchtek.control.customer.type.ICatchCamEventID;
import com.icatchtek.control.customer.type.ICatchCamFeatureID;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yh.zhang C001012 on 2015/11/12 14:51.
 */
public class LaunchPresenter extends BasePresenter {
    private static final String TAG = LaunchPresenter.class.getSimpleName();
    private LaunchView launchView;
    private CameraSlotAdapter cameraSlotAdapter;
    private ArrayList<CameraSlot> camSlotList;
    private final LaunchHandler launchHandler = new LaunchHandler();

    private Activity activity;
    private GlobalEvent globalEvent;
    private LinkedList<SelectedCameraInfo> searchCameraInfoList;
    private int cameraSlotPosition;
    private USBMonitor mUSBMonitor;
    //    private UsbDevice usbDevice;
    private WifiListener wifiListener;

    public LaunchPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void setView(LaunchView launchView) {
        this.launchView = launchView;
        initCfg();
        globalEvent = new GlobalEvent(launchHandler);
    }

    @Override
    public void initCfg() {
//        AppLog.enableAppLog();
        GlobalInfo.getInstance().setCurrentApp(activity);
        // never sleep when run this activity
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//         do not display menu bar
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        CrashHandler.getInstance().init(activity);
//        AppInfo.liveAddress = AppSharedPreferences.readDataByName( activity, AppSharedPreferences.OBJECT_NAME_LIVE_URL );
        AppInfo.inputIp = AppSharedPreferences.readDataByName(activity, AppSharedPreferences.OBJECT_NAME_INPUT_IP);
        ConfigureInfo.getInstance().initCfgInfo(activity.getApplicationContext());
        GlobalInfo.getInstance().startScreenListener();
    }


    public void addGlobalLisnter(int eventId, boolean forAllSession) {
        globalEvent.addGlobalEventListener(eventId, forAllSession);
    }

    public void delGlobalLisnter(int eventId, boolean forAllSession) {
        globalEvent.delGlobalEventListener(eventId, forAllSession);
    }

    private String getCameraIp() {
        String ip = "";
        if (HotSpot.isApEnabled(activity)) {
            ip = HotSpot.getFirstConnectedHotIP();
        } else{
            ip = AppInfo.inputIp;
        }
        return ip;
    }

    public synchronized void launchCamera(final int position, final int cameraType) {
        AppLog.i(TAG, "launchCamera  position:" + position + " cameraType:" + cameraType);
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        if(camera != null && camera.isConnected()){
            AppLog.i(TAG, "launchCamera  camera is connected.");
            return;
        }
        String cameraName = null;
        UsbDevice usbDevice = null;
        AppLog.d(TAG, "launchCamera position=" + position + " cameraType=" + cameraType);
        if (cameraType == CameraType.WIFI_CAMERA) {
            cameraName = MWifiManager.getSsid(activity);
        } else if (cameraType == CameraType.USB_CAMERA) {
            usbDevice = getUsbDevice();
            if (usbDevice != null) {
                cameraName = getDeviceName(usbDevice);
            } else {
                MyToast.show(activity, R.string.text_usb_device_not_detected);
                return;
            }
        }
        if (camSlotList.get(position).isReady) {
            MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
            final String finalWifiSsid1 = cameraName;
            final UsbDevice finalUsbDevice = usbDevice;
            new Thread(new Runnable() {
                public void run() {
                    if (cameraType == CameraType.WIFI_CAMERA) {
                        beginConnectCamera(position, getCameraIp(), finalWifiSsid1);
                    } else if (cameraType == CameraType.USB_CAMERA) {
                        beginConnectUSBCamera(position, finalUsbDevice);
                    }
                }
            }).start();
        } else {
            if (camSlotList.get(position).isOccupied) {
                AppDialog.showDialogWarn(activity, activity.getString(R.string.text_please_connect_camera).replace("$1$", camSlotList.get(position).cameraName));
            } else if (!isRegistered(cameraName)) {
                MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
                final String finalWifiSsid = cameraName;
                final UsbDevice finalUsbDevice1 = usbDevice;
                new Thread(new Runnable() {
                    public void run() {
                        if (cameraType == CameraType.WIFI_CAMERA) {
                            beginConnectCamera(position, getCameraIp(), finalWifiSsid);
                        } else if (cameraType == CameraType.USB_CAMERA) {
                            beginConnectUSBCamera(position, finalUsbDevice1);
                        }
                    }
                }).start();
            } else {
                AppDialog.showDialogWarn(activity, activity.getString(R.string.text_camera_has_been_registered).replace("$1$", cameraName));
            }
        }
    }

    public synchronized void launchCamera(final int position, FragmentManager fm) {
        AppLog.i(TAG, "launchCamera  position = " + position);
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        if(camera != null && camera.isConnected()){
            AppLog.i(TAG, "launchCamera  camera is connected.");
            return;
        }
        cameraSlotPosition = position;
        String wifiSsid = null;
        wifiSsid = MWifiManager.getSsid(activity);
        final int cameraType = camSlotList.get(position).cameraType;
        if (camSlotList.get(position).isReady) {
            MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
            final String finalWifiSsid = wifiSsid;
            new Thread(new Runnable() {
                public void run() {
                    if (cameraType == CameraType.WIFI_CAMERA) {
                        beginConnectCamera(position, getCameraIp(), finalWifiSsid);
                    } else if (cameraType == CameraType.USB_CAMERA) {
                        UsbDevice usbDevice = getUsbDevice();
                        if (usbDevice != null) {
                            beginConnectUSBCamera(position, usbDevice);
                        } else {
                            launchHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MyToast.show(activity, R.string.text_usb_device_not_detected);
                                }
                            });
                        }
                    }
                }
            }).start();
        } else {
            if (camSlotList.get(position).isOccupied) {
                AppDialog.showDialogWarn(activity, activity.getString(R.string.text_please_connect_camera).replace("$1$", camSlotList.get(position).cameraName));
            } else {
                launchView.setLaunchLayoutVisibility(View.GONE);
                launchView.setLaunchSettingFrameVisibility(View.VISIBLE);
                launchView.setNavigationTitle("");
                launchView.setBackBtnVisibility(true);
                AddNewCamFragment addNewCamFragment = new AddNewCamFragment(activity.getApplicationContext(), launchHandler, position);
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.launch_setting_frame, addNewCamFragment, "other");
                ft.addToBackStack("tag");
                ft.commit();
            }
        }
    }

    public void removeCamera(int position) {
        AppLog.i(TAG, "remove camera position = " + position);
        CameraSlotSQLite.getInstance().deleteByPosition(position);
        loadListview();
    }

    public void loadListview() {
        //need to update isReady status
        camSlotList = CameraSlotSQLite.getInstance().getAllCameraSlotFormDb();
        if (cameraSlotAdapter != null) {
            cameraSlotAdapter.notifyDataSetInvalidated();
        }
        cameraSlotAdapter = new CameraSlotAdapter(GlobalInfo.getInstance().getAppContext(), camSlotList, launchHandler, SystemInfo.getMetrics
                (activity.getApplicationContext()).heightPixels);
        UsbDevice usbDevice = getUsbDevice();
        if (usbDevice != null) {
            setReadyState(true, getDeviceName(usbDevice));
        }
        launchView.setListviewAdapter(cameraSlotAdapter);

    }

    public void notifyListview() {
        if (cameraSlotAdapter != null) {
            cameraSlotAdapter.notifyDataSetChanged();
        }
    }

    private void setReadyState(boolean isReady, String cameraName) {
        if (cameraName == null || cameraName.equals("")) {
            return;
        }
        for (CameraSlot temp : camSlotList
        ) {
            if (temp.cameraName != null && temp.cameraName.equals(cameraName)) {
                temp.isReady = isReady;
                break;
            }
        }
    }

    private void resetWifiState() {
        for (CameraSlot temp : camSlotList) {
            if (temp.cameraType != CameraType.USB_CAMERA) {
                temp.isReady = false;
            }
        }
    }

    public void loadLocalThumbnails02() {
        String rootPath = StorageUtil.getRootPath(activity);
        String photoPath = MFileTools.getNewestPhotoFromDirectory(rootPath + AppInfo.DOWNLOAD_PATH_PHOTO);
        if (photoPath != null) {
            launchView.setLocalPhotoThumbnail(photoPath);
        }else {
            launchView.loadDefaultLocalPhotoThumbnail();
        }
        if (MFileTools.getPhotosSize(rootPath + AppInfo.DOWNLOAD_PATH_PHOTO) > 0) {
            launchView.setNoPhotoFilesFoundVisibility(View.GONE);
            launchView.setPhotoClickable(true);
        } else {
            launchView.setNoPhotoFilesFoundVisibility(View.VISIBLE);
            launchView.setPhotoClickable(false);
        }
        String videoPath = MFileTools.getNewestVideoFromDirectory(rootPath + AppInfo.DOWNLOAD_PATH_VIDEO);
        if (videoPath != null) {
            launchView.setLocalVideoThumbnail(videoPath);
        }else{
            launchView.loadDefaultLocalVideoThumbnail();
        }
        if (MFileTools.getVideosSize(rootPath + AppInfo.DOWNLOAD_PATH_VIDEO) > 0) {
            launchView.setNoVideoFilesFoundVisibility(View.GONE);
            launchView.setVideoClickable(true);
        } else {
            launchView.setNoVideoFilesFoundVisibility(View.VISIBLE);
            launchView.setVideoClickable(false);
        }
    }

    public void startSearchCamera() {
        if (searchCameraInfoList != null) {
            searchCameraInfoList.clear();
        }
        searchCameraInfoList = new LinkedList<SelectedCameraInfo>();
        addGlobalLisnter(ICatchCamEventID.ICATCH_EVENT_DEVICE_SCAN_ADD, false);
        CommandSession.stopDeviceScan();
        CommandSession.startDeviceScan();
        startSearchTimeoutTimer();
        MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
    }

    private void startSearchTimeoutTimer() {
        Timer searchTimer = new Timer();
        TimerTask searchTask = new TimerTask() {
            @Override
            public void run() {
                launchHandler.obtainMessage(AppMessage.MESSAGE_CAMERA_SCAN_TIME_OUT).sendToTarget();
            }
        };
        searchTimer.schedule(searchTask, 20000);
    }

    private void showSearchCameraListSingleDialog() {
        if (searchCameraInfoList.isEmpty()) {
            return;
        }
        CharSequence title = "Please selectOrCancelAll camera";
        final CharSequence[] tempsearchCameraInfoList = new CharSequence[searchCameraInfoList.size()];

        for (int ii = 0; ii < tempsearchCameraInfoList.length; ii++) {
            tempsearchCameraInfoList[ii] = searchCameraInfoList.get(ii).cameraName + "\n" + searchCameraInfoList.get(ii).cameraIp + "          " +
                    CameraNetworkMode.getModeConvert(searchCameraInfoList.get(ii).cameraMode);

        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                //appStartHandler.obtainMessage(GlobalInfo.MESSAGE_CAMERA_SEARCH_SELECTED, searchCameraInfoList.get(which)).sendToTarget();
            }
        };
        showOptionDialogSingle(title, tempsearchCameraInfoList, 0, listener, true);
    }

    private void showOptionDialogSingle(CharSequence title, CharSequence[] items, int checkedItem, DialogInterface.OnClickListener listener, boolean
            cancelable) {
        AlertDialog optionDialog = new AlertDialog.Builder(activity).setTitle(title).setSingleChoiceItems(items, checkedItem, listener).create();
        optionDialog.setCancelable(cancelable);
        optionDialog.show();
    }

    public void inputIp() {
        showInputIpDialog(activity);
    }

    public void showInputIpDialog(final Context context) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View contentView = View.inflate(context, R.layout.input_ip, null);
        final EditText resetTxv = (EditText) contentView.findViewById(R.id.ip_address);
        resetTxv.setText(AppInfo.inputIp);
        builder.setTitle(R.string.action_input_ip);
        builder.setView(contentView);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.ok)
                // 为按钮设置监听器
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppInfo.inputIp = resetTxv.getText().toString();
                        AppSharedPreferences.writeDataByName(context, AppSharedPreferences.OBJECT_NAME_INPUT_IP, AppInfo.inputIp);
                    }
                });
        // 为对话框设置一个“取消”按钮
        builder.setNegativeButton(context.getResources().getString(R.string.gallery_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消登录，不做任何事情。
            }
        });
        //创建、并显示对话框
        builder.create().show();
    }

    private class LaunchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppMessage.MESSAGE_CAMERA_CONNECT_FAIL:
                    MyProgressDialog.closeProgressDialog();
                    //AppDialog.showDialogWarn(activity, R.string.dialog_timeout);
                    showHelpDialogWarn(activity, R.string.dialog_timeout_2);
                    break;
                case AppMessage.MESSAGE_CAMERA_CONNECT_SUCCESS:
                    MyProgressDialog.closeProgressDialog();
                    redirectToAnotherActivity(activity);
//                    redirectToAnotherActivity(activity, PreviewActivity.class);
                    break;
                case AppMessage.MESSAGE_DELETE_CAMERA:
                    removeCamera(msg.arg1);
                    break;
                case AppMessage.MESSAGE_CAMERA_SCAN_TIME_OUT:
                    AppLog.i(TAG, "MESSAGE_CAMERA_SCAN_TIME_OUT:count =" + searchCameraInfoList.size());
                    CommandSession.stopDeviceScan();
                    globalEvent.delGlobalEventListener(ICatchCamEventID.ICATCH_EVENT_DEVICE_SCAN_ADD, false);
                    MyProgressDialog.closeProgressDialog();
                    if (searchCameraInfoList.isEmpty()) {
                        MyToast.show(activity, R.string.alert_no_camera_found);
                        break;
                    }
                    showSearchCameraListSingleDialog();
                    break;
                case SDKEvent.EVENT_SEARCHED_NEW_CAMERA:
                    SearchedCameraInfo temp = (SearchedCameraInfo) msg.obj;
                    searchCameraInfoList.addLast(new SelectedCameraInfo(temp.cameraName, temp.cameraIp, temp.cameraMode, temp.uid));
                    break;
                case AppMessage.MESSAGE_CAMERA_CONNECTING_START:
                    launchView.fragmentPopStackOfAll();
                    int cameraType = msg.arg1;
                    int position = msg.arg2;
                    launchCamera(position, cameraType);
                    break;
                case AppMessage.MESSAGE_DISCONNECTED:
                    AppLog.i(TAG, "receive MESSAGE_DISCONNECTED");
                    resetWifiState();
                    notifyListview();
                    break;

                case AppMessage.MESSAGE_CONNECTED:
                    AppLog.i(TAG, "receive MESSAGE_CONNECTED");
                    String ssid = MWifiManager.getSsid(activity);
                    if (ssid != null) {
                        setReadyState(true, ssid);
                        notifyListview();
                    }
                    break;
            }
        }
    }
    void showHelpDialogWarn(final Context context,int messageID) {
        AppLog.i(TAG, "showHelpDialogWarn");
        AlertDialog dialog = null;
        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle("Warning").setMessage(messageID);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.help, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyProgressDialog.closeProgressDialog();
                redirectToAnotherActivity(context, LaunchHelpActivity.class);
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private synchronized void beginConnectCamera(int position, String ip, String wifiSsid) {
        AppLog.i(TAG, "beginConnectCamera position:" + position + " wifiSsid:" + wifiSsid);
        MyCamera currentCamera = CameraManager.getInstance().getCurCamera();
        if(currentCamera != null && currentCamera.isConnected()){
            AppLog.i(TAG, "beginConnectCamera camera is connected.");
            return;
        }
        currentCamera = CameraManager.getInstance().createCamera(CameraType.WIFI_CAMERA, wifiSsid, ip, position, CameraNetworkMode.AP);
        if (currentCamera.connect(true) == false) {
            launchHandler.obtainMessage(AppMessage.MESSAGE_CAMERA_CONNECT_FAIL).sendToTarget();
            return;
        }
        if (currentCamera.getCameraProperties().hasFuction(PropertyId.CAMERA_DATE)) {
            currentCamera.getCameraProperties().setCameraDate();
        }
        CameraSlotSQLite.getInstance().update(new CameraSlot(position, true, wifiSsid, CameraType.WIFI_CAMERA, null, true));
        launchHandler.post(new Runnable() {
            @Override
            public void run() {
                MyProgressDialog.closeProgressDialog();
                redirectToAnotherActivity(activity);
//                redirectToAnotherActivity(activity, PreviewActivity.class);
            }
        });
    }

    public void redirectToAnotherActivity(Context context) {
        ICatchtekImageDownloader downloader = new ICatchtekImageDownloader(activity);
        ImageLoaderConfig.initImageLoader(activity.getApplicationContext(), downloader);
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        CameraProperties cameraProperties = null;
        if (camera != null) {
            cameraProperties = camera.getCameraProperties();
        }
        if (cameraProperties != null
                && cameraProperties.hasFuction(PropertyId.DEFALUT_TO_PREVIEW)
                && cameraProperties.checkCameraCapabilities(ICatchCamFeatureID.ICH_CAM_APP_DEFAULT_TO_PLAYBACK)) {
            if (cameraProperties.isSDCardExist()) {
                Intent intent = new Intent();
                AppLog.i(TAG, "intent:start PbMainActivity.class");
                intent.setClass(context, RemoteMultiPbActivity.class);
                context.startActivity(intent);
            } else {
                AppDialog.showDialogWarn(activity, R.string.dialog_card_lose);
            }
        } else {
            Intent intent = new Intent();
            intent.setClass(context, PreviewActivity.class);
            context.startActivity(intent);
        }

    }

    public synchronized void reconnectUSBCamera(){
        int position = (int) SharedPreferencesUtil.get(activity,SharedPreferencesUtil.CONFIG_FILE,"camera_position",0);
        UsbDevice usbDevice = getUsbDevice();
        beginConnectUSBCamera(position,usbDevice);
    }

    private synchronized void beginConnectUSBCamera(final int position, final UsbDevice usbDevice) {
        AppLog.i(TAG, "beginConnectUSBCamera position:" + position + " usbDevice:" + usbDevice.getDeviceName() + " ProductId:" + usbDevice.getProductId() + " VendorId:" + usbDevice.getVendorId());
        if (!mUSBMonitor.hasPermission(usbDevice)) {
            launchHandler.post(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();
                    if(Build.VERSION.SDK_INT >= 28 && !PermissionTools.checkCameraSelfPermission(activity)){
                        SharedPreferencesUtil.put(activity,SharedPreferencesUtil.CONFIG_FILE,"camera_position",position);
                        AppDialog.showDialogWarn(activity, R.string.request_camera_permission_warn_info, false, new AppDialog.OnDialogSureClickListener() {
                            @Override
                            public void onSure() {
                                PermissionTools.requestCameraPermissions(activity);
                            }
                        });
                    }else {
                        mUSBMonitor.requestPermission(usbDevice);
                    }
                }
            });
            return;
        }
        MyCamera currentCamera = CameraManager.getInstance().getCurCamera();
        if(currentCamera != null && currentCamera.isConnected()){
            AppLog.i(TAG, "beginConnectUSBCamera camera is connected.");
            return;
        }
        currentCamera = CameraManager.getInstance().createUSBCamera(CameraType.USB_CAMERA, usbDevice, position);
        if (currentCamera.connect(false) == false) {
            launchHandler.obtainMessage(AppMessage.MESSAGE_CAMERA_CONNECT_FAIL).sendToTarget();
            return;
        }
        if (currentCamera.getCameraProperties().hasFuction(PropertyId.CAMERA_DATE)) {
            currentCamera.getCameraProperties().setCameraDate();
        }
        CameraSlotSQLite.getInstance().update(new CameraSlot(position, true, currentCamera.getCameraName(), CameraType.USB_CAMERA, null, true));
        launchHandler.post(new Runnable() {
            @Override
            public void run() {
                MyProgressDialog.closeProgressDialog();
                redirectToAnotherActivity(activity, PvParamSettingActivity.class);
            }
        });
    }

    private boolean isRegistered(String ssid) {
        for (CameraSlot camSlot : camSlotList) {
            if (camSlot.cameraName != null && camSlot.cameraName.equals(ssid)) {
                return true;
            }
        }
        return false;
    }

    //usb相关
    public void initUsbMonitor() {
        mUSBMonitor = new USBMonitor(activity, mOnDeviceConnectListener);
    }

    public void registerUSB() {
        if (mUSBMonitor == null) {
            initUsbMonitor();
        }
        mUSBMonitor.register();
    }

    public void unregisterUSB() {
        if (mUSBMonitor != null) {
            mUSBMonitor.unregister();
            mUSBMonitor = null;
        }
    }

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            AppLog.d(TAG, "USBMonitor onAttach:");
            MyToast.show(activity, R.string.text_usb_device_detected);
            setReadyState(true, getDeviceName(device));
            notifyListview();
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            AppLog.d(TAG, "USBMonitor onConnect getDeviceName:" + getDeviceName(device));
            beginConnectUSBCamera(cameraSlotPosition, device);
        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            AppLog.d(TAG, "USBMonitor onDisconnect:");
        }

        @Override
        public void onDettach(final UsbDevice device) {
            AppLog.d(TAG, "USB_DEVICE_DETACHED:");
            MyToast.show(activity, R.string.text_usb_device_disconnected);
            setReadyState(false, getDeviceName(device));
            notifyListview();
        }

        @Override
        public void onCancel() {
            AppLog.d(TAG, "USBMonitor onCancel:");
            MyProgressDialog.closeProgressDialog();
            MyToast.show(activity, R.string.text_usb_permission_has_been_denied);
        }
    };

    public UsbDevice getUsbDevice() {
        AppLog.d(TAG, "getUsbDevice mUSBMonitor:" + mUSBMonitor);
        if (mUSBMonitor != null) {
            final List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(activity, R.xml.device_filter);
            List<UsbDevice> deviceList = mUSBMonitor.getDeviceList(filter.get(0));
//            List<UsbDevice> deviceList = mUSBMonitor.getDeviceList();
            if (deviceList == null || deviceList.size() <= 0) {
                return null;
            } else {
                return deviceList.get(0);
            }
        } else {
            return null;
        }
    }

    public void registerWifiReceiver() {
        if (wifiListener == null) {
            wifiListener = new WifiListener(activity, launchHandler);
        }
        wifiListener.registerReceiver();
    }

    public void unregisterWifiReceiver() {
        if (wifiListener != null) {
            wifiListener.unregisterReceiver();
            wifiListener = null;
        }
    }


    private String getDeviceName(UsbDevice usbDevice) {
        if (usbDevice != null) {
            return "UsbDevice_" + String.valueOf(usbDevice.getVendorId());
        } else {
            return "UsbDevice";
        }

    }

}
