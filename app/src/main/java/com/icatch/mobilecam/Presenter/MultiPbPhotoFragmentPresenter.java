package com.icatch.mobilecam.Presenter;

import android.app.Activity;
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
import com.icatch.mobilecam.utils.PanoramaTools;
import com.icatch.mobilecam.ui.activity.PhotoPbActivity;
import com.icatch.mobilecam.ui.Interface.MultiPbPhotoFragmentView;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchFileType;
import com.icatchtek.reliant.customer.type.ICatchFrameBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiPbPhotoFragmentPresenter extends BasePresenter {

    private String TAG = MultiPbPhotoFragmentPresenter.class.getSimpleName();
    private MultiPbPhotoFragmentView multiPbPhotoView;
    private MultiPbPhotoWallListAdapter photoWallListAdapter;
    private MultiPbPhotoWallGridAdapter photoWallGridAdapter;
    private Activity activity;
    private static int section = 1;
    private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
    private int width;
    // 记录是否是第一次进入该界面
    private boolean isFirstEnterThisActivity = true;
    private int topVisiblePosition = -1;
    private OperationMode curOperationMode = OperationMode.MODE_BROWSE;
    private LimitQueue<Asytask> asytaskList;
    private LruCache<Integer, Bitmap> mLruCache;
    private List<MultiPbItemInfo> pbItemInfoList;
    private FileOperation fileOperation = CameraManager.getInstance().getCurCamera().getFileOperation();
    private Handler handler;

    public MultiPbPhotoFragmentPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        asytaskList = new LimitQueue<Asytask>(SystemInfo.getWindowVisibleCountMax(activity.getApplicationContext(), 4));
        mLruCache = GlobalInfo.getInstance().mLruCache;
        handler = new Handler();
    }

    public void setView(MultiPbPhotoFragmentView localPhotoWallView) {
        this.multiPbPhotoView = localPhotoWallView;
        initCfg();
    }

    public List<MultiPbItemInfo> getPhotoInfoList() {
        String fileDate;
        List<MultiPbItemInfo> photoInfoList = new ArrayList<MultiPbItemInfo>();
        List<ICatchFile> fileList = null;
        if (GlobalInfo.getInstance().getRemotePhotoList() != null) {
            photoInfoList = GlobalInfo.getInstance().getRemotePhotoList();
        } else {
            fileList = fileOperation.getFileList(ICatchFileType.ICH_FILE_TYPE_IMAGE);
            AppLog.i(TAG, "fileList size=" + fileList.size());
            for (int ii = 0; ii < fileList.size(); ii++) {
                fileDate = fileList.get(ii).getFileDate();
                AppLog.i(TAG, "fileDate=" + fileDate);
                if (fileDate == null || fileDate.isEmpty()) {
                    fileDate = "unknown";
                } else if (fileDate.contains("T") == false) {

                } else {
                    int position = fileDate.indexOf("T");
                    fileDate = fileDate.substring(0, position);
                }
                AppLog.d(TAG, " fileDate=[" + fileDate + "]");

                if (!sectionMap.containsKey(fileDate)) {
                    sectionMap.put(fileDate, section);
                    MultiPbItemInfo mGridItem = new MultiPbItemInfo(fileList.get(ii), sectionMap.get(fileDate), PanoramaTools.isPanorama(fileList.get(ii)
                            .getFileWidth(), fileList.get(ii).getFileHeight()));
                    photoInfoList.add(mGridItem);
                    section++;
                } else {
                    MultiPbItemInfo mGridItem = new MultiPbItemInfo(fileList.get(ii), sectionMap.get(fileDate), PanoramaTools.isPanorama(fileList.get(ii)
                            .getFileWidth(), fileList.get(ii).getFileHeight()));
                    photoInfoList.add(mGridItem);
                }
            }
            GlobalInfo.getInstance().setRemotePhotoList(photoInfoList);
        }
        return photoInfoList;
    }

    public void loadPhotoWall() {
        MyProgressDialog.showProgressDialog(activity, R.string.message_loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                pbItemInfoList = getPhotoInfoList();
                AppLog.d(TAG, "pbItemInfoList=" + pbItemInfoList);
                if (pbItemInfoList == null || pbItemInfoList.size() <= 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            multiPbPhotoView.setGridViewVisibility(View.GONE);
                            multiPbPhotoView.setListViewVisibility(View.GONE);
                            multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
//                            MyToast.show(activity, "no file");
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
                            setAdaper();
                            MyProgressDialog.closeProgressDialog();
                        }
                    });
                }
            }
        }).start();
    }

    public void setAdaper() {
        curOperationMode = OperationMode.MODE_BROWSE;
        if (pbItemInfoList == null || pbItemInfoList.size() < 0) {
//            multiPbPhotoView.setRecyclerViewAdapter(null);
//            multiPbPhotoView.setGridViewAdapter(null);
            return;
        }
        String fileDate = pbItemInfoList.get(0).getFileDate();
        AppLog.d(TAG, "fileDate=" + fileDate);
        multiPbPhotoView.setListViewHeaderText(fileDate);
        int curWidth = 0;
        isFirstEnterThisActivity = true;
        if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            multiPbPhotoView.setGridViewVisibility(View.GONE);
            multiPbPhotoView.setListViewVisibility(View.VISIBLE);
            photoWallListAdapter = new MultiPbPhotoWallListAdapter(activity, pbItemInfoList, mLruCache, FileType.FILE_PHOTO);
            multiPbPhotoView.setListViewAdapter(photoWallListAdapter);
        } else {
            width = SystemInfo.getMetrics(activity.getApplicationContext()).widthPixels;
            multiPbPhotoView.setGridViewVisibility(View.VISIBLE);
            multiPbPhotoView.setListViewVisibility(View.GONE);
            AppLog.d(TAG, "width=" + curWidth);
            photoWallGridAdapter = (new MultiPbPhotoWallGridAdapter(activity, pbItemInfoList, width, mLruCache, FileType.FILE_PHOTO, new OnAddAsytaskListener
                    () {
                @Override
                public void addAsytask(int position) {
                    Asytask task = new Asytask(pbItemInfoList.get(position).iCatchFile);
                    asytaskList.offer(task);
                }
            }));
            multiPbPhotoView.setGridViewAdapter(photoWallGridAdapter);
        }
    }

    public void refreshPhotoWall() {
        Log.i("1122", "refreshPhotoWall layoutType=" + AppInfo.photoWallLayoutType);
        pbItemInfoList = getPhotoInfoList();
        if (pbItemInfoList == null || pbItemInfoList.size() <= 0) {
            multiPbPhotoView.setGridViewVisibility(View.GONE);
            multiPbPhotoView.setListViewVisibility(View.GONE);
            multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
        } else {
            multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
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

    public void changePreviewType() {
        if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            AppInfo.photoWallLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_GRID;
        } else {
            AppInfo.photoWallLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_LIST;
        }
        loadPhotoWall();
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
        if (firstVisibleItem != topVisiblePosition) {
            topVisiblePosition = firstVisibleItem;
            if (pbItemInfoList != null && pbItemInfoList.size() > 0) {
                String fileDate = pbItemInfoList.get(firstVisibleItem).getFileDate();
                AppLog.d(TAG, "fileDate=" + fileDate);
                multiPbPhotoView.setListViewHeaderText(fileDate);
            }
        }
        if (isFirstEnterThisActivity && visibleItemCount > 0) {
            loadBitmaps(firstVisibleItem, visibleItemCount);
            isFirstEnterThisActivity = false;
        }
    }


    public void gridViewLoadThumbnails(int scrollState, int firstVisibleItem, int visibleItemCount) {
        AppLog.d(TAG, "onScrollStateChanged scrollState=" + scrollState);
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (asytaskList != null && asytaskList.size() > 0) {
                asytaskList.poll().execute();
            }
        }
    }

    public void gridViewLoadOnceThumbnails(int firstVisibleItem, int visibleItemCount) {
        AppLog.d(TAG, "onScroll firstVisibleItem=" + firstVisibleItem + " visibleItemCount=" + visibleItemCount);
        AppLog.d(TAG, "onScroll isFirstEnterThisActivity=" + isFirstEnterThisActivity);
        if (isFirstEnterThisActivity && visibleItemCount > 0) {
            if (asytaskList != null && asytaskList.size() > 0) {
                asytaskList.poll().execute();
            }
            isFirstEnterThisActivity = false;
        }
    }

    void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        AppLog.i(TAG, "add task loadBitmaps firstVisibleItem=" + firstVisibleItem + " visibleItemCount" + visibleItemCount);
        int fileHandle;
        if (asytaskList == null) {
            asytaskList = new LimitQueue<>(SystemInfo.getWindowVisibleCountMax(activity.getApplicationContext(), 4));
        }
        for (int ii = firstVisibleItem; ii < firstVisibleItem + visibleItemCount; ii++) {
            if (pbItemInfoList != null && pbItemInfoList.size() > 0 && ii < pbItemInfoList.size()) {
                Asytask task = new Asytask(pbItemInfoList.get(ii).iCatchFile);
                asytaskList.offer(task);
                AppLog.i(TAG, "add task loadBitmaps ii=" + ii);
            }
        }
        if (asytaskList != null && asytaskList.size() > 0) {
            asytaskList.poll().execute();
        }
    }


    public void listViewEnterEditMode(int position) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            curOperationMode = OperationMode.MODE_EDIT;
            multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            photoWallListAdapter.setOperationMode(curOperationMode);
            photoWallListAdapter.changeSelectionState(position);
            multiPbPhotoView.setPhotoSelectNumText(photoWallListAdapter.getSelectedCount());
            AppLog.i(TAG, "gridViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
        }
    }

    public void gridViewEnterEditMode(int position) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            curOperationMode = OperationMode.MODE_EDIT;
            multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            photoWallGridAdapter.changeCheckBoxState(position, curOperationMode);
            multiPbPhotoView.setPhotoSelectNumText(photoWallGridAdapter.getSelectedCount());
            AppLog.i(TAG, "gridViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
        }
    }

    public void quitEditMode() {
        if (curOperationMode == OperationMode.MODE_EDIT) {
            curOperationMode = OperationMode.MODE_BROWSE;
            multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.quitEditMode();
            } else {
                photoWallGridAdapter.quitEditMode();
            }
        }
    }

    public void listViewSelectOrCancelOnce(int position) {
        AppLog.i(TAG, "listViewSelectOrCancelOnce positon=" + position + " AppInfo.photoWallPreviewType=" + AppInfo.photoWallLayoutType);
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            AppLog.i(TAG, "listViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
            clealAsytaskList();
//            GlobalInfo.getInstance().initRemotePhotoListInfo();
            Intent intent = new Intent();
            intent.putExtra("curfilePosition", position);
            intent.setClass(activity, PhotoPbActivity.class);
            activity.startActivity(intent);
        } else {
            photoWallListAdapter.changeSelectionState(position);
            multiPbPhotoView.setPhotoSelectNumText(photoWallListAdapter.getSelectedCount());
        }

    }

    public void gridViewSelectOrCancelOnce(int position) {
        AppLog.i(TAG, "gridViewSelectOrCancelOnce positon=" + position + " AppInfo.photoWallPreviewType=" + AppInfo.photoWallLayoutType);
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            clealAsytaskList();
//            GlobalInfo.getInstance().initRemotePhotoListInfo();
            Intent intent = new Intent();
            intent.putExtra("curfilePosition", position);
            intent.setClass(activity, PhotoPbActivity.class);
            intent.setClass(activity, PhotoPbActivity.class);
            activity.startActivity(intent);
        } else {
            photoWallGridAdapter.changeCheckBoxState(position, curOperationMode);
            multiPbPhotoView.setPhotoSelectNumText(photoWallGridAdapter.getSelectedCount());
        }

    }


    public void selectOrCancelAll(boolean isSelectAll) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
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
            multiPbPhotoView.setPhotoSelectNumText(selectNum);
        } else {
            if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.cancelAllSelections();
                selectNum = photoWallListAdapter.getSelectedCount();
            } else {
                photoWallGridAdapter.cancelAllSelections();
                selectNum = photoWallGridAdapter.getSelectedCount();
            }
            multiPbPhotoView.setPhotoSelectNumText(selectNum);
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
            this.file = file;
            fileHandle = file.getFileHandle();
        }

        @Override
        protected Bitmap doInBackground(String... params) {//处理后台执行的任务，在后台线程执行
            Bitmap bm = getBitmapFromLruCache(fileHandle);
            AppLog.d(TAG, "getBitmapFromLruCache fileHandle=" + fileHandle + " bm=" + bm);
            if (bm != null) {
                return bm;
            } else {
//                ICatchFile file = new ICatchFile(fileHandle);
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
                if (bm != null) {
                    addBitmapToLruCache(fileHandle, bm);
                }
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
                imageView = (ImageView) multiPbPhotoView.gridViewFindViewWithTag(fileHandle);
            } else {
                imageView = (ImageView) multiPbPhotoView.listViewFindViewWithTag(fileHandle);
            }
            //imageView = (ImageView) mGridView.getChildAt(ii).findViewById(R.id.local_photo_wall_grid_item);
            AppLog.i(TAG, "loadBitmaps imageView=" + imageView);
            if (imageView != null && !result.isRecycled()) {
                imageView.setImageBitmap(result);
            }

            if (asytaskList != null && asytaskList.size() > 0) {

                Log.i("1111", "eeeee");
                asytaskList.poll().execute();
            }
        }
    }

    public Bitmap getBitmapFromLruCache(int fileHandle) {

        return mLruCache.get(fileHandle);
    }

    protected void addBitmapToLruCache(int fileHandle, Bitmap bm) {
        if (getBitmapFromLruCache(fileHandle) == null) {
            if (bm != null && fileHandle != 0) {
                AppLog.d(TAG, "addBitmapToLruCache fileHandle=" + fileHandle);
                mLruCache.put(fileHandle, bm);
            }

        }
    }

    public void emptyFileList() {
        if (GlobalInfo.getInstance().getRemotePhotoList() != null) {
            GlobalInfo.getInstance().setRemotePhotoList(null);
        }
    }

    public void clealAsytaskList() {
        AppLog.d(TAG, "clealAsytaskList");
        if (asytaskList != null && asytaskList.size() > 0) {
            asytaskList.clear();
//            asytaskList = null;
        }
    }

    public interface OnGetListCompleteListener {
        void onGetFileListComplete();
    }
}