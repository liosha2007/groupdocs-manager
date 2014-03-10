package com.github.liosha2007.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.controller.DashboardController;
import com.google.ads.AdRequest;
import com.google.ads.AdView;


public class DashboardFragment extends BaseFragment {

    protected ListView filesListView;
    protected TextView currentLocation;

    public final DashboardController controller = new DashboardController(this);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState, R.layout.layout_dashboard);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filesListView = view(R.id.filesListView);
        currentLocation = view(R.id.currentLocation);

        // Refresh
        view(R.id.reloadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onRefreshButtonClicked();
            }
        });
        // Go UP
        view(R.id.goUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onGoUpButtonClicked();
            }
        });
        // Upload
        view(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onUploadButtonClicked();
            }
        });
        // Create directory
        view(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onCreateButtonClicked();
            }
        });
        controller.onViewCreated(savedInstanceState);

        AdView adView = (AdView)view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest();
        adView.loadAd(adRequest);
    }

    public void updateListViewAdapter(SimpleAdapter simpleAdapter) {
        filesListView.setAdapter(simpleAdapter);

        filesListView.setItemsCanFocus(false);
        filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View rootFragment, int position, long id) {
                if (rootFragment instanceof LinearLayout) {
                    rootFragment.setSelected(true);
                    controller.onListViewItemClicked((LinearLayout) rootFragment, position);
                }
            }
        });
    }

    public void updateCurrentDirectory(String currentDirectory) {
        currentLocation.setText("/" + currentDirectory);
    }
}
