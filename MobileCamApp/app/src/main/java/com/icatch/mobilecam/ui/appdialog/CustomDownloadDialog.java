package com.icatch.mobilecam.ui.appdialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.icatch.mobilecam.ui.adapter.DownloadManagerAdapter;
import com.icatch.mobilecam.R;

public class CustomDownloadDialog {
    private TextView message;
    private ListView downloadStatus;
    //private ImageButton exit;
    private TextView cancelAllTxv;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    public CustomDownloadDialog(){

    }

    public void showDownloadDialog(Context context,DownloadManagerAdapter adapter) {
        builder = new AlertDialog.Builder(context);
        View contentView = View.inflate(context, R.layout.download_content_dialog, null);
        View titleView = View.inflate(context, R.layout.download_dialog_title, null);
        cancelAllTxv = (TextView) titleView.findViewById(R.id.cancel_all_txv);
        downloadStatus = (ListView) contentView.findViewById(R.id.downloadStatus);
        message = (TextView) contentView.findViewById(R.id.message);
        downloadStatus.setAdapter(adapter);

        builder.setCustomTitle(titleView);
        builder.setView(contentView);
        builder.setCancelable(false);

        //创建、并显示对话框
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismissDownloadDialog(){
        if (alertDialog != null){
            alertDialog.dismiss();
        }
    }


    public void setMessage(String myMessage) {
        message.setText(myMessage);
    }


    public void setBackBtnOnClickListener(View.OnClickListener onClickListener){
        if(onClickListener != null){
            cancelAllTxv.setOnClickListener(onClickListener);
        }
    }

    public void setAdapter(DownloadManagerAdapter adapter){
        downloadStatus.setAdapter(adapter);
    }

}
