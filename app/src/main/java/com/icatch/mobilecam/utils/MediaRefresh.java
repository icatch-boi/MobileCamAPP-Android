package com.icatch.mobilecam.utils;

import java.io.File;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;

import com.icatch.mobilecam.Application.PanoramaApp;
import com.icatch.mobilecam.Log.AppLog;


/**
 * Added by zhangyanhu C01012,2014-7-23
 */
public class MediaRefresh {

    private static final String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";
    private static final String TAG = "MediaRefresh";

    public static void scanDirAsync(Context ctx, String dir) {
        Intent scanIntent = new Intent(ACTION_MEDIA_SCANNER_SCAN_DIR);
        scanIntent.setData(Uri.fromFile(new File(dir)));
        ctx.sendBroadcast(scanIntent);
    }

    public static void scanFileAsync(Context ctx, String filename)// filename是我们的文件全名，包括后缀
    {
        AppLog.d(TAG, "scanFileAsync");
        MediaScannerConnection.scanFile(ctx, new String[]{filename}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
            }
        });
    }

    public static void addMediaToDB(Context ctx, String filePath, String fileType) {
        AppLog.d(TAG, "addMediaToDB filePath=" + filePath);
        AppLog.d(TAG, "addMediaToDB fileType=" + fileType);
        File videofile = new File(filePath);
        ContentValues values = new ContentValues(5);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Video.Media.TITLE, videofile.getName());
        values.put(MediaStore.Video.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Video.Media.MIME_TYPE, fileType);
        values.put(MediaStore.Video.Media.DATA, videofile.getAbsolutePath());
        values.put(MediaStore.Video.Media.ALBUM, "");
        ContentResolver contentResolver = ctx.getContentResolver();
        Uri base = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        contentResolver.insert(base, values);
    }

    public static void notifySystemToScan(String filePath, Context context) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    public static void notifySystemToScan(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        Context context = PanoramaApp.getContext();
        if (context != null) {
            context.sendBroadcast(intent);
        }
    }
}
