package com.github.liosha2007.android.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.liosha2007.android.R;

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

    public static void successMessage(String message, long ms) {
        showMessage(R.drawable.popup_success_icon, message, ms);
    }

    public static void failMessage(String message, long ms) {
        showMessage(R.drawable.popup_fail_icon, message, ms);
    }

    public static void showMessage(int iconId, String message, long ms) {
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
        TextView textView = (TextView) messageDialog.findViewById(R.id.popupMessage);
        if (textView != null) {
            textView.setText(message);
        }
        ImageView imageView = (ImageView) messageDialog.findViewById(R.id.popupIcon);
        if (imageView != null) {
            imageView.setImageResource(iconId);
        }
    }

    public static void hide() {
        if (isShowed) {
            messageDialog.cancel();
            isShowed = false;
        }
    }
}
