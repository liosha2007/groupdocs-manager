package com.github.liosha2007.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.liosha2007.android.R;

public class AuthActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_auth);
    }

    public void authClicked(View view) {
        LinearLayout authLayout = (LinearLayout) findViewById(R.id.authLayout);
        EditText cidLogin = (EditText) authLayout.findViewById(R.id.cidLogin);
        EditText pkeyPassword = (EditText) authLayout.findViewById(R.id.pkeyPassword);
        if (cidLogin.getText().toString().contains("@")) {
            // TODO: implement auth data request
        } else {
            String cid = cidLogin.getText().toString();
            String pkey = pkeyPassword.getText().toString();
            Intent intent = new Intent();
            intent.putExtra(MainActivity.CID_KEY, cid);
            intent.putExtra(MainActivity.PKEY_KEY, pkey);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void rootLayoutClicked(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }
}
