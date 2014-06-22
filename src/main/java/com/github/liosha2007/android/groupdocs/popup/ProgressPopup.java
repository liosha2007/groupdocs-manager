package com.github.liosha2007.android.groupdocs.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.github.liosha2007.android.R;

/**
 * Created by liosha on 10.12.13.
 */
public class ProgressPopup {
    protected static AlertDialog progressDialog = null;

    public static void show(Activity activity) {
        if (progressDialog != null) {
            hide();
            progressDialog = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_progress, null));

        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();
    }

    public static void hide() {
        if (progressDialog != null) {
            progressDialog.cancel();
//            progressDialog.hide();
        }
    }
}
