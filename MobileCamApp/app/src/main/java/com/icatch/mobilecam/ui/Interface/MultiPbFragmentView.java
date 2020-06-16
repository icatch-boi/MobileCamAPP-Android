package com.icatch.mobilecam.ui.Interface;

import android.support.v7.widget.RecyclerView;

import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.ui.adapter.MultiPbPhotoWallListAdapter;
import com.icatch.mobilecam.ui.adapter.MultiPbRecyclerViewAdapter;

public interface MultiPbFragmentView {
    void setRecyclerViewVisibility(int visibility);
    void setRecyclerViewAdapter(MultiPbRecyclerViewAdapter recyclerViewAdapter);
    void setRecyclerViewLayoutManager(RecyclerView.LayoutManager layout);
    void notifyChangeMultiPbMode(OperationMode operationMode);
    void setPhotoSelectNumText(int selectNum);
    void setNoContentTxvVisibility(int visibility);

}
