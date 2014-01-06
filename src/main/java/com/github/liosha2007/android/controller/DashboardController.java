package com.github.liosha2007.android.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.activity.MainActivity;
import com.github.liosha2007.android.adapter.MainDataAdapter;
import com.github.liosha2007.android.adapter.ViewPagerAdapter;
import com.github.liosha2007.android.binder.MainViewBinder;
import com.github.liosha2007.android.common.Handler;
import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.android.fragment.ActionFragment;
import com.github.liosha2007.android.fragment.DashboardFragment;
import com.github.liosha2007.android.popup.AuthPopup;
import com.github.liosha2007.android.popup.ProgressPopup;
import com.github.liosha2007.groupdocs.api.StorageApi;
import com.github.liosha2007.groupdocs.common.ApiClient;
import com.github.liosha2007.groupdocs.model.common.RemoteSystemDocument;
import com.github.liosha2007.groupdocs.model.common.RemoteSystemFolder;
import com.github.liosha2007.groupdocs.model.storage.ListEntitiesResponse;
import com.github.liosha2007.groupdocs.model.storage.ListEntitiesResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liosha on 12.11.13.
 */
public class DashboardController extends BaseController<DashboardFragment> {
    protected AlertDialog authDialog = null;
    protected String cid = null;
    protected String pkey = null;

    protected static final String ATTRIBUTE_FILENAME_KEY = Integer.toString(Utils.makeID());
    protected static final String ATTRIBUTE_FILESIZE_KEY = Integer.toString(Utils.makeID());
    protected static final String ATTRIBUTE_FILEIMAGE_KEY = Integer.toString(Utils.makeID());

    protected ProgressPopup progressPopup;
    protected StorageApi storageApi;
    protected HashMap<String, RemoteSystemFolder> remoteFolderMap = new HashMap<String, RemoteSystemFolder>();
    protected HashMap<String, RemoteSystemDocument> remoteDocumentMap = new HashMap<String, RemoteSystemDocument>();
    protected RemoteSystemFolder selectedFolder = null;
    protected RemoteSystemDocument selectedDocument = null;
    protected String currentDirectory = "";

    public DashboardController(DashboardFragment fragment) {
        super(fragment);
    }

    public void onViewCreated(Bundle savedInstanceState) {
        initializeApplication();
    }

    protected void initializeApplication() {
        final SharedPreferences sharedPreferences = rootFragment.getActivity().getPreferences(Context.MODE_PRIVATE);
        this.cid = sharedPreferences.getString(CID_KEY, null);
        this.pkey = sharedPreferences.getString(PKEY_KEY, null);
        if (this.cid == null || this.pkey == null) {
            new AuthPopup(rootFragment).setOnSaveCallback(new AuthPopup.ICallback() {
                @Override
                public boolean onCallback(String cid, String pkey) {
                    DashboardController.this.cid = cid;
                    DashboardController.this.pkey = pkey;
                    onCredentialsLoaded();
                    return true;
                }
            }).show();
        } else {
            onCredentialsLoaded();
        }
        progressPopup = new ProgressPopup(this.rootFragment);
        //
        final MainActivity mainActivity = (MainActivity) rootFragment.getActivity();
        mainActivity.setOnBackPressed(new MainActivity.IBackPressed() {
            @Override
            public boolean onBackPressed() {
                ViewPager viewPager = mainActivity.getViewPager();
                if (viewPager.getCurrentItem() == 1) {
                    if (currentDirectory == "") {
                        // Exit
                        return true;
                    }
                    onGoUpButtonClicked();
                }
                viewPager.setCurrentItem(1);
                return false;
            }
        });

        boolean aload = sharedPreferences.getBoolean(ALOAD_KEY, true);
        if (aload && checkInternetAvailable()) {
            try {
                listRemoteFileSystem(currentDirectory);
            } catch (Exception e) {
                Utils.err(e.getMessage());
                Toast.makeText(rootFragment.getActivity(), "Unknown error!", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onCredentialsLoaded() {
        try {
            checkInternetAvailable();
            initializeGroupDocs();
        } catch (Exception e) {
            Utils.err(e.getMessage());
            Toast.makeText(rootFragment.getActivity(), "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
        }
    }

    protected boolean checkInternetAvailable() {
        if (!Utils.haveInternet(rootFragment.getActivity())) {
            new AlertDialog.Builder(rootFragment.getActivity())
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

    public void listRemoteFileSystem(String path) throws Exception {
        progressPopup.show();
        new AsyncTask<String, Void, ListEntitiesResponse>() {
            @Override
            protected ListEntitiesResponse doInBackground(String... params) {
                try {
                    return Utils.assertResponse(storageApi.listEntities(params[0]));
                } catch (final Exception e) {
                    Handler.sendMessage(new Handler.ICallback() {
                        @Override
                        public void callback(Object obj) {
                            updateCurrentDirectory(false);
                            if (checkInternetAvailable()) {
                                progressPopup.hide(); // TODO:
                                Utils.err(e.getMessage());
                                Toast.makeText(rootFragment.getActivity(), "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
                            }
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
            listItemData.put(ATTRIBUTE_FILESIZE_KEY, remoteDocument.getSize());
            listItemData.put(ATTRIBUTE_FILEIMAGE_KEY, R.drawable.some_file);
            filesListData.add(listItemData);

            remoteDocumentMap.put(remoteDocument.getGuid(), remoteDocument);
        }
        //
        bindDataToListView(filesListData);
        progressPopup.hide();
    }

    private void bindDataToListView(ArrayList<Map<String, Object>> filesListData) {
        String[] listFrom = new String[]{ATTRIBUTE_FILENAME_KEY, ATTRIBUTE_FILESIZE_KEY, ATTRIBUTE_FILEIMAGE_KEY};
        int[] listTo = new int[]{R.id.itemFileName, R.id.itemFileSize, R.id.itemFileImage};
        SimpleAdapter.ViewBinder viewBinder = new MainViewBinder();
        SimpleAdapter simpleAdapter = new MainDataAdapter(rootFragment.getActivity(), filesListData, R.layout.layout_dashboard_item, listFrom, listTo);
        simpleAdapter.setViewBinder(viewBinder);
        rootFragment.updateListViewAdapter(simpleAdapter);
    }

    public void onListViewItemClicked(LinearLayout linearLayout, int position) {
        TextView textView = (TextView) linearLayout.findViewById(R.id.itemFileName);
        if (textView == null) {
            Utils.err("Error: textView is null!");
            Toast.makeText(rootFragment.getActivity(), "Unknown error", Toast.LENGTH_LONG).show();
        }
        String tag = (String) textView.getTag();
        if (tag != null && remoteFolderMap.containsKey(tag)) {
            onFolderClicked(remoteFolderMap.get(tag));
        } else if (tag != null && remoteDocumentMap.containsKey(tag)) {
            onDocumentClicked(remoteDocumentMap.get(tag));
        } else {
            Utils.deb("tag is not in folders or files map");
        }
    }

    public void onRefreshButtonClicked() {
        try {
            if (checkInternetAvailable()) {
                listRemoteFileSystem(currentDirectory);
            }
        } catch (Exception e) {
            if (checkInternetAvailable()) {
                Utils.err(e.getMessage());
                Toast.makeText(rootFragment.getActivity(), "Unknown error!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onGoUpButtonClicked() {
        if ("".equals(currentDirectory)) {
            return;
        }
        try {
            updateCurrentDirectory(false);
            listRemoteFileSystem(currentDirectory);
        } catch (Exception e) {
            if (checkInternetAvailable()) {
                Utils.err(e.getMessage());
                Toast.makeText(rootFragment.getActivity(), "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onDocumentClicked(RemoteSystemDocument remoteSystemDocument) {
        selectedDocument = remoteSystemDocument;
        selectedFolder = null;

        if (remoteSystemDocument != null) {
            MainActivity mainActivity = (MainActivity) rootFragment.getActivity();
            ViewPagerAdapter viewPagerAdapter = ((ViewPagerAdapter) mainActivity.getViewPager().getAdapter());

            ActionFragment actionFragment = (ActionFragment) viewPagerAdapter.getItem(2);
            actionFragment.initialize(remoteSystemDocument);

            ViewPager viewPager = mainActivity.getViewPager();
            viewPager.setCurrentItem(2);
        }
    }

    private void onFolderClicked(RemoteSystemFolder remoteSystemFolder) {
        selectedFolder = remoteSystemFolder;
        selectedDocument = null;

        try {
            updateCurrentDirectory(true);
            listRemoteFileSystem(currentDirectory);
        } catch (Exception e) {
            if (checkInternetAvailable()) {
                Utils.err(e.getMessage());
                Toast.makeText(rootFragment.getActivity(), "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void updateCurrentDirectory(boolean into) {
        if (into && selectedFolder != null) {
            String dirName = (selectedFolder == null) ? "" : selectedFolder.getName().replaceAll("/", "").replaceAll("\\\\", "");
            currentDirectory = currentDirectory + ((currentDirectory.length() > 0) ? "/" : "") + dirName;
        } else if (!into) {
            currentDirectory = (currentDirectory.contains("/") ? currentDirectory.substring(0, currentDirectory.indexOf("/")) : "");
        }
        rootFragment.updateCurrentDirectory(currentDirectory);
    }
}
