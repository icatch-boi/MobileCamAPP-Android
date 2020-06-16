package com.icatch.mobilecam.ui.ExtendComponent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.icatch.mobilecam.R;

public class BorderImageView extends ImageView{
    private boolean isShow = false;
    private Context context;
    Paint paint;

    public BorderImageView(Context context) {
        super(context);
        this.context = context;
    }

    public BorderImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public BorderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isShow){
            Rect rec=canvas.getClipBounds();
            rec.bottom--;
            rec.right--;
            Paint paint = new Paint();
            paint.setColor(context.getResources().getColor(R.color.gray));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);
            canvas.drawRect(rec, paint);
        }
    }

    public void showBorder(boolean isShow){
        this.isShow = isShow;
    }
}
