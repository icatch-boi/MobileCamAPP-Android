package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.data.entity.LocalPbItemInfo;
import com.icatch.mobilecam.data.type.FileType;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by b.jiang on 2017/5/19.
 */

public class LocalMultiPbWallListAdapter extends BaseAdapter {
    private String TAG = "LocalMultiPbWallListAdapter";
    private Context context;
    private List<LocalPbItemInfo> list;
    private OperationMode curMode = OperationMode.MODE_BROWSE;
    private FileType fileType;

    public LocalMultiPbWallListAdapter(Context context, List<LocalPbItemInfo> list, FileType fileType) {
        this.context = context;
        this.list = list;
        this.fileType = fileType;
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
        if (fileType == FileType.FILE_PHOTO) {
            videoSignImageView.setVisibility(View.GONE);
        } else {
            videoSignImageView.setVisibility(View.VISIBLE);
        }
        if (list.get(position).isPanorama()) {
            mIsPanoramaSign.setVisibility(View.VISIBLE);
        } else {
            mIsPanoramaSign.setVisibility(View.GONE);
        }
        if (curMode == OperationMode.MODE_EDIT) {
            mCheckImageView.setVisibility(View.VISIBLE);
            if (list.get(position).isItemChecked) {
                mCheckImageView.setImageResource(R.drawable.ic_check_box_blue);
            } else {
                mCheckImageView.setImageResource(R.drawable.ic_check_box_blank_grey);
            }
        } else {
            mCheckImageView.setVisibility(View.GONE);
        }
        if (position == 0 || !list.get(position - 1).getFileDate().equals(curFileDate)) {
            mLayout.setVisibility(View.VISIBLE);
            mTextView.setText(list.get(position).getFileDate());
        } else {
            mLayout.setVisibility(View.GONE);
        }
        File file = list.get(position).file;
        if (file != null) {
            Glide.with(context).load(file).placeholder(R.drawable.pictures_no).into(imageView);
        }
        return view;
    }

    public void setOperationMode(OperationMode operationMode) {
        this.curMode = operationMode;
    }

    public void changeSelectionState(int position) {
        list.get(position).isItemChecked = list.get(position).isItemChecked == true ? false : true;
        this.notifyDataSetChanged();
    }

    public List<LocalPbItemInfo> getSelectedList() {
        LinkedList<LocalPbItemInfo> checkedList = new LinkedList<LocalPbItemInfo>();

        for (int ii = 0; ii < list.size(); ii++) {
            if (list.get(ii).isItemChecked) {
                checkedList.add(list.get(ii));
            }
        }
        return checkedList;
    }

    public void quitEditMode() {
        this.curMode = OperationMode.MODE_BROWSE;
        for (int ii = 0; ii < list.size(); ii++) {
            list.get(ii).isItemChecked = false;
        }
        this.notifyDataSetChanged();
    }

    public void selectAllItems() {
        for (int ii = 0; ii < list.size(); ii++) {
            list.get(ii).isItemChecked = true;
        }
        this.notifyDataSetChanged();
    }

    public void cancelAllSelections() {
        for (int ii = 0; ii < list.size(); ii++) {
            list.get(ii).isItemChecked = false;
        }
        this.notifyDataSetChanged();
    }

    public int getSelectedCount() {
        int checkedNum = 0;
        for (int ii = 0; ii < list.size(); ii++) {
            if (list.get(ii).isItemChecked) {
                checkedNum++;
            }
        }
        return checkedNum;
    }
}

