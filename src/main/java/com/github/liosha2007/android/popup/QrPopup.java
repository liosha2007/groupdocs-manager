package com.github.liosha2007.android.popup;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.github.liosha2007.android.R;

/**
 * Created by liosha on 10.12.13.
 */
public class QrPopup {
    protected Fragment rootFragment = null;
    protected AlertDialog qrDialog = null;

    public QrPopup(Fragment rootFragment) {
        this.rootFragment = rootFragment;
    }

    public void show(Bitmap qrImage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(rootFragment.getActivity());
        LayoutInflater inflater = rootFragment.getLayoutInflater(null);
        builder.setView(inflater.inflate(R.layout.layout_qr, null));

        builder.setCancelable(true);
        qrDialog = builder.create();
        qrDialog.show();
        ImageView imageView = (ImageView) qrDialog.findViewById(R.id.qrImage);
        if (imageView != null) {
            imageView.setImageBitmap(qrImage);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QrPopup.this.hide();
            }
        });
    }

    public void hide() {
        if (qrDialog != null) {
            qrDialog.cancel();
        }
    }
}
