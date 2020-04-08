package com.icatch.mobilecam.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.ui.adapter.MultiPbPhotoWallGridAdapter;
import com.icatch.mobilecam.ui.adapter.MultiPbPhotoWallListAdapter;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.type.FileType;
import com.icatch.mobilecam.data.entity.LimitQueue;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.data.type.PhotoWallLayoutType;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.Listener.OnAddAsytaskListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.Presenter.Interface.BasePresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.FileOperation;
import com.icatch.mobilecam.data.SystemInfo.SystemInfo;
import com.icatch.mobilecam.ui.activity.VideoPbActivity;
import com.icatch.mobilecam.ui.Interface.MultiPbVideoFragmentView;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchFileType;
import com.icatchtek.reliant.customer.type.ICatchFrameBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MultiPbVideoFragmentPresenter extends BasePresenter {

    private String TAG = MultiPbVideoFragmentPresenter.class.getSimpleName();
    private MultiPbVideoFragmentView videoView;
    private MultiPbPhotoWallListAdapter photoWallListAdapter;
    private MultiPbPhotoWallGridAdapter photoWallGridAdapter;
    private Activity activity;

    private static int section = 1;
    private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
    private int width;
    // 记录是否是第一次进入该界面
    private boolean isFirstEnterThisActivity = true;
    private int topVisiblePosition = -1;
    private OperationMode operationMode = OperationMode.MODE_BROWSE;
    private LimitQueue<Asytask> asytaskList;
    private Asytask curAsytask;
    private LruCache<Integer, Bitmap> mLruCache;
    private List<MultiPbItemInfo> videoInfoList;
    private FileOperation fileOperation = CameraManager.getInstance().getCurCamera().getFileOperation();
    private Handler handler;

    public MultiPbVideoFragmentPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        this.asytaskList = new LimitQueue<Asytask>(SystemInfo.getWindowVisibleCountMax(activity.getApplicationContext
                (), 4));
        this.mLruCache = GlobalInfo.getInstance().mLruCache;
        handler = new Handler();
    }

    public void setView(MultiPbVideoFragmentView multiPbVideoView) {
        this.videoView = multiPbVideoView;
        initCfg();
    }

    public List<MultiPbItemInfo> getVideoList() {
        String fileDate;
        List<MultiPbItemInfo> videoInfoList = new ArrayList<MultiPbItemInfo>();
        if (GlobalInfo.getInstance().remoteVideoList != null) {
            videoInfoList = GlobalInfo.getInstance().remoteVideoList;
        } else {
            List<ICatchFile> fileList = fileOperation.getFileList(ICatchFileType.ICH_FILE_TYPE_VIDEO);
            Log.d(TAG, "fileList size=" + fileList.size());
            AppLog.d(TAG, "fileList size=" + fileList.size());

            for (int ii = 0; ii < fileList.size(); ii++) {
                fileDate = fileList.get(ii).getFileDate();
                int position = fileDate.indexOf("T");
                fileDate = fileDate.substring(0, position);
                AppLog.d(TAG, " fileDate=[" + fileDate + "]");

                if (!sectionMap.containsKey(fileDate)) {
                    sectionMap.put(fileDate, section);
                    MultiPbItemInfo mGridItem = new MultiPbItemInfo(fileList.get(ii), sectionMap.get(fileDate));
                    videoInfoList.add(mGridItem);
                    section++;
                } else {
                    MultiPbItemInfo mGridItem = new MultiPbItemInfo(fileList.get(ii), sectionMap.get(fileDate));
                    videoInfoList.add(mGridItem);
                }
            }
            GlobalInfo.getInstance().remoteVideoList = videoInfoList;
        }
        return videoInfoList;

    }

    public void loadVideoWall() {
        MyProgressDialog.showProgressDialog(activity, R.string.message_loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                videoInfoList = getVideoList();
                if (videoInfoList == null || videoInfoList.size() <= 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            videoView.setGridViewVisibility(View.GONE);
                            videoView.setListViewVisibility(View.GONE);
                            videoView.setNoContentTxvVisibility(View.VISIBLE);
//                            MyToast.show(activity, "no file");
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            videoView.setNoContentTxvVisibility(View.GONE);
                            setAdaper();
                            MyProgressDialog.closeProgressDialog();
                        }
                    });
                }
            }
        }).start();
    }

    public void setAdaper() {
        operationMode = OperationMode.MODE_BROWSE;
        int curWidth = 0;
        isFirstEnterThisActivity = true;
        if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            videoView.setGridViewVisibility(View.GONE);
            videoView.setListViewVisibility(View.VISIBLE);
            photoWallListAdapter = new MultiPbPhotoWallListAdapter(activity, videoInfoList, mLruCache, FileType.FILE_VIDEO);
            videoView.setListViewAdapter(photoWallListAdapter);
        } else {
            width = SystemInfo.getMetrics(activity.getApplicationContext()).widthPixels;
            videoView.setGridViewVisibility(View.VISIBLE);
            videoView.setListViewVisibility(View.GONE);
            AppLog.d(TAG, "width=" + curWidth);
            photoWallGridAdapter = (new MultiPbPhotoWallGridAdapter(activity, videoInfoList, width, mLruCache, FileType.FILE_VIDEO, new OnAddAsytaskListener() {
                @Override
                public void addAsytask(int position) {
                    Asytask task = new Asytask(videoInfoList.get(position).iCatchFile);
                    asytaskList.offer(task);
                }
            }));
            videoView.setGridViewAdapter(photoWallGridAdapter);
        }
    }

    public void changePreviewType() {
        if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            AppInfo.photoWallLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_GRID;
        } else {
            AppInfo.photoWallLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_LIST;
        }
        loadVideoWall();
//        setAdaper();
    }


    public void refreshPhotoWall() {
        Log.i("1122", "refreshPhotoWall AppInfo.photoWallPreviewType=" + AppInfo.photoWallLayoutType);
        videoInfoList = getVideoList();
        if (videoInfoList == null || videoInfoList.size() <= 0) {
            videoView.setGridViewVisibility(View.GONE);
            videoView.setListViewVisibility(View.GONE);
            videoView.setNoContentTxvVisibility(View.VISIBLE);
        } else {
            videoView.setNoContentTxvVisibility(View.GONE);
            setAdaper();
        }
//        if (AppInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
//            if (photoWallListAdapter != null) {
//                photoWallListAdapter.notifyDataSetChanged();
//            }
//        } else {
//            if (photoWallGridAdapter != null) {
//                photoWallGridAdapter.notifyDataSetChanged();
//            }
//        }
    }

    public void listViewLoadThumbnails(int scrollState, int firstVisibleItem, int visibleItemCount) {
        AppLog.d(TAG, "onScrollStateChanged");
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            AppLog.d(TAG, "onScrollStateChanged firstVisibleItem=" + firstVisibleItem + " visibleItemCount=" + visibleItemCount);
            asytaskList.clear();
            loadBitmaps(firstVisibleItem, visibleItemCount);
        } else {
            asytaskList.clear();
        }
    }

    public void listViewLoadOnceThumbnails(int firstVisibleItem, int visibleItemCount) {
        AppLog.d(TAG, "onScroll firstVisibleItem=" + firstVisibleItem);
        if (videoInfoList == null || videoInfoList.size() <= 0) {
            return;
        }
        if (firstVisibleItem != topVisiblePosition && videoInfoList.size() > 0) {
            topVisiblePosition = firstVisibleItem;
            String fileDate = videoInfoList.get(firstVisibleItem).getFileDate();
            AppLog.d(TAG, "fileDate=" + fileDate);
            videoView.setListViewHeaderText(fileDate);
        }
        if (isFirstEnterThisActivity && visibleItemCount > 0) {
            loadBitmaps(firstVisibleItem, visibleItemCount);
            isFirstEnterThisActivity = false;
        }
    }

    public void gridViewLoadThumbnails(int scrollState, int firstVisibleItem, int visibleItemCount) {
        AppLog.d(TAG, "onScrollStateChanged scrollState=" + scrollState);
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            AppLog.d(TAG, "onScrollStateChanged firstVisibleItem=" + firstVisibleItem + " visibleItemCount=" + visibleItemCount);
            if (asytaskList != null && asytaskList.size() > 0) {
                curAsytask = asytaskList.poll();
                curAsytask.execute();
            }
        }
    }

    public void gridViewLoadOnceThumbnails(int firstVisibleItem, int visibleItemCount) {
        AppLog.d(TAG, "onScroll firstVisibleItem=" + firstVisibleItem + " visibleItemCount=" + visibleItemCount);
        if (videoInfoList == null || videoInfoList.size() <= 0) {
            return;
        }
        if (isFirstEnterThisActivity && visibleItemCount > 0) {
            if (asytaskList != null && asytaskList.size() > 0) {
                curAsytask = asytaskList.poll();
                curAsytask.execute();
            }
            isFirstEnterThisActivity = false;
        }
    }

    void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        AppLog.i(TAG, "add task loadBitmaps 111111 asytaskList=" + asytaskList);
        int fileHandle;
        if (asytaskList == null) {
            asytaskList = new LimitQueue<Asytask>(SystemInfo.getWindowVisibleCountMax(activity.getApplicationContext
                    (), 4));
        }
        for (int ii = firstVisibleItem; ii < firstVisibleItem + visibleItemCount; ii++) {
            if (videoInfoList != null && videoInfoList.size() > 0 && ii < videoInfoList.size()) {
                Asytask task = new Asytask(videoInfoList.get(ii).iCatchFile);
                asytaskList.offer(task);
                AppLog.i(TAG, "add task loadBitmaps ii=" + ii);
            }
        }
        if (asytaskList != null && asytaskList.size() > 0) {
            curAsytask = asytaskList.poll();
            curAsytask.execute();
        }
    }

    public void redirectToAnotherActivity(Context context, Class<?> cls, int position) {
        Intent intent = new Intent();
        intent.putExtra("curfilePath", videoInfoList.get(position).getFilePath());
        AppLog.i(TAG, "intent:start redirectToAnotherActivity class =" + cls.getName());
        intent.setClass(context, cls);
        context.startActivity(intent);
    }

    public void listViewEnterEditMode(int position) {
        if (operationMode == OperationMode.MODE_BROWSE) {
            operationMode = OperationMode.MODE_EDIT;
            videoView.changeMultiPbMode(operationMode);
            photoWallListAdapter.setOperationMode(operationMode);
            photoWallListAdapter.changeSelectionState(position);
            videoView.setVideoSelectNumText(photoWallListAdapter.getSelectedCount());
            Log.d(TAG, "gridViewSelectOrCancelOnce operationMode=" + operationMode);
        }
    }

    public void gridViewEnterEditMode(int position) {
        if (operationMode == OperationMode.MODE_BROWSE) {
            operationMode = OperationMode.MODE_EDIT;
            videoView.changeMultiPbMode(operationMode);
            photoWallGridAdapter.changeCheckBoxState(position, operationMode);
            videoView.setVideoSelectNumText(photoWallGridAdapter.getSelectedCount());
            Log.d(TAG, "gridViewSelectOrCancelOnce operationMode=" + operationMode);
        }
    }

    public void quitEditMode() {
        if (operationMode == OperationMode.MODE_EDIT) {
            operationMode = OperationMode.MODE_BROWSE;
            videoView.changeMultiPbMode(operationMode);
            if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.quitEditMode();
            } else {
                photoWallGridAdapter.quitEditMode();
            }
        }
    }

    public void listViewSelectOrCancelOnce(int position) {
        Log.d(TAG, "listViewSelectOrCancelOnce position=" + position + " operationMode=" + operationMode);
        if (operationMode == OperationMode.MODE_BROWSE) {
//            redirectToAnotherActivity(activity, VideoPbActivity.class);
            clealAsytaskList();
            Intent intent = new Intent();
            intent.putExtra("curfilePosition", position);
            intent.setClass(activity, VideoPbActivity.class);
            activity.startActivity(intent);
        } else {
            photoWallListAdapter.changeSelectionState(position);
            videoView.setVideoSelectNumText(photoWallListAdapter.getSelectedCount());
        }
    }

    public void gridViewSelectOrCancelOnce(final int position) {
        Log.d(TAG, "gridViewSelectOrCancelOnce positon=" + position + " AppInfo.photoWallPreviewType=" + AppInfo.photoWallLayoutType);
        if (operationMode == OperationMode.MODE_BROWSE) {
            Log.d(TAG, "gridViewSelectOrCancelOnce operationMode=" + operationMode);

            clealAsytaskList();
            //ICOM-3428 Start ADD by b.jiang 20160713
            if (curAsytask != null) {
                boolean ret = curAsytask.cancel(true);
                AppLog.d(TAG, "curAsytask cancal ret=" + ret);
            }
            Timer timer = new Timer();//实例化Timer类
            timer.schedule(new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                        }
                    });
                    AppLog.d(TAG, "curAsytask Thread.sleep(500)");
                    Intent intent = new Intent();
                    intent.putExtra("curfilePosition", position);
                    intent.setClass(activity, VideoPbActivity.class);
                    activity.startActivity(intent);
                }
            }, 500);//五百毫秒
            MyProgressDialog.showProgressDialog(activity, R.string.message_loading);

        } else {
            photoWallGridAdapter.changeCheckBoxState(position, operationMode);
            videoView.setVideoSelectNumText(photoWallGridAdapter.getSelectedCount());
        }
    }


    public void selectOrCancelAll(boolean isSelectAll) {
        if (operationMode == OperationMode.MODE_BROWSE) {
            return;
        }
        int selectNum;
        if (isSelectAll) {
            if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.selectAllItems();
                selectNum = photoWallListAdapter.getSelectedCount();
            } else {
                photoWallGridAdapter.selectAllItems();
                selectNum = photoWallGridAdapter.getSelectedCount();
            }
            videoView.setVideoSelectNumText(selectNum);
        } else {
            if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.cancelAllSelections();
                selectNum = photoWallListAdapter.getSelectedCount();
            } else {
                photoWallGridAdapter.cancelAllSelections();
                selectNum = photoWallGridAdapter.getSelectedCount();
            }
            videoView.setVideoSelectNumText(selectNum);
        }
    }

    public List<MultiPbItemInfo> getSelectedList() {
        if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            return photoWallListAdapter.getSelectedList();
        } else {
            return photoWallGridAdapter.getCheckedItemsList();
        }
    }

    class Asytask extends AsyncTask<String, Integer, Bitmap> {
        int fileHandle;
        ICatchFile file;

        public Asytask(ICatchFile file) {
            super();
            this.fileHandle = file.getFileHandle();
            this.file = file;
        }

        @Override
        protected Bitmap doInBackground(String... params) {//处理后台执行的任务，在后台线程执行
            Bitmap bm = getBitmapFromLruCache(fileHandle);

            if (bm != null) {
                return bm;
            } else {
                ICatchFrameBuffer buffer = fileOperation.getThumbnail(file);
                AppLog.d(TAG, "decodeByteArray buffer=" + buffer);
                AppLog.d(TAG, "decodeByteArray fileHandle=" + fileHandle);
                if (buffer == null) {
                    AppLog.e(TAG, "buffer == null  send _LOAD_BITMAP_FAILED");
                    return null;
                }

                int datalength = buffer.getFrameSize();
                if (datalength > 0) {
                    bm = BitmapFactory.decodeByteArray(buffer.getBuffer(), 0, datalength);
                }
                AppLog.d(TAG, "decodeByteArray bm=" + bm);
                addBitmapToLruCache(fileHandle, bm);
                return bm;
            }
        }

        protected void onProgressUpdate(Integer... progress) {//在调用publishProgress之后被调用，在ui线程执行
        }

        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                return;
            }
            //后台任务执行完之后被调用，在ui线程执行
            ImageView imageView;
            if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_GRID) {
                imageView = (ImageView) videoView.gridViewFindViewWithTag(fileHandle);
            } else {
                imageView = (ImageView) videoView.listViewFindViewWithTag(fileHandle);
            }
            AppLog.i(TAG, "loadBitmaps fileHandle=" + fileHandle + " imageView=" + imageView);
            if (imageView != null && !result.isRecycled()) {
                imageView.setImageBitmap(result);
            }
            if (asytaskList != null && asytaskList.size() > 0) {
                curAsytask = asytaskList.poll();
                curAsytask.execute();
            }
        }
    }

    public Bitmap getBitmapFromLruCache(int fileHandle) {
        return mLruCache.get(fileHandle);
    }

    protected void addBitmapToLruCache(int fileHandle, Bitmap bm) {
        if (getBitmapFromLruCache(fileHandle) == null) {
            if (bm != null && fileHandle != 0) {
                AppLog.d("test", "addBitmapToLruCache fileHandle=" + fileHandle);
                AppLog.d("test", "addBitmapToLruCache bitmap=" + bm);
                mLruCache.put(fileHandle, bm);
            }
        }
    }

    public void emptyFileList() {
        if (GlobalInfo.getInstance().remoteVideoList != null) {
            GlobalInfo.getInstance().remoteVideoList.clear();
            GlobalInfo.getInstance().remoteVideoList = null;
        }
    }

    public void clealAsytaskList() {
        AppLog.d(TAG, "clealAsytaskList");
        if (asytaskList != null && asytaskList.size() > 0) {
            asytaskList.clear();
        }
    }
}