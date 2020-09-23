package com.icatch.mobilecam.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.Presenter.RemoteMultiPbPresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.type.PhotoWallLayoutType;
import com.icatch.mobilecam.ui.Fragment.DialogFragmentFromBottom;
import com.icatch.mobilecam.ui.Interface.MultiPbView;
import com.icatch.mobilecam.ui.RemoteFileHelper;
import com.icatch.mobilecam.utils.FileFilter;
import com.icatch.mobilecam.utils.FixedSpeedScroller;

import java.lang.reflect.Field;

public class RemoteMultiPbActivity extends AppCompatActivity implements MultiPbView {
    private String TAG = "RemoteMultiPbActivity";
    private ViewPager viewPager;//页卡内容V
    private RemoteMultiPbPresenter presenter;
    MenuItem menuPhotoWallType;
    ImageButton selectBtn;
    ImageButton deleteBtn;
    ImageButton downloadBtn;
    TextView selectedNumTxv;
    LinearLayout multiPbEditLayout;
    TabLayout tabLayout;
    MenuItem filterItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppLog.d(TAG,"onCreate ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_pb);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.vPager);
        selectBtn = (ImageButton) findViewById(R.id.action_select);
        deleteBtn = (ImageButton) findViewById(R.id.action_delete);
        downloadBtn = (ImageButton) findViewById(R.id.action_download);
        selectedNumTxv = (TextView) findViewById(R.id.info_selected_num);
        multiPbEditLayout = (LinearLayout) findViewById(R.id.edit_layout);
        //viewPager.setPageMargin((int) getResources().getDimensionPixelOffset(R.dimen.space_10));

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        presenter = new RemoteMultiPbPresenter(this);
        presenter.setView(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                presenter.updateViewpagerStatus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.selectOrCancel();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.delete();
            }
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.download();
            }
        });
        presenter.loadViewPager();
        tabLayout.setupWithViewPager(viewPager);

        //修改viewPager切换滑动速度 JIRA ICOM-3509 20160721
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(),
                    new AccelerateInterpolator());
            field.set(viewPager, scroller);
            scroller.setmDuration(280);
        } catch (Exception e) {
            AppLog.e(TAG, "FixedSpeedScroller Exception");
        }
//        tabLayout.setTabsFromPagerAdapter();
//        deleteBtn.setClickable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.submitAppInfo();
        presenter.setSdCardEventListener();
        AppLog.d(TAG, "onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.isAppBackground();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.reset();
        presenter.removeActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multi_pb, menu);
        filterItem = menu.findItem(R.id.menu_multi_pb_filter);
        setFilterItemVisibiliy(RemoteFileHelper.getInstance().isSupportSegmentedLoading());
        return true;
    }

    @Override
    public void setFilterItemVisibiliy(boolean visibility) {
        if (filterItem != null) {
            filterItem.setVisible(visibility);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.grid) {
            presenter.changePreviewType(PhotoWallLayoutType.PREVIEW_TYPE_GRID);
        } else if (id == R.id.quick_liner) {
            presenter.changePreviewType(PhotoWallLayoutType.PREVIEW_TYPE_QUICK_LIST);
        } else if (id == R.id.liner) {
            presenter.changePreviewType(PhotoWallLayoutType.PREVIEW_TYPE_LIST);
        } else if (id == android.R.id.home) {
            presenter.reback();
            return true;
        } else if (id == R.id.menu_multi_pb_filter) {
            showFilterDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showFilterDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragmentFromBottom newFragment = new DialogFragmentFromBottom();
        newFragment.setOutCancel(true);
        newFragment.setLastFilter(RemoteFileHelper.getInstance().getFileFilter());
        newFragment.setOnSureClickListener(new DialogFragmentFromBottom.OnSureClickListener() {
            @Override
            public void onSureClick(FileFilter fileFilter) {
                AppLog.d(TAG, "onSureClick fileFilter:" + fileFilter);
                if (fileFilter != null) {
                    AppLog.d(TAG, "onSureClick startTime:" + fileFilter.getStringTimeString());
                    AppLog.d(TAG, "onSureClick endTime:" + fileFilter.getEndTimeString());
                }
                presenter.setFileFilter(fileFilter);

            }
        });
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                Log.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                Log.d("AppStart", "back");
                presenter.reback();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public void setViewPageAdapter(FragmentPagerAdapter adapter) {
        viewPager.setAdapter(adapter);
    }

    @Override
    public void setViewPageCurrentItem(int item) {
        AppLog.d(TAG, "setViewPageCurrentItem item=" + item);
        viewPager.setCurrentItem(item);
    }

    @Override
    public void setMenuPhotoWallTypeIcon(int iconRes) {
        //menuPhotoWallType.setIcon(iconRes);
    }

    @Override
    public void setViewPagerScanScroll(boolean isCanScroll) {
//        viewPager.setScanScroll(isCanScroll);
    }

    @Override
    public void setSelectNumText(String text) {
        selectedNumTxv.setText(text);
    }

    @Override
    public void setSelectBtnVisibility(int visibility) {
        selectBtn.setVisibility(visibility);
    }

    @Override
    public void setSelectBtnIcon(int icon) {
        selectBtn.setImageResource(icon);
    }

    @Override
    public void setSelectNumTextVisibility(int visibility) {
        selectedNumTxv.setVisibility(visibility);
    }

    @Override
    public void setTabLayoutClickable(boolean value) {
        AppLog.d(TAG, "setTabLayoutClickable value=" + value);
        tabLayout.setClickable(value);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tabLayout.setContextClickable(value);
        }
        tabLayout.setFocusable(value);
        tabLayout.setLongClickable(value);
        tabLayout.setEnabled(value);
    }

    @Override
    public void setEditLayoutVisibiliy(int visibiliy) {
        multiPbEditLayout.setVisibility(visibiliy);
    }

    @Override
    public int getViewPageIndex() {
        return viewPager.getCurrentItem();
    }
}
