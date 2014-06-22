package com.github.liosha2007.android.groupdocs.view;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.groupdocs.controller.QrController;
import com.github.liosha2007.android.library.activity.view.BaseActivityView;

/**
 * @author liosha on 22.06.2014.
 */
public class QrView extends BaseActivityView<QrController> {
    public QrView() {
        super(R.layout.layout_qr);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void showQrImage(Bitmap bitmap) {
        this.<ImageView>view(R.id.qr).setImageBitmap(bitmap);
    }
}
