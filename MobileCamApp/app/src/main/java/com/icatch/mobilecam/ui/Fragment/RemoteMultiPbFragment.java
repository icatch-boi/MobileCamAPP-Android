package com.icatch.mobilecam.ui.Fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icatch.mobilecam.Listener.EndlessRecyclerOnScrollListener;
import com.icatch.mobilecam.Listener.OnRecyclerItemClickListener;
import com.icatch.mobilecam.Listener.OnStatusChangedListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.Presenter.MultiPbFragmentPresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.data.type.FileType;
import com.icatch.mobilecam.data.type.PhotoWallLayoutType;
import com.icatch.mobilecam.ui.Interface.MultiPbFragmentView;
import com.icatch.mobilecam.ui.RemoteFileHelper;
import com.icatch.mobilecam.ui.adapter.MultiPbRecyclerViewAdapter;
import com.icatch.mobilecam.utils.ClickUtils;

import java.util.List;

public class RemoteMultiPbFragment extends BaseMultiPbFragment implements MultiPbFragmentView {
    private static final String TAG = "RemoteMultiPbFragment";
    RecyclerView recyclerView;
    MultiPbFragmentPresenter presenter;
    private OnStatusChangedListener modeChangedListener;
    private boolean isCreated = false;
    private boolean isVisible = false;
    private TextView noContentTxv;
    private FileType fileType;
    private boolean hasDeleted = false;

    public RemoteMultiPbFragment() {
        // Required empty public constructor
    }

    public static RemoteMultiPbFragment newInstance(int param1) {
        RemoteMultiPbFragment fragment = new RemoteMultiPbFragment();
        Bundle args = new Bundle();
        args.putInt("FILE_TYPE", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int fileTypeInt = 0;
        if (getArguments() != null) {
            fileTypeInt = getArguments().getInt("FILE_TYPE");
        }
        this.fileType = FileType.values()[fileTypeInt];
        AppLog.d(TAG, "onCreate fileType=" + fileType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppLog.d(TAG, "onCreateView fileType=" + fileType);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multi_pb, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        noContentTxv = (TextView) view.findViewById(R.id.no_content_txv);

        presenter = new MultiPbFragmentPresenter(getActivity(), fileType);
        presenter.setView(this);
        presenter.setFragment(this);
        recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView) {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder viewHolder) {
                if (!ClickUtils.isFastDoubleClick(R.id.recycler_view)) {
                    presenter.itemClick(position);
                }
            }

            @Override
            public void onItemLongClick(int position, View view, RecyclerView.ViewHolder viewHolder) {
                if (isVisible) {
                    presenter.enterEditMode(position);
                }
            }
        });
        isCreated = true;
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                presenter.loadMoreFile();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppLog.d(TAG, "start onResume() isVisible=" + isVisible + " fileType=" + fileType);
        if (isVisible) {
            if(hasDeleted &&RemoteFileHelper.getInstance().isSupportSegmentedLoading()){
                presenter.resetCurIndex();
                presenter.resetAdpter();
                RemoteFileHelper.getInstance().clearFileList(fileType);
            }
            presenter.loadPhotoWall();
        }
        hasDeleted = false;
        AppLog.d(TAG, "end onResume");
    }

    @Override
    public void onStop() {
        AppLog.d(TAG, "start onStop()");
        //presenter.stopLoad();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        AppLog.d(TAG, "start onDestroy()");
        super.onDestroy();
        presenter.emptyFileList();
    }

    public void changePreviewType(PhotoWallLayoutType layoutType) {
        AppLog.d(TAG, "start changePreviewType presenter=" + presenter);
        if (presenter != null) {
            presenter.changePreviewType(layoutType);
        }
    }

    public void quitEditMode() {
        presenter.quitEditMode();
    }

    @Override
    public void setRecyclerViewVisibility(int visibility) {
        recyclerView.setVisibility(visibility);
    }

    @Override
    public void setRecyclerViewAdapter(MultiPbRecyclerViewAdapter recyclerViewAdapter) {
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void setRecyclerViewLayoutManager(RecyclerView.LayoutManager layout) {
        recyclerView.setLayoutManager(layout);
    }

    @Override
    public void notifyChangeMultiPbMode(OperationMode operationMode) {
        if (modeChangedListener != null) {
            modeChangedListener.onChangeOperationMode(operationMode);
        }
    }

    @Override
    public void setPhotoSelectNumText(int selectNum) {
        if (modeChangedListener != null) {
            modeChangedListener.onSelectedItemsCountChanged(selectNum);
        }
    }

    @Override
    public void setNoContentTxvVisibility(int visibility) {
        int v = noContentTxv.getVisibility();
        if (v != visibility) {
            noContentTxv.setVisibility(visibility);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("1122", "RemoteMultiPbPhotoFragment onConfigurationChanged");
        presenter.refreshPhotoWall();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        AppLog.d(TAG, "setUserVisibleHint isVisibleToUser=" + isVisibleToUser + " fileType=" + fileType);
        AppLog.d(TAG, "setUserVisibleHint isCreated=" + isCreated);
        isVisible = isVisibleToUser;
        if (isCreated == false) {
            return;
        }
        if (isVisibleToUser == false) {
            presenter.quitEditMode();
        } else {
            presenter.loadPhotoWall();
        }
    }

    public void setOperationListener(OnStatusChangedListener modeChangedListener) {
        this.modeChangedListener = modeChangedListener;
    }

    public void selectOrCancelAll(boolean isSelectAll) {
        presenter.selectOrCancelAll(isSelectAll);
    }

    public List<MultiPbItemInfo> getSelectedList() {
        return presenter.getSelectedList();
    }

    public FileType getFileType() {
        return fileType;
    }

    public void deleteFile() {
        presenter.deleteFile();
    }

    public void loadPhotoWall(){
        if (isVisible) {
            presenter.loadPhotoWall();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppLog.d(TAG,"onActivityResult requestCode=" + requestCode);
        AppLog.d(TAG,"onActivityResult data=" + data);
        AppLog.d(TAG,"onActivityResult curfileType=" + fileType.ordinal());
        if(data != null){
            hasDeleted = data.getBooleanExtra("hasDeleted",false);
            int fileTypeInt = data.getIntExtra("fileType",-1);
            AppLog.d(TAG,"onActivityResult hasDeleted=" + hasDeleted + " fileType=" +fileTypeInt);
        }
    }
}
