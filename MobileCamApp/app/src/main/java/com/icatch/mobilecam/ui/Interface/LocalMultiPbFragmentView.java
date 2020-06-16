package com.icatch.mobilecam.ui.Interface;

import android.graphics.Bitmap;
import android.view.View;

import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.ui.adapter.LocalMultiPbWallGridAdapter;
import com.icatch.mobilecam.ui.adapter.LocalMultiPbWallListAdapter;


/**
 * Created by b.jiang on 2017/5/19.
 */

public interface LocalMultiPbFragmentView {
    void setListViewVisibility(int visibility);

    void setGridViewVisibility(int visibility);

    void setListViewAdapter(LocalMultiPbWallListAdapter photoWallListAdapter);

    void setGridViewAdapter(LocalMultiPbWallGridAdapter PhotoWallGridAdapter);

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
