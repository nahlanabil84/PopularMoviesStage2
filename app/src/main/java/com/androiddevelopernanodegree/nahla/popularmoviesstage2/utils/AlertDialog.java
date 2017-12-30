package com.androiddevelopernanodegree.nahla.popularmoviesstage2.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.androiddevelopernanodegree.nahla.popularmoviesstage2.R;


public class AlertDialog {
    private static volatile AlertDialog alertDialog = new AlertDialog();
    Context context;
    ProgressDialog dialog;

    //private constructor.
    private AlertDialog() {
    }

    public static AlertDialog getInstance() {
        return alertDialog;
    }

    public void setAlertDialog(Context context) {
        dialog = new ProgressDialog(context);
        String pleaseWait = context.getString(R.string.Dialog_please_wait);
        dialog.setMessage(pleaseWait);
        dialog.show();
        dialog.setCancelable(false);
    }

    public void disableAlertDialog() {
        dialog.dismiss();
    }
}
