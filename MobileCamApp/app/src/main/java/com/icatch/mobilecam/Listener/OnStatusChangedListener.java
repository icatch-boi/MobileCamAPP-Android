package com.icatch.mobilecam.Listener;

import com.icatch.mobilecam.data.Mode.OperationMode;

public interface OnStatusChangedListener {
    public void onChangeOperationMode(OperationMode curOperationMode);
    public void onSelectedItemsCountChanged(int SelectedNum);
}
