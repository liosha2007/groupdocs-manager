package com.github.liosha2007.android.groupdocs.view;

import android.app.AlertDialog;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.groupdocs.common.Utils;
import com.github.liosha2007.android.groupdocs.controller.ActionController;
import com.github.liosha2007.android.library.activity.view.BaseActivityView;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author liosha on 19.06.2014.
 */
public class ActionView extends BaseActivityView<ActionController> {
    public ActionView() {
        super(R.layout.layout_action);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        (new Thread() {
            public void run() {
                Looper.prepare();
                ActionView.this.<AdView>view(R.id.adView).loadAd(new AdRequest());
            }
        }).start();

        view(R.id.viewfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.onViewClicked();
            }
        });
        view(R.id.downloadfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.onDownloadClicked();
            }
        });
        view(R.id.qrfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.onQrClicked();
            }
        });
        view(R.id.deletefile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.onDeleteClicked();
            }
        });
        view(R.id.filename).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                controller.onFileNameTouched();
                return true;
            }
        });
        view(R.id.fileguid).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                controller.onFileGuidTouched();
                return true;
            }
        });
    }

    public void showDetails(String documentName, String documentGuid, String documentSize) {
        this.<EditText>view(R.id.filename).setText(documentName);
        this.<EditText>view(R.id.fileguid).setText(documentGuid);

        documentSize = Utils.isNullOrBlank(documentSize) ? "0" : documentSize;
        documentSize = String.valueOf(new BigDecimal(Double.parseDouble(documentSize) / 1024).setScale(documentSize.length() > 9 ? 0 : 3, RoundingMode.UP).doubleValue()) + "kb";
        this.<EditText>view(R.id.filesize).setText(documentSize);
    }

    public void showDeleteDialog(String documentName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(controller);
        LayoutInflater inflater = controller.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_delete_dialog, null));
        builder.setCancelable(true);

        final AlertDialog dialog = builder.show();
        Button okButton = (Button) dialog.findViewById(R.id.deleteDialog_okButton);
        if (okButton != null) {
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    controller.onDialogOkClicked();
                }
            });
        }
        Button cancelButton = (Button) dialog.findViewById(R.id.deleteDialog_cancelButton);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }
    }
}
