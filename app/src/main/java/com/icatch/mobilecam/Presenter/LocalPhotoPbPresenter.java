package com.icatch.mobilecam.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.LruCache;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;

import com.icatch.mobilecam.MyCamera.LocalSession;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.ui.adapter.LocalPhotoPbViewPagerAdapter;
import com.icatch.mobilecam.data.entity.LocalPbItemInfo;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.ExtendComponent.ProgressWheel;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.Mode.TouchMode;
import com.icatch.mobilecam.Presenter.Interface.BasePresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.PanoramaPhotoPlayback;
import com.icatch.mobilecam.utils.BitmapTools;
import com.icatch.mobilecam.ui.Interface.LocalPhotoPbView;
import com.icatch.mobilecam.utils.MediaRefresh;
import com.icatchtek.pancam.customer.exception.IchGLSurfaceNotSetException;
import com.icatchtek.pancam.customer.surface.ICatchSurfaceContext;
import com.icatchtek.pancam.customer.type.ICatchGLImage;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;
import com.icatchtek.pancam.customer.type.ICatchGLPoint;
import com.icatchtek.reliant.customer.type.ICatchCodec;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.co.senab.photoview.PhotoView;


public class LocalPhotoPbPresenter extends BasePresenter implements SensorEventListener {
    private String TAG = LocalPhotoPbPresenter.class.getSimpleName();
    private LocalPhotoPbView photoPbView;
    private Activity activity;
    private List<LocalPbItemInfo> fileList;
    private LocalPhotoPbViewPagerAdapter viewPagerAdapter;
    private int curPhotoIdx;
    private int lastItem = -1;
    private int tempLastItem = -1;
    private boolean isScrolling = false;
    private int photoNums = 0;
    private int slideDirection = DIRECTION_RIGHT;
    private final static int DIRECTION_RIGHT = 0x1;
    private final static int DIRECTION_LEFT = 0x2;
    private final static int DIRECTION_UNKNOWN = 0x4;
    private LinkedList<Asytask> asytaskList;
    private Asytask curAsytask;
    private LruCache<String, Bitmap> mLruCache;
    private List<View> viewList;
    private ExecutorService executor;
    private Handler handler;
    private PanoramaPhotoPlayback panoramaPhotoPlayback;
    private final static float MIN_ZOOM = 0.5f;
    private final static float MAX_ZOOM = 2.2f;
    private final static float FIXED_OUTSIDE_FOCUS = 1.0f;
    private final static float FIXED_INSIDE_FOCUS = 2.0f;
    private final static float FIXED_NEAR_DISTANCE = 0.6f;
    private final static float FIXED_OUTSIDE_DISTANCE = 1 / MIN_ZOOM;
    private final static float FIXED_INSIDE_DISTANCE = 1 / MAX_ZOOM;
    private TouchMode touchMode = TouchMode.NONE;
    private float mPreviousY;
    private float mPreviousX;
    private float beforeLenght;
    private float afterLenght;
    private float currentZoomRate = MAX_ZOOM;
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private ICatchSurfaceContext iCatchSurfaceContext;
    private int curPanoramaType= ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;   //ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE 全景 ,ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID 小行星,ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R VR效果

    public LocalPhotoPbPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        viewList = new LinkedList<View>();
        handler = new Handler();
        initLruCache();
        slideDirection = DIRECTION_UNKNOWN;
    }

    public void setView(LocalPhotoPbView localPhotoPbView) {
        this.photoPbView = localPhotoPbView;
        initCfg();
        fileList = GlobalInfo.getInstance().getLocalPhotoList();
        Bundle data = activity.getIntent().getExtras();
        curPhotoIdx = data.getInt("curfilePosition");
        AppLog.d(TAG, "photo position =" + curPhotoIdx);
        initClient();
//        panoramaPhotoPlayback.pancamGLSetFormat(ICatchCodec.ICH_CODEC_BITMAP, BitmapTools.getImageWidth(filePath), BitmapTools.getImageHeight(filePath));
    }

    private void initClient() {
        LocalSession.getInstance().preparePanoramaSession();
        panoramaPhotoPlayback = LocalSession.getInstance().getPanoramaPhotoPlayback();
    }

    private void destroySession() {
        LocalSession.getInstance().destroyPanoramaSession();
    }

    private void initLruCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 16;
        AppLog.d(TAG, "initLruCache maxMemory=" + maxMemory);
        AppLog.d(TAG, "initLruCache cacheMemory=" + cacheMemory);
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                AppLog.d(TAG, "cacheMemory value.getByteCount()=" + value.getByteCount());
                return value.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                // TODO Auto-generated method stub
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (oldValue != null) {
                    AppLog.d(TAG, "cacheMemory entryRemoved key=" + key);
                    //回收bitmap占用的内存空间
                    oldValue.recycle();
                    oldValue = null;
                }
            }
        };
    }

    public void initView() {
        for (int ii = 0; ii < fileList.size(); ii++) {
            viewList.add(ii, null);
        }
        viewPagerAdapter = new LocalPhotoPbViewPagerAdapter(activity, fileList, viewList, mLruCache);
        viewPagerAdapter.setOnPhotoTapListener(new LocalPhotoPbViewPagerAdapter.OnPhotoTapListener() {
            @Override
            public void onPhotoTap() {
                showBar();
            }
        });
        photoPbView.setViewPagerAdapter(viewPagerAdapter);
        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
        ShowCurPageNum();
        loadBitmaps(curPhotoIdx);
        photoPbView.setOnPageChangeListener(new MyViewPagerOnPagerChangeListener());
    }

    public void loadPanoramaImage() {
        loadBitmaps(curPhotoIdx);
    }

    public Bitmap getBitmapFromLruCache(String fileName) {
        AppLog.d(TAG, "getBitmapFromLruCache filePath=" + fileName);
        return mLruCache.get(fileName);
    }

    protected void addBitmapToLruCache(String fileName, Bitmap bm) {
        if (bm == null) {
            return;
        }
        if (bm.getByteCount() > mLruCache.maxSize()) {
            AppLog.d(TAG, "addBitmapToLruCache greater than mLruCache size filePath=" + fileName);
            return;
        }
        if (getBitmapFromLruCache(fileName) == null) {
            if (bm != null && fileName != null) {
                AppLog.d(TAG, "addBitmapToLruCache filePath=" + fileName);
                mLruCache.put(fileName, bm);
            }
        }
    }


    public void showBar() {
        boolean isShowBar = photoPbView.getTopBarVisibility() == View.VISIBLE ? true : false;
        AppLog.d(TAG, "showBar isShowBar=" + isShowBar);
        if (isShowBar) {
            photoPbView.setTopBarVisibility(View.GONE);
            photoPbView.setBottomBarVisibility(View.GONE);
        } else {
            photoPbView.setTopBarVisibility(View.VISIBLE);
            photoPbView.setBottomBarVisibility(View.VISIBLE);
        }
    }



    void loadBitmaps(int curPhotoIdx) {
        AppLog.i(TAG, "add task loadBitmaps curPhotoIdx=" + curPhotoIdx);
        if (curPhotoIdx < 0) {
            return;
        }
        if (curAsytask != null && !curAsytask.isCancelled()) {
            AppLog.i(TAG, "add task curAsytask cancel curAsytask position" + curAsytask.position);
            curAsytask.cancel(true);
        }
        if (asytaskList == null) {
            asytaskList = new LinkedList<Asytask>();
        } else {
            asytaskList.clear();
        }
        if (fileList == null || fileList.size() < 0) {
            AppLog.e(TAG, "fileList is null or size < 0");
            return;
        }
        if (curPhotoIdx == 0) {
            Asytask task1 = new Asytask(fileList.get(curPhotoIdx), curPhotoIdx);
            asytaskList.add(task1);
            if (fileList.size() > 1) {
                Asytask task2 = new Asytask(fileList.get(curPhotoIdx + 1), curPhotoIdx + 1);
                asytaskList.add(task2);
            }

        } else if (curPhotoIdx == fileList.size() - 1) {
            Asytask task1 = new Asytask(fileList.get(curPhotoIdx), curPhotoIdx);
            Asytask task2 = new Asytask(fileList.get(curPhotoIdx - 1), curPhotoIdx - 1);
            asytaskList.add(task1);
            asytaskList.add(task2);
        } else {
            AppLog.d(TAG, "loadBitmaps slideDirection=" + slideDirection);
            if (slideDirection == DIRECTION_RIGHT) {
                Asytask task1 = new Asytask(fileList.get(curPhotoIdx), curPhotoIdx);
                Asytask task2 = new Asytask(fileList.get(curPhotoIdx - 1), curPhotoIdx - 1);
                Asytask task3 = new Asytask(fileList.get(curPhotoIdx + 1), curPhotoIdx + 1);
                asytaskList.add(task1);
                asytaskList.add(task2);
                asytaskList.add(task3);
            } else {
                Asytask task1 = new Asytask(fileList.get(curPhotoIdx), curPhotoIdx);
                Asytask task2 = new Asytask(fileList.get(curPhotoIdx + 1), curPhotoIdx + 1);
                Asytask task3 = new Asytask(fileList.get(curPhotoIdx - 1), curPhotoIdx - 1);
                asytaskList.add(task1);
                asytaskList.add(task2);
                asytaskList.add(task3);
            }
        }
        if (asytaskList != null && asytaskList.size() > 0) {
            curAsytask = asytaskList.removeFirst();
            curAsytask.execute();
        }
    }

    public void loadPreviousImage() {
        AppLog.d(TAG, "loadPreviousImage=");
        if (curPhotoIdx > 0) {
            curPhotoIdx--;
        }
        slideDirection = DIRECTION_LEFT;
        loadBitmaps(curPhotoIdx);
        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
    }

    public void loadNextImage() {
        AppLog.d(TAG, "loadNextImage=");
        if (curPhotoIdx < fileList.size() - 1) {
            curPhotoIdx++;
        }
        slideDirection = DIRECTION_RIGHT;
        loadBitmaps(curPhotoIdx);
        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
    }

    public void setPanoramaType() {
        if(!fileList.get(curPhotoIdx).isPanorama()){
            MyToast.show(activity,R.string.non_360_picture_not_support_switch);
            return;
        }
        if(curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE ){
            panoramaPhotoPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID;
            photoPbView.setPanoramaTypeTxv(R.string.text_asteroid);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }else if(curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID){
            panoramaPhotoPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R;
            photoPbView.setPanoramaTypeTxv(R.string.text_vr);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }else{
            panoramaPhotoPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;
            photoPbView.setPanoramaTypeTxv(R.string.text_panorama);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
       loadPanoramaImage();
    }

    class Asytask extends AsyncTask<String, Integer, Bitmap> {

        LocalPbItemInfo file;
        String filePath;
        int position;

        public Asytask(LocalPbItemInfo file, int position) {
            super();
            this.file = file;
            this.filePath = file.file.getPath();
            this.position = position;
        }

        @Override
        protected Bitmap doInBackground(String... params) {//处理后台执行的任务，在后台线程执行
            Bitmap bm = getBitmapFromLruCache(filePath);
            if (bm != null) {
                return bm;
            } else {
                bm = BitmapTools.getImageByPath(filePath, BitmapTools.getImageWidth(filePath), BitmapTools.getImageHeight(filePath));
                if (bm != null) {
                    AppLog.d(TAG, " position=" + position + "filePath=" + filePath + " bm size=" + bm.getByteCount());
                }
                addBitmapToLruCache(filePath, bm);
                return bm;
            }
        }

        protected void onProgressUpdate(Integer... progress) {//在调用publishProgress之后被调用，在ui线程执行
        }

        protected void onPostExecute(Bitmap result) {
            //后台任务执行完之后被调用，在ui线程执行
            if (position == photoPbView.getViewPagerCurrentItem()) {//current show
                if (result != null) {
                    if (fileList.get(position).isPanorama()) {
//                        clearOrRestoreSurface(false);
//                        panoramaPhotoPlayback.pancamGLClearFormat();
                        panoramaPhotoPlayback.pancamGLSetFormat(ICatchCodec.ICH_CODEC_BITMAP, result.getWidth(), result.getHeight());
                        startRendering(new ICatchGLImage(result));
                        return;
                    }
                    clearOrRestoreSurface(true);
                    photoPbView.setViewPagerVisibility(View.VISIBLE);
                    View view = viewList.get(position);
                    if (view != null) {
                        PhotoView photoView = (PhotoView) view.findViewById(R.id.photo);
                        ProgressWheel progressBar = (ProgressWheel) view.findViewById(R.id.progress_wheel);
                        AppLog.d(TAG, "onPostExecute position=" + position + " filePath=" + filePath + " size=" + result.getByteCount() + " result.isRecycled" +
                                "()=" + result.isRecycled() + " photoView=" + photoView);
                        if (photoView != null && !result.isRecycled()) {
                            photoView.setImageBitmap(result);
                        }
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
            }
            if (asytaskList != null && asytaskList.size() > 0) {
                curAsytask = asytaskList.removeFirst();
                curAsytask.execute();
            }
        }
    }

    private class MyViewPagerOnPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

            switch (arg0) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    isScrolling = true;
                    tempLastItem = photoPbView.getViewPagerCurrentItem();
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
                    if (isScrolling == true && tempLastItem != -1 && tempLastItem != photoPbView.getViewPagerCurrentItem()) {
                        lastItem = tempLastItem;
                    }

                    curPhotoIdx = photoPbView.getViewPagerCurrentItem();
                    isScrolling = false;
                    loadBitmaps(photoPbView.getViewPagerCurrentItem());
                    ShowCurPageNum();
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            if (isScrolling) {
                if (lastItem > arg2) {
                    // 递减，向右侧滑动
                    slideDirection = DIRECTION_RIGHT;
                } else if (lastItem < arg2) {
                    // 递减，向右侧滑动
                    slideDirection = DIRECTION_LEFT;
                } else if (lastItem == arg2) {
                    slideDirection = DIRECTION_RIGHT;
                }
            }
            lastItem = arg2;
        }

        @Override
        public void onPageSelected(int arg0) {
            ShowCurPageNum();
        }
    }

    private void ShowCurPageNum() {
        int curPhoto = photoPbView.getViewPagerCurrentItem() + 1;
        String indexInfo = curPhoto + "/" + fileList.size();
        photoPbView.setIndexInfoTxv(indexInfo);
    }

    public void delete() {
        showDeleteEnsureDialog();
    }

    public void share() {
        int curPosition = photoPbView.getViewPagerCurrentItem();
        String photoPath = fileList.get(curPosition).file.getPath();
        AppLog.d(TAG, "share curPosition=" + curPosition + " photoPath=" + photoPath);
        //com.icatch.mobilecam.provider
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(
                    activity,
                    "com.icatch.mobilecam.provider",
                    new File(photoPath));
        } else {
            fileUri = Uri.fromFile(new File(photoPath));
        }
//        Uri imageUri = Uri.fromFile(new File(photoPath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.setDataAndType( fileUri, "image/*");
        activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getString(R.string.gallery_share_to)));
    }

    public void info() {
    }

    private void showDeleteEnsureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(R.string.image_delete_des);
        builder.setNegativeButton(R.string.gallery_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 这里添加点击确定后的逻辑
                MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                asytaskList.clear();
                executor = Executors.newSingleThreadExecutor();
                executor.submit(new DeleteThread(), null);
            }
        });
        builder.setPositiveButton(R.string.gallery_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private class DeleteThread implements Runnable {
        @Override
        public void run() {
            curPhotoIdx = photoPbView.getViewPagerCurrentItem();
            File tempFile = fileList.get(curPhotoIdx).file;
            if (tempFile.exists()) {
                tempFile.delete();
                MediaRefresh.notifySystemToScan(tempFile);
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    fileList.remove(curPhotoIdx);
                    viewList.remove(curPhotoIdx);
                    viewPagerAdapter.notifyDataSetChanged();
                    photoPbView.setViewPagerAdapter(viewPagerAdapter);
                    photoNums = fileList.size();
                    if (photoNums == 0) {
                        activity.finish();
                        return;
                    } else {
                        if (curPhotoIdx == photoNums) {
                            curPhotoIdx--;
                        }
                        AppLog.d(TAG, "photoNums=" + photoNums + " curPhotoIdx=" + curPhotoIdx);
                        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
                        ShowCurPageNum();
                        loadBitmaps(curPhotoIdx);
                    }
                    MyProgressDialog.closeProgressDialog();
                }
            });
            AppLog.d(TAG, "end DeleteThread");
        }
    }

    public void setShowArea(Surface surface, int width, int height) {
        AppLog.d(TAG, "start initSurface width=" + width + " height=" + height);
        iCatchSurfaceContext = new ICatchSurfaceContext(surface);
        panoramaPhotoPlayback.setSurface(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE, iCatchSurfaceContext);

        String filePath = fileList.get(curPhotoIdx).file.getPath();
        try {
            iCatchSurfaceContext.setViewPort(0, 0,width, height);
        } catch (IchGLSurfaceNotSetException e) {
            AppLog.d(TAG, "setViewPort IchGLSurfaceNotSetException");
            e.printStackTrace();
        }
        panoramaPhotoPlayback.pancamGLSetFormat(ICatchCodec.ICH_CODEC_BITMAP, BitmapTools.getImageWidth(filePath), BitmapTools.getImageHeight(filePath));
        AppLog.d(TAG, "end initSurface");
    }

    public void startRendering(ICatchGLImage image) {
        AppLog.d(TAG, "start startRendering image=" + image);
        photoPbView.setViewPagerVisibility(View.GONE);
        if (panoramaPhotoPlayback == null) {
            return;
        }
        panoramaPhotoPlayback.update(image);
//        registerGyroscopeSensor();
        insidePanorama();
        AppLog.d(TAG, "end startRendering");
    }

    public void stopRendering() {
        AppLog.d(TAG, "start stopRendering");
        if (panoramaPhotoPlayback == null) {
            return;
        }
        panoramaPhotoPlayback.clear();
        AppLog.d(TAG, "end stopRendering");
    }

    public void insidePanorama() {
//        scale(FIXED_INSIDE_FOCUS,FIXED_NEAR_DISTANCE);
        locate(FIXED_INSIDE_DISTANCE);
    }

    private void locate(float distance) {
        panoramaPhotoPlayback.pancamGLTransLocate(distance);
    }


    public void destroyImage(int iCatchSphereType) {
//        removeGyroscopeListener();
        if (panoramaPhotoPlayback != null) {
            panoramaPhotoPlayback.clear();
            if (iCatchSurfaceContext != null) {
                panoramaPhotoPlayback.removeSurface(iCatchSphereType, iCatchSurfaceContext);
                iCatchSurfaceContext = null;
            }

            panoramaPhotoPlayback.release();
        }
    }

    public void finish() {
        destroyImage(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
        destroySession();
        activity.finish();
    }

    public void rotateB(MotionEvent e, float prevX, float prevY) {
        ICatchGLPoint prev = new ICatchGLPoint(prevX, prevY);
        ICatchGLPoint curr = new ICatchGLPoint(e.getX(), e.getY());
        panoramaPhotoPlayback.pancamGLTransformRotate(prev, curr);
    }

    public void onSufaceViewTouchDown(MotionEvent event) {
        touchMode = TouchMode.DRAG;
        mPreviousY = event.getY();// 记录触控笔位置
        mPreviousX = event.getX();// 记录触控笔位置
        beforeLenght = 0;
        afterLenght = 0;
    }

    public void onSufaceViewPointerDown(MotionEvent event) {
        Log.d("2222", "event.getPointerCount()................=" + event.getPointerCount());
        if (event.getPointerCount() == 2) {
            touchMode = TouchMode.ZOOM;
            beforeLenght = getDistance(event);// 获取两点的距离
        }
    }

    public void onSufaceViewTouchMove(MotionEvent event) {
        if (touchMode == TouchMode.DRAG) {
            rotateB(event, mPreviousX, mPreviousY);
            mPreviousY = event.getY();// 记录触控笔位置
            mPreviousX = event.getX();// 记录触控笔位置
        }
        /** 处理缩放 **/
        else if (touchMode == TouchMode.ZOOM) {
            afterLenght = getDistance(event);// 获取两点的距离
            float gapLenght = afterLenght - beforeLenght;// 变化的长度
            if (Math.abs(gapLenght) > 5f) {
                float scale_temp = afterLenght / beforeLenght;// 求的缩放的比例
                setScale(scale_temp);
                beforeLenght = afterLenght;
            }
        }
    }

    /**
     * 获取两点的距离
     **/
    float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) StrictMath.sqrt(x * x + y * y);
    }

    /**
     * 处理缩放
     **/
    void setScale(float scale) {
        if ((currentZoomRate >= MAX_ZOOM && scale > 1) || (currentZoomRate <= MIN_ZOOM && scale < 1)) {
            return;
        }
        float temp = currentZoomRate * scale;
        if (scale > 1) {
            if (temp <= MAX_ZOOM) {
                currentZoomRate = currentZoomRate * scale;
                zoom(currentZoomRate);
            } else {
                currentZoomRate = MAX_ZOOM;
                zoom(currentZoomRate);
            }
        } else if (scale < 1) {
            if (temp >= MIN_ZOOM) {
                currentZoomRate = currentZoomRate * scale;
                zoom(currentZoomRate);
            } else {
                currentZoomRate = MIN_ZOOM;
                zoom(currentZoomRate);
            }
        }

    }

    private void zoom(float currentZoomRate) {
        locate(1 / currentZoomRate);
    }

    public void onSufaceViewTouchUp() {
        touchMode = TouchMode.NONE;
    }

    public void onSufaceViewTouchPointerUp() {
        touchMode = TouchMode.NONE;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(fileList== null || curPhotoIdx < 0){
            return;
        }
        if(!fileList.get(curPhotoIdx).isPanorama()){
            return;
        }
        if (event.sensor == null) {
            return;
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // 从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
            //陀螺仪的XYZ分别代表设备围绕XYZ三个轴旋转的角速度：radians/second
            float speedX = event.values[0];
            float speedY = event.values[1];
            float speedZ = event.values[2];
            if (Math.abs(speedY) < 0.02 && Math.abs(speedZ) < 0.02) {
                return;
            }
            rotate(speedX, speedY, speedZ, event.timestamp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private void rotate(float speedX, float speedY, float speedZ, long timestamp) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        panoramaPhotoPlayback.pancamGLTransformRotate(rotation, speedX, speedY, speedZ, timestamp);
    }

    public void registerGyroscopeSensor() {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // 注册陀螺仪传感器，并设定传感器向应用中输出的时间间隔类型是SensorManager.SENSOR_DELAY_GAME(20000微秒)
        // SensorManager.SENSOR_DELAY_FASTEST(0微秒)：最快。最低延迟，一般不是特别敏感的处理不推荐使用，该模式可能在成手机电力大量消耗，由于传递的为原始数据，诉法不处理好会影响游戏逻辑和UI的性能
        // SensorManager.SENSOR_DELAY_GAME(20000微秒)：游戏。游戏延迟，一般绝大多数的实时性较高的游戏都是用该级别
        // SensorManager.SENSOR_DELAY_NORMAL(200000微秒):普通。标准延时，对于一般的益智类或EASY级别的游戏可以使用，但过低的采样率可能对一些赛车类游戏有跳帧现象
        // SensorManager.SENSOR_DELAY_UI(60000微秒):用户界面。一般对于屏幕方向自动旋转使用，相对节省电能和逻辑处理，一般游戏开发中不使用
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void removeGyroscopeListener() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    public void setDrawingArea(int windowW, int windowH) {
        AppLog.d(TAG, "start setDrawingArea window = " + windowW + " windowH =" + windowH);
        if (iCatchSurfaceContext != null) {
            try {
                iCatchSurfaceContext.setViewPort(0, 0, windowW, windowH);
            } catch (IchGLSurfaceNotSetException e) {
                AppLog.d(TAG, "IchGLSurfaceNotSetException");
                e.printStackTrace();
            }
        }
        if (curPhotoIdx >= 0) {
            String filePath = fileList.get(curPhotoIdx).file.getPath();
            Bitmap bitmap = getBitmapFromLruCache(filePath);
            if (bitmap != null) {
                panoramaPhotoPlayback.update(new ICatchGLImage(bitmap));
            }
        }
        AppLog.d(TAG, "end setDrawingArea iCatchSurfaceContext = " + iCatchSurfaceContext);
    }

    public void uninit() {
        if (panoramaPhotoPlayback == null) {
            return;
        }
        panoramaPhotoPlayback.release();
    }

    public void initPanorama() {
        panoramaPhotoPlayback.pancamGLInit();
    }

    public void clearOrRestoreSurface(boolean value) {
        AppLog.d(TAG, "clearOrRestoreSurface value=" + value);
        if (value == true) {
            destroyImage(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
        }
        photoPbView.setSurfaceviewTransparent(value);
    }
}
