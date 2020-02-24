package com.icatch.mobilecam.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.LruCache;
import android.view.View;

import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.ui.Fragment.RemoteMultiPbPhotoFragment;
import com.icatch.mobilecam.ui.adapter.ViewPagerAdapter;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.type.FileType;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.data.type.PhotoWallPreviewType;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.Function.CameraAction.PbDownloadManager;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.Listener.OnStatusChangedListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.Presenter.Interface.BasePresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.FileOperation;
import com.icatch.mobilecam.data.SystemInfo.SystemInfo;
import com.icatch.mobilecam.ui.Fragment.RemoteMultiPbVideoFragment;
import com.icatch.mobilecam.ui.Interface.MultiPbView;
import com.icatchtek.reliant.customer.type.ICatchFile;

import java.util.LinkedList;
import java.util.List;


public class RemoteMultiPbPresenter extends BasePresenter {
    private static final String TAG = RemoteMultiPbPresenter.class.getSimpleName();
    private MultiPbView multiPbView;
    private Activity activity;
    RemoteMultiPbPhotoFragment multiPbPhotoFragment;
    RemoteMultiPbVideoFragment multiPbVideoFragment;
    OperationMode curOperationMode = OperationMode.MODE_BROWSE;
    ViewPagerAdapter adapter;
    private boolean curSelectAll = false;

    public RemoteMultiPbPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        initLruCache();
    }

    public void setView(MultiPbView multiPbView) {
        this.multiPbView = multiPbView;
        initCfg();
    }

    public void loadViewPager() {
        initViewpager();
    }

    private void initLruCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 4;
        AppLog.d(TAG, "initLruCache cacheMemory=" + cacheMemory);
        GlobalInfo.getInstance().mLruCache = new LruCache<Integer, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
//                return value.getRowBytes() * value.getHeight();
                return value.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
                // TODO Auto-generated method stub
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (oldValue != null) {
                    //回收bitmap占用的内存空间
                    oldValue.recycle();
                    oldValue = null;
                }
            }
        };
    }

    public void clearCache() {
        GlobalInfo.getInstance().mLruCache.evictAll();
    }

    public void reset() {
        AppInfo.photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_GRID;
        AppInfo.currentViewpagerPosition = 0;
        AppInfo.curVisibleItem = 0;
    }


    private void initViewpager() {
        if (multiPbPhotoFragment == null) {
            multiPbPhotoFragment = new RemoteMultiPbPhotoFragment();
        }
        multiPbPhotoFragment.setOperationListener(new OnStatusChangedListener() {
            @Override
            public void onChangeOperationMode(OperationMode operationMode) {
                curOperationMode = operationMode;
                if (curOperationMode == OperationMode.MODE_BROWSE) {
                    multiPbView.setViewPagerScanScroll(true);
                    multiPbView.setTabLayoutClickable(true);
                    multiPbView.setEditLayoutVisibiliy(View.GONE);
                    multiPbView.setSelectBtnIcon(R.drawable.ic_select_all_white_24dp);
                    curSelectAll = false;
                    AppLog.d(TAG, "multiPbPhotoFragment quit EditMode");
                } else {
                    multiPbView.setViewPagerScanScroll(false);
                    multiPbView.setTabLayoutClickable(false);
                    multiPbView.setEditLayoutVisibiliy(View.VISIBLE);
                }
            }

            @Override
            public void onSelectedItemsCountChanged(int SelectedNum) {
                String temp = "Selected(" + SelectedNum + ")";
                multiPbView.setSelectNumText(temp);
            }

        });
        if (multiPbVideoFragment == null) {
            multiPbVideoFragment = new RemoteMultiPbVideoFragment();
        }
        multiPbVideoFragment.setOperationListener(new OnStatusChangedListener() {
            @Override
            public void onChangeOperationMode(OperationMode operationMode) {
                curOperationMode = operationMode;
                if (curOperationMode == OperationMode.MODE_BROWSE) {
                    multiPbView.setViewPagerScanScroll(true);
                    multiPbView.setTabLayoutClickable(true);
                    multiPbView.setSelectBtnIcon(R.drawable.ic_select_all_white_24dp);
                    multiPbView.setEditLayoutVisibiliy(View.GONE);
                    curSelectAll = false;
                    AppLog.d(TAG, "multiPbVideoFragment quit EditMode");
                } else {
                    multiPbView.setViewPagerScanScroll(false);
                    multiPbView.setTabLayoutClickable(false);
                    multiPbView.setEditLayoutVisibiliy(View.VISIBLE);
                }
            }

            @Override
            public void onSelectedItemsCountChanged(int SelectedNum) {
                String temp = "Selected(" + SelectedNum + ")";
                multiPbView.setSelectNumText(temp);
            }

        });
        FragmentManager manager = ((FragmentActivity) activity).getSupportFragmentManager();
        adapter = new ViewPagerAdapter(manager);
        adapter.addFragment(multiPbPhotoFragment, activity.getResources().getString(R.string.title_photo));
        adapter.addFragment(multiPbVideoFragment, activity.getResources().getString(R.string.title_video));
        multiPbView.setViewPageAdapter(adapter);
        multiPbView.setViewPageCurrentItem(AppInfo.currentViewpagerPosition);
    }

    public void updateViewpagerStatus(int arg0) {
        AppLog.d(TAG, "updateViewpagerStatus arg0=" + arg0);
        AppInfo.currentViewpagerPosition = arg0;
    }

    public void changePreviewType() {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            clealAsytaskList();
            if (AppInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
                AppInfo.photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_GRID;
                multiPbView.setMenuPhotoWallTypeIcon(R.drawable.ic_view_grid_white_24dp);
            } else {
                AppInfo.photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_LIST;
                multiPbView.setMenuPhotoWallTypeIcon(R.drawable.ic_view_list_white_24dp);
            }
            multiPbPhotoFragment.changePreviewType();
            multiPbVideoFragment.changePreviewType();
            AppLog.d(TAG, " changePreviewType AppInfo.photoWallPreviewType");
        }
    }

    public void reback() {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            activity.finish();
        } else if (curOperationMode == OperationMode.MODE_EDIT) {
            curOperationMode = OperationMode.MODE_BROWSE;
            if (AppInfo.currentViewpagerPosition == 0) {
                multiPbPhotoFragment.quitEditMode();
            } else if (AppInfo.currentViewpagerPosition == 1) {
                multiPbVideoFragment.quitEditMode();
            }
        }

    }


    public void selectOrCancel() {

        if (curSelectAll) {
            multiPbView.setSelectBtnIcon(R.drawable.ic_select_all_white_24dp);
            curSelectAll = false;
        } else {
            multiPbView.setSelectBtnIcon(R.drawable.ic_unselected_white_24dp);
            curSelectAll = true;
        }
        if (AppInfo.currentViewpagerPosition == 0) {
            multiPbPhotoFragment.selectOrCancelAll(curSelectAll);
        } else if (AppInfo.currentViewpagerPosition == 1) {
            multiPbVideoFragment.select(curSelectAll);
        }
    }

    public void delete() {
        List<MultiPbItemInfo> list = null;
        FileType fileType = null;
        AppLog.d(TAG, "delete AppInfo.currentViewpagerPosition=" + AppInfo.currentViewpagerPosition);
        if (AppInfo.currentViewpagerPosition == 0) {
            list = multiPbPhotoFragment.getSelectedList();
            fileType = FileType.FILE_PHOTO;
        } else if (AppInfo.currentViewpagerPosition == 1) {
            list = multiPbVideoFragment.getSelectedList();
            fileType = FileType.FILE_VIDEO;
        }
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
                    new DeleteFileThread(finalList, finalFileType).run();
                }
            });
            builder.create().show();
        }
    }

    public void download() {
        List<MultiPbItemInfo> list = null;
        LinkedList<ICatchFile> linkedList = new LinkedList<>();
        long fileSizeTotal = 0;
        AppLog.d(TAG, "delete currentViewpagerPosition=" + AppInfo.currentViewpagerPosition);
        if (AppInfo.currentViewpagerPosition == 0) {
            list = multiPbPhotoFragment.getSelectedList();
        } else if (AppInfo.currentViewpagerPosition == 1) {
            list = multiPbVideoFragment.getSelectedList();
        }
        if (list == null || list.size() <= 0) {
            AppLog.d(TAG, "asytaskList size=" + list.size());
            MyToast.show(activity, R.string.gallery_no_file_selected);
        } else {
            for (MultiPbItemInfo temp : list
                    ) {
                linkedList.add(temp.iCatchFile);
                fileSizeTotal += temp.getFileSizeInteger();
            }
            if (SystemInfo.getSDFreeSize() < fileSizeTotal) {
                MyToast.show(activity, R.string.text_sd_card_memory_shortage);
            } else {
                quitEditMode();
                PbDownloadManager downloadManager = new PbDownloadManager(activity, linkedList);
                downloadManager.show();
            }
        }
    }


    class DeleteFileThread implements Runnable {
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

            if (deleteFailedList == null) {
                deleteFailedList = new LinkedList<MultiPbItemInfo>();
            } else {
                deleteFailedList.clear();
            }
            if (deleteSucceedList == null) {
                deleteSucceedList = new LinkedList<MultiPbItemInfo>();
            } else {
                deleteSucceedList.clear();
            }
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

                    if (fileType == FileType.FILE_PHOTO) {
                        GlobalInfo.getInstance().getRemotePhotoList().removeAll(deleteSucceedList);
                        multiPbPhotoFragment.refreshPhotoWall();
                        multiPbPhotoFragment.quitEditMode();

                    } else if (fileType == FileType.FILE_VIDEO) {
                        GlobalInfo.getInstance().remoteVideoList.removeAll(deleteSucceedList);
                        multiPbVideoFragment.refreshPhotoWall();
                        multiPbVideoFragment.quitEditMode();
                    }
                    if (deleteFailedList.isEmpty() == false) {
//                        showDeleteFailedDialog();
                    }

                }
            });
        }
    }

    public void clealAsytaskList() {
        multiPbPhotoFragment.clealAsytaskList();
        multiPbVideoFragment.clealAsytaskList();
    }

    private void quitEditMode() {
        curOperationMode = OperationMode.MODE_BROWSE;
        if (AppInfo.currentViewpagerPosition == 0) {
            multiPbPhotoFragment.quitEditMode();
        } else if (AppInfo.currentViewpagerPosition == 1) {
            multiPbVideoFragment.quitEditMode();
        }
    }
}
