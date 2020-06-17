package com.icatch.mobilecam.utils.imageloader;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

/**
 * @author b.jiang
 * @date 2019/3/8
 * @description
 */
public class ImageLoaderUtil {

    public static void loadImage(String path, final OnLoadListener listener) {
        ImageLoader.getInstance().loadImage(path, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(listener != null){
                    listener.onLoadingStarted(imageUri,null);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(listener != null){
                    listener.onLoadingFailed(imageUri,null);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(listener != null){
                    listener.onLoadingComplete(imageUri,null,loadedImage);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public static void loadLocalImage(File file, final OnLoadListener listener) {
        Uri uri = Uri.fromFile(file);
        ImageLoader.getInstance().loadImage("file://" + uri.getPath(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(listener != null){
                    listener.onLoadingStarted(imageUri,null);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(listener != null){
                    listener.onLoadingFailed(imageUri,null);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(listener != null){
                    listener.onLoadingComplete(imageUri,null,loadedImage);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    //默认加载
    public static void loadImageView(String path, ImageView mImageView) {
//        Glide.with(mContext).load(path).into(mImageView);
        ImageLoader.getInstance().displayImage(path,mImageView);
    }

    public static void loadImageView(String path, ImageView mImageView, int defaultImg) {
//        ImageLoader.getInstance().displayImage(path, mImageView, ImageLoaderConfig.getDefaultDisplayOptions(defaultImg));
        ImageLoader.getInstance().displayImage(path, mImageView);
    }


    public static void loadImageViewNoCache(String path, ImageView mImageView) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                // 设置图片在下载期间显示的图片
                .cacheInMemory(false)
                // 设置下载的图片是否缓存在SD卡中
                .cacheOnDisk(false)
                .build();
        ImageLoader.getInstance().displayImage(path,mImageView,options);
    }

    public static void loadImageView(String path, ImageView mImageView, final OnLoadListener listener){
        ImageLoader.getInstance().displayImage(path, mImageView, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(listener != null){
                    listener.onLoadingStarted(imageUri, view);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(listener != null){
                    listener.onLoadingFailed(imageUri, view);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(listener != null){
                    listener.onLoadingComplete(imageUri, view,loadedImage);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public static void loadLocalImageView(File file, ImageView mImageView, final OnLoadListener listener){
        Uri uri = Uri.fromFile(file);
        ImageLoader.getInstance().displayImage("file://" + uri.getPath(), mImageView, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(listener != null){
                    listener.onLoadingStarted(imageUri, view);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(listener != null){
                    listener.onLoadingFailed(imageUri, view);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(listener != null){
                    listener.onLoadingComplete(imageUri, view,loadedImage);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }


    public static void loadImageView(String path, ImageView mImageView, int defaultImg, final OnLoadListener listener) {
        ImageLoader.getInstance().displayImage(path, mImageView, ImageLoaderConfig.getDefaultDisplayOptions(defaultImg), new ImageLoadingListener() {

                        @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(listener != null){
                    listener.onLoadingStarted(imageUri, view);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(listener != null){
                    listener.onLoadingFailed(imageUri, view);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(listener != null){
                    listener.onLoadingComplete(imageUri, view,loadedImage);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public static void loadImageView(String path, ImageView mImageView, int defaultImg, int delayInMillis, final OnLoadListener listener) {
        ImageLoader.getInstance().displayImage(path, mImageView, getDelayDisplayOptions(defaultImg, delayInMillis), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(listener != null){
                    listener.onLoadingStarted(imageUri, view);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(listener != null){
                    listener.onLoadingFailed(imageUri, view);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(listener != null){
                    listener.onLoadingComplete(imageUri, view,loadedImage);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public static DisplayImageOptions getDefaultDisplayOptions(int defaultImg) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImg)
                .showImageForEmptyUri(defaultImg)
                .showImageOnFail(defaultImg)
                .considerExifParams(true)
                .build();
        return options;
    }

    public static DisplayImageOptions getDelayDisplayOptions(int defaultImg, int delayInMillis) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImg)
                .showImageForEmptyUri(defaultImg)
                .showImageOnFail(defaultImg)
                .cacheInMemory(true)
                .considerExifParams(true)
                .delayBeforeLoading(delayInMillis)
                .build();
        return options;
    }

    public  interface OnLoadListener {
        void onLoadingStarted(String imageUri, View view);

        void onLoadingFailed(String imageUri, View view);

        void onLoadingComplete(String imageUri, View view, Bitmap loadedImage);
    }

    public static void stopLoad(){
        ImageLoader.getInstance().stop();
    }
}
