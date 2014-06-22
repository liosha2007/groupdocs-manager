package com.github.liosha2007.android.groupdocs.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.github.liosha2007.android.groupdocs.application.GMApplication;
import com.github.liosha2007.android.groupdocs.common.Handler;
import com.github.liosha2007.android.groupdocs.popup.FilePopup;
import com.github.liosha2007.android.groupdocs.popup.ProgressPopup;
import com.github.liosha2007.android.groupdocs.view.ActionView;
import com.github.liosha2007.android.library.common.Utils;
import com.github.liosha2007.groupdocs.api.StorageApi;
import com.github.liosha2007.groupdocs.common.ApiClient;
import com.github.liosha2007.groupdocs.common.FileStream;
import com.github.liosha2007.groupdocs.model.common.RemoteSystemDocument;
import com.github.liosha2007.groupdocs.model.storage.DeleteResponse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author liosha on 19.06.2014.
 */
public class ActionController extends AbstractController<ActionView> {
    public static final String REMOTE_DOCUMENT_KEY = "remote-document-key";

    protected static final String VIEWER_CALLBACK = "http://apps.groupdocs.com/document-viewer/embed/{GUID}";
    public static final int ACTION_REQUEST = Utils.makeID();
    public static final int RESULT_UPDATE = Utils.makeID();

    protected RemoteSystemDocument remoteDocument = null;
    protected StorageApi storageApi;
    protected String cid;
    protected String pkey;
    protected String basePath;
    protected boolean aload;

    public ActionController() {
        super(new ActionView());
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        Bundle bundle = getIntent().getExtras();
        this.remoteDocument = (RemoteSystemDocument) bundle.getSerializable(REMOTE_DOCUMENT_KEY);

        SharedPreferences sharedPreferences = GMApplication.getInstance().getSharedPreferences();
        cid = sharedPreferences.getString(CID_KEY, null);
        pkey = sharedPreferences.getString(PKEY_KEY, null);
        basePath = sharedPreferences.getString(BPATH_KEY, null);
        aload = sharedPreferences.getBoolean(ALOAD_KEY, true);

        storageApi = new StorageApi(new ApiClient(pkey, cid, basePath));

        if (remoteDocument != null) {
            view.showDetails(remoteDocument.getName(), remoteDocument.getGuid(), String.valueOf(remoteDocument.getSize()));
        }
    }

    public void onViewClicked() {
        if (remoteDocument != null) {
            String viewer = VIEWER_CALLBACK.replace("{GUID}", remoteDocument.getGuid());
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(viewer)));
        }
    }

    public void onDownloadClicked() {
        if (remoteDocument != null) {
            File mPath = new File(Environment.getExternalStorageDirectory() + "//");
            FilePopup filePopup = new FilePopup(this, mPath);
            filePopup.addDirectoryListener(new FilePopup.DirectorySelectedListener() {
                public void directorySelected(final File directory) {
                    ProgressPopup.show(ActionController.this);
                    Utils.deb("selected dir " + directory.toString());
                    final String guid = remoteDocument.getGuid();
                    final String fileName = remoteDocument.getName();
//                    progressPopup.show();
                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... params) {
                            try {
                                FileStream fileStream = storageApi.downloadFile(guid, fileName);
                                InputStream inputStream = fileStream.getInputStream();
                                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(directory.getAbsolutePath() + "/" + fileName));
                                com.github.liosha2007.android.groupdocs.common.Utils.copy(inputStream, bufferedOutputStream);
                                Utils.closeStreams(inputStream, bufferedOutputStream);
                            } catch (final Exception e) {
                                Handler.sendMessage(new Handler.ICallback() {
                                    @Override
                                    public void callback(Object obj) {
                                        ProgressPopup.hide();
                                        if (com.github.liosha2007.android.groupdocs.common.Utils.haveInternet(ActionController.this)) {
                                            Toast.makeText(ActionController.this, "The Internet is inaccessible!", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(ActionController.this, "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(final String errorMessage) {
//                            progressPopup.hide();
                            ProgressPopup.hide();
                            if (errorMessage != null) {
                                Utils.err(errorMessage);
                            } else {
                                Toast.makeText(ActionController.this, "File downloaded successfully!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }.execute();
                }
            });
            filePopup.setSelectDirectoryOption(true);
            filePopup.showDialog();
        }
    }

    public void onQrClicked() {
        if (remoteDocument == null) {
            return;
        }
        String viewerUrl = VIEWER_CALLBACK.replace("{GUID}", remoteDocument.getGuid());
        Bundle bundle = new Bundle();
        bundle.putString(QrController.VIEWER_URL, viewerUrl);
        run(QrController.class, bundle);
    }

    public void onDeleteClicked() {
        if (remoteDocument == null) {
            return;
        }
        view.showDeleteDialog(remoteDocument.getName());
    }

    public void onFileNameTouched() {
        if (remoteDocument != null) {
            Utils.copyText(this, "File name", remoteDocument.getName());
            Toast.makeText(this, "File name is copied!", Toast.LENGTH_LONG).show();
        }
    }

    public void onFileGuidTouched() {
        if (remoteDocument != null) {
            Utils.copyText(this, "File guid", remoteDocument.getName());
            Toast.makeText(this, "File guid is copied!", Toast.LENGTH_LONG).show();
        }
    }

    public void onDialogOkClicked() {
//        progressPopup.show();
        ProgressPopup.show(this);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    DeleteResponse deleteResponse = storageApi.delete(remoteDocument.getGuid());
                    com.github.liosha2007.android.groupdocs.common.Utils.assertResponse(deleteResponse);
                } catch (final Exception e) {
                    Handler.sendMessage(new Handler.ICallback() {
                        @Override
                        public void callback(Object obj) {
                            ProgressPopup.hide();
                            if (com.github.liosha2007.android.groupdocs.common.Utils.haveInternet(ActionController.this)) {
                                Toast.makeText(ActionController.this, "The Internet is inaccessible!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ActionController.this, "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
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
                ProgressPopup.hide();
                if (errorMessage != null) {
                    Utils.err(errorMessage);
                } else {
                    setResult(RESULT_UPDATE);
                }
                finish();
            }
        }.execute();
    }
}
