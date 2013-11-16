package com.github.liosha2007.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.controller.SettingsController;

public class SettingsFragment extends BaseFragment {
    protected final SettingsController controller = new SettingsController(this);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState, R.layout.layout_settings);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize listeners
    }
}
