package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;

import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.ui.ExtendComponent.ProgressWheel;
import com.icatch.mobilecam.R;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoPbViewPagerAdapter extends PagerAdapter {
    private static final String TAG = "PhotoPbViewPagerAdapter";
    private List<MultiPbItemInfo> filesList;
    private Context context;
    LruCache<Integer, Bitmap> mLruCache;
    private List<View> viewList;
    private OnPhotoTapListener onPhotoTapListener;

    public PhotoPbViewPagerAdapter(Context context, List<MultiPbItemInfo> filesList,List<View> viewList,LruCache<Integer, Bitmap> mLruCache) {
        this.filesList = filesList;
        this.context = context;
        this.viewList = viewList;
        this.mLruCache = mLruCache;

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
//        PhotoView photoView = (PhotoView) v.findViewById(R.id.photo);
//        Bitmap bitmap = mLruCache.get(filesList.get(position).getFileHandle());
//        AppLog.d("", "instantiateItem viewpager bitmap=" + bitmap  + " photoView=" + photoView);
//        if(bitmap != null && photoView != null){
//            photoView.setImageBitmap(bitmap);
//        }

        Bitmap bitmap = mLruCache.get(filesList.get(position).getFileHandle());
        PhotoView photoView = (PhotoView) v.findViewById(R.id.photo);
        ProgressWheel progressBar = (ProgressWheel) v.findViewById(R.id.progress_wheel);
        if(photoView != null){
            photoView.setImageBitmap(bitmap);
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
        viewList.set(position,v);
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