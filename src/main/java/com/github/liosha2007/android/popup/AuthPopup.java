package com.github.liosha2007.android.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.android.controller.BaseController;

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
                final Activity activity = rootFragment.getActivity();
                EditText cidLogin = (EditText) AuthPopup.this.authDialog.findViewById(R.id.cidLogin);
                EditText pkeyPassword = (EditText) AuthPopup.this.authDialog.findViewById(R.id.pkeyPassword);
                final String loginCid = cidLogin.getText().toString();
                final String passwordPkey = pkeyPassword.getText().toString();
                final String bpath = "https://api.groupdocs.com/v2.0";

                Utils.normalizeCredentials(activity, loginCid, passwordPkey, bpath, new Utils.ICredentialsNormalized() {
                    @Override
                    public void onCredentialsNormalized(String cid, String pkey) {
                        savePreferences(activity, cid, pkey);
                    }
                });
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

    private void savePreferences(Activity activity, String cid, String pkey) {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BaseController.CID_KEY, cid);
        editor.putString(BaseController.PKEY_KEY, pkey);
        editor.putString(BaseController.BPATH_KEY, "https://api.groupdocs.com/v2.0");
        editor.putBoolean(BaseController.ALOAD_KEY, true);
        editor.commit();

        Toast.makeText(activity, "Client ID and Private KEY was saved!", Toast.LENGTH_LONG).show();

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
