package com.icatch.mobilecam.ui.Fragment;

import android.support.v4.app.Fragment;

import com.icatch.mobilecam.Listener.OnStatusChangedListener;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.data.type.PhotoWallLayoutType;

import java.util.List;

public abstract class BaseMultiPbFragment extends Fragment {
    public abstract void setOperationListener(OnStatusChangedListener modeChangedListener);
    public abstract void changePreviewType(PhotoWallLayoutType layoutType);
    public abstract void quitEditMode();
    public abstract void selectOrCancelAll(boolean isSelectAll);
    public abstract void deleteFile();
    public abstract List<MultiPbItemInfo> getSelectedList();
    public abstract void loadPhotoWall();


}
