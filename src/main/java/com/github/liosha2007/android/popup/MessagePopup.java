package com.github.liosha2007.android.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.liosha2007.android.R;
import com.github.liosha2007.android.common.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liosha on 11.12.13.
 */
public class MessagePopup {
    protected static AlertDialog messageDialog = null;
    protected static Timer timer = null;
    protected static boolean isShowed = false;

    public static void initialize(Activity rootActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(rootActivity);
        LayoutInflater inflater = rootActivity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_message, null));
        builder.setCancelable(true);
        messageDialog = builder.create();
    }

    public static void showMessage(String message, long ms) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        if (isShowed) {
            messageDialog.cancel();
            isShowed = false;
        }
        timer.schedule(new TimerTask() {
            public void run() {
                timer.cancel();
                messageDialog.cancel();
            }
        }, ms);
        messageDialog.show();
        TextView textView = (TextView)messageDialog.findViewById(R.id.popupMessage);
        if (textView != null) {
            textView.setText(message);
        }
    }

    public static void hide() {
        if (isShowed) {
            messageDialog.cancel();
            isShowed = false;
        }
    }
}
