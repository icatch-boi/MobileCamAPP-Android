package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.icatch.mobilecam.data.type.FileType;
import com.icatch.mobilecam.data.entity.LocalPbItemInfo;
import com.icatch.mobilecam.Listener.OnAddAsytaskListener;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.SystemInfo.SystemInfo;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.LinkedList;
import java.util.List;

public class LocalPhotoWallGridAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {
    private String TAG = "LocalPhotoWallGridAdapter";
    private Context context;
    private List<LocalPbItemInfo> list;
    private LayoutInflater mInflater;
    private int width;
    private OperationMode operationMode = OperationMode.MODE_BROWSE;
    LruCache<String, Bitmap> mLruCache;
    private FileType fileType;
    //    private LimitQueue<LocalPhotoWallPresenter.Asytask> limitQueue;
    private OnAddAsytaskListener listener;

    public LocalPhotoWallGridAdapter(Context context, List<LocalPbItemInfo> list, int width, LruCache<String, Bitmap> mLruCache, FileType fileType,
                                     OnAddAsytaskListener listener) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.mLruCache = mLruCache;
        this.fileType = fileType;
        this.width = SystemInfo.getMetrics(context).widthPixels;
        this.listener = listener;
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
//        AppLog.d(TAG, "getView position=" + position + " convertView=" + convertView);
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_local_photo_wall_grid, parent, false);
            mViewHolder.mImageView = (ImageView) convertView.findViewById(R.id.local_photo_wall_grid_item);
            mViewHolder.mCheckImageView = (ImageView) convertView.findViewById(R.id.local_photo_wall_grid_edit);
            mViewHolder.mVideoSign = (ImageView) convertView.findViewById(R.id.video_sign);
            mViewHolder.mIsPanoramaSign = (ImageView) convertView.findViewById(R.id.is_panorama);
            ViewGroup.LayoutParams photoLayoutParams = mViewHolder.mImageView.getLayoutParams();
            photoLayoutParams.width = (width - 3 * 1) / 4;
            photoLayoutParams.height = (width - 3 * 1) / 4;

            mViewHolder.mImageView.setLayoutParams(photoLayoutParams);
            convertView.setTag(mViewHolder);
        } else {

            mViewHolder = (ViewHolder) convertView.getTag();
            ViewGroup.LayoutParams photoLayoutParams = mViewHolder.mImageView.getLayoutParams();
            photoLayoutParams.width = (width - 3 * 1) / 4;
            photoLayoutParams.height = (width - 3 * 1) / 4;
            mViewHolder.mImageView.setLayoutParams(photoLayoutParams);
        }
        if (fileType == FileType.FILE_PHOTO) {
            mViewHolder.mVideoSign.setVisibility(View.GONE);
        } else {
            mViewHolder.mVideoSign.setVisibility(View.VISIBLE);
        }
        if (list.get(position).isPanorama()) {
            mViewHolder.mIsPanoramaSign.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.mIsPanoramaSign.setVisibility(View.GONE);
        }
        if (operationMode == OperationMode.MODE_EDIT) {
            mViewHolder.mCheckImageView.setVisibility(View.VISIBLE);
//            mViewHolder.mImageView.showBorder(true);
            if (list.get(position).isItemChecked) {
                mViewHolder.mCheckImageView.setImageResource(R.drawable.ic_check_box_blue);
            } else {
                mViewHolder.mCheckImageView.setImageResource(R.drawable.ic_check_box_blank_grey);
            }
        } else {
            mViewHolder.mCheckImageView.setVisibility(View.GONE);
//            mViewHolder.mImageView.showBorder(false);
        }

        String path = list.get(position).getFilePath();
        mViewHolder.mImageView.setTag(path);

        Bitmap bitmap = mLruCache.get(path);
        if (bitmap != null) {
            mViewHolder.mImageView.setImageBitmap(bitmap);
        } else {
            if (listener != null) {
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


    @Override
    public void notifyDataSetChanged() {
        width = SystemInfo.getMetrics(context).widthPixels;
        super.notifyDataSetChanged();

    }

    public static class ViewHolder {
        private ImageView mImageView;
        private ImageView mCheckImageView;
        private ImageView mVideoSign;
        private ImageView mIsPanoramaSign;
    }

    public static class HeaderViewHolder {
        public TextView mTextView;
    }

    public void changeCheckBoxState(int position, OperationMode operationMode) {
        this.operationMode = operationMode;
        list.get(position).isItemChecked = list.get(position).isItemChecked == true ? false : true;
        this.notifyDataSetChanged();
    }

    public List<LocalPbItemInfo> getCheckedItemsList() {
        LinkedList<LocalPbItemInfo> checkedList = new LinkedList<LocalPbItemInfo>();

        for (int ii = 0; ii < list.size(); ii++) {
            if (list.get(ii).isItemChecked) {
                checkedList.add(list.get(ii));
            }
        }
        return checkedList;
    }

    public void quitEditMode() {
        this.operationMode = OperationMode.MODE_BROWSE;
        for (int ii = 0; ii < list.size(); ii++) {
            list.get(ii).isItemChecked = false;
        }
        this.notifyDataSetChanged();
    }

    public void selectAllCheckBoxState() {
        for (int ii = 0; ii < list.size(); ii++) {
            list.get(ii).isItemChecked = true;
        }
        this.notifyDataSetChanged();
    }

    public void cancalSelectAllCheckBoxState() {
        for (int ii = 0; ii < list.size(); ii++) {
            list.get(ii).isItemChecked = false;
        }
        this.notifyDataSetChanged();
    }

    public int getCheckedBoxNum() {
        int checkedNum = 0;
        for (int ii = 0; ii < list.size(); ii++) {
            if (list.get(ii).isItemChecked) {
                checkedNum++;
            }
        }
        return checkedNum;
    }
}

