package com.example.muhammadfarhanbashir.gharkhana.helpers;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by fbashir on 8/23/2016.
 */

public class MySpinner {
    private static ProgressDialog spinner;

    public MySpinner(Context context)
    {
        spinner = new ProgressDialog(context);
        spinner.setIndeterminate(true);
        spinner.setMessage("Loading...");
        spinner.setCanceledOnTouchOutside(false);
    }

    public ProgressDialog getProgressDialog()
    {
        return spinner;
    }


}
