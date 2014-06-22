package com.github.liosha2007.android.groupdocs.view;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.groupdocs.controller.SettingsController;
import com.github.liosha2007.android.library.activity.view.BaseActivityView;

/**
 * @author liosha on 19.06.2014.
 */
public class SettingsView extends BaseActivityView<SettingsController> {
    public SettingsView() {
        super(R.layout.layout_settings);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        view(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.onSaveClicked(
                        SettingsView.this.<EditText>view(R.id.cid).getText().toString(),
                        SettingsView.this.<EditText>view(R.id.pkey).getText().toString(),
                        SettingsView.this.<EditText>view(R.id.bpath).getText().toString(),
                        SettingsView.this.<CheckBox>view(R.id.aload).isChecked()
                );
            }
        });
    }

    public void updateSettings(String cid, String pkey, String basePath, boolean aload) {
        this.<EditText>view(R.id.cid).setText(cid);
        this.<EditText>view(R.id.pkey).setText(pkey);
        this.<EditText>view(R.id.bpath).setText(basePath);
        this.<CheckBox>view(R.id.aload).setChecked(aload);
    }
}
