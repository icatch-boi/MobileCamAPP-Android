package com.icatch.mobilecam.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.entity.FilterItem;
import com.icatch.mobilecam.ui.RemoteFileHelper;
import com.icatch.mobilecam.ui.adapter.FilterGridAdaper;
import com.icatch.mobilecam.utils.DisplayHelper;
import com.icatch.mobilecam.utils.FileFilter;
import com.icatch.mobilecam.utils.TimeTools;
import com.icatchtek.control.customer.type.ICatchCamListFileFilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static java.util.Calendar.YEAR;


/**
 * author : b.jiang
 * time   : 2020/01/21
 * desc   :
 */
public class DialogFragmentFromBottom extends DialogFragment {
    private static final String TAG = DialogFragmentFromBottom.class.getSimpleName();
    private static final String MARGIN = "margin";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String DIM = "dim_amount";
    private static final String BOTTOM = "show_bottom";
    private static final String CANCEL = "out_cancel";
    private static final String ANIM = "anim_style";
    private static final String LAYOUT = "layout_id";
    private static final String formatType = "yyyy-MM-dd HH:mm:ss";

    private int margin;//左右边距
    private int width;//宽度
    private int height;//高度
    private float dimAmount = 0.5f;//灰度深浅
    private boolean showBottom = true;//是否底部显示
    private boolean outCancel = true;//是否点击外部取消
    @StyleRes
    private int animStyle;
    @LayoutRes
    protected int layoutId;
    private GridView timeGridView;
    private GridView cameraTypeGridView;
    private TextView minTimeEdt;
    private TextView maxTimeEdt;
    FrameLayout rootlayout;
    LinearLayout layout;
    private TextView resetTxv;
    private TextView sureTxv;
    private TextView sensorsTypeTxv;
    FilterGridAdaper timeAdaper;
    FilterGridAdaper cameraTypeAdaper;
    OnSureClickListener onSureClickListener;
    private FileFilter lastFilter;
    private int sensorsNum = 2;
    private boolean needFilterSensors = false;
    LinkedList<FilterItem> timeTypelist;
    LinkedList<FilterItem> sensorsTypeList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.NiceDialog);

        //恢复保存的数据
        if (savedInstanceState != null) {
            margin = savedInstanceState.getInt(MARGIN);
            width = savedInstanceState.getInt(WIDTH);
            height = savedInstanceState.getInt(HEIGHT);
            dimAmount = savedInstanceState.getFloat(DIM);
            showBottom = savedInstanceState.getBoolean(BOTTOM);
            outCancel = savedInstanceState.getBoolean(CANCEL);
            animStyle = savedInstanceState.getInt(ANIM);
            layoutId = savedInstanceState.getInt(LAYOUT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_filter, container, false);
        rootlayout = view.findViewById(R.id.root_layout);
        timeGridView = view.findViewById(R.id.time_list);
        cameraTypeGridView = view.findViewById(R.id.camera_type_list);
        minTimeEdt = view.findViewById(R.id.min_time_edt);
        maxTimeEdt = view.findViewById(R.id.max_time_edt);
        resetTxv = view.findViewById(R.id.filter_reset);
        sureTxv = view.findViewById(R.id.filter_sure);
        sensorsTypeTxv = view.findViewById(R.id.sensors_type_txv);
        initData();
        resetTxv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimeFilter();
                resetCameraTypeFilter();
                resetTimeEdit();
            }
        });

        sureTxv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String minTimeString = minTimeEdt.getText().toString();
                String maxTimeString = maxTimeEdt.getText().toString();
                FileFilter fileFilter = null;
                if (!minTimeString.isEmpty() && !maxTimeString.isEmpty()) {
                    try {
                        long minTime = TimeTools.stringToLong(minTimeString, formatType);
                        long maxTime = TimeTools.stringToLong(maxTimeString, formatType);
                        fileFilter = new FileFilter();
                        fileFilter.setStartTime(minTime);
                        fileFilter.setEndTime(maxTime);
                        fileFilter.setTimeFilterType(FileFilter.TIME_TYPE_CUSTOMIZE);
                    } catch (ParseException e) {
                        AppLog.d(TAG, "ParseException");
                        e.printStackTrace();
                    }
                } else if (timeAdaper.getSelectorPosition() >= 0) {
                    int position = timeAdaper.getSelectorPosition();
                    if(position >= 0){
                        fileFilter = new FileFilter();
                        fileFilter.setTimeFilterType(timeTypelist.get(position).getFilterValue());
                    }
                }
                if(needFilterSensors){
                    int position = cameraTypeAdaper.getSelectorPosition();
                    if(position >= 0){
                        if(fileFilter == null){
                            fileFilter = new FileFilter();
                        }
                        fileFilter.setSensorType(sensorsTypeList.get(position).getFilterValue());
                    }
                }

                if (onSureClickListener != null) {
                    onSureClickListener.onSureClick(fileFilter);
                }
                dismiss();
            }
        });
        minTimeEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show02(new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        date.setMinutes(0);
                        date.setSeconds(0);
                        minTimeEdt.setText(getTime(date));
                        resetTimeFilter();
                        resetCameraTypeFilter();

                    }
                });
            }
        });

        maxTimeEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show02(new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        date.setMinutes(59);
                        date.setSeconds(59);
                        maxTimeEdt.setText(getTime(date));
                        resetTimeFilter();
                        resetCameraTypeFilter();
                    }
                });
            }
        });

        timeAdaper = new FilterGridAdaper(getActivity(), timeTypelist);
        timeGridView.setAdapter(timeAdaper);
        timeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                timeAdaper.changeState(position);
                resetTimeEdit();
            }
        });
        cameraTypeAdaper = new FilterGridAdaper(getActivity(), sensorsTypeList);
        cameraTypeGridView.setAdapter(cameraTypeAdaper);
        cameraTypeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cameraTypeAdaper.changeState(position);
                resetTimeEdit();
            }
        });
        initUi();
        return view;
    }

    private void initData(){
        timeTypelist = new LinkedList<>();
        timeTypelist.add(new FilterItem("今天",FileFilter.TIME_TYPE_TODAY));
        timeTypelist.add(new FilterItem("近三天",FileFilter.TIME_TYPE_LAST_THREE_DAY));
        timeTypelist.add(new FilterItem("近一周",FileFilter.TIME_TYPE_ALMOST_A_WEEK));
        timeTypelist.add(new FilterItem("近一个月",FileFilter.TIME_TYPE_ALMOST_A_MONTH));
        timeTypelist.add(new FilterItem("近半年",FileFilter.TIME_TYPE_LAST_HALF_YEAR));

        sensorsTypeList = new LinkedList<>();
        sensorsTypeList.add(new FilterItem("全部",ICatchCamListFileFilter.ICH_TAKEN_BY_ALL_SENSORS));
        sensorsTypeList.add(new FilterItem("前置",ICatchCamListFileFilter.ICH_TAKEN_BY_FRONT_SENSOR));
        sensorsTypeList.add(new FilterItem("后置",ICatchCamListFileFilter.ICH_TAKEN_BY_BACK_SENSOR));
    }

    private void initUi(){
        if(lastFilter != null){
            if(lastFilter.getTimeFilterType() == FileFilter.TIME_TYPE_CUSTOMIZE){
                minTimeEdt.setText(lastFilter.getStringTimeString());
                maxTimeEdt.setText(lastFilter.getEndTimeString());
            }else {
                int position = getPositionByType(timeTypelist,lastFilter.getTimeFilterType());
                if(position >=0){
                    timeAdaper.changeState(position);
                }
            }
        }
        sensorsNum = RemoteFileHelper.getInstance().getSensorsNum();
        if(sensorsNum <= 1){
            needFilterSensors = false;
            sensorsTypeTxv.setVisibility(View.GONE);
            cameraTypeGridView.setVisibility(View.GONE);
            return;
        }else {
            needFilterSensors = true;
            sensorsTypeTxv.setVisibility(View.VISIBLE);
            cameraTypeGridView.setVisibility(View.VISIBLE);
            if(lastFilter ==null){
                cameraTypeAdaper.changeState(0);
            }else {
                int position = getPositionByType(sensorsTypeList,lastFilter.getSensorType());
                if(position >=0){
                    cameraTypeAdaper.changeState(position);
                }
            }
        }
    }

    private int getPositionByType(List<FilterItem> list, int type){
        if(list == null || list.size() <=0){
            return -1;
        }
        int position = -1;
        for (int ii=0 ;ii <list.size();ii++){
            if(list.get(ii).getFilterValue() == type){
                position = ii;
                break;
            }
        }
        return position;
    }

    @Override
    public void onStart() {
        super.onStart();
        initParams();
    }

    public void setLastFilter(FileFilter lastFilter) {
        this.lastFilter = lastFilter;
    }

    public void setOnSureClickListener(OnSureClickListener onSureClickListener) {
        this.onSureClickListener = onSureClickListener;
    }

    private void show02(OnTimeSelectListener listener) {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);
        selectedDate.set(Calendar.MINUTE, 0);
        selectedDate.set(Calendar.SECOND, 0);
        selectedDate.set(Calendar.MILLISECOND, 0);

        //正确设置方式 原因：注意事项有说明
        int end = endDate.get(YEAR) - 10;

        startDate.set(YEAR, end);
        TimePickerView pvTime = new TimePickerBuilder(getActivity(), listener)
                .setType(new boolean[]{true, true, true, true, false, false})// 默认全部显示
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setDecorView(rootlayout)
                .build();

        pvTime.show();
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat(formatType);
        return sf.format(d);
    }

    public String getTime(Date d) {
        SimpleDateFormat sf = new SimpleDateFormat(formatType);
        return sf.format(d);
    }

    /**
     * 屏幕旋转等导致DialogFragment销毁后重建时保存数据
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MARGIN, margin);
        outState.putInt(WIDTH, width);
        outState.putInt(HEIGHT, height);
        outState.putFloat(DIM, dimAmount);
        outState.putBoolean(BOTTOM, showBottom);
        outState.putBoolean(CANCEL, outCancel);
        outState.putInt(ANIM, animStyle);
        outState.putInt(LAYOUT, layoutId);
    }

    private void initParams() {
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            //调节灰色背景透明度[0-1]，默认0.5f
            lp.dimAmount = dimAmount;
            //是否在底部显示
            if (showBottom) {
                lp.gravity = Gravity.BOTTOM;
                if (animStyle == 0) {
                    animStyle = R.style.DefaultAnimation;
                }
            }

            //设置dialog宽度
            if (width == 0) {
                lp.width = DisplayHelper.getScreenWidth(getActivity()) - 2 * DisplayHelper.dp2px(getActivity(), margin);
            } else {
                lp.width = DisplayHelper.dp2px(getActivity(), width);
            }
            //设置dialog高度
            if (height == 0) {
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                lp.height = DisplayHelper.dp2px(getActivity(), height);
            }

            //设置dialog进入、退出的动画
            window.setWindowAnimations(animStyle);
            window.setAttributes(lp);
        }
        setCancelable(outCancel);
    }

    public DialogFragmentFromBottom setMargin(int margin) {
        this.margin = margin;
        return this;
    }

    public DialogFragmentFromBottom setWidth(int width) {
        this.width = width;
        return this;
    }

    public DialogFragmentFromBottom setHeight(int height) {
        this.height = height;
        return this;
    }

    public DialogFragmentFromBottom setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
        return this;
    }

    public DialogFragmentFromBottom setShowBottom(boolean showBottom) {
        this.showBottom = showBottom;
        return this;
    }

    public DialogFragmentFromBottom setOutCancel(boolean outCancel) {
        this.outCancel = outCancel;
        return this;
    }

    public DialogFragmentFromBottom setAnimStyle(@StyleRes int animStyle) {
        this.animStyle = animStyle;
        return this;
    }

    public DialogFragmentFromBottom show(FragmentManager manager) {
        super.show(manager, String.valueOf(System.currentTimeMillis()));
        return this;
    }

    private void resetTimeFilter() {
        if (timeAdaper != null) {
            timeAdaper.changeState(-1);
        }
    }

    private void resetCameraTypeFilter() {
        if (cameraTypeAdaper != null) {
            cameraTypeAdaper.changeState(0);
        }
    }

    private void resetTimeEdit() {
        minTimeEdt.setText("");
        maxTimeEdt.setText("");
    }

    public interface OnSureClickListener {
        void onSureClick(FileFilter fileFilter);
    }


}