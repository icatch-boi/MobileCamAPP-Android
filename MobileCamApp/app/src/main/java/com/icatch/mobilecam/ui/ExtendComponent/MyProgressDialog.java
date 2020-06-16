package com.icatch.mobilecam.ui.ExtendComponent;

import android.app.ProgressDialog;
import android.content.Context;

import com.icatch.mobilecam.R;
import com.icatch.mobilecam.ui.appdialog.CustomProgressDialog;


/**
 * Created by zhang yanhu C001012 on 2015/11/24 12:15.
 */

public class MyProgressDialog {
    private static ProgressDialog mDialog = null;

    public static void showProgressDialog(Context context, String text) {
        closeProgressDialog();
        //if(hasInit == false) {
        mDialog = new CustomProgressDialog(context, R.style.CustomDialog, text);
        mDialog.show();
    }

    public static void showProgressDialog(Context context, int stringID) {
        closeProgressDialog();
        mDialog = new CustomProgressDialog(context, R.style.CustomDialog, context.getString(stringID));
        mDialog.show();
    }

    public static void closeProgressDialog() {
        if (mDialog != null) {
            try {
                mDialog.dismiss();
            } catch (Exception e) {

            } finally {
                mDialog = null;
            }
        }
    }
}