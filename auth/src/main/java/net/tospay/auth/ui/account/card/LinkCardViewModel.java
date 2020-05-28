package net.tospay.auth.ui.account.card;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;

import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.CardRepository;
import net.tospay.auth.remote.response.CardInitRes;
import net.tospay.auth.ui.base.BaseViewModel;
import net.tospay.auth.utils.SharedPrefManager;

public class LinkCardViewModel extends BaseViewModel {
    private CardRepository cardRepository;
    private SharedPrefManager sharedPrefManager;
    private LiveData<Resource<String>> cardResponse;

    public LinkCardViewModel(CardRepository cardRepository, SharedPrefManager sharedPrefManager) {
        this.cardRepository = cardRepository;
        this.sharedPrefManager = sharedPrefManager;
    }

    public void getCardData(){
        cardResponse = cardRepository.getCardData(getBearerToken().get().toString());
    }

    public LiveData<Resource<String>> getCardResponse() {
        return cardResponse;
    }

}
