package com.icatch.mobilecam.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.icatch.mobilecam.Function.live.google.GoogleAuthTool;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.entity.GoogleToken;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.utils.fileutils.FileOper;
import com.icatch.mobilecam.utils.fileutils.FileTools;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginGoogleActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    public static final String TAG = "LoginGoogleActivity";
    private static final int RC_GET_AUTH_CODE = 9003;

    private GoogleApiClient mGoogleApiClient;
    private TextView mAuthCodeTextView;
    private String authCode = "";
    private String refreshToken = "";
    private String accessToken = "";
    private Activity activity;
    private Intent intent;
    private Handler handler = new Handler();
    String directoryPath;
    String fileName = AppInfo.FILE_GOOGLE_TOKEN;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_google);
        // Views
        mAuthCodeTextView = (TextView) findViewById(R.id.detail);
        // Button click listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
        findViewById(R.id.refresh_token_button).setOnClickListener(this);
//        findViewById(R.id.live_button).setOnClickListener(this);

        // For sample only: make sure there is a valid server client ID.
        intent = getIntent();
        validateServerClientID();

        // [START configure_signin]
        // Configure sign-in to request offline access to the user's ID, basic
        // profile, and Google Drive. The first time you request a code you will
        // be able to exchange it for an access token and refresh token, which
        // you should store. In subsequent calls, the code will only result in
        // an access token. By asking for profile access (through
        // DEFAULT_SIGN_IN) you will also get an ID Token as a result of the
        // code exchange.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestScopes(new Scope("https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/user.birthday.read https://www.googleapis.com/auth/youtube"))
                .requestServerAuthCode(GoogleAuthTool.CLIENT_ID)
                .requestEmail()
                .build();
        // [END configure_signin]

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        activity = this;
        directoryPath = activity.getExternalCacheDir() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
    }


    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {

            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    MyProgressDialog.closeProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }

    }

    private void getAuthCode() {
        // Start the retrieval process for a server auth code.  If requested, ask for a refresh
        // token.  Otherwise, only get an access token if a refresh token has been previously
        // retrieved.  Getting a new access token for an existing grant does not require
        // user consent.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_AUTH_CODE);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "signOut:onResult:" + status);
                        GoogleToken googleToken = new GoogleToken("", "");
                        FileOper.createFile(directoryPath, fileName);
                        FileTools.saveSerializable(directoryPath + fileName, googleToken);
                        updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "revokeAccess:onResult:" + status);
                        GoogleToken googleToken = new GoogleToken("", "");
                        FileOper.createFile(directoryPath, fileName);
                        FileTools.saveSerializable(directoryPath + fileName, googleToken);
                        updateUI(false);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult...");

        if (requestCode == RC_GET_AUTH_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult:GET_AUTH_CODE:success:" + result.getStatus().isSuccess());

            if (result.isSuccess()) {
                // [START get_auth_code]
                GoogleSignInAccount acct = result.getSignInAccount();
                authCode = acct.getServerAuthCode();
                // Show signed-in UI.
                mAuthCodeTextView.setText(getString(R.string.auth_code_fmt, authCode));
                if (authCode != null) {
                    MyProgressDialog.showProgressDialog(activity, "getToken...");
                    getToken(authCode);
                }
                updateUI(true);

                // TODO(user): send code to server and exchange for access/refresh/ID tokens.
                // [END get_auth_code]
            } else {
                // Show signed-out UI.
                updateUI(false);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            ((TextView) findViewById(R.id.status)).setText(R.string.signed_in);

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.status)).setText(R.string.signed_out);
            mAuthCodeTextView.setText(getString(R.string.auth_code_fmt, "null"));

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    /**
     * Validates that there is a reasonable server client ID in strings.xml, this is only needed
     * to make sure users of this sample follow the README.
     */
    private void validateServerClientID() {
        String serverClientId = GoogleAuthTool.CLIENT_ID;
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.sign_in_button) {
            getAuthCode();
        } else if (id == R.id.sign_out_button) {
            signOut();
        } else if (id == R.id.disconnect_button) {
            revokeAccess();
        } else if (id == R.id.refresh_token_button) {//                getToken(authCode);
            if (refreshToken != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d(TAG, "Start refreshAccessToken");
                            String clientId = GoogleAuthTool.CLIENT_ID;
                            String clientSecret = GoogleAuthTool.CLIENT_SECRET;
                            final String accessToken = refreshAccessToken(refreshToken, clientId, clientSecret);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String message = "RefreshToken:" + refreshToken + "\n" + "AccessToken:" + accessToken;
                                    mAuthCodeTextView.setText(message);
                                }
                            });
                            GoogleToken googleToken = new GoogleToken(accessToken, refreshToken);
                            String directoryPath = activity.getExternalCacheDir() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
                            String fileName = AppInfo.FILE_GOOGLE_TOKEN;
                            FileTools.saveSerializable(directoryPath + fileName, googleToken);
                            Log.d(TAG, "End refreshAccessToken accessToken=" + accessToken);
                        } catch (IOException e) {
                            Log.d(TAG, "refreshAccessToken IOException=");
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    public void getToken(String code) {
        Log.d(TAG, "getToken code=" + code);
        String clientId = GoogleAuthTool.CLIENT_ID;
        String clientSecret = GoogleAuthTool.CLIENT_SECRET;
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("grant_type", "authorization_code")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("redirect_uri", "")
                .add("code", code)
                .build();
        final Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                MyProgressDialog.closeProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    final String message = jsonObject.toString(5);
                    AppLog.i(TAG, message);
                    refreshToken = getRefreshTokenFromJson(jsonObject);
                    accessToken = getAccessTokenFromJson(jsonObject);

                    AppLog.d(TAG, "onResponse getToken accessToken=" + accessToken);
                    AppLog.d(TAG, "onResponse getToken refreshToken=" + refreshToken);
                    if (accessToken != null) {
//                        AppInfo.accessToken =accessToken;
//                        AppInfo.refreshToken =refreshToken;

                        GoogleToken googleToken = new GoogleToken(accessToken, refreshToken);
                        FileOper.createFile(directoryPath, fileName);
                        FileTools.saveSerializable(directoryPath + fileName, googleToken);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MyToast.show(activity, R.string.message_go_back_and_start_YouTube_live);
//                                activity.setResult(GOOGLE_LOGIN_SUCCEED, intent);
//                                finish();
                            }
                        }, 500);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String refreshAccessToken(String refreshToken, String clientId, String clientSecret) throws IOException {
        try {
            TokenResponse response =
                    new GoogleRefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                            refreshToken, clientId, clientSecret).execute();
            return response.getAccessToken();
        } catch (TokenResponseException e) {
            return null;
        }
    }

    private String getRefreshTokenFromJson(JSONObject jsonObject) {
        Log.d(TAG, "getRefreshTokenFromJson jsonObject=" + jsonObject);
        String refreshToken = "";

        if (jsonObject != null) {
            try {
                Object object = jsonObject.get("refresh_token");
                if (object instanceof String) {
                    Log.d(TAG, "object instanceof String, refreshToken=" + refreshToken);
                    refreshToken = (String) object;
                }
            } catch (JSONException e) {
                Log.d(TAG, "getRefreshTokenFromJson JSONException");
                e.printStackTrace();
            }
        }
        Log.d(TAG, "End getRefreshTokenFromJson refreshToken=" + refreshToken);
        return refreshToken;
    }

    private String getAccessTokenFromJson(JSONObject jsonObject) {
        Log.d(TAG, "getAccessTokenFromJson jsonObject=" + jsonObject);
        String accessToken = "";

        if (jsonObject != null) {
            try {
                Object object = jsonObject.get("access_token");
                if (object instanceof String) {
                    Log.d(TAG, "object instanceof String, accessToken=" + accessToken);
                    accessToken = (String) object;
                }
            } catch (JSONException e) {
                Log.d(TAG, "getAccessTokenFromJson JSONException");
                e.printStackTrace();
            }
        }
        Log.d(TAG, "End getAccessTokenFromJson access_token=" + accessToken);
        return accessToken;
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfolly, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
}
