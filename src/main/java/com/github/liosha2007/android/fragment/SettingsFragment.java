package com.github.liosha2007.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.controller.SettingsController;

public class SettingsFragment extends BaseFragment {
    protected final SettingsController controller = new SettingsController(this);

    protected EditText etCid;
    protected EditText etPkey;
    protected EditText etBpath;
    protected CheckBox chAutoLoad;
    protected Button buSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState, R.layout.layout_settings);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize listeners
        etCid = view(R.id.etCid);
        etPkey = view(R.id.etPkey);
        etBpath = view(R.id.etBpath);
        chAutoLoad = view(R.id.chAutoLoad);
        view(R.id.buSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onSaveClicked(etCid.getText().toString(), etPkey.getText().toString(), etBpath.getText().toString(), chAutoLoad.isChecked());
            }
        });

        controller.onViewCreated(savedInstanceState);
    }

    @Override
    public void onFragmentFocused() {
        controller.onFragmentFocused();
    }

    public void showSavedCredentials(String cid, String pkey, String bpath, boolean aload) {
        etCid.setText(cid);
        etPkey.setText(pkey);
        etBpath.setText(bpath);
        chAutoLoad.setChecked(aload);
    }
}
