package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.ui.ExtendComponent.ProgressWheel;
import com.icatch.mobilecam.utils.imageloader.ImageLoaderUtil;
import com.icatch.mobilecam.utils.imageloader.TutkUriUtil;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoPbViewPagerAdapter extends PagerAdapter {
    private static final String TAG = "PhotoPbViewPagerAdapter";
    private List<MultiPbItemInfo> filesList;
    private Context context;
    private OnPhotoTapListener onPhotoTapListener;

    public PhotoPbViewPagerAdapter(Context context, List<MultiPbItemInfo> filesList) {
        this.filesList = filesList;
        this.context = context;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        AppLog.d(TAG,"destroyItem position:" + position);
        if (position < filesList.size()) {
            container.removeView((View)object);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        AppLog.d(TAG,"instantiateItem position:" + position);
        MultiPbItemInfo itemInfo = filesList.get(position);
        View v = View.inflate(context, R.layout.pb_photo_item, null);
        PhotoView photoView = (PhotoView) v.findViewById(R.id.photo);
        SurfaceView photoSurfaceView = v.findViewById(R.id.photo_surfaceView);
//        photoSurfaceView.setVisibility(itemInfo.isPanorama() ? View.VISIBLE:View.GONE);
//        photoView.setVisibility(itemInfo.isPanorama() ? View.GONE:View.VISIBLE);
        final ProgressWheel progressBar = (ProgressWheel) v.findViewById(R.id.progress_wheel);
        if(photoView != null && !itemInfo.isPanorama()){
            String url = TutkUriUtil.getTutkOriginalUri(itemInfo.iCatchFile);
            ImageLoaderUtil.loadImageView(url, photoView, new ImageLoaderUtil.OnLoadListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.startSpinning();
                }

                @Override
                public void onLoadingFailed(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                    progressBar.stopSpinning();
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                    progressBar.stopSpinning();
                }
            });
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    if(onPhotoTapListener != null){
                        onPhotoTapListener.onPhotoTap();
                    }
                }

                @Override
                public void onOutsidePhotoTap() {

                }
            });
        }
        container.addView(v, 0);
        return v;
    }

    @Override
    public int getCount() {
        return filesList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public interface OnPhotoTapListener{
        void onPhotoTap();
    }

    public void setOnPhotoTapListener(OnPhotoTapListener onPhotoTapListener){
        this.onPhotoTapListener = onPhotoTapListener;
    }

}