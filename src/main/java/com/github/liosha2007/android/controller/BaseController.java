package com.github.liosha2007.android.controller;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by liosha on 12.11.13.
 */
public abstract class BaseController <T extends Context> {
    protected T context = null;

    public static final String CID_KEY = "groupdocs-cid";
    public static final String PKEY_KEY = "groupdocs-pkey";

    public BaseController(T context) {
        this.context = context;
    }

    public abstract void onCreate(Bundle savedInstanceState);
}
