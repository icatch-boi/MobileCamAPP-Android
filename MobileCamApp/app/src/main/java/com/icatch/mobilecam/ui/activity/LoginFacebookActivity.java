package com.icatch.mobilecam.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.R;

import java.util.Arrays;

import static com.icatch.mobilecam.data.Message.AppMessage.FACEBOOK_LOGIN_SUCCEED;


public class LoginFacebookActivity extends Activity {
    private String TAG = "LoginFacebookActivity";
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private Intent intent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_facebook);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPublishPermissions(Arrays.asList("manage_pages", "publish_pages","publish_actions"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                MyToast.show(LoginFacebookActivity.this,"accessToken=" + accessToken);
                AppLog.d(TAG,"FacebookCallback onSuccess");
                AppLog.d(TAG,"FacebookCallback getPermissions" + loginResult.getAccessToken().getPermissions());
                AppLog.d(TAG,"FacebookCallback loginResult.getAccessToken().getDeclinedPermissions()=" + loginResult.getAccessToken().getDeclinedPermissions());
                AppLog.d(TAG,"FacebookCallback loginResult.getRecentlyGrantedPermissions()=" + loginResult.getRecentlyGrantedPermissions());
                AppLog.d(TAG,"FacebookCallback loginResult.getRecentlyDeniedPermissions()=" + loginResult.getRecentlyDeniedPermissions());
                setResult(FACEBOOK_LOGIN_SUCCEED, intent);
                finish();
            }

            @Override
            public void onCancel() {
                MyToast.show(LoginFacebookActivity.this,R.string.message_login_cancel);
            }

            @Override
            public void onError(FacebookException error) {
                MyToast.show(LoginFacebookActivity.this,R.string.message_login_error);
            }
        });
        intent = getIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppLog.d(TAG, "request: " + requestCode + ",  resultCode: " + resultCode
                + ", data: " + data.toString());
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
