package com.icatch.mobilecam.ui.Interface;

import android.support.v4.app.FragmentPagerAdapter;

public interface LocalMultiPbView {
    void setViewPageAdapter(FragmentPagerAdapter adapter);

    void setViewPageCurrentItem(int item);

    void setMenuPhotoWallTypeIcon(int iconRes);

    void setViewPagerScanScroll(boolean isCanScroll);

    void setSelectNumText(String text);

    void setSelectBtnVisibility(int visibility);

    void setSelectBtnIcon(int icon);

    void setSelectNumTextVisibility(int visibility);

    void setTabLayoutClickable(boolean value);

    void setEditLayoutVisibility(int visibility);

}
