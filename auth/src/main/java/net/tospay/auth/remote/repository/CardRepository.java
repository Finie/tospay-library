package net.tospay.auth.remote.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.CardInitRes;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.service.CardService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.remote.util.NetworkBoundResource;

public class CardRepository {
    private final CardService mCardService;
    private final AppExecutors mAppExecutors;

    public CardRepository(CardService mCardService, AppExecutors mAppExecutors) {
        this.mCardService = mCardService;
        this.mAppExecutors = mAppExecutors;
    }

    public LiveData<Resource<String>> getCardData(String bearer){
        return  new NetworkBoundResource<String, Result<String>>(mAppExecutors){
            private String cardInitRes;
            @Override
            protected void saveCallResult(@NonNull Result<String> item) {
                cardInitRes = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable String data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<String> loadFromDb() {
                return new LiveData<String>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(cardInitRes);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<String>>> createCall() {
                return mCardService.getCardData(bearer);
            }
        }.asLiveData();
    }
}
