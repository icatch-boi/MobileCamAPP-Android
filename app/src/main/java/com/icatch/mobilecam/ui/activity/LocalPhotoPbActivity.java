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
import com.icatch.mobilecam.Presenter.LocalPhotoPbPresenter;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.ui.ExtendComponent.HackyViewPager;
import com.icatch.mobilecam.ui.Interface.LocalPhotoPbView;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;

public class LocalPhotoPbActivity extends AppCompatActivity implements LocalPhotoPbView {
    private static final String TAG = LocalPhotoPbActivity.class.getSimpleName();
    private HackyViewPager viewPager;
    private TextView indexInfoTxv;
    private SurfaceView mSurfaceView;
    private ImageButton shareBtn;
    private ImageButton deleteBtn;
    private ImageButton photoInfoBtn;
    private RelativeLayout topBar;
    private LinearLayout bottomBar;
    private ImageButton back;
    private LocalPhotoPbPresenter presenter;
    private ImageButton doPrevious;
    private ImageButton doNext;
    private TextView panoramaTypeTxv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppLog.d( TAG, "onCreate" );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_local_photo_pb );

        viewPager = (HackyViewPager) findViewById(R.id.viewpager);
        viewPager.setPageMargin(30);
        indexInfoTxv = (TextView) findViewById(R.id.pb_index_info);
        mSurfaceView = (SurfaceView) findViewById( R.id.m_surfaceView );
        shareBtn = (ImageButton) findViewById( R.id.local_photo_pb_share );
        deleteBtn = (ImageButton) findViewById( R.id.local_photo_pb_delete );
        photoInfoBtn = (ImageButton) findViewById( R.id.local_photo_pb_info );
        topBar = (RelativeLayout) findViewById( R.id.local_pb_top_layout );
        bottomBar = (LinearLayout) findViewById( R.id.local_pb_bottom_layout );
        back = (ImageButton) findViewById( R.id.local_pb_back );
        doPrevious = (ImageButton) findViewById(R.id.do_previous);
        doNext = (ImageButton) findViewById(R.id.do_next);
        panoramaTypeTxv = (TextView) findViewById(R.id.panorama_type_btn);

        presenter = new LocalPhotoPbPresenter(this);
        presenter.setView( this );


        shareBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.share();
            }
        } );

        deleteBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.delete();
            }
        } );
        photoInfoBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.info();
            }
        } );

        panoramaTypeTxv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.setPanoramaType();

            }
        });

        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                presenter.destroyImage( ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
//                finish();
                presenter.finish();
            }
        } );

        doPrevious.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG,"....doPrevious");
                presenter.loadPreviousImage();
            }
        } );

        doNext.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG,"....doNext");
                presenter.loadNextImage();
            }
        } );

        mSurfaceView.getHolder().addCallback( new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                AppLog.d(TAG,"surfaceCreated");
                presenter.setShowArea(mSurfaceView.getHolder().getSurface());
                presenter.loadPanoramaImage();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                AppLog.d(TAG,"surfaceChanged........width="+width);
                presenter.setDrawingArea(width,height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                AppLog.d(TAG,"surfaceDestroyed");
                presenter.clearImage(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
            }
        } );

        mSurfaceView.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        presenter.onSufaceViewTouchDown( event );
                        break;
                    case MotionEvent.ACTION_UP:
                        presenter.onSufaceViewTouchUp();
                        break;

                    // 多点触摸
                    case MotionEvent.ACTION_POINTER_DOWN:
                        presenter.onSufaceViewPointerDown( event );
                        break;
                    // 多点松开
                    case MotionEvent.ACTION_POINTER_UP:
                        presenter.onSufaceViewTouchPointerUp();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        presenter.onSufaceViewTouchMove( event );
                        break;


                }
                return true;
            }
        } );
        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG,"viewPager.setOnClickListener");
                // presenter.showBar();
            }
        });

        presenter.initPanorama();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                Log.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                Log.d("AppStart", "back");
//                presenter.destroyImage(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
//                finish();
                presenter.finish();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    protected void onResume() {
        AppLog.d( TAG, "onResume" );
        super.onResume();
        presenter.initView();
        presenter.submitAppInfo();
        presenter.registerGyroscopeSensor();
    }


    @Override
    protected void onStart() {
        AppLog.d( TAG, "onStart" );
        super.onStart();
    }

    @Override
    protected void onStop() {
        AppLog.d( TAG, "onStop" );
        super.onStop();
        presenter.isAppBackground();
    }

    @Override
    protected void onDestroy() {
        AppLog.d( TAG, "onDestroy" );
        super.onDestroy();
        presenter.removeActivity();
    }

    @Override
    protected void onPause() {
        presenter.removeGyroscopeListener();
        super.onPause();
    }

    @Override
    public void setViewPagerAdapter(PagerAdapter adapter) {
        if(adapter != null){
            viewPager.setAdapter(adapter);
        }
    }

    @Override
    public void setIndexInfoTxv(String indexInfo) {
        indexInfoTxv.setText(indexInfo);
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
    public void setTopBarVisibility(int visibility) {
        topBar.setVisibility( visibility );
    }

    @Override
    public void setBottomBarVisibility(int visibility) {
        bottomBar.setVisibility( visibility );
    }

    @Override
    public void setSurfaceviewVisibility(int visibility) {
        int curVisibility= mSurfaceView.getVisibility();
        if(curVisibility != visibility){
            mSurfaceView.setVisibility(visibility);
        }
    }

    @Override
    public void setViewPagerVisibility(int visibility) {
        viewPager.setVisibility( visibility );
    }

    @Override
    public void setPanoramaTypeTxv(int resId) {
        panoramaTypeTxv.setText(resId);
    }
}
