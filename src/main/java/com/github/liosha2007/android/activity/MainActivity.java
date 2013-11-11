package com.github.liosha2007.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.adapter.MainDataAdapter;
import com.github.liosha2007.android.binder.MainViewBinder;
import com.github.liosha2007.android.common.Handler;
import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.groupdocs.api.StorageApi;
import com.github.liosha2007.groupdocs.common.ApiClient;
import com.github.liosha2007.groupdocs.model.FileSystemDocument;
import com.github.liosha2007.groupdocs.model.FileSystemFolder;
import com.github.liosha2007.groupdocs.model.ListEntitiesResponse;
import com.github.liosha2007.groupdocs.model.ListEntitiesResult;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@EActivity(R.layout.layout_main)
public class MainActivity extends Activity {

    public static final String CID_KEY = "groupdocs-cid";
    public static final String PKEY_KEY = "groupdocs-pkey";

    private static final String ATTRIBUTE_FILENAME_KEY = "filename";
    private static final String ATTRIBUTE_FILESIZE_KEY = "filesize";
    private static final String ATTRIBUTE_FILEIMAGE_KEY = "fileimage";

    @ViewById
    protected ListView filesListView;
    @ViewById
    protected Button viewBtn;

//    // Saved object
//    @InstanceState
//    protected SomeObject someObject;

//    @StringRes(R.string.hello_world)
//    String myHelloString;

    protected static final int CODE_AUTH = 1;
    protected static final String TAG = "groupdocs-android";
    protected String cid = null;
    protected String pkey = null;
    protected StorageApi storageApi;
    protected HashMap<String, FileSystemFolder> remoteFolderMap = new HashMap<String, FileSystemFolder>();
    protected HashMap<String, FileSystemDocument> remoteDocumentMap = new HashMap<String, FileSystemDocument>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeApplication();
    }

    private void initializeApplication() {
        try {
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            String cid = sharedPreferences.getString(CID_KEY, null);
            String pkey = sharedPreferences.getString(PKEY_KEY, null);
            if (cid == null || pkey == null) {
                startActivityForResult(new Intent(this, AuthActivity_.class), CODE_AUTH);
            } else {
                storeCredentials(cid, pkey, false);
            }
        } catch (Exception e){
            Toast.makeText(this, "Error: '" + e.getMessage() + "'", 3000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_AUTH && resultCode == RESULT_OK) {
            try {
                String cid = data.getStringExtra(CID_KEY);
                String pkey = data.getStringExtra(PKEY_KEY);
                if (cid == null || pkey == null) {
                    throw new Exception("Unknown error!");
                } else {
                    storeCredentials(cid, pkey, true);
                }
            } catch (Exception e){
                Toast.makeText(this, "Error: '" + e.getMessage() + "'", 3000);
            }
        }
    }

    private void storeCredentials(String cid, String pkey, boolean isSave) throws Exception {
        this.cid = cid;
        this.pkey = pkey;

        if (isSave) {
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CID_KEY, cid);
            editor.putString(PKEY_KEY, pkey);
            editor.commit();
        }
        onCredentialsStored();
    }

    protected void onCredentialsStored() throws Exception {
        checkInternetAvailable();
        initializeGroupDocs();
    }

    private void checkInternetAvailable() {
        if (!Utils.haveInternet(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("Internet is unavailable!")
                    .setMessage("Please, enable internet!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        }
    }

    private void initializeGroupDocs() throws Exception {
        if (this.cid == null || this.pkey == null) {
            // TODO:
            throw new Exception("Client ID or Private KEY is null");
        }
        ApiClient apiClient = new ApiClient(pkey);
        apiClient.setCid(cid);
        storageApi = new StorageApi(apiClient);
    }

    @Background
    protected void listGroupDocsDirectory(String path) {
        try {
            ListEntitiesResponse listEntitiesResponse = storageApi.ListEntities(path);
            listEntitiesResponse = Utils.assertResponse(listEntitiesResponse);
            listGroupDocsDirectoryCallback(listEntitiesResponse.getResult());
        } catch (Exception e) {
            listGroupDocsDirectoryErrorCallback(e);
        }
    }

    @UiThread
    protected void listGroupDocsDirectoryCallback(ListEntitiesResult listEntitiesResult) {
        final ArrayList<Map<String, Object>> filesListData = new ArrayList<Map<String, Object>>();
        // Add directories
        remoteFolderMap.clear();
        remoteDocumentMap.clear();
        for (FileSystemFolder remoteFolder : listEntitiesResult.getFolders()) {
            Map<String, Object> listItemData = new HashMap<String, Object>(3);
            listItemData.put(ATTRIBUTE_FILENAME_KEY, remoteFolder);
            listItemData.put(ATTRIBUTE_FILESIZE_KEY, "");
            if (remoteFolder.getFolder_count() == 0 && remoteFolder.getFile_count() == 0) {
                listItemData.put(ATTRIBUTE_FILEIMAGE_KEY, R.drawable.folder_empty);
            } else {
                listItemData.put(ATTRIBUTE_FILEIMAGE_KEY, R.drawable.folder_fill);
            }
            filesListData.add(listItemData);

            remoteFolderMap.put(remoteFolder.getName(), remoteFolder);
        }
        // Add files
        for (FileSystemDocument remoteDocument : listEntitiesResult.getFiles()) {
            Map<String, Object> listItemData = new HashMap<String, Object>(3);
            listItemData.put(ATTRIBUTE_FILENAME_KEY, remoteDocument);
            listItemData.put(ATTRIBUTE_FILESIZE_KEY, remoteDocument.getSize());
            listItemData.put(ATTRIBUTE_FILEIMAGE_KEY, R.drawable.some_file);
            filesListData.add(listItemData);

            remoteDocumentMap.put(remoteDocument.getGuid(), remoteDocument);
        }
        Handler.sendMessage(new Message(), new Handler.ICallback() {
            @Override
            public void callback(Object obj) {
                String[] listFrom = new String[]{ ATTRIBUTE_FILENAME_KEY, ATTRIBUTE_FILESIZE_KEY, ATTRIBUTE_FILEIMAGE_KEY };
                int[] listTo = new int[]{ R.id.itemFileName, R.id.itemFileSize, R.id.itemFileImage };
                SimpleAdapter.ViewBinder viewBinder = new MainViewBinder();
                SimpleAdapter simpleAdapter = new MainDataAdapter(MainActivity.this, filesListData, R.layout.layout_main_item, listFrom, listTo);
                simpleAdapter.setViewBinder(viewBinder);
                filesListView.setAdapter(simpleAdapter);

                filesListView.setItemsCanFocus(false);
                filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        view.setSelected(true);
                    }
                });
            }
        });
    }

    @UiThread
    protected void listGroupDocsDirectoryErrorCallback(final Exception e) {
        Handler.sendMessage(new Message(), new Handler.ICallback() {
            @Override
            public void callback(Object obj) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void reloadClicked(View view) {
        checkInternetAvailable();
        listGroupDocsDirectory("");
    }

    public void showClicked(View view) {
        Log.e(TAG, "test: " + filesListView.getSelectedItem());
    }


//    @Click(R.id.testactivity_first_button)
//    void myButtonWasClicked() {
//        secondTextView.setText("first button was clicked");
//    }
}
