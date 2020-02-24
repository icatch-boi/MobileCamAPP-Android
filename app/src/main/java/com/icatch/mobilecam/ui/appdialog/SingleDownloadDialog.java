package com.icatch.mobilecam.ui.appdialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.icatch.mobilecam.data.entity.DownloadInfo;
import com.icatch.mobilecam.ui.ExtendComponent.NumberProgressBar;
import com.icatch.mobilecam.R;
import com.icatchtek.reliant.customer.type.ICatchFile;

import java.text.DecimalFormat;

public class SingleDownloadDialog {
    private ImageButton exitBtn;
    TextView fileNameTxv;
    TextView fileDownloadStatus;
    NumberProgressBar numberProgressBar;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    Context context;
    ICatchFile curVideoFile;

    public SingleDownloadDialog(Context context,ICatchFile iCatchFile){
        this.context = context;
        this.curVideoFile = iCatchFile;
        builder = new AlertDialog.Builder(context);
        View contentView = View.inflate(context, R.layout.single_download_content_dialog, null);
        View titleView = View.inflate(context, R.layout.download_dialog_title, null);
        exitBtn = (ImageButton) titleView.findViewById(R.id.exit);
        fileNameTxv = (TextView) contentView.findViewById(R.id.fileName);
        fileDownloadStatus = (TextView) contentView.findViewById(R.id.downloadStatus);
        numberProgressBar = (NumberProgressBar) contentView.findViewById(R.id.numberbar);
        fileNameTxv.setText(curVideoFile.getFileName());
        builder.setCustomTitle(titleView);
        builder.setView(contentView);
        builder.setCancelable(false);
        alertDialog = builder.create();
    }

    public void showDownloadDialog() {
        if(alertDialog != null){
            alertDialog.show();
        }
    }

    public void dismissDownloadDialog(){
        if (alertDialog != null){
            alertDialog.dismiss();
        }
    }

    public void setBackBtnOnClickListener(View.OnClickListener onClickListener){
        if(onClickListener != null){
            exitBtn.setOnClickListener(onClickListener);
        }
    }

    public void updateDownloadStatus(DownloadInfo downloadInfo){
        numberProgressBar.setProgress(downloadInfo.progress);
        DecimalFormat df = new DecimalFormat("#.#");
        String curFileLength = df.format(downloadInfo.curFileLength / 1024.0 / 1024) + "M";
        String fileSize = df.format(downloadInfo.fileSize / 1024.0 / 1024) + "M";
        fileDownloadStatus.setText(curFileLength + "/" + fileSize);
    }
}
