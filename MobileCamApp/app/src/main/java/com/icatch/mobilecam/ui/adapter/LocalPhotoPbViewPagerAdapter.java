package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.entity.LocalPbItemInfo;
import com.icatch.mobilecam.ui.ExtendComponent.ProgressWheel;
import com.icatch.mobilecam.utils.imageloader.ImageLoaderUtil;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class LocalPhotoPbViewPagerAdapter extends PagerAdapter {
    private static final String TAG = "PhotoPbViewPagerAdapter";
    private List<LocalPbItemInfo> filesList;
    private Context context;
    private OnPhotoTapListener onPhotoTapListener;

    public LocalPhotoPbViewPagerAdapter(Context context, List<LocalPbItemInfo> filesList) {
        this.filesList = filesList;
        this.context = context;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position < filesList.size()) {
            container.removeView((View)object);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = View.inflate(context, R.layout.pb_photo_item, null);
        LocalPbItemInfo itemInfo = filesList.get(position);
        PhotoView photoView = (PhotoView) v.findViewById(R.id.photo);
        final ProgressWheel progressBar = (ProgressWheel) v.findViewById(R.id.progress_wheel);
        if(photoView != null && !itemInfo.isPanorama()){
            String path = itemInfo.file.getAbsolutePath();
//            ImageLoaderUtil.loadImageView(path, photoView);
            ImageLoaderUtil.loadLocalImageView(itemInfo.file, photoView, new ImageLoaderUtil.OnLoadListener() {
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
