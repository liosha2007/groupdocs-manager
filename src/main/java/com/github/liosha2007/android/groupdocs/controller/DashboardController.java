package com.github.liosha2007.android.groupdocs.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.groupdocs.adapter.MainDataAdapter;
import com.github.liosha2007.android.groupdocs.application.GMApplication;
import com.github.liosha2007.android.groupdocs.binder.MainViewBinder;
import com.github.liosha2007.android.groupdocs.common.Handler;
import com.github.liosha2007.android.groupdocs.common.Utils;
import com.github.liosha2007.android.groupdocs.popup.FilePopup;
import com.github.liosha2007.android.groupdocs.popup.ProgressPopup;
import com.github.liosha2007.android.groupdocs.view.DashboardView;
import com.github.liosha2007.groupdocs.api.StorageApi;
import com.github.liosha2007.groupdocs.common.ApiClient;
import com.github.liosha2007.groupdocs.model.common.RemoteSystemDocument;
import com.github.liosha2007.groupdocs.model.common.RemoteSystemFolder;
import com.github.liosha2007.groupdocs.model.storage.CreateFolderResponse;
import com.github.liosha2007.groupdocs.model.storage.ListEntitiesResponse;
import com.github.liosha2007.groupdocs.model.storage.ListEntitiesResult;
import com.github.liosha2007.groupdocs.model.storage.UploadFileResponse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liosha on 19.06.2014.
 */
public class DashboardController extends AbstractController<DashboardView> {
    protected static final String ATTRIBUTE_FILENAME_KEY = Integer.toString(Utils.makeID());
    protected static final String ATTRIBUTE_FILESIZE_KEY = Integer.toString(Utils.makeID());
    protected static final String ATTRIBUTE_FILEIMAGE_KEY = Integer.toString(Utils.makeID());

    protected String cid;
    protected String pkey;
    protected String basePath;
    protected boolean aload;

    protected StorageApi storageApi;
    protected HashMap<String, RemoteSystemFolder> remoteFolderMap = new HashMap<String, RemoteSystemFolder>();
    protected HashMap<String, RemoteSystemDocument> remoteDocumentMap = new HashMap<String, RemoteSystemDocument>();
    protected RemoteSystemFolder selectedFolder = null;
    protected RemoteSystemDocument selectedDocument = null;
    protected String currentDirectory = "";

    public DashboardController() {
        super(new DashboardView());
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = GMApplication.getInstance().getSharedPreferences();
        cid = sharedPreferences.getString(CID_KEY, null);
        pkey = sharedPreferences.getString(PKEY_KEY, null);
        basePath = sharedPreferences.getString(BPATH_KEY, null);
        aload = sharedPreferences.getBoolean(ALOAD_KEY, true);

        ApiClient apiClient = new ApiClient(pkey, cid, basePath);
        storageApi = new StorageApi(apiClient);

        if (aload) {
            refreshCurrentDirectory();
        }
    }

    public void onUpClicked() {
        if (Utils.isNullOrBlank(currentDirectory)) {
            return;
        }
        refreshCurrentDirectory();
    }

    protected void refreshCurrentDirectory() {
        try {
            updateCurrentDirectory(false);
            listRemoteFileSystem(currentDirectory);
        } catch (Exception e) {
            if (Utils.haveInternet(this)) {
                Toast.makeText(this, "The Internet is inaccessible!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onReloadClicked() {
        try {
            listRemoteFileSystem(currentDirectory);
        } catch (Exception e) {
            Toast.makeText(this, "Error during loading files: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onUploadClicked() {
        File mPath = new File(Environment.getExternalStorageDirectory() + "//");
        FilePopup filePopup = new FilePopup(this, mPath);
        filePopup.addFileListener(new FilePopup.FileSelectedListener() {
            public void fileSelected(final File file) {
                ProgressPopup.show(DashboardController.this);
                Utils.deb("selected file " + file.toString());
//                progressPopup.show();
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            String path = currentDirectory.startsWith("/") ? currentDirectory.substring(1) : currentDirectory;
                            path += (path.isEmpty() ? file.getName() : "/" + file.getName());
                            UploadFileResponse uploadFileResponse = storageApi.uploadFile(
                                    path,
                                    new BufferedInputStream(new FileInputStream(file))
                            );
                            Utils.assertResponse(uploadFileResponse);
                        } catch (final Exception e) {
                            Handler.sendMessage(new Handler.ICallback() {
                                @Override
                                public void callback(Object obj) {
//                                    progressPopup.hide();
                                    ProgressPopup.hide();
                                    if (Utils.haveInternet(DashboardController.this)) {
                                        Toast.makeText(DashboardController.this, "The Internet is inaccessible!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(DashboardController.this, "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            return e.getMessage();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(final String errorMessage) {
//                        progressPopup.hide();
                        ProgressPopup.hide();
                        if (errorMessage != null) {
                            Utils.err(errorMessage);
                        } else {
                            Toast.makeText(DashboardController.this, "File uploaded successfully!", Toast.LENGTH_LONG).show();
                            try {
                                listRemoteFileSystem(currentDirectory);
                            } catch (Exception e) {
                                if (Utils.haveInternet(DashboardController.this)) {
                                    Toast.makeText(DashboardController.this, "The Internet is inaccessible!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(DashboardController.this, "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }.execute();
            }
        });
        filePopup.setSelectDirectoryOption(false);
        filePopup.showDialog();
    }

    public void onCreateClicked() {
        view.showCreateDialog();
    }

    public void onMenuClicked() {
        run(SettingsController.class);
    }

    public void listRemoteFileSystem(String path) throws Exception {
        if (storageApi == null) {
            return;
        }
        ProgressPopup.show(this);
        new AsyncTask<String, Void, ListEntitiesResponse>() {
            @Override
            protected ListEntitiesResponse doInBackground(String... params) {
                try {
                    return Utils.assertResponse(storageApi.listEntities(params[0]));
                } catch (final Exception e) {
                    Handler.sendMessage(new Handler.ICallback() {
                        @Override
                        public void callback(Object obj) {
                            ProgressPopup.hide();
                            updateCurrentDirectory(false);
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onPostExecute(final ListEntitiesResponse listEntitiesResponse) {
                if (listEntitiesResponse != null) {
                    onListRemoteFileSystem_Callback(listEntitiesResponse.getResult());
                }
                ProgressPopup.hide();
            }
        }.execute(path);
    }

    public void onListRemoteFileSystem_Callback(ListEntitiesResult listEntitiesResult) {
        final ArrayList<Map<String, Object>> filesListData = new ArrayList<Map<String, Object>>();
        // Add directories
        remoteFolderMap.clear();
        remoteDocumentMap.clear();
        for (RemoteSystemFolder remoteFolder : listEntitiesResult.getFolders()) {
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
        for (RemoteSystemDocument remoteDocument : listEntitiesResult.getFiles()) {
            Map<String, Object> listItemData = new HashMap<String, Object>(3);
            listItemData.put(ATTRIBUTE_FILENAME_KEY, remoteDocument);
            String size = Long.toString(remoteDocument.getSize());
            size = (size == null) ? "0" : size;
            size = String.valueOf(new BigDecimal(Double.parseDouble(size) / 1024).setScale(size.length() > 9 ? 0 : 3, RoundingMode.UP).doubleValue()) + "kb";

            listItemData.put(ATTRIBUTE_FILESIZE_KEY, size);
            listItemData.put(ATTRIBUTE_FILEIMAGE_KEY, R.drawable.some_file);
            filesListData.add(listItemData);

            remoteDocumentMap.put(remoteDocument.getGuid(), remoteDocument);
        }
        //
        bindDataToListView(filesListData);
    }

    private void bindDataToListView(ArrayList<Map<String, Object>> filesListData) {
        String[] listFrom = new String[]{ATTRIBUTE_FILENAME_KEY, ATTRIBUTE_FILESIZE_KEY, ATTRIBUTE_FILEIMAGE_KEY};
        int[] listTo = new int[]{R.id.itemFileName, R.id.itemFileSize, R.id.itemFileImage};
        SimpleAdapter.ViewBinder viewBinder = new MainViewBinder();
        SimpleAdapter simpleAdapter = new MainDataAdapter(this, filesListData, R.layout.layout_dashboard_item, listFrom, listTo);
        simpleAdapter.setViewBinder(viewBinder);
        view.updateListViewAdapter(simpleAdapter);
    }

    protected void updateCurrentDirectory(boolean into) {
        if (into && selectedFolder != null) {
            String dirName = (selectedFolder == null) ? "" : selectedFolder.getName().replaceAll("/", "").replaceAll("\\\\", "");
            currentDirectory = currentDirectory + ((currentDirectory.length() > 0) ? "/" : "") + dirName;
        } else if (!into) {
            currentDirectory = (currentDirectory.contains("/") ? currentDirectory.substring(0, currentDirectory.lastIndexOf("/")) : "");
        }
        view.updateCurrentDirectory(currentDirectory);
    }

    public void onListViewItemClicked(String tag) {
        if (tag != null && remoteFolderMap.containsKey(tag)) {
            onFolderClicked(remoteFolderMap.get(tag));
        } else if (tag != null && remoteDocumentMap.containsKey(tag)) {
            onDocumentClicked(remoteDocumentMap.get(tag));
        } else {
            Utils.deb("tag is not in folders or files map");
        }
    }

    private void onDocumentClicked(RemoteSystemDocument remoteSystemDocument) {
        selectedDocument = remoteSystemDocument;
        selectedFolder = null;

        if (remoteSystemDocument != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ActionController.REMOTE_DOCUMENT_KEY, selectedDocument);

            Intent intent = new Intent(this, ActionController.class);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityForResult(intent, ActionController.ACTION_REQUEST);

//            run(ActionController.class, bundle, true, ActionController.ACTION_REQUEST);
        }
    }

    private void onFolderClicked(RemoteSystemFolder remoteSystemFolder) {
        selectedFolder = remoteSystemFolder;
        selectedDocument = null;
        try {
            updateCurrentDirectory(true);
            listRemoteFileSystem(currentDirectory);
        } catch (Exception e) {
            Utils.err(e.getMessage());
            Toast.makeText(this, "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
        }
    }

    public void onDialogCreateClicked(final String directoryName, final AlertDialog alertDialog) {
        if (Utils.isNullOrBlank(directoryName)) {
            return;
        }
        ProgressPopup.show(this);
        Utils.deb("create directory name: " + directoryName);
//        progressPopup.show();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    String path = currentDirectory.startsWith("/") ? currentDirectory.substring(1) : currentDirectory;
                    path += (path.isEmpty() ? directoryName : "/" + directoryName);
                    CreateFolderResponse createFolderResponse = storageApi.createFolder(path);
                    Utils.assertResponse(createFolderResponse);
                } catch (final Exception e) {
                    Handler.sendMessage(new Handler.ICallback() {
                        @Override
                        public void callback(Object obj) {
                            ProgressPopup.hide();
                            if (Utils.haveInternet(DashboardController.this)) {
                                Toast.makeText(DashboardController.this, "The Internet is inaccessible!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DashboardController.this, "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(final String errorMessage) {
//                progressPopup.hide();
                alertDialog.cancel();
                ProgressPopup.hide();
                if (errorMessage != null) {
                    Utils.err(errorMessage);
                } else {
                    Toast.makeText(DashboardController.this, "Directory created successfully!", Toast.LENGTH_LONG).show();
                    try {
                        listRemoteFileSystem(currentDirectory);
                    } catch (Exception e) {
                        Toast.makeText(DashboardController.this, "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ActionController.ACTION_REQUEST == requestCode) {
            if (resultCode == ActionController.RESULT_UPDATE) {
                Toast.makeText(this, "File deleted successfully!", Toast.LENGTH_LONG).show();
                refreshCurrentDirectory();
                ProgressPopup.hide();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
