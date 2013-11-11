package com.github.liosha2007.android.common;

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
            ICallback callback = MESSAGE_I_CALLBACK_MAP.get(message);
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
    private static final android.os.Handler handler = new android.os.Handler(handlerCallback);

    public static void sendMessage(Message message, ICallback callback) {
        synchronized (MESSAGE_I_CALLBACK_MAP) {
            MESSAGE_I_CALLBACK_MAP.put(message, callback);
        }
        handler.sendMessage(message);
    }

    public interface ICallback {
        void callback(Object obj);
    }
}
