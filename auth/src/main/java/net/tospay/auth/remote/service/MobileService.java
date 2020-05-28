package net.tospay.auth.remote.service;

import androidx.lifecycle.LiveData;

import net.tospay.auth.model.Account;
import net.tospay.auth.remote.request.MobileAccountVerificationRequest;
import net.tospay.auth.remote.request.MobileRequest;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.AccountLinkResponse;
import net.tospay.auth.remote.response.Result;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * <p>Handle Mobile linking and verification operation</p>
 */
public interface MobileService {

    @POST("v1/mobile/link")
    LiveData<ApiResponse<Result<AccountLinkResponse>>> link(
            @Header("Authorization") String bearer,
            @Body MobileRequest request
    );

    @POST("v1/mobile/verify")
    LiveData<ApiResponse<Result>> verify(
            @Header("Authorization") String bearer,
            @Body MobileAccountVerificationRequest request
    );

    @POST("v1/mobile/resend")
    LiveData<ApiResponse<Result>> resend(
            @Header("Authorization") String bearer,
            @Body Map<String, Object> request
    );

    @GET("v1/mobile/accounts")
    LiveData<ApiResponse<Result<List<Account>>>> accounts(
            @Header("Authorization") String bearer
    );

    @POST("v1/mobile/delete")
    LiveData<ApiResponse<Result>> delete(
            @Header("Authorization") String bearer,
            @Body Map<String, Object> request
    );

    @POST("v1/mobile/update")
    LiveData<ApiResponse<Result>> update(
            @Header("Authorization") String bearer,
            @Body Map<String, Object> request
    );
}
