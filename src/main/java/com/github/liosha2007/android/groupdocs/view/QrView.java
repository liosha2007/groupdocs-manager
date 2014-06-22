package com.github.liosha2007.android.groupdocs.view;

import android.graphics.Bitmap;
import android.os.Looper;
import android.widget.ImageView;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.groupdocs.controller.QrController;
import com.github.liosha2007.android.library.activity.view.BaseActivityView;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

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

        (new Thread() {
            public void run() {
                Looper.prepare();
                QrView.this.<AdView>view(R.id.adView).loadAd(new AdRequest());
            }
        }).start();
    }

    public void showQrImage(Bitmap bitmap) {
        this.<ImageView>view(R.id.qr).setImageBitmap(bitmap);
    }
}
