package com.icatch.mobilecam.Presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.view.View;


import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.Presenter.Interface.BasePresenter;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.data.entity.LocalPbItemInfo;
import com.icatch.mobilecam.data.type.FileType;
import com.icatch.mobilecam.data.type.PhotoWallLayoutType;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.Interface.LocalMultiPbFragmentView;
import com.icatch.mobilecam.ui.activity.LocalPhotoPbActivity;
import com.icatch.mobilecam.ui.activity.LocalVideoPbActivity;
import com.icatch.mobilecam.ui.adapter.LocalMultiPbWallGridAdapter;
import com.icatch.mobilecam.ui.adapter.LocalMultiPbWallListAdapter;
import com.icatch.mobilecam.utils.StorageUtil;
import com.icatch.mobilecam.utils.fileutils.MFileTools;
import com.icatch.mobilecam.utils.PanoramaTools;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by b.jiang on 2017/5/19.
 */

public class LocalMultiPbFragmentPresenter extends BasePresenter {

    private String TAG = LocalMultiPbFragmentPresenter.class.getSimpleName();
    private LocalMultiPbFragmentView multiPbPhotoView;
    private LocalMultiPbWallListAdapter photoWallListAdapter;
    private LocalMultiPbWallGridAdapter photoWallGridAdapter;
    private Activity activity;
    private static int section = 1;
    private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
    private OperationMode curOperationMode = OperationMode.MODE_BROWSE;
    private List<LocalPbItemInfo> pbItemInfoList;
    private Handler handler;
    private FileType fileType = FileType.FILE_PHOTO;

    public LocalMultiPbFragmentPresenter(Activity activity, FileType fileType) {
        super(activity);
        this.activity = activity;
        handler = new Handler();
        this.fileType = fileType;
    }

    public void setView(LocalMultiPbFragmentView localPhotoWallView) {
        this.multiPbPhotoView = localPhotoWallView;
        initCfg();
    }

    public List<LocalPbItemInfo> getPhotoInfoList(FileType fileType) {
        String fileDate;
        String rootPath = StorageUtil.getRootPath(activity);
        final List<LocalPbItemInfo> photoList = new ArrayList<LocalPbItemInfo>();
        List<File> fileList;
        if (fileType == FileType.FILE_PHOTO) {
            String filePath = rootPath + AppInfo.DOWNLOAD_PATH_PHOTO;

            fileList = MFileTools.getPhotosOrderByDate(filePath);
        } else {
            String filePath = rootPath + AppInfo.DOWNLOAD_PATH_VIDEO;
            fileList = MFileTools.getVideosOrderByDate(filePath);
        }
        if (fileList == null || fileList.size() <= 0) {
            return null;
        }

        AppLog.i(TAG, "fileList size=" + fileList.size());
        for (int ii = 0; ii < fileList.size(); ii++) {
            long time = fileList.get(ii).lastModified();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            fileDate = format.format(new Date(time));

            if (!sectionMap.containsKey(fileDate)) {
                sectionMap.put(fileDate, section);
                LocalPbItemInfo mGridItem = new LocalPbItemInfo(fileList.get(ii), section, PanoramaTools.isPanorama
                        (fileList.get(ii).getPath()));
                photoList.add(mGridItem);
                section++;
            } else {
                LocalPbItemInfo mGridItem = new LocalPbItemInfo(fileList.get(ii), sectionMap.get(fileDate),
                        PanoramaTools.isPanorama(fileList.get(ii).getPath()));
                photoList.add(mGridItem);
            }
        }

        if (fileType == FileType.FILE_PHOTO) {
            GlobalInfo.getInstance().setLocalPhotoList(photoList);
        } else {
            GlobalInfo.getInstance().setLocalVideoList(photoList);
        }
        return photoList;
    }

    public void loadPhotoWall() {
        MyProgressDialog.showProgressDialog(activity, "Loading...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                pbItemInfoList =  getPhotoInfoList(fileType);
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
        if (pbItemInfoList != null && pbItemInfoList.size() > 0) {
            String fileDate = pbItemInfoList.get(0).getFileDate();
            AppLog.d(TAG, "fileDate=" + fileDate);
            multiPbPhotoView.setListViewHeaderText(fileDate);
        }
        int curWidth = 0;
        if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            multiPbPhotoView.setGridViewVisibility(View.GONE);
            multiPbPhotoView.setListViewVisibility(View.VISIBLE);
            photoWallListAdapter = new LocalMultiPbWallListAdapter(activity, pbItemInfoList, FileType.FILE_PHOTO);
            multiPbPhotoView.setListViewAdapter(photoWallListAdapter);
        } else {
            multiPbPhotoView.setGridViewVisibility(View.VISIBLE);
            multiPbPhotoView.setListViewVisibility(View.GONE);
            AppLog.d(TAG, "width=" + curWidth);
            photoWallGridAdapter = (new LocalMultiPbWallGridAdapter(activity, pbItemInfoList, FileType.FILE_PHOTO));
            multiPbPhotoView.setGridViewAdapter(photoWallGridAdapter);
        }
    }

    public void refreshPhotoWall() {
        AppLog.d(TAG, "refreshPhotoWall layoutType=" + AppInfo.photoWallLayoutType);
        pbItemInfoList = getPhotoInfoList(fileType);
        if (pbItemInfoList == null || pbItemInfoList.size() <= 0) {
            multiPbPhotoView.setGridViewVisibility(View.GONE);
            multiPbPhotoView.setListViewVisibility(View.GONE);
            multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
        } else {
            multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
            setAdaper();
        }
    }

    public void changePreviewType() {
        if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            AppInfo.photoWallLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_GRID;
        } else {
            AppInfo.photoWallLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_LIST;
        }
        loadPhotoWall();
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
        String videoPath = pbItemInfoList.get(position).getFilePath();
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            AppLog.i(TAG, "listViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
            if (fileType == FileType.FILE_PHOTO) {
                Intent intent = new Intent();
                intent.putExtra("curfilePosition", position);
                intent.setClass(activity, LocalPhotoPbActivity.class);
                activity.startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.putExtra("curfilePath", videoPath);
                intent.putExtra("curfilePosition", position);
                intent.setClass(activity, LocalVideoPbActivity.class);
                activity.startActivity(intent);
            }

        } else {
            photoWallListAdapter.changeSelectionState(position);
            multiPbPhotoView.setPhotoSelectNumText(photoWallListAdapter.getSelectedCount());
        }

    }

    public void gridViewSelectOrCancelOnce(int position) {
        AppLog.i(TAG, "gridViewSelectOrCancelOnce positon=" + position + " AppInfo.photoWallPreviewType=" + AppInfo.photoWallLayoutType);
        String videoPath = pbItemInfoList.get(position).getFilePath();
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            if (fileType == FileType.FILE_PHOTO) {
                Intent intent = new Intent();
                intent.putExtra("curfilePosition", position);
                intent.setClass(activity, LocalPhotoPbActivity.class);
                activity.startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.putExtra("curfilePath", videoPath);
                intent.putExtra("curfilePosition", position);
                intent.setClass(activity, LocalVideoPbActivity.class);
                activity.startActivity(intent);
            }
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

    public List<LocalPbItemInfo> getSelectedList() {
        if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            return photoWallListAdapter.getSelectedList();
        } else {
            return photoWallGridAdapter.getCheckedItemsList();
        }
    }
}
