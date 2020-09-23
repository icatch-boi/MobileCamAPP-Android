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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;

import com.icatch.mobilecam.Function.SDKEvent;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.Presenter.Interface.BasePresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.FileOperation;
import com.icatch.mobilecam.SdkApi.PanoramaPhotoPlayback;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.data.Mode.TouchMode;
import com.icatch.mobilecam.data.SystemInfo.SystemInfo;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.data.type.FileType;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.ui.Interface.PhotoPbView;
import com.icatch.mobilecam.ui.RemoteFileHelper;
import com.icatch.mobilecam.ui.adapter.PhotoPbViewPagerAdapter;
import com.icatch.mobilecam.ui.appdialog.AppDialog;
import com.icatch.mobilecam.utils.MediaRefresh;
import com.icatch.mobilecam.utils.StorageUtil;
import com.icatch.mobilecam.utils.fileutils.FileOper;
import com.icatch.mobilecam.utils.fileutils.FileTools;
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
import java.util.concurrent.Future;

public class PhotoPbPresenter extends BasePresenter implements SensorEventListener {
    private String TAG = PhotoPbPresenter.class.getSimpleName();
    private PhotoPbView photoPbView;
    private Activity activity;
    private List<MultiPbItemInfo> fileList;
    private PhotoPbViewPagerAdapter viewPagerAdapter;
    private Handler handler;
    private int curPhotoIdx;
    private int lastItem = -1;
    private int tempLastItem = -1;
    private boolean isScrolling = false;
    private final static int DIRECTION_RIGHT = 0x1;
    private final static int DIRECTION_LEFT = 0x2;
    private final static int DIRECTION_UNKNOWN = 0x4;

    public String downloadingFilename;
    public long downloadProcess;
    public long downloadingFilesize;
    private ExecutorService executor;
    private Future<Object> future;
    private PanoramaPhotoPlayback panoramaPhotoPlayback = CameraManager.getInstance().getCurCamera().getPanoramaPhotoPlayback();
    private FileOperation fileOperation = CameraManager.getInstance().getCurCamera().getFileOperation();

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
    private String curFilePath = "";
    private int curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;
    private boolean hasDeleted = false;
    private boolean surfaceCreated = false;


    public PhotoPbPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        handler = new Handler();
    }

    public void setView(PhotoPbView photoPbView) {
        this.photoPbView = photoPbView;
        initCfg();
        fileList = new LinkedList<>();
        List<MultiPbItemInfo> tempList = RemoteFileHelper.getInstance().getLocalFileList(FileType.FILE_PHOTO);
        if (tempList != null) {
            fileList.addAll(tempList);
        }
        Bundle data = activity.getIntent().getExtras();
        curPhotoIdx = data.getInt("curfilePosition");
    }


    public void loadPanoramaImage() {
        int curIndex = photoPbView.getViewPagerCurrentItem();
        loadPanoramaPhoto(fileList.get(curIndex));
    }

    public void initView() {
        viewPagerAdapter = new PhotoPbViewPagerAdapter(activity, fileList);
        viewPagerAdapter.setOnPhotoTapListener(new PhotoPbViewPagerAdapter.OnPhotoTapListener() {
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

    public void delete() {
        showDeleteEnsureDialog();
    }

    public void download() {
        showDownloadEnsureDialog();
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

    public void back() {
        clearImage(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
        Intent intent = new Intent();
        intent.putExtra("hasDeleted", hasDeleted);
        intent.putExtra("fileType", FileType.FILE_PHOTO.ordinal());
        activity.setResult(1000, intent);
        activity.finish();
    }


    private class MyViewPagerOnPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
//            AppLog.d(TAG,"onPageScrollStateChanged arg0:" + arg0);
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

    private class DownloadThread implements Runnable {
        private String TAG = "DownloadThread";
        private int curIdx = photoPbView.getViewPagerCurrentItem();

        @Override
        public void run() {
            AppLog.d(TAG, "begin DownloadThread");
            AppInfo.isDownloading = true;
            String path = StorageUtil.getRootPath(activity) + AppInfo.DOWNLOAD_PATH_PHOTO;
//            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                path = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_PHOTO;
//            } else {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        MyProgressDialog.closeProgressDialog();
//                        MyToast.show(activity, R.string.message_download_failed);
//                    }
//                });
//                return;
//            }
            String fileName = fileList.get(curIdx).getFileName();
            AppLog.d(TAG, "------------fileName =" + fileName);
            FileOper.createDirectory(path);
            downloadingFilename = path + fileName;
            downloadingFilesize = fileList.get(curIdx).iCatchFile.getFileSize();
            File tempFile = new File(downloadingFilename);
            //ICOM-4116 Begin modify by b.jiang 20170315
//            if (tempFile.exists()) {
//                handler.post( new Runnable() {
//                    @Override
//                    public void run() {
//                        MyProgressDialog.closeProgressDialog();
//                        MyToast.show( activity, "Downloaded to" + AppInfo.DOWNLOAD_PATH_PHOTO );
//                    }
//                } );
//            } else {
            curFilePath = FileTools.chooseUniqueFilename(downloadingFilename);
            boolean temp = fileOperation.downloadFile(fileList.get(curIdx).iCatchFile, curFilePath);
            //ICOM-4116 End modify by b.jiang 20170315
            if (temp == false) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        MyToast.show(activity, R.string.message_download_failed);
                    }
                });
                AppInfo.isDownloading = false;
                return;
            }
            MediaRefresh.scanFileAsync(activity, downloadingFilename);
            AppLog.d(TAG, "end downloadFile temp =" + temp);
            AppInfo.isDownloading = false;
            final String message = activity.getResources().getString(R.string.message_download_to).replace("$1$", path);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();
                    MyToast.show(activity, message);
                }
            });
            AppLog.d(TAG, "end DownloadThread");
        }
    }

    private class DeleteThread implements Runnable {
        @Override
        public void run() {
            curPhotoIdx = photoPbView.getViewPagerCurrentItem();
            ICatchFile curFile = fileList.get(curPhotoIdx).iCatchFile;
            Boolean retValue = false;
            retValue = fileOperation.deleteFile(curFile);
            if (retValue == false) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        MyToast.show(activity, R.string.dialog_delete_failed_single);
                    }
                });
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        RemoteFileHelper.getInstance().remove(fileList.get(curPhotoIdx), FileType.FILE_PHOTO);
                        fileList.remove(curPhotoIdx);
                        viewPagerAdapter.notifyDataSetChanged();
                        photoPbView.setViewPagerAdapter(viewPagerAdapter);
                        int photoNums = fileList.size();
                        if (photoNums == 0) {
                            back();
                            return;
                        } else {
                            if (curPhotoIdx == photoNums) {
                                curPhotoIdx--;
                            }
                            AppLog.d(TAG, "photoNums=" + photoNums + " curPhotoIdx=" + curPhotoIdx);
                            photoPbView.setViewPagerCurrentItem(curPhotoIdx);
                            updateUi();
                        }
                    }
                });
            }
            AppLog.d(TAG, "end DeleteThread");
        }
    }

    private void updateUi() {
        int curIndex = photoPbView.getViewPagerCurrentItem();
        String indexInfo = (curIndex + 1) + "/" + fileList.size();
        photoPbView.setIndexInfoTxv(indexInfo);
        MultiPbItemInfo itemInfo = fileList.get(curIndex);
        photoPbView.setSurfaceviewVisibility(itemInfo.isPanorama() ? View.VISIBLE : View.GONE);
        photoPbView.setViewPagerVisibility(itemInfo.isPanorama() ? View.GONE : View.VISIBLE);
        if(itemInfo.isPanorama() && surfaceCreated){
            loadPanoramaPhoto(itemInfo);
        }
    }

    public void showDownloadEnsureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_downloading_single);
        long videoFileSize = 0;
        videoFileSize = fileList.get(curPhotoIdx).getFileSizeInteger() / 1024 / 1024;
        long minute = videoFileSize / 60;
        long seconds = videoFileSize % 60;
        CharSequence what = activity.getResources().getString(R.string.gallery_download_with_vid_msg).replace("$1$", "1").replace("$3$", String.valueOf
                (seconds)).replace("$2$", String.valueOf(minute));
        builder.setMessage(what);
        builder.setNegativeButton(R.string.gallery_download, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                AppLog.d(TAG, "showProgressDialog");
                downloadProcess = 0;
                if (SystemInfo.getSDFreeSize(activity) < fileList.get(curPhotoIdx).getFileSizeInteger()) {
                    dialog.dismiss();
                    MyToast.show(activity, R.string.text_sd_card_memory_shortage);
                } else {
                    MyProgressDialog.showProgressDialog(activity, R.string.dialog_downloading_single);
                    executor = Executors.newSingleThreadExecutor();
                    future = executor.submit(new DownloadThread(), null);
                }

            }
        });
        builder.setPositiveButton(R.string.gallery_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void showDeleteEnsureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(R.string.image_delete_des);
        builder.setNegativeButton(R.string.gallery_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 这里添加点击确定后的逻辑
                MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                executor = Executors.newSingleThreadExecutor();
                hasDeleted = true;
                future = executor.submit(new DeleteThread(), null);
            }
        });
        builder.setPositiveButton(R.string.gallery_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 这里添加点击确定后的逻辑
                dialog.dismiss();
            }
        });
        builder.create().show();
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
                this.setScale(scale_temp);
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
        if (event.sensor == null) {
            return;
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // 从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
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

    private void registerGyroscopeSensor() {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // 注册陀螺仪传感器，并设定传感器向应用中输出的时间间隔类型是SensorManager.SENSOR_DELAY_GAME(20000微秒)
        // SensorManager.SENSOR_DELAY_FASTEST(0微秒)：最快。最低延迟，一般不是特别敏感的处理不推荐使用，该模式可能在成手机电力大量消耗，由于传递的为原始数据，诉法不处理好会影响游戏逻辑和UI的性能
        // SensorManager.SENSOR_DELAY_GAME(20000微秒)：游戏。游戏延迟，一般绝大多数的实时性较高的游戏都是用该级别
        // SensorManager.SENSOR_DELAY_NORMAL(200000微秒):普通。标准延时，对于一般的益智类或EASY级别的游戏可以使用，但过低的采样率可能对一些赛车类游戏有跳帧现象
        // SensorManager.SENSOR_DELAY_UI(60000微秒):用户界面。一般对于屏幕方向自动旋转使用，相对节省电能和逻辑处理，一般游戏开发中不使用
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void removeGyroscopeListener() {
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

    private void loadPanoramaPhoto(final MultiPbItemInfo itemInfo) {
        if (itemInfo == null) {
            return;
        }
        if (itemInfo.isPanorama()) {
            String url = TutkUriUtil.getTutkOriginalUri(itemInfo.iCatchFile);
            ImageLoaderUtil.loadImage(url, new ImageLoaderUtil.OnLoadListener() {
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
                    ICatchFile iCatchFile = TutkUriUtil.getInfoOfUri(imageUri);
                    if (loadedImage != null && iCatchFile != null && iCatchFile.getFileHandle() == itemInfo.getFileHandle()) {
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

    public void release() {
        if (panoramaPhotoPlayback != null) {
            panoramaPhotoPlayback.release();
        }
    }

    public void initPanorama() {
        panoramaPhotoPlayback.pancamGLInit();
    }

    public void setPanoramaType() {
        if (!fileList.get(curPhotoIdx).isPanorama()) {
            MyToast.show(activity, R.string.non_360_picture_not_support_switch);
            return;
        }
        if (curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE) {
            panoramaPhotoPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID;
            photoPbView.setPanoramaTypeTxv(R.string.text_asteroid);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else if (curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID) {
            panoramaPhotoPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R;
            photoPbView.setPanoramaTypeTxv(R.string.text_vr);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            panoramaPhotoPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;
            photoPbView.setPanoramaTypeTxv(R.string.text_panorama);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        loadPanoramaImage();
    }

    public void setSdCardEventListener() {
        GlobalInfo.getInstance().setOnEventListener(new GlobalInfo.OnEventListener() {
            @Override
            public void eventListener(int sdkEventId) {
                switch (sdkEventId){
                    case SDKEvent.EVENT_SDCARD_REMOVED:
                        RemoteFileHelper.getInstance().clearAllFileList();
                        AppDialog.showDialogWarn(activity, R.string.dialog_card_removed_and_back_photo_pb, false,new AppDialog.OnDialogSureClickListener() {
                            @Override
                            public void onSure() {
                                back();
                            }
                        });
                        break;
//                    case SDKEvent.EVENT_SDCARD_INSERT:
//                        MyToast.show(activity,R.string.dialog_card_inserted);
//                        break;
                }
            }
        });
    }
}


