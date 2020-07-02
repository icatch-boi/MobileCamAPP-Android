package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.data.SystemInfo.SystemInfo;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.data.type.FileType;
import com.icatch.mobilecam.utils.imageloader.ImageLoaderUtil;
import com.icatch.mobilecam.utils.imageloader.TutkUriUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * @author b.jiang
 * @date 2020/1/8
 * @description
 */
public class MultiPbRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // 网格布局
    public static final int TYPE_GRID = 1;
    // 列表布局
    public static final int TYPE_LIST = 2;
    // 快速预览布局
    public static final int TYPE_QUICK_LIST = 3;
    // 脚布局
    private final int TYPE_FOOTER = 4;

    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;
    private int curViewType = TYPE_GRID;
    // 当前加载状态，默认为加载完成
    private int loadState = 2;

    private List<MultiPbItemInfo> list;
    private OperationMode operationMode = OperationMode.MODE_BROWSE;
    private FileType fileType;
    private int width;

    public MultiPbRecyclerViewAdapter(Context context, List<MultiPbItemInfo> list, FileType fileType) {
        this.list = list;
        this.fileType = fileType;
        this.width = SystemInfo.getMetrics(context).widthPixels;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为FooterView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return curViewType;
        }
    }

    public void setCurViewType(int curViewType) {
        this.curViewType = curViewType;
//        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 通过判断显示类型，来创建不同的View
        if (viewType == TYPE_GRID) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pb_recyclerview_grid, parent, false);
            return new RecyclerViewGridHolder(view);

        } else if (viewType == TYPE_LIST || viewType == TYPE_QUICK_LIST) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pb_recyclerview_list, parent, false);
            return new RecyclerViewListHolder(view, viewType == TYPE_LIST ? true : false);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_refresh_footer, parent, false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecyclerViewGridHolder) {
            RecyclerViewGridHolder gridHolder = (RecyclerViewGridHolder) holder;
            gridHolder.videoSignImageView.setVisibility(fileType == FileType.FILE_PHOTO ? View.GONE : View.VISIBLE);
            gridHolder.mIsPanoramaSign.setVisibility(list.get(position).isPanorama() ? View.VISIBLE : View.GONE);
            gridHolder.mCheckImageView.setVisibility(operationMode == OperationMode.MODE_EDIT ? View.VISIBLE : View.GONE);
            if (operationMode == OperationMode.MODE_EDIT) {
                gridHolder.mCheckImageView.setImageResource(list.get(position).isItemChecked ? R.drawable.ic_check_box_blue : R.drawable.ic_check_box_blank_grey);
            }
            ViewGroup.LayoutParams photoLayoutParams = gridHolder.mImageView.getLayoutParams();
            photoLayoutParams.width = (width - 3 * 1) / 4;
            photoLayoutParams.height = (width - 3 * 1) / 4;
            gridHolder.mImageView.setLayoutParams(photoLayoutParams);
            MultiPbItemInfo itemInfo = list.get(position);
            if (itemInfo != null) {
                String uri = TutkUriUtil.getTutkThumbnailUri(itemInfo.iCatchFile);
                ImageLoaderUtil.loadImageView(uri, gridHolder.mImageView, R.drawable.pictures_no);
            }
        } else if (holder instanceof RecyclerViewListHolder) {
            RecyclerViewListHolder listHolder = (RecyclerViewListHolder) holder;
            listHolder.imageNameTextView.setText(list.get(position).getFileName());
            listHolder.imageSizeTextView.setText(list.get(position).getFileSize());
            listHolder.imageDateTextView.setText(list.get(position).getFileDateMMSS());
            listHolder.videoSignImageView.setVisibility(fileType == FileType.FILE_PHOTO ? View.GONE : View.VISIBLE);
            listHolder.imageDurationView.setVisibility(fileType == FileType.FILE_PHOTO ? View.GONE : View.VISIBLE);
            if(fileType != FileType.FILE_PHOTO){
                listHolder.imageDurationView.setText(list.get(position).getFileDuration());
            }
            listHolder.mIsPanoramaSign.setVisibility(list.get(position).isPanorama() ? View.VISIBLE : View.GONE);
            listHolder.mCheckImageView.setVisibility(operationMode == OperationMode.MODE_EDIT ? View.VISIBLE : View.GONE);

            if (operationMode == OperationMode.MODE_EDIT) {
                listHolder.mCheckImageView.setImageResource(list.get(position).isItemChecked ? R.drawable.ic_check_box_blue : R.drawable.ic_check_box_blank_grey);
            }
            listHolder.thumbnailLayout.setVisibility(listHolder.showThumbnail ? View.VISIBLE : View.GONE);
            if (listHolder.showThumbnail) {
                MultiPbItemInfo itemInfo = list.get(position);
                if (itemInfo != null) {
                    String uri = TutkUriUtil.getTutkThumbnailUri(itemInfo.iCatchFile);
                    ImageLoaderUtil.loadImageView(uri, listHolder.imageView, R.drawable.pictures_no);
                }
            }
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (loadState) {
                case LOADING: // 正在加载
                    footViewHolder.pbLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_COMPLETE: // 加载完成
                    footViewHolder.pbLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_END: // 加载到底
                    footViewHolder.pbLoading.setVisibility(View.GONE);
                    footViewHolder.tvLoading.setVisibility(View.GONE);
                    footViewHolder.llEnd.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 如果当前是footer的位置，那么该item占据2个单元格，正常情况下占据1个单元格
//                    return getItemViewType(position) == TYPE_FOOTER ? gridManager.getSpanCount() : 1;
                    return getItemViewType(position) == TYPE_FOOTER ? 4 : 1;
                }
            });
        }
    }

    private class RecyclerViewGridHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        ImageView mCheckImageView;
        ImageView videoSignImageView;
        ImageView mIsPanoramaSign;

        RecyclerViewGridHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.local_photo_wall_grid_item);
            mCheckImageView = (ImageView) itemView.findViewById(R.id.local_photo_wall_grid_edit);
            videoSignImageView = (ImageView) itemView.findViewById(R.id.video_sign);
            mIsPanoramaSign = (ImageView) itemView.findViewById(R.id.is_panorama);
        }
    }

    private class RecyclerViewListHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView imageNameTextView;
        TextView imageSizeTextView;
        TextView imageDateTextView;
        ImageView mCheckImageView;
        ImageView videoSignImageView;
        ImageView mIsPanoramaSign;
        FrameLayout thumbnailLayout;
        TextView imageDurationView;
        boolean showThumbnail;

        RecyclerViewListHolder(View itemView, boolean showThumbnail) {
            super(itemView);
            this.showThumbnail = showThumbnail;
            imageView = (ImageView) itemView.findViewById(R.id.local_photo_thumbnail_list);
            imageNameTextView = (TextView) itemView.findViewById(R.id.local_photo_name);
            imageSizeTextView = (TextView) itemView.findViewById(R.id.local_photo_size);
            imageDateTextView = (TextView) itemView.findViewById(R.id.local_photo_date);
            mCheckImageView = (ImageView) itemView.findViewById(R.id.local_photo_wall_list_edit);
            videoSignImageView = (ImageView) itemView.findViewById(R.id.video_sign);
            mIsPanoramaSign = (ImageView) itemView.findViewById(R.id.is_panorama);
            thumbnailLayout = (FrameLayout) itemView.findViewById(R.id.thumbnail_layout);
            imageDurationView = (TextView) itemView.findViewById(R.id.local_video_duration);
        }
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {

        ProgressBar pbLoading;
        TextView tvLoading;
        LinearLayout llEnd;

        FootViewHolder(View itemView) {
            super(itemView);
            pbLoading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_loading);
            llEnd = (LinearLayout) itemView.findViewById(R.id.ll_end);
        }
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    public void setOperationMode(OperationMode operationMode) {
        this.operationMode = operationMode;
    }

    public void changeCheckBoxState(int position) {
        if (position < list.size()) {
            list.get(position).isItemChecked = list.get(position).isItemChecked == true ? false : true;
            this.notifyDataSetChanged();
        }
    }

    public List<MultiPbItemInfo> getCheckedItemsList() {
        LinkedList<MultiPbItemInfo> checkedList = new LinkedList<MultiPbItemInfo>();

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