package com.icatch.mobilecam.ui.ExtendComponent;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

    private boolean isCanScroll = true;
    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {

        super(context, attrs);

    }

    public void setScanScroll(boolean isCanScroll){

        this.isCanScroll = isCanScroll;

    }





    @Override

    public void scrollTo(int x, int y){

        if (isCanScroll){

            super.scrollTo(x, y);

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isCanScroll){
            return super.onTouchEvent(ev);
        }else{
            return true;
        }

    }


}