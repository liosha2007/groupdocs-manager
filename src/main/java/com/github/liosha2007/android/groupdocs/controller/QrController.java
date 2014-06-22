package com.github.liosha2007.android.groupdocs.controller;

import android.os.Bundle;
import android.widget.Toast;

import com.github.liosha2007.android.groupdocs.common.Utils;
import com.github.liosha2007.android.groupdocs.view.QrView;

/**
 * @author liosha on 22.06.2014.
 */
public class QrController extends AbstractController<QrView> {
    public static final String VIEWER_URL = "viewer-url";

    public QrController() {
        super(new QrView());
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        Bundle bundle = getIntent().getExtras();
        String viewerUrl = bundle.getString(VIEWER_URL);
        try {
            view.showQrImage(Utils.createQRImage(viewerUrl, 250));
        } catch (Exception e) {
            Toast.makeText(this, "Can't create QR code!", Toast.LENGTH_LONG).show();
        }
    }
}
