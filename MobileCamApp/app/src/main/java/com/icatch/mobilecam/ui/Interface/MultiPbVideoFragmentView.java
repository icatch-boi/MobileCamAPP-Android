package com.icatch.mobilecam.ui.Interface;

import android.view.View;

import com.icatch.mobilecam.ui.adapter.MultiPbPhotoWallGridAdapter;
import com.icatch.mobilecam.ui.adapter.MultiPbPhotoWallListAdapter;
import com.icatch.mobilecam.data.Mode.OperationMode;

public interface MultiPbVideoFragmentView {
    void setListViewVisibility(int visibility);
    void setGridViewVisibility(int visibility);
    void setListViewAdapter(MultiPbPhotoWallListAdapter multiPbPhotoWallListAdapter);
    void setGridViewAdapter(MultiPbPhotoWallGridAdapter multiPbPhotoWallGridAdapter);
    void setListViewHeaderText(String headerText);
    View listViewFindViewWithTag(int tag);
    View gridViewFindViewWithTag(int tag);
    void setVideoSelectNumText(int selectNum);
    void changeMultiPbMode(OperationMode operationMode);
    void setListViewSelection(int position);
    void setGridViewSelection(int position);
    void setNoContentTxvVisibility(int visibility);
}
