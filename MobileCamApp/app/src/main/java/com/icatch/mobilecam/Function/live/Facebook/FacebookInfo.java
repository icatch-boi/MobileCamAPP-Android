package com.icatch.mobilecam.Function.live.Facebook;

/**
 * Created by b.jiang on 2017/5/16.
 */

public class FacebookInfo {
    private static String streamUrl = null;
    private static String videoId = null;

    public static String getStreamUrl() {
        return streamUrl;
    }

    public static void setStreamUrl(String streamUrl) {
        FacebookInfo.streamUrl = streamUrl;
    }

    public static String getVideoId() {
        return videoId;
    }

    public static void setVideoId(String videoId) {
        FacebookInfo.videoId = videoId;
    }
}
