package com.icatch.mobilecam.Function.live.google;

/**
 * Created by zhang yanhu C001012 on 2016/12/28 16:04.
 */
public class YouTubeUrl {
    private String push_addr;
    private String share_addr;
    public YouTubeUrl(String push_addr, String share_addr) {
        this.push_addr = push_addr;
        this.share_addr = share_addr;
    }

    public String getPush_addr() {
        return push_addr;
    }

    public String getShare_addr() {
        return share_addr;
    }
}
