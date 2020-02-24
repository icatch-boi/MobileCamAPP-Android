package com.icatch.mobilecam.Function.live.google;

/**
 * Created by zhang yanhu C001012 on 2016/12/28 14:15.
 */

import android.app.Activity;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.collect.Lists;
import com.icatch.mobilecam.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Shared class used by every sample. Contains methods for authorizing a user and caching credentials.
 */
public class YoutubeCredential {

    /**
     * Define a global instance of the HTTP transport.
     */
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Define a global instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();


    public static Credential authorize(GoogleClientSecrets clientSecrets, OAuth2AccessToken token) throws IOException {
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/plus.login",
                                                "https://www.googleapis.com/auth/user.birthday.read",
                                                "https://www.googleapis.com/auth/youtube");

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).build();

        TokenResponse response = new TokenResponse();
        response.setAccessToken(token.getAccessToken());
        response.setRefreshToken(token.getRefreshToken() );

        return flow.createAndStoreCredential(response, "user");
    }

    public static Credential authorize(GoogleClientSecrets clientSecrets, String accessToken,String refreshToken) throws IOException {
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/plus.login",
                "https://www.googleapis.com/auth/user.birthday.read",
                "https://www.googleapis.com/auth/youtube");

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).build();

        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        return flow.createAndStoreCredential(response, "user");
    }
    public static GoogleClientSecrets readClientSecrets(Activity activity){
        GoogleClientSecrets clientSecrets = null;
        InputStream inputStream =  activity.getResources().openRawResource(R.raw.client_secret);


        Reader clientSecretReader = new InputStreamReader(inputStream);
        try {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clientSecrets;
    }
}
