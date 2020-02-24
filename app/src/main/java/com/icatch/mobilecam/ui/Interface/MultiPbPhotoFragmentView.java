package com.icatch.mobilecam.ui.Interface;

import android.graphics.Bitmap;
import android.view.View;

import com.icatch.mobilecam.ui.adapter.MultiPbPhotoWallGridAdapter;
import com.icatch.mobilecam.ui.adapter.MultiPbPhotoWallListAdapter;
import com.icatch.mobilecam.data.Mode.OperationMode;

public interface MultiPbPhotoFragmentView {
    void setListViewVisibility(int visibility);
    void setGridViewVisibility(int visibility);
    void setListViewAdapter(MultiPbPhotoWallListAdapter photoWallListAdapter);
    void setGridViewAdapter(MultiPbPhotoWallGridAdapter PhotoWallGridAdapter);
    void setListViewSelection(int position);
    void setGridViewSelection(int position);
    void setListViewHeaderText(String headerText);
    View listViewFindViewWithTag(int tag);
    View gridViewFindViewWithTag(int tag);
    void updateGridViewBitmaps(String tag, Bitmap bitmap);
    void notifyChangeMultiPbMode(OperationMode operationMode);
    void setPhotoSelectNumText(int selectNum);
    void setNoContentTxvVisibility(int visibility);

}
