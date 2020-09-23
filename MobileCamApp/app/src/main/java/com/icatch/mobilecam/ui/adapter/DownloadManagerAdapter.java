package com.icatch.mobilecam.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.icatch.mobilecam.data.entity.DownloadInfo;
import com.icatch.mobilecam.ui.ExtendComponent.NumberProgressBar;
import com.icatch.mobilecam.data.Message.AppMessage;
import com.icatch.mobilecam.R;
import com.icatchtek.reliant.customer.type.ICatchFile;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Added by zhangyanhu C01012,2014-5-28
 */
public class DownloadManagerAdapter extends BaseAdapter {
    private String TAG = "DownloadManagerAdapter";
    private Context context;
    private HashMap<Integer, DownloadInfo> chooseListMap;
    private List<ICatchFile> actList;
    private Handler handler;
    private OnCancelBtnClickListener onCancelBtnClickListener;



    public DownloadManagerAdapter(Context context, HashMap<Integer, DownloadInfo> downloadDataList, List<ICatchFile> actList, Handler handler) {
        this.context = context;
        this.chooseListMap = downloadDataList;
        this.actList = actList;
        this.handler = handler;
    }

    public void setOnCancelBtnClickListener(OnCancelBtnClickListener onCancelBtnClickListener) {
        this.onCancelBtnClickListener = onCancelBtnClickListener;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return actList.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.download, null);
        }
        if (position >= actList.size()) {
            return convertView;
        }
        final ImageButton cancelImv = (ImageButton) convertView.findViewById(R.id.doAction);
        TextView fileName = (TextView) convertView.findViewById(R.id.fileName);
        TextView downloadStatus = (TextView) convertView.findViewById(R.id.downloadStatus);

        fileName.setText(actList.get(position).getFileName());
//        ProgressBar processBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
        NumberProgressBar numberProgressBar = (NumberProgressBar) convertView.findViewById(R.id.numberbar);
        final ICatchFile downloadFile = actList.get(position);
        final DownloadInfo downloadInfo = chooseListMap.get(downloadFile.getFileHandle());
//        processBar.setProgress(downloadInfo.progress);
//        numberProgressBar.incrementProgressBy(downloadInfo.progress);
        numberProgressBar.setProgress(downloadInfo.progress);
        DecimalFormat df = new DecimalFormat("#.#");
        String curFileLength = df.format(downloadInfo.curFileLength / 1024.0 / 1024) + "M";
        String fileSize = df.format(downloadInfo.fileSize / 1024.0 / 1024) + "M";

        if (downloadInfo.progress >= 100 || downloadInfo.isDone()) {
            downloadStatus.setText(curFileLength + "/" + fileSize);
            cancelImv.setImageResource(R.drawable.ic_done_cyan);
            cancelImv.setClickable(false);
        } else if (downloadInfo.progress <= 0) {
            downloadStatus.setText(curFileLength + "/" + fileSize);
            cancelImv.setImageResource(R.drawable.ic_close_black);
        } else {
            downloadStatus.setText(curFileLength + "/" + fileSize);
            cancelImv.setImageResource(R.drawable.ic_close_black);
        }
        cancelImv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if(onCancelBtnClickListener != null){
                    if (downloadInfo.progress < 100) {
//                    handler.obtainMessage(AppMessage.MESSAGE_CANCEL_DOWNLOAD_SINGLE, 0, 0, downloadFile).sendToTarget();
                        onCancelBtnClickListener.onClick(downloadFile);
                    }
                }

            }
        });
        return convertView;
    }

    public interface OnCancelBtnClickListener{
        void onClick(ICatchFile downloadFile);
    }
}
