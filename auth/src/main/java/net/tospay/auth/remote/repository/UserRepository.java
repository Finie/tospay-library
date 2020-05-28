package net.tospay.auth.remote.repository;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.model.Token;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.model.TransactionPin;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.request.LoginRequest;
import net.tospay.auth.remote.request.OtpRequest;
import net.tospay.auth.remote.request.RefreshTokenRequest;
import net.tospay.auth.remote.request.RegisterRequest;
import net.tospay.auth.remote.request.ResendEmailRequest;
import net.tospay.auth.remote.request.VerifyEmailRequest;
import net.tospay.auth.remote.request.VerifyPhoneRequest;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.remote.util.NetworkBoundResource;
import net.tospay.auth.ui.device_model.Errors;
import net.tospay.auth.ui.device_model.LocationReq;
import net.tospay.auth.ui.device_model.LocationRes;
import net.tospay.auth.ui.device_model.api.DeviceApi;
import net.tospay.auth.utils.AbsentLiveData;
import net.tospay.auth.utils.SharedPrefManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private final UserService mUserService;
    private final AppExecutors mAppExecutors;

    /**
     * @param mAppExecutors -AppExecutor
     * @param mUserService  - User service
     */
    public UserRepository(AppExecutors mAppExecutors, UserService mUserService) {
        this.mUserService = mUserService;
        this.mAppExecutors = mAppExecutors;
    }


    public LiveData<Resource<TransactionPin>> getTransactionPinUrl(String bearer){
        return new NetworkBoundResource<TransactionPin,Result<TransactionPin>>(mAppExecutors){
            private TransactionPin response;
            @Override
            protected void saveCallResult(@NonNull Result<TransactionPin> item) {
                response = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable TransactionPin data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<TransactionPin> loadFromDb() {
                return new LiveData<TransactionPin>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(response);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<TransactionPin>>> createCall() {
                return mUserService.getTransactionPinUrl(bearer);
            }
        }.asLiveData();
    }
    public LiveData<Resource<TospayUser>> login(LoginRequest request) {
        return new NetworkBoundResource<TospayUser, Result<TospayUser>>(mAppExecutors) {

            private TospayUser resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<TospayUser> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable TospayUser data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<TospayUser> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<TospayUser>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<TospayUser>>> createCall() {
                return mUserService.login(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<TospayUser>> register(RegisterRequest request) {
        return new NetworkBoundResource<TospayUser, Result<TospayUser>>(mAppExecutors) {

            private TospayUser resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<TospayUser> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable TospayUser data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<TospayUser> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<TospayUser>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<TospayUser>>> createCall() {
                return mUserService.register(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> verifyEmail(VerifyEmailRequest request) {

        return new NetworkBoundResource<Result, Result>(mAppExecutors) {

            private Result resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result item) {
                resultsDb = item;
            }

            @Override
            protected boolean shouldFetch(@Nullable Result data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Result> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<Result>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return mUserService.verifyEmail(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> resendEmailToken(ResendEmailRequest request) {

        return new NetworkBoundResource<Result, Result>(mAppExecutors) {

            private Result resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result item) {
                resultsDb = item;
            }

            @Override
            protected boolean shouldFetch(@Nullable Result data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Result> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<Result>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return mUserService.resendEmailToken(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<TospayUser>> verifyPhone(VerifyPhoneRequest request) {

        return new NetworkBoundResource<TospayUser, Result<TospayUser>>(mAppExecutors) {

            private TospayUser resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<TospayUser> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable TospayUser data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<TospayUser> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<TospayUser>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<TospayUser>>> createCall() {
                return mUserService.verifyPhone(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> resendPhoneToken(OtpRequest request) {

        return new NetworkBoundResource<Result, Result>(mAppExecutors) {

            private Result resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result item) {
                resultsDb = item;
            }

            @Override
            protected boolean shouldFetch(@Nullable Result data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Result> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<Result>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return mUserService.resendOtp(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> forgotPassword(String email) {

        Map<String, String> request = new HashMap<>();
        request.put("email", email);

        return new NetworkBoundResource<Result, Result>(mAppExecutors) {

            private Result resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result item) {
                resultsDb = item;
            }

            @Override
            protected boolean shouldFetch(@Nullable Result data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Result> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<Result>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return mUserService.forgotPassword(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> resetPassword(String email, String verificationCode, String password) {

        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("verification_code", verificationCode);
        request.put("password", password);

        return new NetworkBoundResource<Result, Result>(mAppExecutors) {

            private Result resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result item) {
                resultsDb = item;
            }

            @Override
            protected boolean shouldFetch(@Nullable Result data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Result> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<Result>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return mUserService.resetPassword(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<TospayUser>> getUserInfo(String bearerToken, SharedPrefManager sharedPrefManager) {
        return new NetworkBoundResource<TospayUser, Result<TospayUser>>(mAppExecutors) {

            private TospayUser resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<TospayUser> item) {

                TospayUser serverUser = item.getData();

                TospayUser user = sharedPrefManager.getActiveUser();
                user.setFirstname(serverUser.getFirstname());
                user.setLastname(serverUser.getLastname());
                user.setCountryCode(serverUser.getCountryCode());
                user.setPhone(serverUser.getPhone());
                user.setProfilePic(serverUser.getProfilePic());
                user.setAddress(serverUser.getAddress());

                sharedPrefManager.setActiveUser(user);

                resultsDb = user;
            }

            @Override
            protected boolean shouldFetch(@Nullable TospayUser data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<TospayUser> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<TospayUser>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<TospayUser>>> createCall() {
                return mUserService.user(bearerToken);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Token>> refreshToken(String bearerToken, SharedPrefManager sharedPrefManager, RefreshTokenRequest request) {
        return new NetworkBoundResource<Token, Result<Token>>(mAppExecutors) {

            private Token resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<Token> item) {

                Token token = item.getData();

                TospayUser user = sharedPrefManager.getActiveUser();
                user.setToken(token.getToken());
                user.setRefreshToken(token.getRefreshToken());
                user.setExpiredAt(token.getExpiredAt());

                sharedPrefManager.setActiveUser(user);

                resultsDb = token;
            }

            @Override
            protected boolean shouldFetch(@Nullable Token data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Token> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<Token>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<Token>>> createCall() {
                return mUserService.refreshToken(bearerToken, request);
            }
        }.asLiveData();
    }

    public LiveData<LocationRes> getGeoLocation(LocationReq request){
        MutableLiveData<LocationRes> resMutableLiveData = new MutableLiveData<>();
        Retrofit retrofit = DeviceApi.getRetrofitClient();
        UserService service = retrofit.create(UserService.class);
        Call<LocationRes> call = service.getLocation(request);
        call.enqueue(new Callback<LocationRes>() {

            LocationRes locationRes = new LocationRes();
            @Override
            public void onResponse(Call<LocationRes> call, Response<LocationRes> response) {
                if (response.isSuccessful()){
                  locationRes = response.body();
                  locationRes.setStatus("success");
                  resMutableLiveData.setValue(locationRes);
                }
                else {
                    LocationRes locationRes = new LocationRes();
                    locationRes = response.body();
                   /* Errors errors = new Errors();
                    errors.setCode(400);
                    errors.setMessage("Invalid content");
                    locationRes.setError(errors);*/
           /*         locationRes.setStatus("fail");*/
                    resMutableLiveData.setValue(locationRes);
                }
            }

            @Override
            public void onFailure(Call<LocationRes> call, Throwable t) {
                Errors errors = new Errors();
                errors.setCode(400);
                errors.setMessage(t.getLocalizedMessage());
                locationRes.setError(errors);
                locationRes.setStatus("fail");
                resMutableLiveData.setValue(locationRes);
                Log.e(TAG, "onFailure: "+t.getLocalizedMessage() );
            }
        });
       return resMutableLiveData;
    }
}
