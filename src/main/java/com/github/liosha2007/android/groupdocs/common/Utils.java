package com.github.liosha2007.android.groupdocs.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

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

    public static Bitmap createQRImage(String qrCodeText, int size) throws Exception {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText,
                BarcodeFormat.QR_CODE, size, size, hintMap);
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(matrixWidth, matrixWidth, Bitmap.Config.RGB_565);

        Paint paint = new Paint();
        paint.setColor(android.graphics.Color.WHITE);
        paint.setStrokeWidth(0);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, matrixWidth, matrixWidth, paint);

        // Paint and save the image using the BitMatrix
        paint.setColor(android.graphics.Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    canvas.drawRect(i, j, i + 1, j + 1, paint);
                }
            }
        }
        canvas.save();
        return bitmap;
    }
}
