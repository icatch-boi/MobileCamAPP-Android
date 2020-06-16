package com.icatch.mobilecam.data.entity;

/**
 * Created by zhang yanhu C001012 on 2015/11/23 19:32.
 */
public class SearchedCameraInfo {
    public String cameraName;
    public String cameraIp;
    public int cameraMode;
    public String uid;

    public SearchedCameraInfo(String cameraName,String cameraIp, int cameraMode,String uid) {
        this.cameraName = cameraName;
        this.cameraIp = cameraIp;
        this.cameraMode = cameraMode;
        this.uid = uid;
    }
}
