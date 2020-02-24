package com.icatch.mobilecam.MyCamera;

import android.hardware.usb.UsbDevice;

/**
 * Created by b.jiang on 2017/9/14.
 */

public class CameraManager {
    private final String TAG = CameraManager.class.getSimpleName();
    private MyCamera curCamera;
    private static CameraManager instance;

    public static synchronized CameraManager getInstance() {
        if (instance == null) {
            instance = new CameraManager();
        }

        return instance;
    }

    public MyCamera getCurCamera() {
        return curCamera;
    }

    public void setCurCamera(MyCamera curCamera) {
        this.curCamera = curCamera;
    }

    public void createCamera(int cameraType, String ssid, String ipAddress,int position, int mode) {
        curCamera = new MyCamera(cameraType, ssid, ipAddress,position, mode);
    }

    public void createUSBCamera(int cameraType, UsbDevice usbDevice, int  position) {
        curCamera = new MyCamera(cameraType, usbDevice, position);
    }
}
