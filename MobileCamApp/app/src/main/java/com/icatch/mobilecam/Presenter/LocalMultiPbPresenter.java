package com.icatch.mobilecam.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.view.View;

import com.icatch.mobilecam.Listener.OnStatusChangedListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.Presenter.Interface.BasePresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.data.entity.LocalPbItemInfo;
import com.icatch.mobilecam.data.type.FileType;
import com.icatch.mobilecam.data.type.PhotoWallLayoutType;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.ui.Fragment.LocalMultiPbFragment;
import com.icatch.mobilecam.ui.Interface.LocalMultiPbView;
import com.icatch.mobilecam.ui.adapter.ViewPagerAdapter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


public class LocalMultiPbPresenter extends BasePresenter {
    private static final String TAG = LocalMultiPbPresenter.class.getSimpleName();
    private LocalMultiPbView multiPbView;
    private Activity activity;
    LocalMultiPbFragment multiPbPhotoFragment;
    LocalMultiPbFragment multiPbVideoFragment;
    OperationMode curOperationMode = OperationMode.MODE_BROWSE;
    ViewPagerAdapter adapter;
    private boolean curSelectAll = false;

    public LocalMultiPbPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        Intent intent = activity.getIntent();
        AppInfo.currentViewpagerPosition = intent.getIntExtra("CUR_POSITION", 0);
    }

    public void setView(LocalMultiPbView multiPbView) {
        this.multiPbView = multiPbView;
        initCfg();
    }

    public void loadViewPager() {
        initViewpager();
    }

    public void reset() {
        AppInfo.photoWallLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_GRID;
        AppInfo.currentViewpagerPosition = 0;
        AppInfo.curVisibleItem = 0;
    }

    private void initViewpager() {
        if (multiPbPhotoFragment == null) {
            multiPbPhotoFragment = LocalMultiPbFragment.newInstance(1);
        }
        multiPbPhotoFragment.setOperationListener(new OnStatusChangedListener() {
            @Override
            public void onChangeOperationMode(OperationMode operationMode) {
                curOperationMode = operationMode;
                if (curOperationMode == OperationMode.MODE_BROWSE) {
                    multiPbView.setViewPagerScanScroll(true);
                    multiPbView.setTabLayoutClickable(true);
                    multiPbView.setSelectBtnVisibility(View.GONE);
                    multiPbView.setSelectNumTextVisibility(View.GONE);
                    multiPbView.setEditLayoutVisibility(View.GONE);
                    multiPbView.setSelectBtnIcon(R.drawable.ic_select_all_white_24dp);
                    curSelectAll = false;
                    AppLog.d(TAG, "multiPbPhotoFragment quit EditMode");
                } else {
                    multiPbView.setViewPagerScanScroll(false);
                    multiPbView.setTabLayoutClickable(false);
                    multiPbView.setSelectBtnVisibility(View.VISIBLE);
                    multiPbView.setSelectNumTextVisibility(View.VISIBLE);
                    multiPbView.setEditLayoutVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSelectedItemsCountChanged(int SelectedNum) {
                String temp = activity.getString(R.string.text_selected).replace("$1$",String.valueOf(SelectedNum));;
                multiPbView.setSelectNumText(temp);
            }

        });
        if (multiPbVideoFragment == null) {
            multiPbVideoFragment = LocalMultiPbFragment.newInstance(0);
        }
        multiPbVideoFragment.setOperationListener(new OnStatusChangedListener() {
            @Override
            public void onChangeOperationMode(OperationMode operationMode) {
                curOperationMode = operationMode;
                if (curOperationMode == OperationMode.MODE_BROWSE) {
                    multiPbView.setViewPagerScanScroll(true);
                    multiPbView.setTabLayoutClickable(true);
                    multiPbView.setSelectBtnVisibility(View.GONE);
                    multiPbView.setSelectNumTextVisibility(View.GONE);
                    multiPbView.setEditLayoutVisibility(View.GONE);
                    multiPbView.setSelectBtnIcon(R.drawable.ic_select_all_white_24dp);
                    curSelectAll = false;
                    AppLog.d(TAG, "multiPbVideoFragment quit EditMode");
                } else {
                    multiPbView.setViewPagerScanScroll(false);
                    multiPbView.setTabLayoutClickable(false);
                    multiPbView.setSelectBtnVisibility(View.VISIBLE);
                    multiPbView.setSelectNumTextVisibility(View.VISIBLE);
                    multiPbView.setEditLayoutVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSelectedItemsCountChanged(int SelectedNum) {
                String temp = activity.getString(R.string.text_selected).replace("$1$",String.valueOf(SelectedNum));;
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
            if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
                AppInfo.photoWallLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_GRID;
                multiPbView.setMenuPhotoWallTypeIcon(R.drawable.ic_view_grid_white_24dp);
            } else {
                AppInfo.photoWallLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_LIST;
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
            multiPbVideoFragment.selectOrCancelAll(curSelectAll);
        }
    }

    public void delete() {
        List<LocalPbItemInfo> list = null;
        FileType fileType = FileType.FILE_PHOTO;
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

            final List<LocalPbItemInfo> finalList = list;
            final FileType finalFileType = fileType;
            builder.setNegativeButton(activity.getResources().getString(R.string.gallery_delete), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
//                    new DeleteFileThread(finalList, finalFileType).run();
                    new Thread(new DeleteFileThread(finalList, finalFileType)).start();
                }
            });
            builder.create().show();
        }
    }


    class DeleteFileThread implements Runnable {
        private List<LocalPbItemInfo> fileList;
        private List<LocalPbItemInfo> deleteFailedList;
        private List<LocalPbItemInfo> deleteSucceedList;
        private Handler handler;
        private FileType fileType;

        public DeleteFileThread(List<LocalPbItemInfo> fileList, FileType fileType) {
            this.fileList = fileList;
            this.handler = new Handler();
            this.fileType = fileType;
        }

        @Override
        public void run() {

            AppLog.d(TAG, "DeleteThread");

            if (deleteFailedList == null) {
                deleteFailedList = new LinkedList<LocalPbItemInfo>();
            } else {
                deleteFailedList.clear();
            }
            if (deleteSucceedList == null) {
                deleteSucceedList = new LinkedList<LocalPbItemInfo>();
            } else {
                deleteSucceedList.clear();
            }
            for (LocalPbItemInfo tempFile : fileList) {
                File file = tempFile.file;
                if (file.delete() == false) {
                    deleteFailedList.add(tempFile);
                } else {
                    deleteSucceedList.add(tempFile);
                }
            }
            clealAsytaskList();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();

//                        if (AppInfo.currentViewpagerPosition == 0) {
//                            multiPbPhotoFragment.quitEditMode();
//                        } else if (AppInfo.currentViewpagerPosition == 1) {
//                            multiPbVideoFragment.quitEditMode();
//                        }
                    curOperationMode = OperationMode.MODE_BROWSE;
                    if (AppInfo.currentViewpagerPosition == 0) {
                        multiPbPhotoFragment.quitEditMode();
                        multiPbPhotoFragment.refreshPhotoWall();
                    } else if (AppInfo.currentViewpagerPosition == 1) {
                        multiPbVideoFragment.quitEditMode();
                        multiPbVideoFragment.refreshPhotoWall();
                    }
//                    curOperationMode = OperationMode.MODE_BROWSE;
//                    multiPbView.setEditLayoutVisibility(View.GONE);
//                    if (fileType == FileType.FILE_PHOTO) {
//                        multiPbPhotoFragment.refreshPhotoWall();
//                        multiPbPhotoFragment.quitEditMode();
//
//                    } else if (fileType == FileType.FILE_VIDEO) {
//                        multiPbVideoFragment.refreshPhotoWall();
//                        multiPbVideoFragment.quitEditMode();
//                    }

                }
            });
        }
    }

    public void clealAsytaskList() {
        multiPbPhotoFragment.clealAsytaskList();
        multiPbVideoFragment.clealAsytaskList();
    }

}
