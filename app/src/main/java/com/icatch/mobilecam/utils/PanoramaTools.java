package com.icatch.mobilecam.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import com.icatch.mobilecam.Log.AppLog;

import java.io.File;

/**
 * Created by zhang yanhu C001012 on 2016/10/18 13:55.
 */
public class PanoramaTools {
    private static final String TAG = PanoramaTools.class.getSimpleName();

    public static boolean isPanorama(String imagePath){
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        AppLog.e(TAG, "Bitmap Height == " + options.outHeight);
        AppLog.e(TAG, "Bitmap Width == " + options.outWidth);
        if(options.outHeight == options.outWidth || options.outHeight *2 == options.outWidth){
            return true;
        }
        return false;
    }

    public static boolean isPanoramaForVideo(String videoPath){
//        BitmapFactory.Options options = new BitmapFactory.Options();
//
//        /**
//         * 最关键在此，把options.inJustDecodeBounds = true;
//         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
//         */
//        options.inJustDecodeBounds = true;
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options); // 此时返回的bitmap为null
//        /**
//         *options.outHeight为原始图片的高
//         */
//        AppLog.e(TAG, "Bitmap Height == " + options.outHeight);
//        AppLog.e(TAG, "Bitmap Width == " + options.outWidth);
//        if(options.outHeight == options.outWidth || options.outHeight *2 == options.outWidth){
//            return true;
//        }
//        return false;
        if(videoPath == null){
            return false;
        }

        File file = new File(videoPath);
        if(file == null || !file.exists()){
            return false;
        }

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        boolean isPanorama = false;
        try {
//            if (videoPath != null)
//            {
//                HashMap<String, String> headers = null;
//                if (headers == null)
//                {
//                    headers = new HashMap<String, String>();
//                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
//                }
//                mmr.setDataSource(videoPath, headers);
//            }
            mmr.setDataSource(videoPath);
//            String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
            AppLog.d(TAG, "isPanoramaForVideo w="+width+" h="+height);
            if(width == null || height == null){
                isPanorama = false;
            }else {
                int widthInt = Integer.parseInt(width) ;
                int heightInt = Integer.parseInt(height) ;
                if(widthInt == 0 || heightInt == 0){
                    isPanorama = false;
                }else if(heightInt *2 == widthInt){
                    isPanorama = true;
                }
            }

        } catch (Exception ex) {
            AppLog.e(TAG, "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }

        return isPanorama;
    }

    public static boolean isPanorama(long width, long height){
        AppLog.e(TAG, "isPanorama width=" + width + " height=" + height);
        if(width== 0 || height == 0){
            return false;
        }
        if(width >= height *2){
            return true;
        }
        return false;
    }
}
