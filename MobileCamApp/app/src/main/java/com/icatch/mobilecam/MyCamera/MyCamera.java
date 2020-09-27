package com.icatch.mobilecam.MyCamera;

import android.hardware.usb.UsbDevice;

import com.icatch.mobilecam.Application.PanoramaApp;
import com.icatch.mobilecam.Function.BaseProrertys;
import com.icatch.mobilecam.Function.USB.USBHost_Feature;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.SdkApi.CameraAction;
import com.icatch.mobilecam.SdkApi.CameraFixedInfo;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatch.mobilecam.SdkApi.CameraState;
import com.icatch.mobilecam.SdkApi.FileOperation;
import com.icatch.mobilecam.SdkApi.PanoramaControl;
import com.icatch.mobilecam.SdkApi.PanoramaPhotoPlayback;
import com.icatch.mobilecam.SdkApi.PanoramaPreviewPlayback;
import com.icatch.mobilecam.SdkApi.PanoramaVideoPlayback;
import com.icatch.mobilecam.data.type.TimeLapseMode;
import com.icatchtek.control.customer.ICatchCameraSession;
import com.icatchtek.pancam.customer.ICatchPancamSession;
import com.icatchtek.reliant.customer.exception.IchInvalidArgumentException;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchTransportException;
import com.icatchtek.reliant.customer.transport.ICatchINETTransport;
import com.icatchtek.reliant.customer.transport.ICatchITransport;
import com.icatchtek.reliant.customer.transport.ICatchUVCBulkTransport;

/**
 * Created by zhang yanhu C001012 on 2015/11/18 11:43.
 */
public class MyCamera {
    private final String TAG = MyCamera.class.getSimpleName();
    private CommandSession commandSession;
    private PanoramaSession panoramaSession;
    private CameraAction cameraAction;
    private CameraFixedInfo cameraFixedInfo;
    private CameraProperties cameraProperties;
    private CameraState cameraState;
    private FileOperation fileOperation;
    private PanoramaPhotoPlayback panoramaPhotoPlayback;
    private PanoramaPreviewPlayback panoramaPreviewPlayback;
    private PanoramaVideoPlayback panoramaVideoPlayback;
    private PanoramaControl panoramaControl;
    private BaseProrertys baseProrertys;
    public int timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_VIDEO;
    public String cameraName;
    private String ipAddress;
    public int mode;
    public boolean needInputPassword = true;
    public boolean isStreamReady = false;
    private int cameraType;
    private boolean isConnected = false;
    private int position;
    private UsbDevice usbDevice;
    private ICatchITransport transport;
    private boolean isLoadThumbnail = false;

    public MyCamera(int cameraType) {
        this.cameraType = cameraType;
    }

    public MyCamera(int cameraType, String cameraName) {
        this.cameraName = cameraName;
        this.cameraType = cameraType;
    }

    public synchronized void setLoadThumbnail(boolean loadThumbnail) {
        isLoadThumbnail = loadThumbnail;
    }

    public synchronized boolean isLoadThumbnail() {
        return isLoadThumbnail;
    }

    public MyCamera(int cameraType, String ssid, String ipAddress, int position, int mode) {
        this.cameraName = ssid;
        this.ipAddress = ipAddress;
        this.mode = mode;
        this.cameraType = cameraType;
        this.position = position;
    }

    public MyCamera(int cameraType, UsbDevice usbDevice, int position) {
        this.cameraType = cameraType;
        this.position = position;
        this.usbDevice = usbDevice;
        this.cameraName = "UsbDevice_" + String.valueOf(usbDevice.getVendorId());
    }

    public synchronized boolean connect(boolean enablePTPIP) {
        AppLog.d(TAG,"connect cameraType=" + cameraType + " enablePTPIP=" + enablePTPIP);
        boolean ret = false;
        commandSession = new CommandSession();
        panoramaSession = new PanoramaSession();
//        ICatchITransport transport = null;
        if (cameraType == CameraType.PANORAMA_CAMERA) {
            transport = new ICatchINETTransport(ipAddress);
        } else if (cameraType == CameraType.USB_CAMERA) {
            USBHost_Feature feature = new USBHost_Feature(PanoramaApp.getContext());
            feature.setUsbDevice(usbDevice.getVendorId(), usbDevice.getProductId());
            try {
                transport = new ICatchUVCBulkTransport(feature.getUsbDevice(), feature.getUsbDeviceConnection());
            } catch (IchInvalidArgumentException e) {
                AppLog.i(TAG, "ICatchUVCBulkTransport IchInvalidArgumentException");
                e.printStackTrace();
            } catch (IchTransportException e) {
                e.printStackTrace();
            }
            //prepare usb transport
        }
        if (transport != null) {
            AppLog.i(TAG, "transport is"+  transport.getClass().getSimpleName());
            try {
                transport.prepareTransport();
            } catch (IchTransportException e) {
                AppLog.i(TAG, "ICatchUVCBulkTransport IchTransportException");
                e.printStackTrace();
            }
            ret = commandSession.prepareSession(transport,enablePTPIP);
            if (!ret) {
                return false;
            }
//            ret = commandSession.checkWifiConnection();
//            if (!ret) {
//                return false;
//            }
            ret = panoramaSession.prepareSession(transport);
        }
        if (ret) {
            isConnected = true;
            try {
                cameraAction = new CameraAction(commandSession.getSDKSession().getControlClient(), ICatchCameraSession.getCameraAssist(transport));
            } catch (IchInvalidSessionException e) {
                e.printStackTrace();
            } catch (IchInvalidArgumentException e) {
                e.printStackTrace();
            }
            initCamera();
        }
        return ret;
    }

    public synchronized boolean disconnect() {
        if (isConnected) {
            if(transport != null){
                try {
                    transport.destroyTransport();
                } catch (IchTransportException e) {
                    e.printStackTrace();
                }
            }
            commandSession.destroySession();
            panoramaSession.destroySession();
            isConnected = false;
        }
        return true;
    }

    private boolean initCamera() {
        boolean retValue = false;
        AppLog.i(TAG, "Start initClient");
        ICatchCameraSession iCatchCommandSession = commandSession.getSDKSession();
        ICatchPancamSession iCatchPancamSession = panoramaSession.getSession();
        try {
            cameraFixedInfo = new CameraFixedInfo(iCatchCommandSession.getInfoClient());
            cameraProperties = new CameraProperties(iCatchCommandSession.getPropertyClient(), iCatchCommandSession.getControlClient());
            cameraState = new CameraState(iCatchCommandSession.getStateClient());
            fileOperation = new FileOperation(iCatchCommandSession.getPlaybackClient());
            panoramaPhotoPlayback = new PanoramaPhotoPlayback(iCatchPancamSession);
            panoramaPreviewPlayback = new PanoramaPreviewPlayback(iCatchPancamSession);
            panoramaVideoPlayback = new PanoramaVideoPlayback(iCatchPancamSession);
            panoramaControl = new PanoramaControl(iCatchPancamSession);
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        }
        baseProrertys = new BaseProrertys(cameraProperties);
        return retValue;
    }

    public int getMyMode() {
        return mode;
    }

    public CommandSession getSDKsession() {
        return commandSession;
    }

    public CameraAction getCameraAction() {
        return cameraAction;
    }

    public CameraFixedInfo getCameraFixedInfo() {
        return cameraFixedInfo;
    }

    public CameraProperties getCameraProperties() {
        return cameraProperties;
    }

    public CameraState getCameraState() {
        return cameraState;
    }

    public FileOperation getFileOperation() {
        return fileOperation;
    }

    public PanoramaPhotoPlayback getPanoramaPhotoPlayback() {
        return panoramaPhotoPlayback;
    }

    public PanoramaPreviewPlayback getPanoramaPreviewPlayback() {
        return panoramaPreviewPlayback;
    }

    public PanoramaVideoPlayback getPanoramaVideoPlayback() {
        return panoramaVideoPlayback;
    }

    public PanoramaControl getPanoramaControl() {
        return panoramaControl;
    }

    public BaseProrertys getBaseProrertys() {
        return baseProrertys;
    }

    public synchronized boolean isConnected() {
        AppLog.d(TAG, "isConnected:" + isConnected);
        return isConnected;
    }

    public String getCameraName() {
        return cameraName;
    }

    public int getPosition() {
        return position;
    }

    public UsbDevice getUsbDevice(){
        return usbDevice;
    }

    public int getCameraType(){
        return cameraType;
    }

}
