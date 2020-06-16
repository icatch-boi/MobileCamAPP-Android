package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.icatch.mobilecam.Listener.OnAddAsytaskListener;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.icatch.mobilecam.data.entity.LocalPbItemInfo;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.R;

import java.util.List;

public class LocalVideoWallGridAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {
    private String TAG = "LocalVideoWallGridAdapter";
    private Context context;
    private List<LocalPbItemInfo> list;
    private LayoutInflater mInflater;
    private int width;
    private boolean isItemChecked[];
    private OperationMode operationMode = OperationMode.MODE_BROWSE;
    LruCache<String, Bitmap> mLruCache;
    private OnAddAsytaskListener listener;

    public LocalVideoWallGridAdapter(Context context, List<LocalPbItemInfo> list, int width, LruCache<String, Bitmap> mLruCache, OnAddAsytaskListener listener){
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.width = width;
        this.mLruCache = mLruCache;
        this.isItemChecked = new boolean[list.size()];
        this.listener = listener;
        for (int ii = 0; ii < list.size();ii ++){
            isItemChecked[ii] = false;
        }
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.iten_local_video_wall_grid, parent, false);
            mViewHolder.mImageView = (ImageView) convertView.findViewById(R.id.local_video_thumbnail_grid);
            mViewHolder.mCheckImageView = (ImageView) convertView.findViewById(R.id.local_video_wall_grid_edit);
            ViewGroup.LayoutParams photoLayoutParams = mViewHolder.mImageView.getLayoutParams();
            photoLayoutParams.width = (width-3*1)/4;
            photoLayoutParams.height = (width-3*1)/4;

            mViewHolder.mImageView.setLayoutParams(photoLayoutParams);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        if(operationMode == OperationMode.MODE_EDIT){
            mViewHolder.mCheckImageView.setVisibility(View.VISIBLE);
//            mViewHolder.mImageView.showBorder(true);
            if (isItemChecked[position]){
                mViewHolder.mCheckImageView.setImageResource(R.drawable.ic_check_box_blue);
            }else {
                mViewHolder.mCheckImageView.setImageResource(R.drawable.ic_check_box_blank_grey);
            }
        }else {
            mViewHolder.mCheckImageView.setVisibility(View.GONE);
//            mViewHolder.mImageView.showBorder(false);
        }

        String path = list.get(position).getFilePath();
        mViewHolder.mImageView.setTag(path);

        Bitmap bitmap = mLruCache.get(path);
        if(bitmap != null){
            mViewHolder.mImageView.setImageBitmap(bitmap);

        }else {
            if(listener != null){
                listener.addAsytask(position);
            }
            mViewHolder.mImageView.setImageResource(R.drawable.pictures_no);
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int i) {
        return list.get(i).section;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder mHeaderHolder;
        if (convertView == null) {
            mHeaderHolder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.item_local_wall_grid_header, parent, false);
            mHeaderHolder.mTextView = (TextView) convertView.findViewById(R.id.photo_wall_header);
            convertView.setTag(mHeaderHolder);
        } else {
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }
        mHeaderHolder.mTextView.setText(list.get(position).getFileDate());

        return convertView;
    }

    public static class ViewHolder {
        public ImageView mImageView;
        public ImageView mCheckImageView;
    }

    public static class HeaderViewHolder {
        public TextView mTextView;
    }

    public void changeCheckBoxState(int post,OperationMode operationMode)
    {
        this.operationMode = operationMode;
        Log.d(TAG, "changeSelectionState positon=" + post + "state = " + isItemChecked[post]);
        isItemChecked[post] = isItemChecked[post]==true?false:true;
        this.notifyDataSetChanged();
        Log.d(TAG, "end changeSelectionState positon=" + post + "state = " + isItemChecked[post]);
    }

    public boolean[] getCheckBoxState(){
        return isItemChecked;
    }

    public void initCheckBoxState(){
        this.operationMode = OperationMode.MODE_BROWSE;
        if(isItemChecked == null){
            isItemChecked = new boolean[list.size()];
        }
        for (int ii = 0; ii < list.size();ii ++){
            isItemChecked[ii] = false;
        }
        this.notifyDataSetChanged();
    }
    public void selectAllCheckBoxState(){
        if(isItemChecked == null){
            isItemChecked = new boolean[list.size()];
        }
        for (int ii = 0; ii < list.size();ii ++){
            isItemChecked[ii] = true;
        }
        this.notifyDataSetChanged();
    }
    public void cancalSelectAllCheckBoxState(){
        if(isItemChecked == null){
            isItemChecked = new boolean[list.size()];
        }
        for (int ii = 0; ii < list.size();ii ++){
            isItemChecked[ii] = false;
        }
        this.notifyDataSetChanged();
    }

    public int getCheckedBoxNum(){
        int checkedNum = 0;
        for (int ii = 0; ii < list.size();ii ++){
            if(isItemChecked[ii]){
                checkedNum ++;
            }
        }
        return checkedNum;
    }

    public void getCheckedItemsList() {
    }
}
