package com.spstanchev.tvseries.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Stefan on 2/8/2015.
 */
public class Utils {
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static String getFilenameFromUrl(String url) {
        String[] urlParts = url.split("/");
        return urlParts[urlParts.length - 1];
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static SimpleDateFormat getAirdateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    public static SimpleDateFormat getAirtimeFormat() {
        return new SimpleDateFormat("HH:mm");
    }

    public static SimpleDateFormat getJsonAirstampFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
    }

    public static Date getDateFromString(String stringDate, SimpleDateFormat sdf) {
        Date date = null;
        try {
            date = sdf.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static ArrayList<Integer> getUniqueRandomNumbers(int min, int max) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return list;
    }

}
