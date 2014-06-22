package com.github.liosha2007.android.groupdocs.binder;

import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.liosha2007.groupdocs.model.common.RemoteSystemDocument;
import com.github.liosha2007.groupdocs.model.common.RemoteSystemFolder;

/**
 * Created by liosha on 07.11.13.
 */
public class MainViewBinder implements SimpleAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Object data, String textRepresentation) {
        if (data instanceof RemoteSystemFolder && view instanceof TextView) {
            RemoteSystemFolder fileSystemFolder = (RemoteSystemFolder) data;
            TextView textView = (TextView) view;

            textView.setText(fileSystemFolder.getName());
            textView.setTag(fileSystemFolder.getName());

            return true;
        } else if (data instanceof RemoteSystemDocument && view instanceof TextView) {
            RemoteSystemDocument fileSystemDocument = (RemoteSystemDocument) data;
            TextView textView = (TextView) view;

            textView.setText(fileSystemDocument.getName());
            textView.setTag(fileSystemDocument.getGuid());

            return true;
        }
        return false;
    }
}
