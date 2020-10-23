package com.icatch.mobilecam.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.Presenter.Interface.BasePresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.FileOperation;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.data.SystemInfo.SystemInfo;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.data.type.FileType;
import com.icatch.mobilecam.data.type.PhotoWallLayoutType;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.ui.Interface.MultiPbPhotoFragmentView;
import com.icatch.mobilecam.ui.RemoteFileHelper;
import com.icatch.mobilecam.ui.activity.PhotoPbActivity;
import com.icatch.mobilecam.ui.activity.VideoPbActivity;
import com.icatch.mobilecam.ui.adapter.MultiPbPhotoWallGridAdapter;
import com.icatch.mobilecam.ui.adapter.MultiPbPhotoWallListAdapter;
import com.icatch.mobilecam.utils.ConvertTools;
import com.icatch.mobilecam.utils.PanoramaTools;
import com.icatch.mobilecam.utils.imageloader.ImageLoaderConfig;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchFileType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
    private List<MultiPbItemInfo> pbItemInfoList = new LinkedList<>();
    private FileOperation fileOperation = CameraManager.getInstance().getCurCamera().getFileOperation();
    private Handler handler;
    private FileType fileType;
    private PhotoWallLayoutType curLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_LIST;

    public MultiPbPhotoFragmentPresenter(Activity activity, FileType fileType) {
        super(activity);
        this.activity = activity;
        handler = new Handler();
        this.fileType = fileType;
    }

    public void setView(MultiPbPhotoFragmentView localPhotoWallView) {
        this.multiPbPhotoView = localPhotoWallView;
        initCfg();
    }

    public List<MultiPbItemInfo> getPhotoInfoList() {
        String fileDate;
        List<MultiPbItemInfo> photoInfoList = new ArrayList<MultiPbItemInfo>();
        List<ICatchFile> fileList = null;
        int iCatchFileType;
        if(fileType == FileType.FILE_PHOTO){
            iCatchFileType = ICatchFileType.ICH_FILE_TYPE_IMAGE;
        }else {
            iCatchFileType = ICatchFileType.ICH_FILE_TYPE_VIDEO;
        }
        List<MultiPbItemInfo> temp = RemoteFileHelper.getInstance().getLocalFileList(fileType);
        if (temp != null && temp.size() > 0) {
            photoInfoList.addAll(temp);
        } else {
            fileList = fileOperation.getFileList(iCatchFileType);
            AppLog.i(TAG, "fileList size=" + fileList.size());
            String fileSize;
            String fileTime;
            String fileDuration;
            boolean isPanorama;
            for (int ii = 0; ii < fileList.size(); ii++) {
                ICatchFile iCatchFile = fileList.get(ii);
                fileDate = ConvertTools.getTimeByfileDate(iCatchFile.getFileDate());
                fileSize = ConvertTools.ByteConversionGBMBKB(iCatchFile.getFileSize());
                fileTime = ConvertTools.getDateTimeString(iCatchFile.getFileDate());
                fileDuration = ConvertTools.millisecondsToMinuteOrHours(fileList.get(ii).getFileDuration());
                isPanorama = PanoramaTools.isPanorama(iCatchFile.getFileWidth(), iCatchFile.getFileHeight());
                if (!sectionMap.containsKey(fileDate)) {
                    sectionMap.put(fileDate, section);
                    MultiPbItemInfo mGridItem = new MultiPbItemInfo(fileList.get(ii), sectionMap.get(fileDate), isPanorama,fileSize,fileTime,fileDate,fileDuration);
                    photoInfoList.add(mGridItem);
                    section++;
                } else {
                    MultiPbItemInfo mGridItem = new MultiPbItemInfo(fileList.get(ii), sectionMap.get(fileDate), isPanorama,fileSize,fileTime,fileDate,fileDuration);
                    photoInfoList.add(mGridItem);
                }
            }
        }
        return photoInfoList;
    }

    public void loadPhotoWall() {
        MyProgressDialog.showProgressDialog(activity, R.string.message_loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                pbItemInfoList.clear();
                List<MultiPbItemInfo> tempList = getPhotoInfoList();
                if(tempList != null && tempList.size() > 0){
                    pbItemInfoList.addAll(tempList);
                }
                AppLog.d(TAG, "pbItemInfoList=" + pbItemInfoList);
                RemoteFileHelper.getInstance().setLocalFileList(pbItemInfoList, fileType);
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
        if (curLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            multiPbPhotoView.setGridViewVisibility(View.GONE);
            multiPbPhotoView.setListViewVisibility(View.VISIBLE);
            if (photoWallListAdapter != null) {
                photoWallListAdapter.notifyDataSetChanged();
            }else {
                photoWallListAdapter = new MultiPbPhotoWallListAdapter(activity, pbItemInfoList, fileType);
                multiPbPhotoView.setListViewAdapter(photoWallListAdapter);
            }

        } else {
            width = SystemInfo.getMetrics(activity.getApplicationContext()).widthPixels;
            multiPbPhotoView.setGridViewVisibility(View.VISIBLE);
            multiPbPhotoView.setListViewVisibility(View.GONE);
            AppLog.d(TAG, "width=" + curWidth);
            if (photoWallGridAdapter != null) {
                photoWallGridAdapter.notifyDataSetChanged();
            }else {
                photoWallGridAdapter = new MultiPbPhotoWallGridAdapter(activity, pbItemInfoList, fileType);
                multiPbPhotoView.setGridViewAdapter(photoWallGridAdapter);
            }
        }
    }

    public void refreshPhotoWall() {
        Log.i("1122", "refreshPhotoWall layoutType=" + curLayoutType);
        pbItemInfoList.clear();
        List<MultiPbItemInfo> tempList = getPhotoInfoList();
        if(tempList != null && tempList.size() > 0){
            pbItemInfoList.addAll(tempList);
        }
        if (pbItemInfoList == null || pbItemInfoList.size() <= 0) {
            multiPbPhotoView.setGridViewVisibility(View.GONE);
            multiPbPhotoView.setListViewVisibility(View.GONE);
            multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
        } else {
            multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
            setAdaper();
        }
    }

    public void changePreviewType(PhotoWallLayoutType layoutType) {
        curLayoutType = layoutType;
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
            if (curLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.quitEditMode();
            } else {
                photoWallGridAdapter.quitEditMode();
            }
        }
    }

    public void listViewSelectOrCancelOnce(int position) {
        AppLog.i(TAG, "listViewSelectOrCancelOnce positon=" + position + " photoWallPreviewType=" + curLayoutType);
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            AppLog.i(TAG, "listViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
            gotoSinglePb(position);
        } else {
            photoWallListAdapter.changeSelectionState(position);
            multiPbPhotoView.setPhotoSelectNumText(photoWallListAdapter.getSelectedCount());
        }
    }

    public void gridViewSelectOrCancelOnce(int position) {
        AppLog.i(TAG, "gridViewSelectOrCancelOnce positon=" + position + " AppInfo.photoWallPreviewType=" + curLayoutType);
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            gotoSinglePb(position);
        } else {
            photoWallGridAdapter.changeCheckBoxState(position, curOperationMode);
            multiPbPhotoView.setPhotoSelectNumText(photoWallGridAdapter.getSelectedCount());
        }
    }

    public void gotoSinglePb(final int position){
        if (fileType == FileType.FILE_PHOTO) {
            Intent intent = new Intent();
            intent.putExtra("curfilePosition", position);
            intent.putExtra("fileType", fileType.ordinal());
            intent.setClass(activity, PhotoPbActivity.class);
            activity.startActivity(intent);
        } else {
            MyProgressDialog.showProgressDialog(activity, R.string.wait);
            stopLoad();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.putExtra("curfilePosition", position);
                    intent.putExtra("fileType", fileType.ordinal());
                    intent.setClass(activity, VideoPbActivity.class);
                        activity.startActivity(intent);
                    MyProgressDialog.closeProgressDialog();
                }
            }, 1500);
        }
    }


    public void selectOrCancelAll(boolean isSelectAll) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            return;
        }
        int selectNum;
        if (isSelectAll) {
            if (curLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.selectAllItems();
                selectNum = photoWallListAdapter.getSelectedCount();
            } else {
                photoWallGridAdapter.selectAllItems();
                selectNum = photoWallGridAdapter.getSelectedCount();
            }
            multiPbPhotoView.setPhotoSelectNumText(selectNum);
        } else {
            if (curLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
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
        if (curLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            return photoWallListAdapter.getSelectedList();
        } else {
            return photoWallGridAdapter.getCheckedItemsList();
        }
    }




    public void emptyFileList() {
        RemoteFileHelper.getInstance().clearFileList(fileType);
    }

    public void stopLoad() {
        ImageLoaderConfig.stopLoad();
    }

    public void deleteFile() {
        List<MultiPbItemInfo> list = null;
        list = getSelectedList();
        if (list == null || list.size() <= 0) {
            AppLog.d(TAG, "asytaskList size=" + list.size());
            MyToast.show(activity, R.string.gallery_no_file_selected);
        } else {
            CharSequence what = activity.getResources().getString(R.string.gallery_delete_des).replace("$1$", String.valueOf(list.size()));
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setMessage(what);
            builder.setPositiveButton(activity.getResources().getString(R.string.gallery_cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            final List<MultiPbItemInfo> finalList = list;
            final FileType finalFileType = fileType;
            builder.setNegativeButton(activity.getResources().getString(R.string.gallery_delete), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                    quitEditMode();
//                    new DeleteFileThread(finalList, finalFileType).run();
                    new Thread(new DeleteFileThread(finalList, finalFileType)).start();
                }
            });
            builder.create().show();
        }
    }

    private class DeleteFileThread implements Runnable {
        private List<MultiPbItemInfo> fileList;
        private List<MultiPbItemInfo> deleteFailedList;
        private List<MultiPbItemInfo> deleteSucceedList;
        private Handler handler;
        private FileOperation fileOperation;
        private FileType fileType;

        public DeleteFileThread(List<MultiPbItemInfo> fileList, FileType fileType) {
            this.fileList = fileList;
            this.handler = new Handler();
            this.fileOperation = CameraManager.getInstance().getCurCamera().getFileOperation();
            this.fileType = fileType;
        }

        @Override
        public void run() {
            AppLog.d(TAG, "DeleteThread");
            deleteFailedList = new LinkedList<MultiPbItemInfo>();
            deleteSucceedList = new LinkedList<MultiPbItemInfo>();
            for (MultiPbItemInfo tempFile : fileList) {
                AppLog.d(TAG, "deleteFile f.getFileHandle =" + tempFile.getFileHandle());
                if (fileOperation.deleteFile(tempFile.iCatchFile) == false) {
                    deleteFailedList.add(tempFile);
                } else {
                    deleteSucceedList.add(tempFile);
                }
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();
                    pbItemInfoList.removeAll(deleteSucceedList);
                    RemoteFileHelper.getInstance().setLocalFileList(pbItemInfoList, fileType);
                    quitEditMode();
                    refreshPhotoWall();
                }
            });
        }
    }
}