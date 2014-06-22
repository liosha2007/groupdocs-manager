package com.github.liosha2007.android.groupdocs.controller;

import android.content.SharedPreferences;
import android.widget.Toast;

import com.github.liosha2007.android.groupdocs.application.GMApplication;
import com.github.liosha2007.android.groupdocs.view.SettingsView;

/**
 * @author liosha on 19.06.2014.
 */
public class SettingsController extends AbstractController<SettingsView> {
    protected String cid;
    protected String pkey;
    protected String basePath;
    protected boolean aload;

    public SettingsController() {
        super(new SettingsView());
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = GMApplication.getInstance().getSharedPreferences();
        cid = sharedPreferences.getString(CID_KEY, null);
        pkey = sharedPreferences.getString(PKEY_KEY, null);
        basePath = sharedPreferences.getString(BPATH_KEY, null);
        aload = sharedPreferences.getBoolean(ALOAD_KEY, true);

        view.updateSettings(cid, pkey, basePath, aload);
    }

    public void onSaveClicked(String cid, String pkey, String basePath, boolean aload) {
        SharedPreferences sharedPreferences = GMApplication.getInstance().getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CID_KEY, cid);
        editor.putString(PKEY_KEY, pkey);
        editor.putString(BPATH_KEY, basePath);
        editor.putBoolean(ALOAD_KEY, aload);
        editor.commit();

        Toast.makeText(this, "Credentials saved!", Toast.LENGTH_LONG).show();
        finish();
    }
}
