package com.icatch.mobilecam.ui.appdialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.icatch.mobilecam.data.GlobalApp.ExitApp;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.R;

/**
 * Created by yh.zhang C001012 on 2015/10/15:13:28.
 * Fucntion:
 */
public class AppDialog {
    private final static String tag="AppDialog";
    private static boolean needShown = true;
    private static AlertDialog dialog;

    public void showDialog(String title,String message,boolean cancelable){
        //show a dialog
    }

    public static void showDialogQuit(final Context context, final int messageID){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle(R.string.title_warning).setMessage(messageID);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppLog.i(tag, "ExitApp because of "+context.getResources().getString(messageID));
                ExitApp.getInstance().exit();
            }
        });
        builder.create().show();
    }

    public static void showDialogQuit(final Context context, final String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle(R.string.title_warning).setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppLog.i(tag, "ExitApp because of "+message);
                ExitApp.getInstance().exit();
            }
        });
        builder.create().show();
    }

    public static void showDialogWarn(final Context context, String message){
        if(dialog != null){
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle(R.string.title_warning).setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public static void showDialogWarn(final Context context, int messageID){
        if(dialog != null){
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle(R.string.title_warning).setMessage(messageID);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public static void showConectFailureWarning(final Context context){
        if(needShown == false){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle(R.string.title_warning).setMessage(R.string.dialog_timeout);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExitApp.getInstance().exit();
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.dialog_btn_reconnect, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                needShown = true;
            }
        });
        builder.create().show();
    }

    public static void showLowBatteryWarning(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle(R.string.title_warning).setMessage(R.string.low_battery);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
