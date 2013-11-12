package com.github.liosha2007.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.common.Handler;
import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.android.controller.MainController;
import com.github.liosha2007.groupdocs.model.ListEntitiesResult;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;


@EActivity(R.layout.layout_main)
public class MainActivity extends Activity {
    @ViewById
    protected ListView filesListView;
    @ViewById
    protected Button viewBtn;

//    // Saved object
//    @InstanceState
//    protected SomeObject someObject;

//    @StringRes(R.string.hello_world)
//    String myHelloString;

    protected final MainController controller = new MainController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        controller.onActivityResult(requestCode, resultCode, data);
    }

    @Background
    public void listGroupDocsDirectory(String path) {
        try {
            controller.listRemoteFileSystem_Thread(path);
        } catch (final Exception e) {
            onListRemoteFileSystem_Error(e);
        }
    }

    @UiThread
    protected void onListRemoteFileSystem_Error(final Exception e) {
        Handler.sendMessage(new Message(), new Handler.ICallback() {
            @Override
            public void callback(Object obj) {
                Log.e(Utils.TAG, e.getMessage());
                controller.onListRemoteFileSystem_Error(e);
            }
        });
    }

    @UiThread
    public void onListRemoteFileSystem_Callback(final ListEntitiesResult listEntitiesResult) {
        Handler.sendMessage(new Message(), new Handler.ICallback() {
            @Override
            public void callback(Object obj) {
                controller.onListRemoteFileSystem_Callback(listEntitiesResult);
            }
        });
    }

    public void updateListViewAdapter(SimpleAdapter simpleAdapter) {
        filesListView.setAdapter(simpleAdapter);

        filesListView.setItemsCanFocus(false);
        filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof LinearLayout) {
                    view.setSelected(true);
                    controller.onListViewItemClicked((LinearLayout)view, position);
                }
            }
        });
    }

    @Click(R.id.reloadBtn)
    protected void onRefreshButtonClicked() {
        controller.onRefreshButtonClicked();
    }

    @Click(R.id.viewBtn)
    protected void onShowButtonClicked() {
        controller.onShowButtonClicked();
    }
}
