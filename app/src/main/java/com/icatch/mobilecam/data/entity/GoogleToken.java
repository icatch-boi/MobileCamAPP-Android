package com.icatch.mobilecam.data.entity;

import java.io.Serializable;

/**
 * Created by b.jiang on 2017/4/18.
 */

public class GoogleToken implements Serializable {
    private String accessToken = null;
    private String refreshToken = null;

    public GoogleToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setCurrentAccessToken(String token) {
        accessToken = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setCurrentRefreshToken(String token) {
        refreshToken = token;
    }
}
