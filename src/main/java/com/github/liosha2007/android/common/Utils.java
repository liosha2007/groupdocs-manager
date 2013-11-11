package com.github.liosha2007.android.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by liosha on 07.11.13.
 */
public class Utils {
    private static int _uniqueId = 0;

    /**
     * @param r
     * @param <R>
     * @return
     * @throws Exception
     */
    public static <R> R assertResponse(R r) throws Exception {
        if (r == null) {
            throw new Exception("response is null!");
        }
        if (!"Ok".equalsIgnoreCase((String) r.getClass().getDeclaredMethod("getStatus").invoke(r))) {
            throw new Exception((String) r.getClass().getDeclaredMethod("getError_message").invoke(r));
        }
        return r;
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        return str == null ? true : false;
    }

    /**
     *
     * @param param
     * @return
     */
    public static boolean isNullOrBlank(String param) {
        if (isNull(param) || param.trim().length() == 0) {
            return true;
        }
        return false;
    }

    private static int makeID(){
        return _uniqueId++;
    }

    public static boolean haveInternet(Context context){
        NetworkInfo info = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info==null || !info.isConnected()) {
            return false;
        }
        if (info.isRoaming()) {
            // here is the roaming option you can change it if you want to disable internet while roaming, just return false
            return true;
        }
        return true;
    }
}
