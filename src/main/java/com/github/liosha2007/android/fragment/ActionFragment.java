package com.github.liosha2007.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.android.controller.ActionController;
import com.github.liosha2007.groupdocs.model.common.RemoteSystemDocument;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by liosha on 13.11.13.
 */
public class ActionFragment extends BaseFragment {
    protected final ActionController controller = new ActionController(this);
    protected EditText fileName = null;
    protected EditText fileGuid = null;
    protected EditText fileSize = null;


    public void initialize(RemoteSystemDocument remoteDocument) {
        if (remoteDocument == null) {
            Utils.err("Unknown error: remoteDocument in ActionFragment is null!");
        }
        controller.initialize(remoteDocument);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState, R.layout.layout_action);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fileName = view(R.id.action_fileName);
        fileGuid = view(R.id.action_fileGuid);
        fileSize = view(R.id.action_fileSize);

        // View button
        view(R.id.action_viewFileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onViewFileClicked();
            }
        });
        // Download button
        view(R.id.action_downloadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onDownloadClicked();
            }
        });
        // File name
        view(R.id.action_fileName).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                controller.onFileNameTouched();
                return true;
            }
        });
        // File GUID
        view(R.id.action_fileGuid).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                controller.onFileGuidTouched();
                return true;
            }
        });
    }

    public void showFileName(String name) {
        fileName.setText(name);
    }

    public void showFileGuid(String guid) {
        fileGuid.setText(guid);
    }

    public void showFileSize(String size) {
        size = (size == null) ? "0" : size;
        size = String.valueOf(new BigDecimal(Double.parseDouble(size) / 1024).setScale(size.length() > 9 ? 0 : 3, RoundingMode.UP).doubleValue()) + "kb";
        fileSize.setText(size);
    }
}
