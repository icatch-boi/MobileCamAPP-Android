package com.icatch.mobilecam.data.entity;

/**
 * Created by zhang yanhu C001012 on 2015/12/11 17:18.
 */
public class StreamInfo {
    public String mediaCodecType;
    public int width;
    public int height;
    public int bitrate;
    public int fps;

    public StreamInfo(String mediaCodecType,int width,int height,int bitrate,int fps){
        this.mediaCodecType = mediaCodecType;
        this.width = width;
        this.height =height;
        this.bitrate = bitrate;
        this.fps = fps;
    }

    public StreamInfo(){

    }
}
