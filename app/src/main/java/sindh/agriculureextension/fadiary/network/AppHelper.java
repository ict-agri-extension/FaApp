package sindh.agriculureextension.fadiary.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class AppHelper {

    // https://fas.ictagrisindh.gov.pk/services/index.php
    public static final String BASE_URL = " http://192.168.0.103/AgriExtSindh/services/Main.php";
    /*public static final String FCM_SERVER="key=AAAA0eQEMy4:APA91bFDRZHrS_DYgz0WRlDEhMjKg_5m0B1GmojeoQIy" +
            "1Or_bY2_46YHhmzK5BJiiZMz2OvF04lGIJvHZVqSHlmVpGtgQw2ahCFu7MGfrTrxg3q6NSxi5vaVUQ2gPZ_" +
            "G6Kj7cEQqpZfq";
    public static final String FCM_URL="https://fcm.googleapis.com/fcm/send";
    public static final String CONTENT_TYPE="application/json";
    */public static String getDeviceName() {
        return "DEVICE: " + Build.DEVICE + "\nMODEL:" + Build.MODEL + "\nBRAND:" + Build.BRAND;
    }

    public static String getDeviceInfo() {
        return "HARDWARE:" + Build.HARDWARE +
                "\nDISPLAY:" + Build.DISPLAY + "\nID:" + Build.ID +
                "\nMANUFACTURER:" + Build.MANUFACTURER + "\nHOST:" + Build.HOST
                + "\nPRODUCT:" + Build.PRODUCT + "\nTYPE:" + Build.TYPE;
    }

    public static String getBase64(String image) {
        if (image == null)
            return null;
        Bitmap bitmap = BitmapFactory.decodeFile(image);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, byteArrayOutputStream);
        try {
            FileOutputStream outputStream = new FileOutputStream(image);
            byteArrayOutputStream.writeTo(outputStream);
            bitmap = BitmapFactory.decodeFile(image);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 5, byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] arr = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(arr, Base64.DEFAULT);
    }

    /**
     * Get Address from LAT LANG
     */

    public static Address getLocationData(Double lat, Double lang, Context context) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> list = geocoder.getFromLocation(lat, lang, 1);
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String saveImage(Bitmap bitmap) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/GPS Camera");
            if (!file.mkdir())
                file.mkdir();
            String mn = file.getAbsolutePath() + "/" + SystemClock.currentThreadTimeMillis() + ".jpeg";
            FileOutputStream fos = new FileOutputStream(mn);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            System.out.println("IMAGE SAVED");
            return "Image saved in " + file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to save image!";
        }
    }
}