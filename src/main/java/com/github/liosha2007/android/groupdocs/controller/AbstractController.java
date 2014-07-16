package com.github.liosha2007.android.groupdocs.controller;

/**
 * @author liosha on 19.06.2014.
 */

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.github.liosha2007.android.groupdocs.application.GMApplication;
import com.github.liosha2007.android.groupdocs.common.Handler;
import com.github.liosha2007.android.groupdocs.common.Utils;
import com.github.liosha2007.android.library.activity.controller.BaseActivityController;
import com.github.liosha2007.android.library.activity.view.BaseActivityView;
import com.github.liosha2007.groupdocs.api.StorageApi;
import com.github.liosha2007.groupdocs.api.UserApi;
import com.github.liosha2007.groupdocs.common.ApiClient;
import com.github.liosha2007.groupdocs.model.storage.ListEntitiesResult;
import com.github.liosha2007.groupdocs.model.user.UserInfoResult;

public abstract class AbstractController<T extends BaseActivityView> extends BaseActivityController<T> {
    public static final String CID_KEY = "groupdocs-cid";
    public static final String PKEY_KEY = "groupdocs-pkey";
    public static final String BPATH_KEY = "groupdocs-bpath";
    public static final String ALOAD_KEY = "groupdocs-aload";
    public static final String DEFAULT_BASE_PATH = "https://api.groupdocs.com/v2.0";

    public AbstractController(T view) {
        super(view);
    }

    public interface ICredentialsNormalized {
        void onCredentialsNormalized(String cid, String pkey, String bpath);

        void onCredentialsError(String message);
    }

    public void normalizeCredentials(final String loginCid, final String passwordPkey, final String bpath, final ICredentialsNormalized credentialsNormalized) {
        if (loginCid.contains("@")) {
            new AsyncTask<String, Void, UserInfoResult>() {
                protected boolean isErrorShowed = false;

                @Override
                protected UserInfoResult doInBackground(String... params) {
                    isErrorShowed = false;
                    try {
                        ApiClient apiClient = new ApiClient("123", "123", bpath);
                        UserApi userApi = new UserApi(apiClient);
                        return Utils.assertResponse(userApi.loginUser(params[0], params[1])).getResult();
                    } catch (final Exception e) {
                        isErrorShowed = true;
                        Handler.sendMessage(new Handler.ICallback() {
                            @Override
                            public void callback(Object obj) {
                                credentialsNormalized.onCredentialsError(e.getMessage());
                            }
                        });
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(final UserInfoResult userInfoResult) {
                    if (userInfoResult != null) {
                        credentialsNormalized.onCredentialsNormalized(userInfoResult.getUser().getGuid(), userInfoResult.getUser().getPkey(), bpath);
                    } else if (!isErrorShowed) {
                        credentialsNormalized.onCredentialsError("Unknown error!");
                    }
                }
            }.execute(loginCid, passwordPkey);
        } else {
            if (Utils.isNullOrBlank(loginCid) || Utils.isNullOrBlank(passwordPkey)) {
                credentialsNormalized.onCredentialsError("Please enter Client ID and Private KEY!");
            } else {
                credentialsNormalized.onCredentialsNormalized(loginCid, passwordPkey, bpath);
            }
        }
    }

    public static interface ICredentialsCheck {
        public void checkSuccessed(String cid, String pkey, String bpath);

        public void checkFailed(String message);
    }

    public void checkCredentials(final String cid, final String pkey, final String bpath, final ICredentialsCheck credentialsCheck) {
        new AsyncTask<String, Void, ListEntitiesResult>() {

            @Override
            protected ListEntitiesResult doInBackground(String... params) {
                String cid = params[0];
                String pkey = params[1];
                String bpath = params[2];
                ApiClient apiClient = new ApiClient(pkey, cid, bpath);
                try {
                    return new StorageApi(apiClient).listEntities("").getResult();
                } catch (final Exception e) {
                    Handler.sendMessage(new Handler.ICallback() {
                        @Override
                        public void callback(Object obj) {
                            credentialsCheck.checkFailed(e.getMessage());
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onPostExecute(ListEntitiesResult listEntitiesResult) {
                if (listEntitiesResult == null) {
                    if (!Utils.haveInternet(AbstractController.this)) {
                        credentialsCheck.checkFailed("The Internet is inaccessible!");
                    } else {
                        credentialsCheck.checkFailed("Unknown error!");
                    }
                } else {
                    credentialsCheck.checkSuccessed(cid, pkey, bpath);
                }
            }
        }.execute(cid, pkey, bpath);
    }

    public boolean savePreferences(String cid, String pkey, String basePath) {
        SharedPreferences sharedPreferences = GMApplication.getInstance().getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CID_KEY, cid);
        editor.putString(PKEY_KEY, pkey);
        editor.putString(BPATH_KEY, basePath);
        editor.putBoolean(ALOAD_KEY, true);
        editor.commit();
        return true;
    }
}
