package net.tospay.auth.remote.interceptor;

import android.content.Context;

import androidx.annotation.NonNull;

import net.tospay.auth.remote.exception.NoConnectivityException;
import net.tospay.auth.utils.NetworkUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <p>Intercepts network and check for internet connection</p>
 */
public class NetworkConnectionInterceptor implements Interceptor {

    private Context mContext;

    public NetworkConnectionInterceptor(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (!NetworkUtils.isNetworkAvailable(mContext)) {
            throw new NoConnectivityException();
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }
}
