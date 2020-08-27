package com.icatch.mobilecam.MyCamera;

import android.hardware.usb.UsbDevice;

/**
 * Created by b.jiang on 2017/9/14.
 */

public class CameraManager {
    private final String TAG = CameraManager.class.getSimpleName();
    private MyCamera curCamera;
    private static CameraManager instance;

    public static CameraManager getInstance() {
//        if (instance == null) {
//            instance = new CameraManager();
//        }
        if (instance == null) {
            synchronized (CameraManager.class) {
                if (instance == null) {
                    instance = new CameraManager();
                }
            }
        }
        return instance;
    }

    public MyCamera getCurCamera() {
        return curCamera;
    }

    public void setCurCamera(MyCamera curCamera) {
        this.curCamera = curCamera;
    }

    public synchronized MyCamera createCamera(int cameraType, String ssid, String ipAddress,int position, int mode) {
        this.curCamera = new MyCamera(cameraType, ssid, ipAddress,position, mode);
        return this.curCamera;
    }

    public synchronized MyCamera createUSBCamera(int cameraType, UsbDevice usbDevice, int  position) {
        this.curCamera = new MyCamera(cameraType, usbDevice, position);
        return this.curCamera;
    }
}
