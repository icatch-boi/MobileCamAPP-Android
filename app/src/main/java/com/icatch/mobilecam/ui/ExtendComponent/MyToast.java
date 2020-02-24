package com.icatch.mobilecam.ui.ExtendComponent;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.icatch.mobilecam.R;

/**
 * Created by zhang yanhu C001012 on 2015/12/9 09:26.
 */
public class MyToast {
    private static  Toast toast;

    public static void show(Context context,String message)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_toast, null);
        TextView textView = (TextView) view.findViewById(R.id.message_text);
        if(toast != null){
            toast.cancel();
        }
        toast = new Toast(context);
        textView.setText(message);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    public static void show(Context context,int stringId)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_toast, null);
        TextView textView = (TextView) view.findViewById(R.id.message_text);
        if(toast != null){
            toast.cancel();
        }
        toast = new Toast(context);
        textView.setText(stringId);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }
}