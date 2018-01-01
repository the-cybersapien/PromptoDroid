package xyz.cybersapien.promptodroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ogcybersapien on 31/12/17.
 */

public final class NetUtils {

    private NetUtils() {
        // Empty private constructor as this class must not be able to initialize data
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager != null ? cManager.getActiveNetworkInfo() : null;
        return info != null && info.isConnected();
    }
}
