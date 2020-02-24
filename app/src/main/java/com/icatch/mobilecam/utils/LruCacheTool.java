package com.icatch.mobilecam.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.icatch.mobilecam.Log.AppLog;

public class LruCacheTool {
    private final static String TAG = "LruCacheTool";
    private static LruCacheTool instance;
    private LruCache<String, Bitmap> localThumbnailLruCache;

    public static LruCacheTool getInstance() {
        if (instance == null) {
            instance = new LruCacheTool();
        }
        return instance;
    }

    private LruCacheTool() {

    }

    public LruCache<String, Bitmap> getLruCache(){
        return localThumbnailLruCache;
    }

    public void initLruCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
//        int cacheMemory = 50000;
        AppLog.d(TAG, "initLruCache cacheMemory=" + cacheMemory);
        localThumbnailLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
//                return value.getRowBytes() * value.getHeight();
                AppLog.d(TAG, "cacheMemory value.getByteCount()=" + value.getByteCount() + " key=" + key);
                return value.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                // TODO Auto-generated method stub
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (oldValue != null) {
                    AppLog.d(TAG,"cacheMemory entryRemoved key=" + key);
                    //回收bitmap占用的内存空间
                    oldValue.recycle();
                    oldValue = null;
                }
            }
        };
    }

    public void clearCache() {
        AppLog.d(TAG, "clearCache");
        localThumbnailLruCache.evictAll();
    }

    public Bitmap getBitmapFromLruCache(String file) {
        if(file == null || file.isEmpty()){
            return null;
        }
        Bitmap bitmap = localThumbnailLruCache.get(file);
        AppLog.d(TAG, "getBitmapFromLruCache key=" + file + " bitmap=" + bitmap);
        return bitmap;
    }

    public void addBitmapToLruCache(String key, Bitmap bm) {
        if (getBitmapFromLruCache(key) == null) {
            if (bm != null && key != null) {
                AppLog.d(TAG, "addBitmapToLruCache key=" + key + " size=" + bm.getByteCount() + " bitmap=" + bm);
                localThumbnailLruCache.put(key, bm);
//                localThumbnailLruCache.put(key, bm);
            }
        }
    }


}
