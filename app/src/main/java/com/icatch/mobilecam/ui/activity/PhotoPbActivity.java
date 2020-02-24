package com.icatch.mobilecam.ui.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.Presenter.PhotoPbPresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.ui.ExtendComponent.HackyViewPager;
import com.icatch.mobilecam.ui.Interface.PhotoPbView;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;

public class PhotoPbActivity extends AppCompatActivity implements PhotoPbView {
    private static final String TAG = PhotoPbActivity.class.getSimpleName();
    private HackyViewPager viewPager;
    private ImageButton downloadBtn;
    private ImageButton deleteBtn;
    private TextView indexInfoTxv;
    private RelativeLayout topBar;
    private LinearLayout bottomBar;
    private PhotoPbPresenter presenter;
    private ImageButton back;

    private SurfaceView mSurfaceView;
    private ImageButton doPrevious;
    private ImageButton doNext;
    private TextView panoramaTypeTxv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pb);

        viewPager = (HackyViewPager) findViewById(R.id.viewpager);
        indexInfoTxv = (TextView) findViewById(R.id.pb_index_info);
        downloadBtn = (ImageButton) findViewById(R.id.photo_pb_download);
        deleteBtn = (ImageButton) findViewById(R.id.photo_pb_delete);
        topBar = (RelativeLayout) findViewById(R.id.pb_top_layout);
        bottomBar = (LinearLayout) findViewById(R.id.pb_bottom_layout);
        back = (ImageButton) findViewById(R.id.pb_back);
        doPrevious = (ImageButton) findViewById(R.id.do_previous);
        doNext = (ImageButton) findViewById(R.id.do_next);
        mSurfaceView = (SurfaceView) findViewById(R.id.m_surfaceView);
        panoramaTypeTxv = (TextView) findViewById(R.id.panorama_type_btn);

        presenter = new PhotoPbPresenter(this);
        presenter.setView(this);
        viewPager.setPageMargin(30);

        panoramaTypeTxv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.setPanoramaType();

            }
        });
        doPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG, "....doPrevious");
                presenter.loadPreviousImage();
            }
        });

        doNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG, "....doNext");
                presenter.loadNextImage();
            }
        });

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                AppLog.d(TAG, "surfaceCreated");
                presenter.initPanorama();
                presenter.setShowArea(mSurfaceView.getHolder().getSurface());
                presenter.loadPanoramaImage();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                AppLog.d(TAG, "surfaceChanged........width=" + width);
                presenter.setDrawingArea(width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                AppLog.d(TAG, "surfaceDestroyed");
                presenter.destroyImage(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
            }
        });

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        presenter.onSufaceViewTouchDown(event);
                        break;
                    // 多点触摸
                    case MotionEvent.ACTION_POINTER_DOWN:
                        presenter.onSufaceViewPointerDown(event);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        presenter.onSufaceViewTouchMove(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        presenter.onSufaceViewTouchUp();
                        break;

                    // 多点松开
                    case MotionEvent.ACTION_POINTER_UP:
                        presenter.onSufaceViewTouchPointerUp();
                        break;
                }
                return true;
            }
        });

        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG, "viewPager.setOnClickListener");
//                presenter.showBar();
            }
        });


        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.download();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.delete();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.destroyImage(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.initView();
        presenter.submitAppInfo();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                Log.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                Log.d("AppStart", "back");
                presenter.destroyImage(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
                finish();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.isAppBackground();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.removeActivity();
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged( newConfig );
//        presenter.reloadBitmap();
//    }

    @Override
    public void setViewPagerAdapter(PagerAdapter adapter) {
        if (adapter != null) {
            viewPager.setAdapter(adapter);
        }
    }

    @Override
    public void setTopBarVisibility(int visibility) {
        topBar.setVisibility(visibility);

    }

    @Override
    public void setBottomBarVisibility(int visibility) {
        bottomBar.setVisibility(visibility);
    }

    @Override
    public void setIndexInfoTxv(String photoName) {
        indexInfoTxv.setText(photoName);
    }

    @Override
    public void setViewPagerCurrentItem(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        viewPager.addOnPageChangeListener(listener);
    }

    @Override
    public int getViewPagerCurrentItem() {
        return viewPager.getCurrentItem();
    }

    @Override
    public int getTopBarVisibility() {
        return topBar.getVisibility();
    }

    @Override
    public void setSurfaceviewTransparent(boolean value) {
        if (value) {
            mSurfaceView.setVisibility(View.GONE);
//            mSurfaceView.setZOrderOnTop( true );//设置画布  背景透明
//            mSurfaceView.getHolder().setFormat( PixelFormat.TRANSLUCENT );
        } else {
//            mSurfaceView.getHolder().
            mSurfaceView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setPanoramaTypeTxv(int resId) {
        panoramaTypeTxv.setText(resId);
    }

    @Override
    public void setViewPagerVisibility(int visibility) {
        viewPager.setVisibility(visibility);
    }

}

