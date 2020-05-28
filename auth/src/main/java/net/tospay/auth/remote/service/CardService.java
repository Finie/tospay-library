package net.tospay.auth.remote.service;

import androidx.lifecycle.LiveData;

import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.CardInitRes;
import net.tospay.auth.remote.response.Result;

import retrofit2.http.GET;
import retrofit2.http.Header;

public interface CardService {

    @GET("v1/card/init")
    LiveData<ApiResponse<Result<String>>> getCardData(
            @Header("Authorization") String bearer
    );
}
