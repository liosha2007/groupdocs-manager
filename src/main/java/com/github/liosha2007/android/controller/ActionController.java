package com.github.liosha2007.android.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.github.liosha2007.android.common.Handler;
import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.android.fragment.ActionFragment;
import com.github.liosha2007.android.popup.FilePopup;
import com.github.liosha2007.android.popup.MessagePopup;
import com.github.liosha2007.android.popup.ProgressPopup;
import com.github.liosha2007.groupdocs.api.StorageApi;
import com.github.liosha2007.groupdocs.common.ApiClient;
import com.github.liosha2007.groupdocs.common.FileStream;
import com.github.liosha2007.groupdocs.model.common.RemoteSystemDocument;

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

    public ActionController(ActionFragment view) {
        super(view);
    }

    public void initialize(RemoteSystemDocument remoteDocument) {
        this.remoteDocument = remoteDocument;
        this.progressPopup = new ProgressPopup(this.rootFragment);
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
            MessagePopup.showMessage("File name is copied!", 2000);
        }
    }

    public void onFileGuidTouched() {
        if (remoteDocument != null) {
            Utils.copyText(this.rootFragment.getActivity(), "File GUID", remoteDocument.getGuid());
            MessagePopup.showMessage("File GUID is copied!", 2000);
        }
    }

    public void onDownloadClicked() {
        if (remoteDocument != null) {
            File mPath = new File(Environment.getExternalStorageDirectory() + "//");
            FilePopup filePopup = new FilePopup(rootFragment.getActivity(), mPath);
            filePopup.addDirectoryListener(new FilePopup.DirectorySelectedListener() {
                public void directorySelected(final File directory) {
                    Utils.deb("selected dir " + directory.toString());
                    SharedPreferences sharedPreferences = rootFragment.getActivity().getPreferences(Context.MODE_PRIVATE);
                    String cid = sharedPreferences.getString(CID_KEY, null);
                    String pkey = sharedPreferences.getString(PKEY_KEY, null);
                    String bpath = sharedPreferences.getString(BPATH_KEY, null);

                    final String guid = remoteDocument.getGuid();
                    final String fileName = remoteDocument.getName();
                    final StorageApi storageApi = new StorageApi(new ApiClient(pkey, cid, bpath));

                    progressPopup.show();
                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... params) {
                            try {
                                FileStream fileStream = storageApi.DownloadFile(guid, fileName);
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
                                        MessagePopup.showMessage("Error: '" + e.getMessage() + "'", 2000);
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
                                MessagePopup.showMessage("File downloaded successfully!", 2000);
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
        // TODO: Need ti omplement Utils.createQRImage()
    }
}
