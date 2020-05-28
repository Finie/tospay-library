package net.tospay.auth.remote;

import android.content.Context;

import net.tospay.auth.remote.interceptor.NetworkConnectionInterceptor;
import net.tospay.auth.remote.util.LiveDataCallAdapterFactory;
import net.tospay.auth.remote.interceptor.RequestInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static net.tospay.auth.remote.ApiConstants.CONNECT_TIMEOUT;
import static net.tospay.auth.remote.ApiConstants.READ_TIMEOUT;
import static net.tospay.auth.remote.ApiConstants.WRITE_TIMEOUT;

public class ServiceGenerator {

    /**
     * Logs network requests
     *
     * @return HttpLoggingInterceptor
     */
    private static HttpLoggingInterceptor loggingInterceptor() {
        /*return new HttpLoggingInterceptor().
                setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
                        : HttpLoggingInterceptor.Level.NONE);*/

        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    /**
     * Configure OkHttpClient. This helps us override some of the default configuration. Like the
     * connection timeout.
     *
     * @return OkHttpClient
     */
    private static OkHttpClient okHttpClient(HttpLoggingInterceptor httpLoggingInterceptor, Context mContext) {
        return new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new NetworkConnectionInterceptor(mContext))
                .addInterceptor(new RequestInterceptor())
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * * Creates an instance of retrofit
     *
     * @param baseUrl - url endpoint
     * @return retrofit instance
     */
    private static Retrofit retrofitClient(String baseUrl, Context mContext) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient(loggingInterceptor(), mContext))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * Creates the client instance
     *Uses the url provided in the library
     * @param serviceClass - interface class
     * @param <S>          -interface class
     * @return S
     */
    public static <S> S createService(Class<S> serviceClass, Context mContext) {
        return retrofitClient(ApiConstants.BASE_URL, mContext).create(serviceClass);
    }

    /**
     * Creates the client instance
     *Uses the url provided by the host app
     * @param serviceClass - interface class
     * @param <S>          -interface class
     * @return S
     */
    public static <S> S createService(Class<S> serviceClass, String url, Context mContext) {
        return retrofitClient(url, mContext).create(serviceClass);
    }
}
