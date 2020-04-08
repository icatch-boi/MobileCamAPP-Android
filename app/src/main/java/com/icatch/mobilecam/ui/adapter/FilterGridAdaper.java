package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.icatch.mobilecam.R;
import com.icatch.mobilecam.data.entity.FilterItem;

import java.util.List;

/**
 * @author b.jiang
 * @date 2019/12/27
 * @description
 */
public class FilterGridAdaper extends BaseAdapter {
    private String TAG = "FilterGridAdaper";
    private Context context;
    private List<FilterItem> list;
    private LayoutInflater mInflater;
    private int width;
    private int selectorPosition = -1;

    public FilterGridAdaper(Context context, List<FilterItem> list) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        AppLog.d(TAG, "getView position=" + position + " convertView=" + convertView);
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_filter, parent, false);
            mViewHolder.textView = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        if(position == selectorPosition){
            mViewHolder.textView.setTextColor(context.getResources().getColor(R.color.text_w));
            mViewHolder.textView.setBackgroundResource(R.drawable.shape_bg_blue_circle);
        }else {
            mViewHolder.textView.setTextColor(context.getResources().getColor(R.color.text));
            mViewHolder.textView.setBackgroundResource(R.drawable.shape_bg_transparent_circle);
        }
        mViewHolder.textView.setText(list.get(position).getValue());
        return convertView;
    }

    public static class ViewHolder {
        private TextView textView;
    }

    public void setSelectorPosition(int selectorPosition) {
        this.selectorPosition = selectorPosition;
    }

    public int getSelectorPosition() {
        return selectorPosition;
    }

    public void changeState(int pos) {
        selectorPosition = pos;
        notifyDataSetChanged();
    }
}