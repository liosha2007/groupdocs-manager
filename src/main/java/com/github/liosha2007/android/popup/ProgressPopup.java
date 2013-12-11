package com.github.liosha2007.android.popup;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import com.github.liosha2007.android.R;

/**
 * Created by liosha on 10.12.13.
 */
public class ProgressPopup {
    protected Fragment rootFragment = null;
    protected AlertDialog progressDialog = null;

    public ProgressPopup(Fragment rootFragment) {
        this.rootFragment = rootFragment;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(rootFragment.getActivity());
        LayoutInflater inflater = rootFragment.getLayoutInflater(null);
        builder.setView(inflater.inflate(R.layout.layout_progress, null));

        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();
    }

    public void hide() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }
}
