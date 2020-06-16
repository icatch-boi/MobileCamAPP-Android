package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icatch.mobilecam.data.entity.LocalPbItemInfo;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.R;

import java.util.List;

public class LocalVideoWallListAdapter extends BaseAdapter{
    private String TAG = "LocalPhotoWallListAdapter";
    private Context context;
    private List<LocalPbItemInfo> list;
    private LayoutInflater mInflater;
    private boolean isItemChecked[];
    private OperationMode operationMode = OperationMode.MODE_BROWSE;
    LruCache<String, Bitmap> mLruCache;

    public LocalVideoWallListAdapter(Context context, List<LocalPbItemInfo> list, LruCache<String, Bitmap> mLruCache) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.mLruCache = mLruCache;
        this.isItemChecked = new boolean[list.size()];
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
        String path = list.get(position).getFilePath();
        String curFileDate = list.get(position).getFileDate();
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_local_video_wall_list, null);
        } else {
            view = convertView;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.local_video_thumbnail_list);
        TextView mTextView = (TextView) view.findViewById(R.id.photo_wall_header);
        RelativeLayout mLayout = (RelativeLayout) view.findViewById(R.id.local_video_wall_header_layout);
        TextView videoNameTextView = (TextView) view.findViewById(R.id.local_video_name);
        TextView videoSizeTextView = (TextView) view.findViewById(R.id.local_video_size);
        TextView videoDateTextView = (TextView) view.findViewById(R.id.local_video_date);
        ImageView mCheckImageView = (ImageView) view.findViewById(R.id.local_video_wall_list_edit);
        videoNameTextView.setText(list.get(position).getFileName());
        videoSizeTextView.setText(list.get(position).getFileSize());
        videoDateTextView.setText(list.get(position).getFileDateMMSS());
//
        // 为该ImageView设置一个Tag,防止图片错位
        //view.setTag(path);
        if(operationMode == OperationMode.MODE_EDIT){
            mCheckImageView.setVisibility(View.VISIBLE);
            if (isItemChecked[position]){
                mCheckImageView.setImageResource(R.drawable.ic_check_box_blue);
            }else {
                mCheckImageView.setImageResource(R.drawable.ic_check_box_blank_grey);
            }
        }else {
            mCheckImageView.setVisibility(View.GONE);
        }
        imageView.setTag(path);
        if (position == 0 || !list.get(position - 1).getFileDate().equals(curFileDate)) {
            mLayout.setVisibility(View.VISIBLE);
            mTextView.setText(list.get(position).getFileDate());
        } else {
            mLayout.setVisibility(View.GONE);
        }
        Bitmap bitmap = mLruCache.get(path);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);

        }else {
            imageView.setImageResource(R.drawable.pictures_no);
        }
        return view;
    }
}
