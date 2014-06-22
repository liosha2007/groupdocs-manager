package com.github.liosha2007.android.groupdocs.view;

import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.groupdocs.controller.AuthController;
import com.github.liosha2007.android.groupdocs.popup.ProgressPopup;
import com.github.liosha2007.android.library.activity.view.BaseActivityView;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

/**
 * @author liosha on 19.06.2014.
 */
public class AuthView extends BaseActivityView<AuthController> {
    public AuthView() {
        super(R.layout.layout_auth);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        (new Thread() {
            public void run() {
                Looper.prepare();
                AuthView.this.<AdView>view(R.id.adView).loadAd(new AdRequest());
            }
        }).start();


        view(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onOkClicked(
                        AuthView.this.<EditText>view(R.id.cidLogin).getText().toString(),
                        AuthView.this.<EditText>view(R.id.pkeyPassword).getText().toString(),
                        AuthView.this.<EditText>view(R.id.basePath).getText().toString()
                );
            }
        });
        view(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onCancelClicked();
            }
        });
        view(R.id.howto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.onHowToClicked();
            }
        });
    }

    public void setDefaultData(String cid, String pkey, String defaultBasePath) {
        this.<EditText>view(R.id.cidLogin).setText(cid);
        this.<EditText>view(R.id.pkeyPassword).setText(pkey);
        this.<EditText>view(R.id.basePath).setText(defaultBasePath);
    }

    public void showCredentialsError(String message) {
        ProgressPopup.hide();
        TextView errorMessage = view(R.id.errorMessage);
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

    public void showCredentialsSaved() {
        ProgressPopup.hide();
        this.<TextView>view(R.id.errorMessage).setVisibility(View.INVISIBLE);
        Toast.makeText(controller, "Credentials saved!", Toast.LENGTH_LONG).show();
    }
}
