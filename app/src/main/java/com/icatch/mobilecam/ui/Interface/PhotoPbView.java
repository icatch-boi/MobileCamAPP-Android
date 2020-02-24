package com.icatch.mobilecam.ui.Interface;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public interface PhotoPbView {
    void setViewPagerAdapter(PagerAdapter adapter);
    void setTopBarVisibility(int visibility);
    void setBottomBarVisibility(int visibility);
    void setIndexInfoTxv(String indexInfo);
    void setViewPagerCurrentItem(int position);
    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);
    int getViewPagerCurrentItem();
    int getTopBarVisibility();

    void setViewPagerVisibility(int visibility);

    void setSurfaceviewTransparent(boolean value);
    void setPanoramaTypeTxv( int resId);
}
