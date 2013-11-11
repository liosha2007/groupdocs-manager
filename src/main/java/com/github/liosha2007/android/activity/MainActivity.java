package com.github.liosha2007.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.groupdocs.api.StorageApi;
import com.github.liosha2007.groupdocs.common.ApiClient;
import com.github.liosha2007.groupdocs.model.ListEntitiesResponse;
import com.github.liosha2007.groupdocs.model.ListEntitiesResult;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends Activity {

    public static final String CID_KEY = "groupdocs-cid";
    public static final String PKEY_KEY = "groupdocs-pkey";

    private static final String ATTRIBUTE_FILENAME_KEY = "filename";
    private static final String ATTRIBUTE_FILESIZE_KEY = "filesize";
    private static final String ATTRIBUTE_FILEIMAGE_KEY = "fileimage";

    private static final int CODE_AUTH = 1;
    private static final String TAG = "groupdocs-android";
    private String cid = null;
    private String pkey = null;
    private StorageApi storageApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        initializeApplication();

    }

    private void initializeApplication() {
        try {
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            String cid = sharedPreferences.getString(CID_KEY, null);
            String pkey = sharedPreferences.getString(PKEY_KEY, null);
            if (cid == null || pkey == null) {
                startActivityForResult(new Intent(this, AuthActivity.class), CODE_AUTH);
            } else {
                this.cid = cid;
                this.pkey = pkey;
                initializeGroupDocs();
                refreshFilesList();
            }
        } catch (Exception e){
            Toast.makeText(this, "Error: '" + e.getMessage() + "'", 3000);
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

        String _url = "https://api.groupdocs.com/v2.0/storage/2319d8d4b10875be/folders/";
        String _pkey = "1234567890";
        Log.e(TAG, ApiClient.signUrl(_pkey, _url));
    }

    private void refreshFilesList() throws Exception {
        if (this.cid == null || this.pkey == null) {
            // TODO:
            throw new Exception("Client ID or Private KEY is null");
        }
        ListView filesListView = (ListView)findViewById(R.id.filesListView);

        ListEntitiesResult listEntitiesResult = listGroupDocsDirectory("");

        String[] listFrom = new String[]{ ATTRIBUTE_FILENAME_KEY, ATTRIBUTE_FILESIZE_KEY, ATTRIBUTE_FILEIMAGE_KEY };
        int[] listTo = new int[]{ R.id.itemFileName, R.id.itemFileSize, R.id.itemFileImage };
        ArrayList<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();

//        // Add directories
//        for (FileSystemFolder systemFolder : listEntitiesResult.getFolders()) {
//            Map<String, Object> listItemData = new HashMap<String, Object>(3);
//            listItemData.put(ATTRIBUTE_FILENAME_KEY, systemFolder.getName());
//            listItemData.put(ATTRIBUTE_FILESIZE_KEY, "");
//            if (systemFolder.getFolder_count() == 0 && systemFolder.getFile_count() == 0) {
//                listItemData.put(ATTRIBUTE_FILEIMAGE_KEY, R.drawable.folder_empty);
//            } else {
//                listItemData.put(ATTRIBUTE_FILEIMAGE_KEY, R.drawable.folder_fill);
//            }
//            listData.add(listItemData);
//        }
//
//        SimpleAdapter.ViewBinder viewBinder = new MainViewBinder();
//        SimpleAdapter simpleAdapter = new MainDataAdapter(this, listData, R.layout.layout_main_item, listFrom, listTo);
//        simpleAdapter.setViewBinder(viewBinder);
//
//        ListView listView = (ListView) findViewById(R.id.filesListView);
//        listView.setAdapter(simpleAdapter);
    }

    private ListEntitiesResult listGroupDocsDirectory(String path) throws Exception{
        ListEntitiesResponse listEntitiesResponse = storageApi.ListEntities(path);
        listEntitiesResponse = Utils.assertResponse(listEntitiesResponse);
        return listEntitiesResponse.getResult();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_AUTH && resultCode == RESULT_OK) {
            try {
                String cid = data.getStringExtra(CID_KEY);
                String pkey = data.getStringExtra(PKEY_KEY);
                if (cid == null || pkey == null) {
                    // TODO:
                    Toast.makeText(this, "Unknown error!", 3000);
                }

                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CID_KEY, cid);
                editor.putString(PKEY_KEY, pkey);
                editor.commit();

                this.cid = cid;
                this.pkey = pkey;

                refreshFilesList();
            } catch (Exception e){
                Toast.makeText(this, "Error: '" + e.getMessage() + "'", 3000);
            }
        }
    }
}
