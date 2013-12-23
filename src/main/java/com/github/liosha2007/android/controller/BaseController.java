package com.github.liosha2007.android.controller;

import android.support.v4.app.Fragment;

/**
 * Created by liosha on 12.11.13.
 */
public abstract class BaseController<T extends Fragment> {
    protected T rootFragment = null;

    public static final String CID_KEY = "groupdocs-cid";
    public static final String PKEY_KEY = "groupdocs-pkey";
    public static final String BPATH_KEY = "groupdocs-bpath";
    public static final String ALOAD_KEY = "groupdocs-aload";

    public BaseController(T view) {
        this.rootFragment = view;
    }
}
