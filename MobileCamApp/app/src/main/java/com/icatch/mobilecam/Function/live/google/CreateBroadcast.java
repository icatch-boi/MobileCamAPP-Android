/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.icatch.mobilecam.Function.live.google;


import android.content.Context;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CdnSettings;
import com.google.api.services.youtube.model.IngestionInfo;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastContentDetails;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;
import com.google.api.services.youtube.model.LiveBroadcastSnippet;
import com.google.api.services.youtube.model.LiveBroadcastStatus;
import com.google.api.services.youtube.model.LiveStream;
import com.google.api.services.youtube.model.LiveStreamListResponse;
import com.google.api.services.youtube.model.LiveStreamSnippet;
import com.google.common.collect.Lists;
import com.icatch.mobilecam.Log.AppLog;

import java.io.IOException;
import java.util.List;

/**
 * Use the YouTube Live Streaming API to insert a broadcast and a stream
 * and then bind them together. Use OAuth 2.0 to authorize the API requests.
 *
 * @author Ibrahim Ulukaya
 */
public class CreateBroadcast {

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;
    private static String push_addr;
    private static String share_addr;
    private static String TAG = "CreateBroadcast";
    private static LiveBroadcast liveBroadcast;

    /**
     * Create and insert a liveBroadcast resource.
     */
    public static String createLive(Context context, Credential credential) {
        // This OAuth 2.0 access scope allows for full read/write access to the
        // authenticated user's account.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube", "https://www.googleapis.com/auth/youtube.force-ssl");

        try {
            // Authorize the request.
            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(YoutubeCredential.HTTP_TRANSPORT, YoutubeCredential.JSON_FACTORY, credential).
                    setApplicationName("youtube-cmdline-createbroadcast-sample").build();

            // Prompt the user to enter a title for the broadcast.
            String title = getBroadcastTitle();
            AppLog.d(TAG, "You chose " + title + " for broadcast title.");

            // Create a snippet with the title and scheduled start and end
            // times for the broadcast. Currently, those times are hard-coded.
            LiveBroadcastSnippet broadcastSnippet = new LiveBroadcastSnippet();
            broadcastSnippet.setTitle(title);
            long time = System.currentTimeMillis();
            broadcastSnippet.setPublishedAt(new DateTime(time));
            broadcastSnippet.setScheduledStartTime(new DateTime(time));
            broadcastSnippet.setScheduledEndTime(new DateTime(time + 60 * 60 * 1000));

            // Set the broadcast's privacy status to "private". See:
            // https://developers.google.com/youtube/v3/live/docs/liveBroadcasts#status.privacyStatus
            LiveBroadcastStatus status = new LiveBroadcastStatus();
            status.setPrivacyStatus("public");

            LiveBroadcast broadcast = new LiveBroadcast();
            broadcast.setKind("youtube#liveBroadcast");
            broadcast.setSnippet(broadcastSnippet);
            broadcast.setStatus(status);

            LiveBroadcastContentDetails contentDetails = new LiveBroadcastContentDetails();
            contentDetails.setProjection("360");
            contentDetails.setEnableLowLatency(true);
            broadcast.setContentDetails(contentDetails);
            // Construct and execute the API request to insert the broadcast.
            YouTube.LiveBroadcasts.Insert liveBroadcastInsert = youtube.liveBroadcasts().insert("snippet,status,contentDetails",
                    broadcast);
            liveBroadcast = liveBroadcastInsert.execute();


            // Print information from the API response.
            AppLog.d(TAG, "\n================== Returned Broadcast ==================\n");
            AppLog.d(TAG, " publish - Id: " + broadcast.getId());
            AppLog.d(TAG, " publish - Title: " + broadcast.getSnippet().getTitle());
            AppLog.d(TAG, " publish - Description: " + broadcast.getSnippet().getDescription());
            AppLog.d(TAG, " publish - Published At: " + broadcast.getSnippet().getPublishedAt());
            AppLog.d(TAG, " publish - Scheduled Start Time: " + broadcast.getSnippet().getScheduledStartTime());
            AppLog.d(TAG, " publish - Scheduled End Time: " + broadcast.getSnippet().getScheduledEndTime());

            // Prompt the user to enter a title for the video stream.
            title = getStreamTitle();
            AppLog.d(TAG, "You chose " + title + " for stream title.");

            // Create a snippet with the video stream's title.
            LiveStreamSnippet streamSnippet = new LiveStreamSnippet();
            streamSnippet.setTitle(title);

            IngestionInfo ingestionInfo = new IngestionInfo();
            ingestionInfo.setStreamName("Education");
            ingestionInfo.setIngestionAddress("rtmp://a.rtmp.youtube.com/live2");
            // Define the content distribution network settings for the
            // video stream. The settings specify the stream's format and
            // ingestion type. See:
            // https://developers.google.com/youtube/v3/live/docs/liveStreams#cdn
            CdnSettings cdnSettings = new CdnSettings();
            cdnSettings.setIngestionInfo(ingestionInfo);
            cdnSettings.setFormat("720p");
            cdnSettings.setIngestionType("rtmp");

            LiveStream stream = new LiveStream();
            stream.setKind("youtube#liveStream");
            stream.setSnippet(streamSnippet);
            stream.setCdn(cdnSettings);

            // Construct and execute the API request to insert the stream.
            YouTube.LiveStreams.Insert liveStreamInsert = youtube.liveStreams().insert("snippet,cdn", stream);
            LiveStream returnedStream = liveStreamInsert.execute();

            push_addr = returnedStream.getCdn().getIngestionInfo().getIngestionAddress() + "/" + returnedStream.getCdn().getIngestionInfo().getStreamName();

            // Print information from the API response.
            AppLog.d(TAG, "\n================== Returned Stream ==================\n");
            AppLog.d(TAG, " publish - Id: " + returnedStream.getId());
            AppLog.d(TAG, " publish - Title: " + returnedStream.getSnippet().getTitle());
            AppLog.d(TAG, " publish - Description: " + returnedStream.getSnippet().getDescription());
            AppLog.d(TAG, " publish - Published At: " + returnedStream.getSnippet().getPublishedAt());
            AppLog.d(TAG, " publish - Stream push address:" + push_addr);

            // Construct and execute a request to bind the new broadcast
            // and stream.
            YouTube.LiveBroadcasts.Bind liveBroadcastBind = youtube.liveBroadcasts().bind(liveBroadcast.getId(), "id,contentDetails");
            liveBroadcastBind.setStreamId(returnedStream.getId());
            liveBroadcast = liveBroadcastBind.execute();

            share_addr = "https://www.youtube.com/watch?v=" + liveBroadcast.getId();

            // Print information from the API response.
            AppLog.d(TAG, "\n================== Returned Bound Broadcast ==================\n");
            AppLog.d(TAG, " publish - Broadcast Id: " + liveBroadcast.getId());
            AppLog.d(TAG, " publish - Bound Stream Id: " + liveBroadcast.getContentDetails().getBoundStreamId());
            AppLog.d(TAG, " publish - Stream share address: " + share_addr);
        } catch (GoogleJsonResponseException e) {
            System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }

        return push_addr;
    }

    public static String startLive() {
        String share_addr = null;
        try {
            YouTube.LiveStreams.List liveStreamRequest = youtube.liveStreams().list("id,status").setId(liveBroadcast.getContentDetails().getBoundStreamId());
            LiveStreamListResponse returnedList = liveStreamRequest.execute();
            List<LiveStream> liveStreams = returnedList.getItems();
            if (liveStreams != null && liveStreams.size() > 0) {
                LiveStream liveStream = liveStreams.get(0);
                AppLog.d(TAG, "liveStream.getStatus().getStreamStatus() =" + liveStream.getStatus().getStreamStatus());
                while (!liveStream.getStatus().getStreamStatus().equals("active")) {
                    AppLog.d(TAG, "liveStream.getStatus().getStreamStatus() =" + liveStream.getStatus().getStreamStatus());
                    Thread.sleep(5000);
                    returnedList = liveStreamRequest.execute();
                    liveStreams = returnedList.getItems();
                    liveStream = liveStreams.get(0);
                }
                AppLog.d(TAG, "publish broadcast getStatus: active");
                //the broadcast cann't transition the status from ready to live directly,
                //it must change to testing first and then live.
                YouTube.LiveBroadcasts.Transition broadCastTestingRequest = youtube.liveBroadcasts().
                        transition("testing", liveBroadcast.getId(), "id,contentDetails,status");
                LiveBroadcast testLiveBroadCast = broadCastTestingRequest.execute();
                waitTestingState();

                AppLog.d("Info", "publish broadcast - set Status: testing");
                Thread.sleep(5000);
                //set brodcast to publish
                String broadCastId = liveBroadcast.getId();
                if (broadCastId != null) startEvent(broadCastId);
                else AppLog.d("Info", "publish broadcast - liveBroadCast id :" + broadCastId);

                AppLog.d("Info", "publish broadcast - set Status: live");
                share_addr = "https://www.youtube.com/watch?v=" + testLiveBroadCast.getId();
                AppLog.d("Info", "publish broadcast - youTubeLink:" + share_addr);
                AppLog.d("Info", "publish broadcast - Broad EmbedHtml:" + testLiveBroadCast.getContentDetails().getMonitorStream().getEmbedHtml());
                AppLog.d("Info", "publish broadcast - Broad Cast Status:" + testLiveBroadCast.getStatus().getLifeCycleStatus());
            }
        } catch (GoogleJsonResponseException e) {
            AppLog.d(TAG, "GoogleJsonResponseException code: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            AppLog.d(TAG, "IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            AppLog.d(TAG, "Throwable: " + t.getMessage());
            t.printStackTrace();
        }
        Log.d("publish broadcast", "Stream share url..." + share_addr);
        return share_addr;

    }

    /*
     * Prompt the user to enter a title for a broadcast.
     */
    private static String getBroadcastTitle() throws IOException {
        String title = "360Test";
        if (title.length() < 1) {
            // Use "New Broadcast" as the default title.
            title = "New Broadcast";
        }
        return title;
    }

    /*
     * Prompt the user to enter a title for a stream.
     */
    private static String getStreamTitle() throws IOException {
        String title = "Live Stream";

        if (title.length() < 1) {
            // Use "New Stream" as the default title.
            title = "New Stream";
        }
        return title;
    }

    private static void startEvent(String broadcastId) throws IOException {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            AppLog.d("Info", "publish broadcast - set Status: testing");
        }
        AppLog.d("Info", "publish broadcast - start Event");
        YouTube.LiveBroadcasts.Transition transitionRequest = youtube.liveBroadcasts().transition("live", broadcastId, "status");
        AppLog.d("Info", "publish broadcast - start Execute");
        transitionRequest.execute();
        AppLog.d("Info", "publish broadcast - start Execute success");
    }

    public static void stopLive() throws IOException {
        AppLog.d("Info", "Start publish broadcast - stop Live");
        YouTube.LiveBroadcasts.Transition transitionRequest = youtube.liveBroadcasts().transition("complete", liveBroadcast.getId(), "status");
        transitionRequest.execute();
        AppLog.d("Info", "End publish broadcast - stop Live");
    }

    private static void waitTestingState() {
        YouTube.LiveBroadcasts.List liveBroadRequest;
        LiveBroadcast liveBroadcastReq = null;
        try {
            liveBroadRequest = youtube.liveBroadcasts().list("id,status");
            liveBroadRequest.setBroadcastStatus("all");

            LiveBroadcastListResponse liveBroadcastResponse = liveBroadRequest.execute();
            List<LiveBroadcast> returnedList = liveBroadcastResponse.getItems();
            if (returnedList != null && returnedList.size() > 0) {
                liveBroadcastReq = returnedList.get(0);
                if (liveBroadcastReq != null)
                    while (!liveBroadcastReq.getStatus().getLifeCycleStatus().equals("testing")) {
                        Thread.sleep(1000);
                        AppLog.d("Error", "publish broadcast - getLifeCycleStatus: " + liveBroadcastReq.getStatus().getLifeCycleStatus());
                        liveBroadcastResponse = liveBroadRequest.execute();
                        returnedList = liveBroadcastResponse.getItems();
                        liveBroadcastReq = returnedList.get(0);
                    }
            }
            AppLog.d("Error", "publish broadcast - getLifeCycleStatus: " + liveBroadcastReq.getStatus().getLifeCycleStatus());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            AppLog.d("Error", "publish broadcast - waitTestingState: " + e);

        }
        AppLog.d("Info", "publish broadcast - set Status Success: testing");
    }

}
