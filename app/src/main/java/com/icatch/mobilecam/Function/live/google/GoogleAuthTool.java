package com.icatch.mobilecam.Function.live.google;

import android.content.Context;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.icatch.mobilecam.Log.AppLog;

import java.io.IOException;

/**
 * Created by b.jiang on 2017/4/18.
 */

public class GoogleAuthTool {
    private static String TAG = "GoogleAuthTool";
    public static final String CLIENT_SECRET="6sMzO0akSmW2GOcSyQPGkm4o";
    public static final String CLIENT_ID="168811923581-u0njo0me7v4dd2ihb1n1c5hbkk0d1v9d.apps.googleusercontent.com";


    public static String refreshAccessToken(Context context,String refreshToken) throws IOException {
        AppLog.d(TAG,"start refreshAccessToken refreshToken=" + refreshToken);
        String accessToken = null;
        try {
            TokenResponse response =
                    new GoogleRefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                            refreshToken, CLIENT_ID, CLIENT_SECRET).execute();
            accessToken =  response.getAccessToken();
        } catch (TokenResponseException e) {
            accessToken =  null;
        }
        AppLog.d(TAG,"Ens refreshAccessToken accessToken=" + accessToken);
        return accessToken;
    }
}
