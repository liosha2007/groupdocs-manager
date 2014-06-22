package com.github.liosha2007.android.groupdocs.common;

import android.os.Looper;
import android.os.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liosha on 11.11.13.
 */
public class Handler {
    private static final Map<Message, ICallback> MESSAGE_I_CALLBACK_MAP = new HashMap<Message, ICallback>();

    private static final android.os.Handler.Callback handlerCallback = new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
//            Looper.getMainLooper().quit();
            ICallback callback = null;
            synchronized (MESSAGE_I_CALLBACK_MAP) {
                callback = MESSAGE_I_CALLBACK_MAP.get(message);
            }
            if (callback != null) {
                synchronized (MESSAGE_I_CALLBACK_MAP) {
                    MESSAGE_I_CALLBACK_MAP.remove(message);
                }
                callback.callback(message.obj);
                return true;
            }
            return false;
        }
    };
    private static android.os.Handler handler = new android.os.Handler(Looper.getMainLooper(), handlerCallback);

    public static void sendMessage(ICallback callback) {
//        Looper.prepare();
        Message message = new Message();
        synchronized (MESSAGE_I_CALLBACK_MAP) {
            MESSAGE_I_CALLBACK_MAP.put(message, callback);
        }
        handler.sendMessage(message);
//        Looper.loop();
    }

    public interface ICallback {
        void callback(Object obj);
    }

    public static void initialize() {
        // TODO: This method initialize class and create handler
    }
}
