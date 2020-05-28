package net.tospay.auth.remote.service;

import androidx.lifecycle.LiveData;

import net.tospay.auth.model.Account;
import net.tospay.auth.remote.response.AccountResponse;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.ui.account.topup.StatusReq;
import net.tospay.auth.ui.account.topup.StatusRes;
import net.tospay.auth.ui.device_model.LocationReq;
import net.tospay.auth.ui.device_model.LocationRes;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AccountService {

    @GET("v1/combined/accounts")
    LiveData<ApiResponse<Result<AccountResponse>>> accounts(
            @Header("Authorization") String bearer
    );

    @GET("v1/card/accounts")
    LiveData<ApiResponse<Result<List<Account>>>> getCardAccounts(
            @Header("Authorization") String bearer
    );

    @GET("v1/mobile/accounts")
    LiveData<ApiResponse<Result<List<Account>>>> getMobileAccounts(
            @Header("Authorization") String bearer
    );

    @GET("v1/bank/accounts")
    LiveData<ApiResponse<Result<List<Account>>>> getBankAccounts(
            @Header("Authorization") String bearer
    );

    @POST("v1/mobile/delete")
    LiveData<ApiResponse<Result>> delete(@Header("Authorization") String var1, @Body Map<String, Object> var2);

    @POST("v1/transfer/transaction/status/id")
    LiveData<ApiResponse<Result<StatusRes>>> checkStatus(
            @Header("Authorization") String bearer,
            @Body StatusReq response
    );

    @POST("v1/geolocate?key=AIzaSyAwh4-fvYm3M771I87fPnozI_7_ibB6Z6s")
    Call<LocationRes> getLocation(
            @Body LocationReq accessPoints
    );

}
