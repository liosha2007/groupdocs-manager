package com.github.liosha2007.android.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.github.liosha2007.android.activity.MainActivity;
import com.github.liosha2007.android.common.Handler;
import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.android.fragment.ActionFragment;
import com.github.liosha2007.android.popup.FilePopup;
import com.github.liosha2007.android.popup.MessagePopup;
import com.github.liosha2007.android.popup.ProgressPopup;
import com.github.liosha2007.android.popup.QrPopup;
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
 * Created by liosha on 17.11.13.
 */
public class ActionController extends BaseController<ActionFragment> {
    protected static final String VIEWER_CALLBACK = "http://apps.groupdocs.com/document-viewer/embed/{GUID}";

    protected RemoteSystemDocument remoteDocument = null;
    protected ProgressPopup progressPopup;
    protected StorageApi storageApi;

    public ActionController(ActionFragment view) {
        super(view);
    }

    public void initialize(RemoteSystemDocument remoteDocument) {
        this.remoteDocument = remoteDocument;
        this.progressPopup = new ProgressPopup(this.rootFragment);

        SharedPreferences sharedPreferences = rootFragment.getActivity().getPreferences(Context.MODE_PRIVATE);
        String cid = sharedPreferences.getString(CID_KEY, null);
        String pkey = sharedPreferences.getString(PKEY_KEY, null);
        String bpath = sharedPreferences.getString(BPATH_KEY, null);
        storageApi = new StorageApi(new ApiClient(pkey, cid, bpath));

        if (remoteDocument != null) {
            rootFragment.showFileName(remoteDocument.getName());
            rootFragment.showFileGuid(remoteDocument.getGuid());
            rootFragment.showFileSize(String.valueOf(remoteDocument.getSize()));
        }
    }

    public void onViewFileClicked() {
        if (remoteDocument != null) {
            String viewer = VIEWER_CALLBACK.replace("{GUID}", remoteDocument.getGuid());
            rootFragment.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(viewer)));
        }
    }

    public void onFileNameTouched() {
        if (remoteDocument != null) {
            Utils.copyText(this.rootFragment.getActivity(), "File name", remoteDocument.getName());
            MessagePopup.successMessage("File name is copied!", 2000);
        }
    }

    public void onFileGuidTouched() {
        if (remoteDocument != null) {
            Utils.copyText(this.rootFragment.getActivity(), "File GUID", remoteDocument.getGuid());
            MessagePopup.successMessage("File GUID is copied!", 2000);
        }
    }

    public void onDownloadClicked() {
        if (remoteDocument != null) {
            File mPath = new File(Environment.getExternalStorageDirectory() + "//");
            FilePopup filePopup = new FilePopup(rootFragment.getActivity(), mPath);
            filePopup.addDirectoryListener(new FilePopup.DirectorySelectedListener() {
                public void directorySelected(final File directory) {
                    Utils.deb("selected dir " + directory.toString());
                    final String guid = remoteDocument.getGuid();
                    final String fileName = remoteDocument.getName();

                    progressPopup.show();
                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... params) {
                            try {
                                FileStream fileStream = storageApi.downloadFile(guid, fileName);
                                BufferedOutputStream bufferedOutputStream = null;
                                try {
                                    InputStream inputStream = fileStream.getInputStream();
                                    bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(directory.getAbsolutePath() + "/" + fileName));
                                    Utils.copy(inputStream, bufferedOutputStream);
                                    bufferedOutputStream.close();
                                    inputStream.close();
                                } catch (Exception e) {
                                    return e.getMessage();
                                }
                            } catch (final Exception e) {
                                Handler.sendMessage(new Handler.ICallback() {
                                    @Override
                                    public void callback(Object obj) {
                                        progressPopup.hide();
                                        Utils.err(e.getMessage());
                                        MessagePopup.failMessage("Error: '" + e.getMessage() + "'", 2000);
                                    }
                                });
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(final String errorMessage) {
                            progressPopup.hide();
                            if (errorMessage != null) {
                                Utils.err(errorMessage);
                            } else {
                                MessagePopup.successMessage("File downloaded successfully!", 2000);
                            }
                        }
                    }.execute();
                }
            });
            filePopup.setSelectDirectoryOption(true);
            filePopup.showDialog();
        }
    }

    public void onQrShowClicked() {
        if (remoteDocument == null) {
            return;
        }
        QrPopup qrPopup = new QrPopup(rootFragment);
        try {
            String viewer = VIEWER_CALLBACK.replace("{GUID}", remoteDocument.getGuid());
            qrPopup.show(Utils.createQRImage(viewer, 250));
        } catch (Exception e) {
            Utils.err(e.getMessage());
            MessagePopup.failMessage("Error: '" + e.getMessage() + "'", 2000);
        }
    }

    public void onDeleteClicked() {
        if (remoteDocument == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(rootFragment.getActivity());
        builder.setTitle("Delete file?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressPopup.show();
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            DeleteResponse deleteResponse = storageApi.delete(remoteDocument.getGuid());
                            Utils.assertResponse(deleteResponse);
                        } catch (final Exception e) {
                            Handler.sendMessage(new Handler.ICallback() {
                                @Override
                                public void callback(Object obj) {
                                    progressPopup.hide();
                                    Utils.err(e.getMessage());
                                    MessagePopup.failMessage("Error: '" + e.getMessage() + "'", 2000);
                                }
                            });
                            return e.getMessage();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(final String errorMessage) {
                        progressPopup.hide();
                        if (errorMessage != null) {
                            Utils.err(errorMessage);
                        } else {
                            MessagePopup.successMessage("File successfully deleted!", 2000);
                            MainActivity mainActivity = (MainActivity) rootFragment.getActivity();
                            mainActivity.onBackPressed();
                            mainActivity.refreshRemoteFileList();
                        }
                    }
                }.execute();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void onCloseClicked() {
        // TODO: Close action view
    }
}
