package com.github.liosha2007.android.common;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.github.liosha2007.groupdocs.api.UserApi;
import com.github.liosha2007.groupdocs.common.ApiClient;
import com.github.liosha2007.groupdocs.model.user.UserInfoResult;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by liosha on 07.11.13.
 */
public class Utils {
    private static int _uniqueId = 0;
    public static final String TAG = "groupdocs-manager";

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
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        return str == null ? true : false;
    }

    /**
     * @param param
     * @return
     */
    public static boolean isNullOrBlank(String param) {
        if (isNull(param) || param.trim().length() == 0) {
            return true;
        }
        return false;
    }

    public static int makeID() {
        return _uniqueId++;
    }

    public static boolean haveInternet(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        if (info.isRoaming()) {
            // here is the roaming option you can change it if you want to disable internet while roaming, just return false
            return true;
        }
        return true;
    }

    public static void err(String error_message) {
        Log.e(TAG, isNullOrBlank(error_message) ? "Unknown error!" : error_message);
    }

    public static void deb(String error_message) {
        Log.d(TAG, error_message);
    }

    public static void copyText(Context context, String label, String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, textToCopy);
        clipboard.setPrimaryClip(clip);
    }

    public interface ICredentialsNormalized {
        void onCredentialsNormalized(String cid, String pkey);
    }

    public static void normalizeCredentials(final Activity activity, final String loginCid, final String passwordPkey, final String bpath, final ICredentialsNormalized credentialsNormalized) {
        if (loginCid.contains("@")) {
            new AsyncTask<String, Void, UserInfoResult>() {

                @Override
                protected UserInfoResult doInBackground(String... params) {
                    try {
                        ApiClient apiClient = new ApiClient("123", "123", bpath);
                        UserApi userApi = new UserApi(apiClient);
                        return Utils.assertResponse(userApi.loginUser(params[0], params[1])).getResult();
                    } catch (final Exception e) {
                        Handler.sendMessage(new Handler.ICallback() {
                            @Override
                            public void callback(Object obj) {
                                Utils.err(e.getMessage());
                                Toast.makeText(activity, "Error: '" + e.getMessage() + "'", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(final UserInfoResult userInfoResult) {
                    if (userInfoResult != null) {
                        credentialsNormalized.onCredentialsNormalized(userInfoResult.getUser().getGuid(), userInfoResult.getUser().getPkey());
                    }
                }
            }.execute(loginCid, passwordPkey);
        } else {
            if (Utils.isNullOrBlank(loginCid) || Utils.isNullOrBlank(passwordPkey)) {
                Toast.makeText(activity, "Please enter Client ID and Private KEY!", Toast.LENGTH_LONG).show();
            } else {
                credentialsNormalized.onCredentialsNormalized(loginCid, passwordPkey);
            }
        }
    }

    /**
     * @param input
     * @param output
     * @return
     * @throws Exception
     */
    public static int copy(InputStream input, OutputStream output) throws Exception {
        byte[] buffer = new byte[1024];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
