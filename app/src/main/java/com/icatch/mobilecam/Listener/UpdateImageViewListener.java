package com.icatch.mobilecam.Listener;

import android.graphics.Bitmap;

public interface UpdateImageViewListener {
    void onBitmapLoadComplete(String tag, Bitmap bitmap);
}
