package net.tospay.auth.remote.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.response.AccountResponse;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.service.AccountService;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.remote.util.NetworkBoundResource;
import net.tospay.auth.ui.account.topup.StatusReq;
import net.tospay.auth.ui.account.topup.StatusRes;
import net.tospay.auth.ui.device_model.Errors;
import net.tospay.auth.ui.device_model.LocationReq;
import net.tospay.auth.ui.device_model.LocationRes;
import net.tospay.auth.ui.device_model.api.DeviceApi;
import net.tospay.auth.utils.AbsentLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AccountRepository {

    private final AccountService mAccountService;
    private final AppExecutors mAppExecutors;

    /**
     * @param mAppExecutors   - AppExecutor
     * @param mAccountService - Account service
     */
    public AccountRepository(AppExecutors mAppExecutors, AccountService mAccountService) {
        this.mAppExecutors = mAppExecutors;
        this.mAccountService = mAccountService;
    }

    /**
     * Retrieve user accounts
     *
     * @return LiveData
     */
    public LiveData<Resource<List<AccountType>>> accounts(String bearerToken, boolean showWallet) {
        return new NetworkBoundResource<List<AccountType>, Result<AccountResponse>>(mAppExecutors) {

            private List<AccountType> resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<AccountResponse> item) {
                AccountResponse accounts = item.getData();

                List<AccountType> accountTypeList = new ArrayList<>();
                List<Account> accountList;

                //Wallets
                if (showWallet) {
                    accountTypeList.addAll(accounts.getWallet());
                }

                //Mobile Accounts
                accountList = setAccountType(accounts.getMobile(), AccountType.MOBILE);
                accountTypeList.addAll(accountList);

                //Card accounts
                accountList = setAccountType(accounts.getCard(), AccountType.CARD);
                accountTypeList.addAll(accountList);

                //Bank accounts
                accountList = setAccountType(accounts.getBank(), AccountType.BANK);
                accountTypeList.addAll(accountList);

                resultsDb = accountTypeList;
            }

            @Override
            protected boolean shouldFetch(@Nullable List<AccountType> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<AccountType>> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<List<AccountType>>() {
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
            protected LiveData<ApiResponse<Result<AccountResponse>>> createCall() {
                return mAccountService.accounts(bearerToken);
            }

            private List<Account> setAccountType(List<Account> accounts, int accountType) {
                if (accounts != null) {
                    for (Account account : accounts) {
                        account.setAccountType(accountType);
                        if (accountType == AccountType.CARD) {
                            account.setVerified(true);
                        }
                    }

                    return accounts;
                }

                return new ArrayList<>();
            }

        }.asLiveData();
    }

    /**
     * Retrieve user accounts
     *
     * @return LiveData
     */
    public LiveData<Resource<List<Account>>> accounts(String bearerToken) {
        return new NetworkBoundResource<List<Account>, Result<AccountResponse>>(mAppExecutors) {

            private List<Account> resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<AccountResponse> item) {
                AccountResponse response = item.getData();

                List<Account> accounts = new ArrayList<>();

                for (Account account : response.getMobile()) {
                    account.setAccountType(AccountType.MOBILE);
                    accounts.add(account);
                }

                for (Account account : response.getCard()) {
                    account.setAccountType(AccountType.CARD);
                    accounts.add(account);
                }

                for (Account account : response.getBank()) {
                    account.setAccountType(AccountType.BANK);
                    accounts.add(account);
                }

                resultsDb = accounts;
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
            protected LiveData<ApiResponse<Result<AccountResponse>>> createCall() {
                return mAccountService.accounts(bearerToken);
            }

        }.asLiveData();
    }

    public LiveData<Resource<List<Account>>> getCards(String bearerToken){
        return new NetworkBoundResource<List<Account>,Result<List<Account>>>(mAppExecutors){
            private List<Account> accounts;
            @Override
            protected void saveCallResult(@NonNull Result<List<Account>> item) {
                accounts = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Account> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Account>> loadFromDb() {
                return new LiveData<List<Account>>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(accounts);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<List<Account>>>> createCall() {
                return mAccountService.getCardAccounts(bearerToken);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Account>>> getMobile(String bearer){
        return new NetworkBoundResource<List<Account>,Result<List<Account>>>(mAppExecutors){
            private List<Account> accounts;
            @Override
            protected void saveCallResult(@NonNull Result<List<Account>> item) {
                accounts = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Account> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Account>> loadFromDb() {
                return new LiveData<List<Account>>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(accounts);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<List<Account>>>> createCall() {
                return mAccountService.getMobileAccounts(bearer);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Account>>> getBank(String bearer){
        return new NetworkBoundResource<List<Account>,Result<List<Account>>>(mAppExecutors){
            private List<Account> accounts;
            @Override
            protected void saveCallResult(@NonNull Result<List<Account>> item) {
                accounts = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Account> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Account>> loadFromDb() {
                return new LiveData<List<Account>>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(accounts);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<List<Account>>>> createCall() {
                return mAccountService.getBankAccounts(bearer);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Result>> delete(final String bearerToken, final Map<String, Object> param) {
        return (new NetworkBoundResource<Result, Result>(this.mAppExecutors) {
            private Result resultsDb;

            protected void saveCallResult(@NonNull Result item) {
                this.resultsDb = item;
            }

            protected boolean shouldFetch(@Nullable Result data) {
                return true;
            }

            @NonNull
            protected LiveData<Result> loadFromDb() {
                return this.resultsDb == null ? AbsentLiveData.create() : new LiveData<Result>() {
                    protected void onActive() {
                        super.onActive();
                        this.setValue(resultsDb);
                    }
                };
            }

            @NonNull
            protected LiveData<ApiResponse<Result>> createCall() {
                return mAccountService.delete(bearerToken, param);
            }
        }).asLiveData();
    }

    public  LiveData<Resource<StatusRes>> getStatus(String token, StatusReq request){
       return new NetworkBoundResource<StatusRes,Result<StatusRes>>(mAppExecutors){
           private StatusRes response;
           @Override
           protected void saveCallResult(@NonNull Result<StatusRes> item) {
               response = item.getData();
           }

           @Override
           protected boolean shouldFetch(@Nullable StatusRes data) {
               return true;
           }

           @NonNull
           @Override
           protected LiveData<StatusRes> loadFromDb() {
               return new LiveData<StatusRes>() {
                   @Override
                   protected void onActive() {
                       super.onActive();
                       setValue(response);
                   }
               };
           }

           @NonNull
           @Override
           protected LiveData<ApiResponse<Result<StatusRes>>> createCall() {
               return mAccountService.checkStatus(token, request);
           }
       }.asLiveData();
    }

    public LiveData<LocationRes> getGeoLocation(LocationReq request){
        MutableLiveData<LocationRes> resMutableLiveData = new MutableLiveData<>();
        Retrofit retrofit = DeviceApi.getRetrofitClient();
        AccountService service = retrofit.create(AccountService.class);
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

            }
        });
        return resMutableLiveData;
    }
}
