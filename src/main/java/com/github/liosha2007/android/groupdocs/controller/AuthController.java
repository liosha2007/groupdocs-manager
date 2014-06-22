package com.github.liosha2007.android.groupdocs.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.github.liosha2007.android.groupdocs.application.GMApplication;
import com.github.liosha2007.android.groupdocs.common.Utils;
import com.github.liosha2007.android.groupdocs.popup.ProgressPopup;
import com.github.liosha2007.android.groupdocs.view.AuthView;

import static com.github.liosha2007.android.groupdocs.common.Utils.isNullOrBlank;

/**
 * @author liosha on 19.06.2014.
 */
public class AuthController extends AbstractController<AuthView> {
    public AuthController() {
        super(new AuthView());
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ProgressPopup.show(this);
        SharedPreferences sharedPreferences = GMApplication.getInstance().getSharedPreferences();
        String cid = sharedPreferences.getString(CID_KEY, null);
        String pkey = sharedPreferences.getString(PKEY_KEY, null);
        String basePath = sharedPreferences.getString(BPATH_KEY, null);
        view.setDefaultData(cid, pkey, com.github.liosha2007.android.library.common.Utils.or(basePath, DEFAULT_BASE_PATH));
        if (!isNullOrBlank(cid) && !isNullOrBlank(pkey) && !isNullOrBlank(basePath)) {
            checkCredentials(cid, pkey, basePath, new ICredentialsCheck() {
                @Override
                public void checkSuccessed(String cid, String pkey, String bpath) {
                    ProgressPopup.hide();

                    run(DashboardController.class);
                }

                @Override
                public void checkFailed(String message) {
                    ProgressPopup.hide();
                    view.showCredentialsError("Credentials checking is failed!");
                }
            });
        } else {
            ProgressPopup.hide();
        }
    }

    public void onOkClicked(String cidLogin, String pkeyPassword, String basePath) {
        ProgressPopup.show(this);
        normalizeCredentials(cidLogin, pkeyPassword, basePath, new ICredentialsNormalized() {
            @Override
            public void onCredentialsNormalized(String cid, String pkey, String basePath) {
                checkCredentials(cid, pkey, basePath, new ICredentialsCheck() {
                    @Override
                    public void checkSuccessed(String cid, String pkey, String basePath) {
                        onCredentialsCorrect(cid, pkey, basePath);
                    }

                    @Override
                    public void checkFailed(String message) {
                        view.showCredentialsError(message);
                    }
                });
            }

            @Override
            public void onCredentialsError(String message) {
                view.showCredentialsError(message);
            }
        });
    }

    private void onCredentialsCorrect(String cid, String pkey, String basePath) {
        if (savePreferences(cid, pkey, basePath)) {
            view.showCredentialsSaved();
        }
        run(DashboardController.class);
    }

    public void onCancelClicked() {
        finish();
    }

    public void onHowToClicked() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://groupdocs.com/docs/display/documentation/How+to+Get+Your+GroupDocs+API+Keys")));
    }
}
