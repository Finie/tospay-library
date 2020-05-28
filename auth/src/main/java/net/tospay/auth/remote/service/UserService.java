package net.tospay.auth.remote.service;

import androidx.lifecycle.LiveData;

import net.tospay.auth.model.TransactionPin;
import net.tospay.auth.remote.request.LoginRequest;
import net.tospay.auth.remote.request.OtpRequest;
import net.tospay.auth.remote.request.RefreshTokenRequest;
import net.tospay.auth.remote.request.RegisterRequest;
import net.tospay.auth.remote.request.ResendEmailRequest;
import net.tospay.auth.remote.request.VerifyEmailRequest;
import net.tospay.auth.remote.request.VerifyPhoneRequest;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.model.Token;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.ui.device_model.LocationReq;
import net.tospay.auth.ui.device_model.LocationRes;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * User Authentication Endpoints
 */
public interface UserService {

    @POST("v1/auth/user/signin")
    LiveData<ApiResponse<Result<TospayUser>>> login(@Body LoginRequest request);

    @POST("v1/auth/user/signup")
    LiveData<ApiResponse<Result<TospayUser>>> register(@Body RegisterRequest request);

    @POST("v1/auth/user/verify-email")
    LiveData<ApiResponse<Result>> verifyEmail(@Body VerifyEmailRequest request);

    @POST("v1/auth/user/resend-email")
    LiveData<ApiResponse<Result>> resendEmailToken(@Body ResendEmailRequest request);

    @POST("v1/auth/user/verify-phone")
    LiveData<ApiResponse<Result<TospayUser>>> verifyPhone(@Body VerifyPhoneRequest request);

    @POST("v1/auth/user/resend-phone")
    LiveData<ApiResponse<Result>> resendOtp(@Body OtpRequest request);

    @POST("v1/auth/user/forgot-password")
    LiveData<ApiResponse<Result>> forgotPassword(@Body Map<String, String> request);

    @POST("v1/auth/user/reset-password")
    LiveData<ApiResponse<Result>> resetPassword(@Body Map<String, String> request);

    @GET("v1/auth/user/profile")
    LiveData<ApiResponse<Result<TospayUser>>> user(@Header("Authorization") String bearer);

    @POST("v1/auth/user/refresh-token")
    LiveData<ApiResponse<Result<Token>>> refreshToken(
            @Header("Authorization") String bearer,
            @Body RefreshTokenRequest request
    );

    @POST("v1/geolocate?key=AIzaSyAwh4-fvYm3M771I87fPnozI_7_ibB6Z6s")
    Call<LocationRes> getLocation(
            @Body LocationReq accessPoints
    );


    @GET("v1/auth/user/user/pin/setup")
    LiveData<ApiResponse<Result<TransactionPin>>> getTransactionPinUrl(
            @Header("Authorization") String bearer
    );

}
