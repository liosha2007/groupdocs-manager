package com.github.liosha2007.android.binder;

import android.view.View;
import android.widget.SimpleAdapter;

/**
 * Created by liosha on 07.11.13.
 */
public class MainViewBinder implements SimpleAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Object data, String textRepresentation) {
        return false;
    }
}
