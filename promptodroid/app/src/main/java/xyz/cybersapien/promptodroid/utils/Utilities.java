package xyz.cybersapien.promptodroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import xyz.cybersapien.promptodroid.R;

/**
 * Created by ogcybersapien on 31/12/17.
 */

public final class Utilities {

    private Utilities() {
        // Empty private constructor as this class must not be able to initialize data
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager != null ? cManager.getActiveNetworkInfo() : null;
        return info != null && info.isConnected();
    }

    public static String getFormattedDate(long date) {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance();
        return dateFormat.format(new Date(date));
    }

    public static String getWordCount(Context context, String detailText) {
        int words = detailText.split(" ").length;
        return context.getString(R.string.words_string, words);
    }
}
