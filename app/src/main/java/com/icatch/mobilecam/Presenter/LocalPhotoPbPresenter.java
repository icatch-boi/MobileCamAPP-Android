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
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;

import com.icatch.mobilecam.MyCamera.LocalSession;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
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
import com.icatch.mobilecam.utils.imageloader.ImageLoaderUtil;
import com.icatch.mobilecam.utils.imageloader.TutkUriUtil;
import com.icatchtek.pancam.customer.exception.IchGLSurfaceNotSetException;
import com.icatchtek.pancam.customer.surface.ICatchSurfaceContext;
import com.icatchtek.pancam.customer.type.ICatchGLImage;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;
import com.icatchtek.pancam.customer.type.ICatchGLPoint;
import com.icatchtek.reliant.customer.type.ICatchCodec;
import com.icatchtek.reliant.customer.type.ICatchFile;

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
    private boolean surfaceCreated = false;

    public LocalPhotoPbPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        handler = new Handler();
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
    }

    private void initClient() {
        LocalSession.getInstance().preparePanoramaSession();
        panoramaPhotoPlayback = LocalSession.getInstance().getPanoramaPhotoPlayback();
    }

    private void destroySession() {
        LocalSession.getInstance().destroyPanoramaSession();
    }

    public void initView() {
        viewPagerAdapter = new LocalPhotoPbViewPagerAdapter(activity, fileList);
        viewPagerAdapter.setOnPhotoTapListener(new LocalPhotoPbViewPagerAdapter.OnPhotoTapListener() {
            @Override
            public void onPhotoTap() {
                showBar();
            }
        });
        photoPbView.setViewPagerAdapter(viewPagerAdapter);
        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
        updateUi();
        photoPbView.setOnPageChangeListener(new MyViewPagerOnPagerChangeListener());
    }

    public void loadPanoramaImage() {
        int curIndex = photoPbView.getViewPagerCurrentItem();
        loadPanoramaPhoto(fileList.get(curIndex));
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

    public void loadPreviousImage() {
        AppLog.d(TAG, "loadPreviousImage=");
        if (curPhotoIdx > 0) {
            curPhotoIdx--;
        }
        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
    }

    public void loadNextImage() {
        AppLog.d(TAG, "loadNextImage=");
        if (curPhotoIdx < fileList.size() - 1) {
            curPhotoIdx++;
        }
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
//                    updateUi();
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    break;

                default:
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
//            AppLog.d(TAG,"onPageScrolled arg0:" + arg0 + " arg1:" + arg1 + " arg2:" + arg2);
            if (isScrolling) {
                if (lastItem > arg2) {
                    // 递减，向右侧滑动
                } else if (lastItem < arg2) {
                    // 递减，向右侧滑动
                } else if (lastItem == arg2) {
                }
            }
            lastItem = arg2;
        }

        @Override
        public void onPageSelected(int arg0) {
            AppLog.d(TAG, "onPageSelected arg0:" + arg0);
            updateUi();
        }
    }

    private void updateUi() {
        int curIndex = photoPbView.getViewPagerCurrentItem();
        String indexInfo = (curIndex + 1) + "/" + fileList.size();
        photoPbView.setIndexInfoTxv(indexInfo);
        LocalPbItemInfo itemInfo = fileList.get(curIndex);
        photoPbView.setSurfaceviewVisibility(itemInfo.isPanorama() ? View.VISIBLE : View.GONE);
        photoPbView.setViewPagerVisibility(itemInfo.isPanorama() ? View.GONE : View.VISIBLE);
        if(itemInfo.isPanorama() && surfaceCreated){
            loadPanoramaPhoto(itemInfo);
        }
    }

    private void loadPanoramaPhoto(final LocalPbItemInfo itemInfo) {
        if (itemInfo == null) {
            return;
        }
        if (itemInfo.isPanorama()) {
            final String url = itemInfo.file.getAbsolutePath();
            ImageLoaderUtil.loadLocalImage(itemInfo.file, new ImageLoaderUtil.OnLoadListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    AppLog.d(TAG, "onLoadingStarted imageUri:" + imageUri);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view) {
                    AppLog.d(TAG, "onLoadingFailed imageUri:" + imageUri);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    AppLog.d(TAG, "onLoadingComplete imageUri:" + imageUri);
                    AppLog.d(TAG, "onLoadingComplete url:" + url);
                    if (loadedImage != null) {
                        photoPbView.setViewPagerVisibility(View.GONE);
                        panoramaPhotoPlayback.pancamGLSetFormat(ICatchCodec.ICH_CODEC_BITMAP, loadedImage.getWidth(), loadedImage.getHeight());
                        panoramaPhotoPlayback.update(new ICatchGLImage(loadedImage));
                        registerGyroscopeSensor();
                        insidePanorama();
                    }
                }
            });
        }
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
                        updateUi();
                    }
                    MyProgressDialog.closeProgressDialog();
                }
            });
            AppLog.d(TAG, "end DeleteThread");
        }
    }

    public void setShowArea(Surface surface) {
        AppLog.d(TAG, "start initSurface");
        iCatchSurfaceContext = new ICatchSurfaceContext(surface);
        panoramaPhotoPlayback.setSurface(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE, iCatchSurfaceContext);
        AppLog.d(TAG, "end initSurface");
        surfaceCreated = true;
    }

    public void insidePanorama() {
        locate(FIXED_INSIDE_DISTANCE);
    }

    private void locate(float distance) {
        panoramaPhotoPlayback.pancamGLTransLocate(distance);
    }

    public void clearImage(int iCatchSphereType) {
        surfaceCreated = false;
        removeGyroscopeListener();
        if (panoramaPhotoPlayback != null) {
            if (iCatchSurfaceContext != null) {
                panoramaPhotoPlayback.removeSurface(iCatchSphereType, iCatchSurfaceContext);
                iCatchSurfaceContext = null;
            }
            panoramaPhotoPlayback.clear();
        }
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
        AppLog.d(TAG, "start setDrawingArea windowW= " + windowW + " windowH= " + windowH);
        if (iCatchSurfaceContext != null) {
            try {
                iCatchSurfaceContext.setViewPort(0, 0, windowW, windowH);
            } catch (IchGLSurfaceNotSetException e) {
                e.printStackTrace();
            }
        }
        AppLog.d(TAG, "end setDrawingArea");
    }

    public void initPanorama() {
        panoramaPhotoPlayback.pancamGLInit();
    }

}
