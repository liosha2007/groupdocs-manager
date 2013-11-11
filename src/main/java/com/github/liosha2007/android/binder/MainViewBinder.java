package com.github.liosha2007.android.binder;

import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.liosha2007.groupdocs.model.FileSystemDocument;
import com.github.liosha2007.groupdocs.model.FileSystemFolder;

/**
 * Created by liosha on 07.11.13.
 */
public class MainViewBinder implements SimpleAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Object data, String textRepresentation) {
        if (data instanceof FileSystemFolder && view instanceof TextView) {
            FileSystemFolder fileSystemFolder = (FileSystemFolder) data;
            TextView textView = (TextView) view;

            textView.setText(fileSystemFolder.getName());
            textView.setTag(fileSystemFolder.getName());

            return true;
        } else if (data instanceof FileSystemDocument && view instanceof TextView) {
            FileSystemDocument fileSystemDocument = (FileSystemDocument) data;
            TextView textView = (TextView) view;

            textView.setText(fileSystemDocument.getName());
            textView.setTag(fileSystemDocument.getGuid());

            return true;
        }
        return false;
    }
}
