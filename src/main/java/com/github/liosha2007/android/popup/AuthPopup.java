package com.github.liosha2007.android.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.common.Handler;
import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.android.controller.BaseController;
import com.github.liosha2007.groupdocs.api.UserApi;
import com.github.liosha2007.groupdocs.common.ApiClient;
import com.github.liosha2007.groupdocs.model.user.UserInfoResult;

/**
 * Created by liosha on 16.11.13.
 */
public class AuthPopup {
    protected Fragment rootFragment = null;
    protected AlertDialog authDialog = null;
    protected ICallback onSaveCallback = null;
    protected ICallback onCancelCallback = null;


    public AuthPopup(Fragment rootFragment) {
        this.rootFragment = rootFragment;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(rootFragment.getActivity());
        LayoutInflater inflater = rootFragment.getLayoutInflater(null);
        builder.setView(inflater.inflate(R.layout.layout_auth, null));
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Activity activity = rootFragment.getActivity();
                EditText cidLogin = (EditText) AuthPopup.this.authDialog.findViewById(R.id.cidLogin);
                EditText pkeyPassword = (EditText) AuthPopup.this.authDialog.findViewById(R.id.pkeyPassword);

                if (cidLogin.getText().toString().contains("@")) {
                    final String login = cidLogin.getText().toString();
                    final String password = pkeyPassword.getText().toString();
                    new AsyncTask<String, Void, UserInfoResult>() {

                        @Override
                        protected UserInfoResult doInBackground(String... params) {
                            try {
                                ApiClient apiClient = new ApiClient("123", "123");
                                UserApi userApi = new UserApi(apiClient);
                                return Utils.assertResponse(userApi.loginUser(params[0], params[1])).getResult();
                            } catch (final Exception e) {
                                Handler.sendMessage(new Handler.ICallback() {
                                    @Override
                                    public void callback(Object obj) {
                                        Utils.err(e.getMessage());
                                        Toast.makeText(rootFragment.getActivity(), "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(final UserInfoResult userInfoResult) {
                            if (userInfoResult != null) {
                                savePreferences(userInfoResult.getUser().getGuid(), userInfoResult.getUser().getPkey());
                            }
                        }
                    }.execute(login, password);
                } else {
                    String cid = cidLogin.getText().toString();
                    String pkey = pkeyPassword.getText().toString();
                    if (Utils.isNullOrBlank(cid) || Utils.isNullOrBlank(pkey)) {
                        Toast.makeText(activity, "Please enter Client ID and Private KEY!", Toast.LENGTH_LONG).show();
                    } else {
                        savePreferences(cid, pkey);
                    }
                }
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                boolean exit = true;
                if (onCancelCallback != null) {
                    exit = onCancelCallback.onCallback(null, null);
                }
                if (exit) {
                    rootFragment.getActivity().finish();
                }
            }
        });
        builder.setCancelable(false);
        authDialog = builder.create();
        authDialog.show();
    }

    private void savePreferences(String cid, String pkey) {
        SharedPreferences sharedPreferences = rootFragment.getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BaseController.CID_KEY, cid);
        editor.putString(BaseController.PKEY_KEY, pkey);
        editor.commit();

        Toast.makeText(rootFragment.getActivity(), "Client ID and Private KEY was saved!", Toast.LENGTH_LONG).show();

        boolean close = true;
        if (onSaveCallback != null) {
            close = onSaveCallback.onCallback(cid, pkey);
        }
        if (close) {
            AuthPopup.this.authDialog.cancel();
        }
    }

    public ICallback getOnCancelCallback() {
        return onCancelCallback;
    }

    public AuthPopup setOnCancelCallback(ICallback onCancelCallback) {
        this.onCancelCallback = onCancelCallback;
        return this;
    }

    public ICallback getOnSaveCallback() {
        return onSaveCallback;
    }

    public AuthPopup setOnSaveCallback(ICallback onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
        return this;
    }

    public interface ICallback {
        boolean onCallback(String cid, String pkey);
    }
}
