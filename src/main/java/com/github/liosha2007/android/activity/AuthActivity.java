package com.github.liosha2007.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.common.Utils;
import com.github.liosha2007.android.controller.MainController;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.layout_auth)
public class AuthActivity extends Activity {

    @ViewById
    protected LinearLayout authLayout;
    @ViewById
    protected EditText cidLogin;
    @ViewById
    protected EditText pkeyPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void authClicked(View view) {
        if (cidLogin.getText().toString().contains("@")) {
            // TODO: implement auth data request
        } else {
            String cid = cidLogin.getText().toString();
            String pkey = pkeyPassword.getText().toString();
            if (Utils.isNullOrBlank(cid) || Utils.isNullOrBlank(pkey)) {
                Toast.makeText(this, "Please enter credentials!", Toast.LENGTH_LONG);
            } else {
                Intent intent = new Intent();
                intent.putExtra(MainController.CID_KEY, cid);
                intent.putExtra(MainController.PKEY_KEY, pkey);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    public void rootLayoutClicked(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }
}
