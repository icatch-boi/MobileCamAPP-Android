package com.icatch.mobilecam.ui.Fragment;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.icatch.mobilecam.ui.adapter.MultiPbPhotoWallGridAdapter;
import com.icatch.mobilecam.ui.adapter.MultiPbPhotoWallListAdapter;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.Listener.OnStatusChangedListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.Mode.OperationMode;
import com.icatch.mobilecam.Presenter.MultiPbPhotoFragmentPresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.ui.Interface.MultiPbPhotoFragmentView;

import java.util.List;

public class RemoteMultiPbPhotoFragment extends Fragment implements MultiPbPhotoFragmentView {
    private static final String TAG = "RemoteMultiPbPhotoFragment";
    StickyGridHeadersGridView multiPbPhotoGridView;
    ListView listView;
    TextView headerView;
    FrameLayout multiPbPhotoListLayout;
    MultiPbPhotoFragmentPresenter presenter;
    private OnStatusChangedListener modeChangedListener;
    private boolean isCreated = false;
    private boolean isVisible = false;
    TextView noContentTxv;


    public RemoteMultiPbPhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppLog.d(TAG,"RemoteMultiPbPhotoFragment onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multi_pb_photo, container, false);
        multiPbPhotoGridView = (StickyGridHeadersGridView) view.findViewById(R.id.multi_pb_photo_grid_view);
        listView = (ListView) view.findViewById(R.id.multi_pb_photo_list_view);
        headerView = (TextView) view.findViewById(R.id.photo_wall_header);
        multiPbPhotoListLayout = (FrameLayout) view.findViewById(R.id.multi_pb_photo_list_layout);
        noContentTxv = (TextView) view.findViewById(R.id.no_content_txv);

        presenter = new MultiPbPhotoFragmentPresenter(getActivity());
        presenter.setView(this);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int firstVisible = listView.getFirstVisiblePosition();
                int lastVisible = listView.getLastVisiblePosition();
                AppLog.d(TAG, "listView onScrollStateChanged firstVisible=" + firstVisible + " lastVisible=" + lastVisible);
                if(isVisible){
                    presenter.listViewLoadThumbnails(scrollState,firstVisible,lastVisible-firstVisible+1);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(isVisible){
                    presenter.listViewLoadOnceThumbnails(firstVisibleItem, visibleItemCount);
                }
            }
        });

        multiPbPhotoGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                AppLog.d(TAG, "11333 view onScrollStateChanged firstVisible=" + view.getFirstVisiblePosition() + " lastVisible=" + view.getLastVisiblePosition());

                int firstVisible = multiPbPhotoGridView.getFirstVisiblePosition();
                int lastVisible = multiPbPhotoGridView.getLastVisiblePosition();
                AppLog.d(TAG, "11333 onScrollStateChanged firstVisible=" + firstVisible + " lastVisible=" + lastVisible);
                if(isVisible){
                    presenter.gridViewLoadThumbnails(scrollState,firstVisible,lastVisible-firstVisible+1);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                AppLog.d(TAG, "1122 onScroll firstVisibleItem=" + firstVisibleItem + " visibleItemCount=" + visibleItemCount);

                if(isVisible){
                    presenter.gridViewLoadOnceThumbnails(firstVisibleItem, visibleItemCount);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(isVisible){
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
        if(isVisible){
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
        presenter.clealAsytaskList();
        presenter.emptyFileList();
    }

    public void changePreviewType(){
        AppLog.d(TAG, "start changePreviewType presenter=" + presenter);
        if(presenter != null){
            presenter.changePreviewType();
        }
    }

    public void quitEditMode(){
        presenter.quitEditMode();
    }

    @Override
    public void setListViewVisibility(int visibility) {
        multiPbPhotoListLayout.setVisibility(visibility);
    }

    @Override
    public void setGridViewVisibility(int visibility) {
        multiPbPhotoGridView.setVisibility(visibility);
    }

    @Override
    public void setListViewAdapter(MultiPbPhotoWallListAdapter photoWallListAdapter) {
        listView.setAdapter(photoWallListAdapter);
    }

    @Override
    public void setGridViewAdapter(MultiPbPhotoWallGridAdapter photoWallGridAdapter) {
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
        if(imageView != null){
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void notifyChangeMultiPbMode(OperationMode operationMode) {
        if (modeChangedListener != null){
            modeChangedListener.onChangeOperationMode(operationMode);
        }
    }

    @Override
    public void setPhotoSelectNumText(int selectNum) {
        if (modeChangedListener != null){
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
            presenter.loadPhotoWall();
        }
    }

    public void refreshPhotoWall(){
        presenter.refreshPhotoWall();
    }

    public void setOperationListener(OnStatusChangedListener modeChangedListener){
        this.modeChangedListener = modeChangedListener;
    }

    public void selectOrCancelAll(boolean isSelectAll){
        presenter.selectOrCancelAll(isSelectAll);
    }

    public List<MultiPbItemInfo> getSelectedList() {
        return presenter.getSelectedList();
    }

    public void clealAsytaskList(){
        presenter.clealAsytaskList();
    }


}
