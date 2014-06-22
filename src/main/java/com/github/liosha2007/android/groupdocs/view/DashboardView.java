package com.github.liosha2007.android.groupdocs.view;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.groupdocs.controller.DashboardController;
import com.github.liosha2007.android.library.activity.view.BaseActivityView;
import com.github.liosha2007.android.library.common.Utils;

/**
 * @author liosha on 19.06.2014.
 */
public class DashboardView extends BaseActivityView<DashboardController> {
    protected ListView filesListView;
    protected TextView currentLocation;

    public DashboardView() {
        super(R.layout.layout_dashboard);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        filesListView = view(R.id.filesListView);
        currentLocation = view(R.id.current_location);

        view(R.id.goUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onUpClicked();
            }
        });
        view(R.id.reloadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onReloadClicked();
            }
        });
        view(R.id.uploadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onUploadClicked();
            }
        });
        view(R.id.createBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onCreateClicked();
            }
        });
        view(R.id.menuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onMenuClicked();
            }
        });
    }

    public void updateListViewAdapter(SimpleAdapter simpleAdapter) {
        filesListView.setAdapter(simpleAdapter);

        filesListView.setItemsCanFocus(false);
        filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View rootView, int position, long id) {
                if (rootView instanceof LinearLayout) {
                    rootView.setSelected(true);
                    controller.onListViewItemClicked(Utils.<TextView>view(rootView, R.id.itemFileName).getTag().toString());
                }
            }
        });
    }

    public void updateCurrentDirectory(String currentDirectory) {
        currentLocation.setText("/" + currentDirectory);
    }

    public void showCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(controller);
        LayoutInflater inflater = controller.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_create_directory, null));
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.show();
        final EditText editText = (EditText) alertDialog.findViewById(R.id.textValue);
        final Button okButton = (Button) alertDialog.findViewById(R.id.okButton);
        final Button cancelButton = (Button) alertDialog.findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.onDialogCreateClicked(editText.getText().toString(), alertDialog);
            }
        });
        //
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }
}
