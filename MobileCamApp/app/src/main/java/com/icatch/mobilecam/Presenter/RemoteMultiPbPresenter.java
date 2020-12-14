package com.icatch.mobilecam.Presenter;

import android.app.Activity;
import android.os.Handler;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.view.View;

import com.icatch.mobilecam.Function.CameraAction.PbDownloadManager;
import com.icatch.mobilecam.Function.SDKEvent;
import com.icatch.mobilecam.Listener.OnStatusChangedListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.MyCamera.MyCamera;
import com.icatch.mobilecam.Presenter.Interface.BasePresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.data.PropertyId.PropertyId;
import com.icatch.mobilecam.data.SystemInfo.SystemInfo;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.data.type.FileType;
import com.icatch.mobilecam.data.type.PhotoWallLayoutType;
import com.icatch.mobilecam.ui.ExtendComponent.MyProgressDialog;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;
import com.icatch.mobilecam.ui.Fragment.BaseMultiPbFragment;
import com.icatch.mobilecam.ui.Fragment.RemoteMultiPbFragment;
import com.icatch.mobilecam.ui.Interface.MultiPbView;
import com.icatch.mobilecam.ui.RemoteFileHelper;
import com.icatch.mobilecam.ui.adapter.ViewPagerAdapter;
import com.icatch.mobilecam.utils.FileFilter;
import com.icatch.mobilecam.utils.imageloader.ImageLoaderConfig;
import com.icatchtek.control.customer.type.ICatchCamFeatureID;
import com.icatchtek.reliant.customer.type.ICatchFile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class RemoteMultiPbPresenter extends BasePresenter {
    private static final String TAG = RemoteMultiPbPresenter.class.getSimpleName();
    private MultiPbView multiPbView;
    private Activity activity;
    private List<BaseMultiPbFragment> fragments;
    OperationMode curOperationMode = OperationMode.MODE_BROWSE;
    ViewPagerAdapter adapter;
    private boolean curSelectAll = false;
    PhotoWallLayoutType curLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_LIST;
    Handler handler = new Handler();

    public RemoteMultiPbPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void setView(MultiPbView multiPbView) {
        this.multiPbView = multiPbView;
        initCfg();
    }

    public void loadViewPager() {
        RemoteFileHelper.getInstance().initSupportCapabilities();
        initViewpager();
        initEditLayout();
    }

    public void initEditLayout(){
        boolean isSupportSegmentedLoading = RemoteFileHelper.getInstance().isSupportSegmentedLoading();
        multiPbView.setSelectBtnVisibility(isSupportSegmentedLoading?View.GONE:View.VISIBLE);
        multiPbView.setSelectNumTextVisibility(isSupportSegmentedLoading?View.GONE:View.VISIBLE);
    }

    public void reset() {
        AppInfo.currentViewpagerPosition = 0;
        AppInfo.curVisibleItem = 0;
        RemoteFileHelper.getInstance().setFileFilter(null);
        MyCamera camera = CameraManager.getInstance().getCurCamera();

        CameraProperties cameraProperties = camera.getCameraProperties();
        if (cameraProperties != null
                && cameraProperties.hasFuction(PropertyId.DEFALUT_TO_PREVIEW)
                && cameraProperties.checkCameraCapabilities(ICatchCamFeatureID.ICH_CAM_APP_DEFAULT_TO_PLAYBACK)) {
            camera.disconnect();
        }
    }

    private OnStatusChangedListener onStatusChangedListener = new OnStatusChangedListener() {
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
            String temp = activity.getString(R.string.text_selected).replace("$1$",String.valueOf(SelectedNum));
            multiPbView.setSelectNumText(temp);
        }
    };

    private void initViewpager() {
        if (fragments == null) {
            fragments = new ArrayList<>();
        } else {
            fragments.clear();
        }
        FragmentManager manager = ((FragmentActivity) activity).getSupportFragmentManager();
        adapter = new ViewPagerAdapter(manager);
        //图片
        BaseMultiPbFragment multiPbPhotoFragment = RemoteMultiPbFragment.newInstance(FileType.FILE_PHOTO.ordinal());
//        BaseMultiPbFragment multiPbPhotoFragment = RemoteMultiPbPhotoFragment.newInstance(FileType.FILE_PHOTO.ordinal());
        multiPbPhotoFragment.setOperationListener(onStatusChangedListener);
        fragments.add(multiPbPhotoFragment);
        adapter.addFragment(multiPbPhotoFragment, activity.getResources().getString(R.string.title_photo));
        //视频
        BaseMultiPbFragment multiPbVideoFragment = RemoteMultiPbFragment.newInstance(FileType.FILE_VIDEO.ordinal());
//        BaseMultiPbFragment multiPbVideoFragment = RemoteMultiPbPhotoFragment.newInstance(FileType.FILE_VIDEO.ordinal());
        multiPbVideoFragment.setOperationListener(onStatusChangedListener);
        fragments.add(multiPbVideoFragment);
        adapter.addFragment(multiPbVideoFragment, activity.getResources().getString(R.string.title_video));
        //紧急录影
//        RemoteMultiPbFragment multiPbEmergencyVideoFragment = RemoteMultiPbFragment.newInstance(FileType.FILE_EMERGENCY_VIDEO.ordinal());
//        multiPbEmergencyVideoFragment.setOperationListener(onStatusChangedListener);
//        fragments.add(multiPbEmergencyVideoFragment);
//        adapter.addFragment(multiPbEmergencyVideoFragment, activity.getResources().getString(R.string.title_emergency_video));

        multiPbView.setViewPageAdapter(adapter);
        multiPbView.setViewPageCurrentItem(AppInfo.currentViewpagerPosition);
    }

    public void updateViewpagerStatus(int arg0) {
        AppLog.d(TAG, "updateViewpagerStatus arg0=" + arg0);
        AppInfo.currentViewpagerPosition = arg0;
    }

    public void changePreviewType(PhotoWallLayoutType layoutType) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            if (layoutType == curLayoutType) {
                return;
            }
            curLayoutType = layoutType;
            if (fragments != null) {
                for (BaseMultiPbFragment fragment : fragments
                ) {
                    fragment.changePreviewType(layoutType);
                }
            }
            AppLog.d(TAG, " changePreviewType AppInfo.photoWallPreviewType");
        } else {
            MyToast.show(activity, "编辑中，无法切换");
        }
    }

    public void reback() {
        AppLog.i(TAG,"reback curOperationMode:" + curOperationMode);
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            MyProgressDialog.showProgressDialog(activity, R.string.wait);
            MyCamera camera = CameraManager.getInstance().getCurCamera();
            if(camera != null){
                camera.setLoadThumbnail(false);
            }
            ImageLoaderConfig.stopLoad();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();
                    activity.finish();
                }
            }, 1500);

        } else if (curOperationMode == OperationMode.MODE_EDIT) {
            curOperationMode = OperationMode.MODE_BROWSE;
            int index = multiPbView.getViewPageIndex();
            BaseMultiPbFragment fragment = fragments.get(index);
            if (fragment != null) {
                fragment.quitEditMode();
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
        if (fragments != null && fragments.size() > 0) {
            int index = multiPbView.getViewPageIndex();
            BaseMultiPbFragment fragment = fragments.get(index);
            if (fragment != null) {
                fragment.selectOrCancelAll(curSelectAll);
            }
        }
    }

    public void delete() {
        AppLog.d(TAG, "delete AppInfo.currentViewpagerPosition=" + AppInfo.currentViewpagerPosition);
        if (fragments != null && fragments.size() > 0) {
            BaseMultiPbFragment fragment = fragments.get(multiPbView.getViewPageIndex());
            if (fragment != null) {
                fragment.deleteFile();
            }
        }
    }

    public void download() {
        List<MultiPbItemInfo> list = null;
        LinkedList<ICatchFile> linkedList = new LinkedList<>();
        long fileSizeTotal = 0;
        AppLog.d(TAG, "delete currentViewpagerPosition=" + AppInfo.currentViewpagerPosition);
        if (fragments != null && fragments.size() > 0) {
            BaseMultiPbFragment fragment = fragments.get(multiPbView.getViewPageIndex());
            if (fragment != null) {
                list = fragment.getSelectedList();
            }
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
            if (SystemInfo.getSDFreeSize(activity) < fileSizeTotal) {
                MyToast.show(activity, R.string.text_sd_card_memory_shortage);
            } else {
                quitEditMode();
                PbDownloadManager downloadManager = new PbDownloadManager(activity, linkedList);
                downloadManager.show();
            }
        }
    }


    private void quitEditMode() {
        curOperationMode = OperationMode.MODE_BROWSE;
        if (fragments != null && fragments.size() > 0) {
            BaseMultiPbFragment fragment = fragments.get(multiPbView.getViewPageIndex());
            if (fragment != null) {
                fragment.quitEditMode();
            }
        }
    }

    private void reloadFileList(){
        RemoteFileHelper.getInstance().clearAllFileList();
        if (fragments != null && fragments.size() > 0) {
            BaseMultiPbFragment fragment = fragments.get(multiPbView.getViewPageIndex());
            if (fragment != null) {
                fragment.loadPhotoWall();
            }
        }
    }

    public void setFileFilter(FileFilter fileFilter) {
        RemoteFileHelper.getInstance().setFileFilter(fileFilter);
        reloadFileList();
    }

    public void setSdCardEventListener() {
        GlobalInfo.getInstance().setOnEventListener(new GlobalInfo.OnEventListener() {
            @Override
            public void eventListener(int sdkEventId) {
                switch (sdkEventId){
                    case SDKEvent.EVENT_SDCARD_REMOVED:
                        MyToast.show(activity,R.string.dialog_card_removed);
                        reloadFileList();
                        break;
                    case SDKEvent.EVENT_SDCARD_INSERT:
                        MyToast.show(activity,R.string.dialog_card_inserted);
                        reloadFileList();
                        break;
                }
            }
        });
    }
}
