package com.github.liosha2007.android.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.android.fragment.SettingsFragment;
import com.github.liosha2007.android.popup.MessagePopup;

/**
 * Created by liosha on 16.11.13.
 */
public class SettingsController extends BaseController<SettingsFragment> {
    public SettingsController(SettingsFragment context) {
        super(context);
    }

    public void onViewCreated(Bundle savedInstanceState) {
    }

    public void onFragmentFocused() {
        SharedPreferences sharedPreferences = rootFragment.getActivity().getPreferences(Context.MODE_PRIVATE);
        String cid = sharedPreferences.getString(CID_KEY, "");
        String pkey = sharedPreferences.getString(PKEY_KEY, "");
        String bpath = sharedPreferences.getString(BPATH_KEY, "");
        boolean aload = sharedPreferences.getBoolean(ALOAD_KEY, true);

        rootFragment.showSavedCredentials(cid, pkey, bpath, aload);
    }

    public void onSaveClicked(String cid, String pkey, final String bpath, final boolean aload) {
        final Activity activity = rootFragment.getActivity();
        Utils.normalizeCredentials(activity, cid, pkey, bpath, new Utils.ICredentialsNormalized() {
            @Override
            public void onCredentialsNormalized(String cid, String pkey) {
                SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(BaseController.CID_KEY, cid);
                editor.putString(BaseController.PKEY_KEY, pkey);
                editor.putString(BaseController.BPATH_KEY, Utils.isNullOrBlank(bpath) ? "https://api.groupdocs.com/v2.0" : bpath);
                editor.putBoolean(BaseController.ALOAD_KEY, aload);
                editor.commit();

                MessagePopup.successMessage("Restart application for apply changes!", 2500);
            }
        });
    }
}