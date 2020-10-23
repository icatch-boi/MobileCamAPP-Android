package com.icatch.mobilecam.ui.adapter;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MultiPbViewPageAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> list;

    public MultiPbViewPageAdapter(FragmentManager fm,ArrayList<Fragment> list){
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
