package com.icatch.mobilecam.ui.Fragment;


import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.icatch.mobilecam.Listener.OnStatusChangedListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.Presenter.LocalMultiPbFragmentPresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.data.entity.LocalPbItemInfo;
import com.icatch.mobilecam.data.type.FileType;
import com.icatch.mobilecam.ui.Interface.LocalMultiPbFragmentView;
import com.icatch.mobilecam.ui.adapter.LocalMultiPbWallGridAdapter;
import com.icatch.mobilecam.ui.adapter.LocalMultiPbWallListAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import java.util.List;


public class LocalMultiPbFragment extends Fragment implements LocalMultiPbFragmentView {
    private static final String TAG = LocalMultiPbFragment.class.getSimpleName();
    StickyGridHeadersGridView multiPbPhotoGridView;
    ListView listView;
    TextView headerView;
    TextView noContentTxv;
    FrameLayout multiPbPhotoListLayout;
    LocalMultiPbFragmentPresenter presenter;
    private OnStatusChangedListener modeChangedListener;
    private boolean isCreated = false;
    private boolean isVisible = false;
    private FileType fileType = FileType.FILE_PHOTO;

    public LocalMultiPbFragment() {
        // Required empty public constructor
    }

    public static LocalMultiPbFragment newInstance(int param1) {
        LocalMultiPbFragment fragment = new LocalMultiPbFragment();
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
        if (fileTypeInt == 0) {
            this.fileType = FileType.FILE_VIDEO;
        } else {
            this.fileType = FileType.FILE_PHOTO;
        }
        AppLog.d(TAG, "onCreate fileType=" + fileTypeInt);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.d(TAG, "MultiPbPhotoFragment onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multi_pb_photo, container, false);
        multiPbPhotoGridView = (StickyGridHeadersGridView) view.findViewById(R.id.multi_pb_photo_grid_view);
        listView = (ListView) view.findViewById(R.id.multi_pb_photo_list_view);
        headerView = (TextView) view.findViewById(R.id.photo_wall_header);
        noContentTxv = (TextView) view.findViewById(R.id.no_content_txv);
        multiPbPhotoListLayout = (FrameLayout) view.findViewById(R.id.multi_pb_photo_list_layout);

        presenter = new LocalMultiPbFragmentPresenter(getActivity(), fileType);
        presenter.setView(this);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (isVisible) {
                    presenter.listViewEnterEditMode(position);
                }
                return true;
            }
        });
        multiPbPhotoGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.gridViewEnterEditMode(position);
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("1111", "listView.setOnItemClickListener");
                presenter.listViewSelectOrCancelOnce(position);
            }
        });

        multiPbPhotoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("1111", "multiPbPhotoGridView.setOnItemClickListener");
//                MyToast.show(getActivity(), "item " + position + " clicked!");
                presenter.gridViewSelectOrCancelOnce(position);
            }
        });
        isCreated = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppLog.d(TAG, "start onResume() isVisible=" + isVisible + " presenter=" + presenter);
        if (isVisible) {
            presenter.loadPhotoWall();
        }
        AppLog.d(TAG, "end onResume");
    }

    @Override
    public void onStop() {
        AppLog.d(TAG, "start onStop()");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        AppLog.d(TAG, "start onDestroy()");
        super.onDestroy();
    }

    public void changePreviewType() {
        AppLog.d(TAG, "start changePreviewType presenter=" + presenter);
        if (presenter != null) {
            presenter.changePreviewType();
        }
    }

    public void quitEditMode() {
        presenter.quitEditMode();
    }

    @Override
    public void setListViewVisibility(int visibility) {
        if (multiPbPhotoListLayout.getVisibility() != visibility) {
            multiPbPhotoListLayout.setVisibility(visibility);
        }
    }

    @Override
    public void setGridViewVisibility(int visibility) {
        if (multiPbPhotoGridView.getVisibility() != visibility) {
            multiPbPhotoGridView.setVisibility(visibility);
        }
    }

    @Override
    public void setListViewAdapter(LocalMultiPbWallListAdapter photoWallListAdapter) {
        listView.setAdapter(photoWallListAdapter);
    }

    @Override
    public void setGridViewAdapter(LocalMultiPbWallGridAdapter photoWallGridAdapter) {
        multiPbPhotoGridView.setAdapter(photoWallGridAdapter);
    }

    @Override
    public void setListViewSelection(int position) {
        listView.setSelection(position);
    }

    @Override
    public void setGridViewSelection(int position) {
        multiPbPhotoGridView.setSelection(position);
    }

    @Override
    public void setListViewHeaderText(String headerText) {
        headerView.setText(headerText);
    }

    @Override
    public View listViewFindViewWithTag(int tag) {
        return listView.findViewWithTag(tag);
    }

    @Override
    public View gridViewFindViewWithTag(int tag) {
        return multiPbPhotoGridView.findViewWithTag(tag);
    }

    @Override
    public void updateGridViewBitmaps(String tag, Bitmap bitmap) {
        ImageView imageView = (ImageView) multiPbPhotoGridView.findViewWithTag(tag);
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
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
        Log.i("1122", "MultiPbPhotoFragment onConfigurationChanged");
        presenter.refreshPhotoWall();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        AppLog.d(TAG, "setUserVisibleHint isVisibleToUser=" + isVisibleToUser);
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

    public void refreshPhotoWall() {
        presenter.refreshPhotoWall();
    }

    public void setOperationListener(OnStatusChangedListener modeChangedListener) {
        this.modeChangedListener = modeChangedListener;
    }

    public void selectOrCancelAll(boolean isSelectAll) {
        presenter.selectOrCancelAll(isSelectAll);
    }

    public List<LocalPbItemInfo> getSelectedList() {
        return presenter.getSelectedList();
    }

    public void clealAsytaskList() {
//        presenter.clealAsytaskList();
    }


}

