package com.icatch.mobilecam.ui.appdialog;

import android.content.Context;
import android.widget.Toast;

public class AppToast {
    private static  Toast toast;

    public static void show(Context context, CharSequence text, int duration){
        if(toast == null){
            toast = Toast.makeText(context, "默认的Toast", Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.setDuration(duration);
        toast.show();
    }

    public static void show(Context context, int stringId, int duration){
        if(toast == null){
            toast = Toast.makeText(context, "默认的Toast", Toast.LENGTH_SHORT);
        }
        toast.setText(stringId);
        toast.setDuration(duration);
        toast.show();
    }

}
