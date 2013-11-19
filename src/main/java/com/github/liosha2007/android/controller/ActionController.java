package com.github.liosha2007.android.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.liosha2007.android.fragment.ActionFragment;
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

        rootFragment.showFileName(remoteDocument.getName());
        rootFragment.showFileGuid(remoteDocument.getGuid());
        rootFragment.showFileSize(String.valueOf(remoteDocument.getSize()));
    }

    public void onViewFileClicked() {
        String viewer = VIEWER_CALLBACK.replace("{GUID}", remoteDocument.getGuid());
        rootFragment.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(viewer)));
    }
}
