package com.icatch.mobilecam.ui.Fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.icatch.mobilecam.ui.adapter.MultiPbPhotoWallGridAdapter;
import com.icatch.mobilecam.ui.adapter.MultiPbPhotoWallListAdapter;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.data.type.PhotoWallLayoutType;
import com.icatch.mobilecam.Listener.OnStatusChangedListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.Presenter.MultiPbVideoFragmentPresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.ui.Interface.MultiPbVideoFragmentView;

import java.util.List;

public class RemoteMultiPbVideoFragment extends Fragment implements MultiPbVideoFragmentView {
    String TAG = "RemoteMultiPbVideoFragment";
    StickyGridHeadersGridView gridView;
    ListView listView;
    TextView headerView;
    FrameLayout listLayout;
    private boolean isCreated = false;
    private boolean isVisible = false;
    TextView noContentTxv;
   
    MultiPbVideoFragmentPresenter presenter;
    private OnStatusChangedListener modeChangedListener;

    private PhotoWallLayoutType layoutType = PhotoWallLayoutType.PREVIEW_TYPE_GRID;
    public RemoteMultiPbVideoFragment() {
        // Required empty public constructor
        this.layoutType = layoutType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multi_pb_video, container, false);
        gridView = (StickyGridHeadersGridView) view.findViewById(R.id.multi_pb_video_grid_view);
        listView = (ListView) view.findViewById(R.id.multi_pb_video_list_view);
        headerView = (TextView) view.findViewById(R.id.photo_wall_header);
        listLayout = (FrameLayout) view.findViewById(R.id.multi_pb_video_list_layout);
        noContentTxv = (TextView) view.findViewById(R.id.no_content_txv);

        presenter = new MultiPbVideoFragmentPresenter(getActivity());
        presenter.setView(this);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                AppLog.d(TAG, "ListView start onScrollStateChanged");
                int firstVisible = listView.getFirstVisiblePosition();
                int lastVisible = listView.getLastVisiblePosition();
                AppLog.d(TAG, "listView onScrollStateChanged firstVisible=" + firstVisible + " lastVisible=" + lastVisible);

                if(isVisible){
                    presenter.listViewLoadThumbnails(scrollState,firstVisible,lastVisible-firstVisible+1);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                AppLog.d(TAG, "ListView start onScroll");
                if (isVisible) {
                    presenter.listViewLoadOnceThumbnails(firstVisibleItem, visibleItemCount);
                }
            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                AppLog.d(TAG, "GridView start onScrollStateChanged isVisible=" + isVisible);
                int firstVisible = gridView.getFirstVisiblePosition();
                int lastVisible = gridView.getLastVisiblePosition();
                AppLog.d(TAG, "gridView onScrollStateChanged firstVisible=" + firstVisible + " lastVisible=" + lastVisible);

                if (isVisible) {
                    presenter.gridViewLoadThumbnails(scrollState,firstVisible,lastVisible-firstVisible +1);
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    AppLog.d(TAG, "GridView start onScroll");
                if (isVisible) {
                    presenter.gridViewLoadOnceThumbnails(firstVisibleItem, visibleItemCount);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.listViewEnterEditMode(position);
                return true;
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("1111", "multiPbPhotoGridView.setOnItemClickListener");
                presenter.gridViewSelectOrCancelOnce(position);
            }
        });
        isCreated = true;
        return view;

    }

    @Override
    public void onResume() {
        AppLog.d(TAG, "start onResume isVisible="+ isVisible);
        if(isVisible){
            presenter.loadVideoWall();
        }
        super.onResume();

    }

    @Override
    public void onStop() {
        AppLog.d(TAG, "start onStop()");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        AppLog.d(TAG, "start onDestroy()");
        presenter.clealAsytaskList();
        presenter.emptyFileList();
        super.onDestroy();
    }

    public void changePreviewType() {
        AppLog.d(TAG, "start changePreviewType presenter=" + presenter);
        if(presenter != null){
            presenter.changePreviewType();
        }
//        presenter.changePreviewType();
    }

    public void quitEditMode(){
        presenter.quitEditMode();
    }

    @Override
    public void setListViewVisibility(int visibility) {
        listLayout.setVisibility(visibility);
    }

    @Override
    public void setGridViewVisibility(int visibility) {
        gridView.setVisibility(visibility);
    }

    @Override
    public void setListViewAdapter(MultiPbPhotoWallListAdapter multiPbPhotoWallListAdapter) {
        listView.setAdapter(multiPbPhotoWallListAdapter);
    }

    @Override
    public void setGridViewAdapter(MultiPbPhotoWallGridAdapter multiPbPhotoWallGridAdapter) {
        gridView.setAdapter(multiPbPhotoWallGridAdapter);
    }

    @Override
    public void setListViewSelection(int position) {
        listView.setSelection(position);
    }

    @Override
    public void setGridViewSelection(int position) {
        gridView.setSelection(position);
    }

    @Override
    public void setNoContentTxvVisibility(int visibility) {
        int v = noContentTxv.getVisibility();
        if (v != visibility) {
            noContentTxv.setVisibility(visibility);
        }
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
        return gridView.findViewWithTag(tag);
    }



    @Override
    public void changeMultiPbMode(OperationMode operationMode) {
        if (modeChangedListener != null){
            modeChangedListener.onChangeOperationMode(operationMode);
        }
    }

    @Override
    public void setVideoSelectNumText(int selectNum) {
        if (modeChangedListener != null){
            modeChangedListener.onSelectedItemsCountChanged(selectNum);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.refreshPhotoWall();
    }

    public void refreshPhotoWall(){
        presenter.refreshPhotoWall();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        AppLog.d(TAG, "setUserVisibleHint isVisibleToUser=" + isVisibleToUser);
        AppLog.d(TAG, "setUserVisibleHint isCreated=" + isCreated);
        isVisible = isVisibleToUser;
        if(isCreated == false){
            return;
        }
        if (isVisibleToUser == false) {
            presenter.quitEditMode();
            presenter.clealAsytaskList();
        }else{
            presenter.loadVideoWall();
        }
    }

    public void setOperationListener(OnStatusChangedListener modeChangedListener){
        this.modeChangedListener = modeChangedListener;
    }

    public void select(boolean isSelectAll){
        presenter.selectOrCancelAll(isSelectAll);
    }

    public List<MultiPbItemInfo> getSelectedList() {
        return presenter.getSelectedList();
    }

    public void clealAsytaskList(){
        presenter.clealAsytaskList();
    }
}
