package com.github.liosha2007.android.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.android.fragment.ActionFragment;
import com.github.liosha2007.android.popup.MessagePopup;
import com.github.liosha2007.groupdocs.model.common.RemoteSystemDocument;

/**
 * Created by liosha on 17.11.13.
 */
public class ActionController extends BaseController<ActionFragment> {
    protected static final String VIEWER_CALLBACK = "http://apps.groupdocs.com/document-viewer/embed/{GUID}";

    protected RemoteSystemDocument remoteDocument = null;

    public ActionController(ActionFragment view) {
        super(view);
    }

    public void initialize(RemoteSystemDocument remoteDocument) {
        this.remoteDocument = remoteDocument;
        if (remoteDocument != null) {
            rootFragment.showFileName(remoteDocument.getName());
            rootFragment.showFileGuid(remoteDocument.getGuid());
            rootFragment.showFileSize(String.valueOf(remoteDocument.getSize()));
        }
    }

    public void onViewFileClicked() {
        if (remoteDocument != null){
            String viewer = VIEWER_CALLBACK.replace("{GUID}", remoteDocument.getGuid());
            rootFragment.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(viewer)));
        }
    }

    public void onFileNameTouched() {
        if (remoteDocument != null){
            Utils.copyText(this.rootFragment.getActivity(), "File name", remoteDocument.getName());
            MessagePopup.showMessage("File name is copied!", 2000);
        }
    }

    public void onFileGuidTouched() {
        if (remoteDocument != null){
            Utils.copyText(this.rootFragment.getActivity(), "File GUID", remoteDocument.getGuid());
            MessagePopup.showMessage("File GUID is copied!", 2000);
        }
    }
}
