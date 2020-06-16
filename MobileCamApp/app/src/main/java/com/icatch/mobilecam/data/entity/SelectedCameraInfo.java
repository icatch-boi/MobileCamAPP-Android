package com.icatch.mobilecam.data.entity;

/**
 * Created by zhang yanhu C001012 on 2015/11/24 15:13.
 */
public class SelectedCameraInfo {
    public String cameraName;
    public String cameraIp;
    public int cameraMode;
    public String password;
    public String uid;

    public SelectedCameraInfo(String cameraName, String cameraIp, int cameraMode, String uid) {
        this.cameraName = cameraName;
        this.cameraIp = cameraIp;
        this.cameraMode = cameraMode;
        this.uid = uid;
    }
}
