package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.icatch.mobilecam.R;

import java.util.ArrayList;

public class MyViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> viewList;
    private Context context;

    public MyViewPagerAdapter(Context context, ArrayList<View> viewList) {
        this.viewList = viewList;
        this.context = context;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position < viewList.size()) {
            container.removeView(viewList.get(position));
            viewList.set(position, null);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = View.inflate(context, R.layout.pb_photo_item, null);
        viewList.set(position, v);
        container.addView(v, 0);
        return v;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}

