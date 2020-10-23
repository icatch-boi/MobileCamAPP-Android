package com.icatch.mobilecam.ui.Interface;

import androidx.fragment.app.FragmentPagerAdapter;

public interface MultiPbView {
    void setViewPageAdapter(FragmentPagerAdapter adapter);
    void setViewPageCurrentItem(int item);
    void setMenuPhotoWallTypeIcon(int iconRes);
    void setViewPagerScanScroll(boolean isCanScroll);
    void setSelectNumText(String text);
    void setSelectBtnVisibility(int visibility);
    void setSelectBtnIcon(int icon);
    void setSelectNumTextVisibility(int visibility);
    void setTabLayoutClickable(boolean value);
    void setEditLayoutVisibiliy(int visibiliy);
    int getViewPageIndex();
    void setFilterItemVisibiliy(boolean visibility);
}
