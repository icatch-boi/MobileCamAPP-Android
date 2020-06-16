package com.icatch.mobilecam.ui.ExtendComponent;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.icatch.mobilecam.R;


/**
 * Added by zhangyanhu C01012,2014-7-17
 */
public class DownloadDialog extends AlertDialog {
    /**
     * Added by zhangyanhu C01012,2014-7-17
     */
    private TextView message;
    private ListView downloadStatus;
    private TextView exit;
    private Context context;

    public DownloadDialog(Context context) {
        super(context);
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.download_dialog);
        View contentView = View.inflate(context, R.layout.download_content_dialog, null);
        View titleView = View.inflate(context, R.layout.download_dialog_title, null);
        setCustomTitle(titleView);
        setView(contentView);


        exit = (TextView) titleView.findViewById(R.id.exit);

        downloadStatus = (ListView) contentView.findViewById(R.id.downloadStatus);
        message = (TextView) contentView.findViewById(R.id.message);





    }

    public void setMessage(String myMessage) {
        message.setText(myMessage);
    }

    public void setAdapter(ListAdapter adapter) {
        downloadStatus.setAdapter(adapter);
    }

    public TextView getDrawBackButton() {
        return exit;
    }
}

