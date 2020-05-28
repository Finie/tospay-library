package net.tospay.auth.remote.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.request.BankRequest;
import net.tospay.auth.remote.response.AccountLinkResponse;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.LinkBankResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.service.BankService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.remote.util.NetworkBoundResource;
import net.tospay.auth.ui.account.bank.bank_request.LinkBankReq;
import net.tospay.auth.ui.account.bank.model.BankOtpRequest;
import net.tospay.auth.ui.account.bank.model.ResendOtp;
import net.tospay.auth.utils.AbsentLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BankRepository {

    private final BankService mBankService;
    private final AppExecutors mAppExecutors;

    /**
     * @param mAppExecutors -AppExecutor
     * @param mBankService  - Bank service
     */
    public BankRepository(AppExecutors mAppExecutors,
                          BankService mBankService) {
        this.mAppExecutors = mAppExecutors;
        this.mBankService = mBankService;
    }

    public LiveData<Resource<Result>> resendOtp(String bearerToken, ResendOtp request){
        return new NetworkBoundResource<Result, Result>(mAppExecutors){

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
                return mBankService.resendOtp(bearerToken, request);
            }
        }.asLiveData();
    }
    public LiveData<Resource<LinkBankResponse>> link(String bearerToken, LinkBankReq request) {
        return new NetworkBoundResource<LinkBankResponse, Result<LinkBankResponse>>(mAppExecutors) {

            private LinkBankResponse resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<LinkBankResponse> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable LinkBankResponse data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<LinkBankResponse> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<LinkBankResponse>() {
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
            protected LiveData<ApiResponse<Result<LinkBankResponse>>> createCall() {
                return mBankService.link(bearerToken, request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Account>>> accounts(String bearerToken) {
        return new NetworkBoundResource<List<Account>, Result<List<Account>>>(mAppExecutors) {

            private List<Account> resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<List<Account>> items) {
                resultsDb = new ArrayList<>();
                for (Account account : items.getData()) {
                    account.setAccountType(AccountType.BANK);
                    resultsDb.add(account);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Account> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Account>> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<List<Account>>() {
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
            protected LiveData<ApiResponse<Result<List<Account>>>> createCall() {
                return mBankService.accounts(bearerToken);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> delete(String bearerToken, Map<String, Object> param) {
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
                return mBankService.delete(bearerToken, param);
            }
        }.asLiveData();
    }

    public  LiveData<Resource<Result>>  verifyBankLink(String BearerToken, BankOtpRequest request){
        return  new NetworkBoundResource<Result,Result>(mAppExecutors){
            private Result result;
            @Override
            protected void saveCallResult(@NonNull Result item) {
                result = item;
            }

            @Override
            protected boolean shouldFetch(@Nullable Result data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Result> loadFromDb() {
                return new LiveData<Result>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(result);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result>> createCall() {
                return mBankService.VerifyBankLink(BearerToken,request);
            }
        }.asLiveData();
    }
}
