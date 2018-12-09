package com.android.agzakhanty.general.application;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.agzakhanty.R;

/**
 * Created by Aly on 05/05/2018.
 */

public class DialogCreator {
    private static ProgressDialog dialog;

    private DialogCreator(){

    }

    public static ProgressDialog getInstance(Context ctx){
        dialog = new ProgressDialog(ctx, R.style.DialogTheme);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

}
