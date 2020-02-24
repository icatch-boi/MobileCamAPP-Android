package com.icatch.mobilecam.ui.activity;

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
import com.icatch.mobilecam.Presenter.LocalMultiPbPresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.ui.Interface.LocalMultiPbView;
import com.icatch.mobilecam.utils.FixedSpeedScroller;

import java.lang.reflect.Field;

public class LocalMultiPbActivity extends AppCompatActivity implements LocalMultiPbView {
    private String TAG = "LocalMultiPbActivity";
    private ViewPager viewPager;//页卡内容V
    private LocalMultiPbPresenter presenter;
    MenuItem menuPhotoWallType;
    ImageButton selectBtn;
    ImageButton deleteBtn;
    ImageButton shareBtn;
    TextView selectedNumTxv;
    LinearLayout multiPbEditLayout;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_multi_pb);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.vPager);
        selectBtn = (ImageButton) findViewById(R.id.action_select);
        deleteBtn = (ImageButton) findViewById(R.id.action_delete);
        shareBtn = (ImageButton) findViewById(R.id.action_share);
        selectedNumTxv = (TextView) findViewById(R.id.info_selected_num);
        multiPbEditLayout = (LinearLayout) findViewById(R.id.edit_layout);
        //viewPager.setPageMargin((int) getResources().getDimensionPixelOffset(R.dimen.space_10));

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        presenter = new LocalMultiPbPresenter(this);
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
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                presenter.share();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.submitAppInfo();
        AppLog.d(TAG, "onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        presenter.isAppBackground();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_multi_pb_preview_type) {
            menuPhotoWallType = item;
            presenter.changePreviewType();
        } else if (id == android.R.id.home) {
            presenter.reback();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        menuPhotoWallType.setIcon(iconRes);
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
    public void setEditLayoutVisibility(int visibility) {
        multiPbEditLayout.setVisibility(visibility);
    }
}
