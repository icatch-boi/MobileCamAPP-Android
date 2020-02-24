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

import com.icatch.mobilecam.data.type.FileType;
import com.icatch.mobilecam.data.entity.LocalPbItemInfo;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.R;

import java.util.List;


public class LocalPhotoWallListAdapter extends BaseAdapter{
    private String TAG = "LocalPhotoWallListAdapter";
    private Context context;
    private List<LocalPbItemInfo> list;
    private boolean isItemChecked[];
    private OperationMode operationMode = OperationMode.MODE_BROWSE;
    LruCache<String, Bitmap> mLruCache;
    private FileType fileType;

    public LocalPhotoWallListAdapter(Context context, List<LocalPbItemInfo> list, LruCache<String, Bitmap> mLruCache, FileType fileType) {
        this.context = context;
        this.list = list;
        this.mLruCache = mLruCache;
        this.fileType = fileType;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_local_photo_wall_list, null);
        } else {
            view = convertView;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.local_photo_thumbnail_list);
        TextView mTextView = (TextView) view.findViewById(R.id.photo_wall_header);
        RelativeLayout mLayout = (RelativeLayout) view.findViewById(R.id.local_photo_wall_header_layout);
        TextView imageNameTextView = (TextView) view.findViewById(R.id.local_photo_name);
        TextView imageSizeTextView = (TextView) view.findViewById(R.id.local_photo_size);
        TextView imageDateTextView = (TextView) view.findViewById(R.id.local_photo_date);
        ImageView mCheckImageView = (ImageView) view.findViewById(R.id.local_photo_wall_list_edit);
        ImageView videoSignImageView = (ImageView) view.findViewById(R.id.video_sign);
        ImageView mIsPanoramaSign = (ImageView) view.findViewById(R.id.is_panorama);
        imageNameTextView.setText(list.get(position).getFileName());
        imageSizeTextView.setText(list.get(position).getFileSize());
        imageDateTextView.setText(list.get(position).getFileDateMMSS());
//
        if(fileType == FileType.FILE_PHOTO){
            videoSignImageView.setVisibility(View.GONE);
        }else {
            videoSignImageView.setVisibility(View.VISIBLE);
        }
        if(list.get( position ).isPanorama()){
            mIsPanoramaSign.setVisibility( View.VISIBLE );
        }else{
            mIsPanoramaSign.setVisibility(View.GONE);
        }
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
        Bitmap bitmap =  mLruCache.get(path);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);

        }else {
            imageView.setImageResource(R.drawable.pictures_no);
        }
        return view;
    }

}
