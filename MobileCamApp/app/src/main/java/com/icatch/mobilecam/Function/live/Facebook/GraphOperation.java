package com.icatch.mobilecam.Function.live.Facebook;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.icatch.mobilecam.Log.AppLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by b.jiang on 2016/12/13.
 */

public class GraphOperation {
    private static final String TAG = "GraphOperation";

    public void getPageAccessToken(final AccessToken accessToken, String graphPath, final RequestCallback callback) {
        final GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
//                "/1203037683120998",
                graphPath,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Insert your code here
                        AppLog.d(TAG, " response=" + response);
                        JSONObject jsonObject = response.getJSONObject();
                        AppLog.d(TAG, " response.getJSONObject()=" + jsonObject);
                        AppLog.d(TAG, " response.getJSONArray()=" + response.getJSONArray());
                        AppLog.d(TAG, " response.getRequest()=" + response.getRequest());
                        AppLog.d(TAG, " response.getRequest().getAccessToken()=" + response.getRequest().getAccessToken());
//                        response.getRequest().getAccessToken();


                        if (jsonObject != null) {
                            try {
                                AppLog.d(TAG, " mjsonObject=" + jsonObject.get("access_token"));
                                Object object = jsonObject.get("access_token");
                                if (object instanceof AccessToken) {
                                    AppLog.d(TAG, "object instanceof AccessToken");
                                } else if (object instanceof String) {
                                    AppLog.d(TAG, "object instanceof String=" + object);
                                    AccessToken accessToken = create((String) object);
                                } else {
                                    AppLog.d(TAG, "object instanceof other");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "access_token");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static void getStreamByPageToken(AccessToken accessToken, final String pageName, final RequestCallback callback) {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/me/accounts",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        AppLog.d(TAG, " response=" + response);
                        AppLog.d(TAG, " response.getJSONObject()=" + response.getJSONObject());
                        AppLog.d(TAG, " response.getJSONArray()=" + response.getJSONArray());
                        JSONArray jsonArray = null;
                        JSONObject jsonObject = response.getJSONObject();
                        if (jsonObject == null) {
                            callback.onError("jsonObject is null");
                            return;
                        }
                        try {
                            jsonArray = (JSONArray) jsonObject.get("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        AppLog.d(TAG, " jsonArray=" + jsonArray);
                        jsonObject = null;
                        String accToken = null;
                        String id = null;
                        for (int ii = 0; ii < jsonArray.length(); ii++) {
                            try {
                                jsonObject = (JSONObject) jsonArray.get(ii);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
//                                if(jsonObject.get("name").equals("Test My live")){
                                if (jsonObject.get("name").equals(pageName)) {
                                    accToken = (String) jsonObject.get("access_token");
                                    id = (String) jsonObject.get("id");
                                    AppLog.d(TAG, " accToken=" + accToken);
                                    AppLog.d(TAG, " id=" + id);
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (accToken != null || id != null) {
                            getStreamurl(create(accToken), id, callback);
                        } else {
                            callback.onError("Did not find the " + pageName + " cam home page");
                        }
                        // Insert your code here
                    }
                });

        request.executeAsync();
    }

    public static void getStreamByToken(final AccessToken accessToken, final RequestCallback callback) {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/me?fields=id,name",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        AppLog.d(TAG, " response=" + response);
                        AppLog.d(TAG, " response.getJSONObject()=" + response.getJSONObject());
                        AppLog.d(TAG, " response.getJSONArray()=" + response.getJSONArray());
                        //JSONArray jsonArray = null;
                        String id = null;
                        JSONObject jsonObject = response.getJSONObject();
                        if (jsonObject == null) {
                            callback.onError("jsonObject is null");
                            return;
                        }
                        try {
                            id = (String) jsonObject.get("id");
                        } catch (JSONException e) {
                            AppLog.d(TAG, "get id exception");
                            e.printStackTrace();
                            callback.onError("get id exception");
                            return;
                        }
                        AppLog.d(TAG, " id=" + id);
                        if (id != null) {
                            getStreamurl(accessToken, id, callback);
                        } else {
                            callback.onError("Did not find the user id ");
                        }
                        // Insert your code here
                    }
                });

        request.executeAsync();
    }

    private static void getStreamurl(AccessToken accessToken, String id, final RequestCallback callback) {
        GraphRequest request = null;
//        GraphRequest.newGraphPathRequest()
        String graphPath = "/" + id + "/live_videos";
        AppLog.d(TAG, "getStreamurl graphPath=" + graphPath);
        try {
            request = GraphRequest.newPostRequest(
                    accessToken,
//                    "/1203037683120998/live_videos",
                    graphPath,
                    new JSONObject("{}"),
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            // Insert your code here
                            AppLog.d(TAG, " response=" + response);
                            JSONObject jsonObject = response.getJSONObject();
                            AppLog.d(TAG, " response.getJSONObject()=" + jsonObject);
                            try {
                                String streamUrl = jsonObject.get("stream_url").toString();
                                String videoId = jsonObject.get("id").toString();
                                AppLog.d(TAG, " jsonObject stream_url=" + streamUrl);
                                AppLog.d(TAG, " jsonObject id=" + videoId);
                                FacebookInfo.setStreamUrl(streamUrl);
                                FacebookInfo.setVideoId(videoId);
                                callback.onCompleted(streamUrl, videoId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id,name,link");
//        Bundle bundle = new Bundle();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss	");
        String description = sdf.format(date) + " start live";
        parameters.putString("title", "My Test 360");
        parameters.putString("description", description);
        parameters.putString("privacy", "{\"value\":\"EVERYONE\"}");
        parameters.putBoolean("save_vod", false);
        parameters.putString("status", "LIVE_NOW");
        parameters.putString("stream_type", "AMBIENT");
        parameters.putBoolean("is_spherical", true);
        request.setParameters(parameters);
        request.executeAsync();
    }

    private static AccessToken create(String accessTokenStr) {
        AccessToken curAccessToken = AccessToken.getCurrentAccessToken();
        AppLog.d(TAG, "create AccessToken curAccessToken=" + curAccessToken);
        AccessToken accessToken = new AccessToken(accessTokenStr, curAccessToken.getApplicationId(),
                curAccessToken.getUserId(), curAccessToken.getPermissions(), curAccessToken.getDeclinedPermissions(), null, null, null);
        AppLog.d(TAG, "create AccessToken accessToken=" + accessToken);
        return accessToken;
    }

    public interface RequestCallback {
        void onCompleted(String url, String videoId);

        void onError(String errorInfo);
    }

    public static void endLiveStream(AccessToken accessToken, String videoId) {
        AppLog.d(TAG, "start endLiveStream");
        GraphRequest request = null;
        try {
            request = GraphRequest.newPostRequest(
                    accessToken,
                    "/" + videoId,
                    //                new JSONObject("{\"end_live_video\":\"true\"}"),
                    new JSONObject("{}"),
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            // Insert your code here
                            AppLog.d(TAG, "endLiveStream onCompleted");
                        }
                    });
        } catch (JSONException e) {
            AppLog.d(TAG, "endLiveStream JSONException");
            e.printStackTrace();
        }
        Bundle parameters = new Bundle();
        parameters.putBoolean("end_live_video", true);
        request.setParameters(parameters);
        request.executeAsync();
    }
}
