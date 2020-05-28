package net.tospay.auth.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

/**
 * This class is used to check whether Internet is available or not
 */
public class NetworkUtils {

    private NetworkUtils() {
        throw new IllegalStateException("NetworkUtils class");
    }

    /**
     * check whether Internet is available or not
     *
     * @param context {@link Context}.
     * @return true if internet is available else false
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
}
