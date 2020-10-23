package com.icatch.mobilecam.ui.Interface;

import androidx.recyclerview.widget.RecyclerView;

import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.ui.adapter.MultiPbRecyclerViewAdapter;

public interface MultiPbFragmentView {
    void setRecyclerViewVisibility(int visibility);
    void setRecyclerViewAdapter(MultiPbRecyclerViewAdapter recyclerViewAdapter);
    void setRecyclerViewLayoutManager(RecyclerView.LayoutManager layout);
    void notifyChangeMultiPbMode(OperationMode operationMode);
    void setPhotoSelectNumText(int selectNum);
    void setNoContentTxvVisibility(int visibility);

}
