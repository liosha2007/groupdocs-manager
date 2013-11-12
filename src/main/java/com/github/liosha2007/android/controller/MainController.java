package com.github.liosha2007.android.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.activity.AuthActivity_;
import com.github.liosha2007.android.activity.MainActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liosha on 12.11.13.
 */
public class MainController extends BaseController<MainActivity> {
    public static final int CODE_AUTH = Utils.makeID();
    protected static final String ATTRIBUTE_FILENAME_KEY = Integer.toString(Utils.makeID());
    protected static final String ATTRIBUTE_FILESIZE_KEY = Integer.toString(Utils.makeID());
    protected static final String ATTRIBUTE_FILEIMAGE_KEY = Integer.toString(Utils.makeID());
    protected static final int RESULT_OK = Utils.makeID();

    protected static final String VIEWER_CALLBACK = "http://apps.groupdocs.com/document-viewer/embed/{GUID}";

    protected String cid = null;
    protected String pkey = null;
    protected StorageApi storageApi;
    protected HashMap<String, FileSystemFolder> remoteFolderMap = new HashMap<String, FileSystemFolder>();
    protected HashMap<String, FileSystemDocument> remoteDocumentMap = new HashMap<String, FileSystemDocument>();
    protected FileSystemFolder selectedFolder = null;
    protected FileSystemDocument selectedDocument = null;
    protected String currentDirectory = "";

    public MainController(MainActivity activity) {
        super(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initializeApplication();
    }

    protected void initializeApplication() {
        try {
            SharedPreferences sharedPreferences = context.getPreferences(Context.MODE_PRIVATE);
            String cid = sharedPreferences.getString(CID_KEY, null);
            String pkey = sharedPreferences.getString(PKEY_KEY, null);
            if (cid == null || pkey == null) {
                context.startActivityForResult(new Intent(context, AuthActivity_.class), CODE_AUTH);
            } else {
                storeCredentials(cid, pkey, false);
            }
        } catch (Exception e){
            Utils.err(e.getMessage());
            Toast.makeText(context, "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_AUTH && resultCode == RESULT_OK) {
            try {
                String cid = data.getStringExtra(CID_KEY);
                String pkey = data.getStringExtra(PKEY_KEY);
                if (cid == null || pkey == null) {
                    throw new Exception("Error: Client ID or Private KEY is null");
                } else {
                    storeCredentials(cid, pkey, true);
                }
            } catch (Exception e){
                Utils.err(e.getMessage());
                Toast.makeText(context, "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG);
            }
        }
    }

    protected void storeCredentials(String cid, String pkey, boolean isSave) throws Exception {
        this.cid = cid;
        this.pkey = pkey;

        if (isSave) {
            SharedPreferences sharedPreferences = context.getPreferences(Context.MODE_PRIVATE);
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

    protected boolean checkInternetAvailable() {
        if (!Utils.haveInternet(context)) {
            new AlertDialog.Builder(context)
                    .setTitle("Internet is unavailable!")
                    .setMessage("Please, enable internet!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            return false;
        }
        return true;
    }

    protected void initializeGroupDocs() throws Exception {
        ApiClient apiClient = new ApiClient(pkey);
        apiClient.setCid(cid);
        storageApi = new StorageApi(apiClient);
    }

    public void listRemoteFileSystem_Thread(String path) throws Exception {
        final ListEntitiesResponse listEntitiesResponse = Utils.assertResponse(storageApi.ListEntities(path));
        // Call UI thread with data
        context.onListRemoteFileSystem_Callback(listEntitiesResponse.getResult());
    }

    public void onListRemoteFileSystem_Callback(ListEntitiesResult listEntitiesResult) {
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
        //
        bindDataToListView(filesListData);
    }

    private void bindDataToListView(ArrayList<Map<String, Object>> filesListData) {
        String[] listFrom = new String[]{ ATTRIBUTE_FILENAME_KEY, ATTRIBUTE_FILESIZE_KEY, ATTRIBUTE_FILEIMAGE_KEY };
        int[] listTo = new int[]{ R.id.itemFileName, R.id.itemFileSize, R.id.itemFileImage };
        SimpleAdapter.ViewBinder viewBinder = new MainViewBinder();
        SimpleAdapter simpleAdapter = new MainDataAdapter(context, filesListData, R.layout.layout_main_item, listFrom, listTo);
        simpleAdapter.setViewBinder(viewBinder);
        context.updateListViewAdapter(simpleAdapter);
    }

    public void onListRemoteFileSystem_Error(Exception e) {
        if (checkInternetAvailable()) {
            Utils.err(e.getMessage());
            Toast.makeText(context, "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG);
        }
    }

    private void onDocumentClicked(FileSystemDocument fileSystemDocument) {
        selectedDocument = fileSystemDocument;
        selectedFolder = null;
    }

    private void onFolderClicked(FileSystemFolder fileSystemFolder) {
        selectedFolder = fileSystemFolder;
        selectedDocument = null;
    }

    public void onListViewItemClicked(LinearLayout linearLayout, int position) {
        TextView textView = (TextView) linearLayout.findViewById(R.id.itemFileName);
        if (textView == null){
            Utils.err("Error: textView is null!");
            Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG);
        }
        String tag = (String)textView.getTag();
        if (tag != null && remoteFolderMap.containsKey(tag)) {
            onFolderClicked(remoteFolderMap.get(tag));
        } else if (tag != null && remoteDocumentMap.containsKey(tag)) {
            onDocumentClicked(remoteDocumentMap.get(tag));
        } else {
            Utils.deb("tag is not in folders or files map");
        }
    }

    public void onRefreshButtonClicked() {
        if (checkInternetAvailable()) {
            context.listGroupDocsDirectory(currentDirectory);
        }
    }

    public void onShowButtonClicked() {
        if (selectedDocument == null) {
            return;
        }
        String viewer = VIEWER_CALLBACK.replace("{GUID}", selectedDocument.getGuid());
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(viewer)));
    }
}
